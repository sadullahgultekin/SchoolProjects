import tornado.web
import tornado.websocket
import tornado.httpserver
import tornado.ioloop
from tornado.ioloop import IOLoop

import json
from pprint import pprint

from jinja2 import Environment, FileSystemLoader, TemplateNotFound
from sqlalchemy import create_engine

import sql

engine = None

class TemplateRendering:
    def render_template(self, template_name, variables={}):
        template_dirs = [ "./templates" ]
        env = Environment(
            loader = FileSystemLoader(template_dirs),
            auto_reload=True,
            autoescape=False
        )
        env.globals["S"] = "/static"

        try:
            template = env.get_template(template_name)
        except TemplateNotFound:
            raise TemplateNotFound(template_name)

        content = template.render(variables)

        return content

class IndexPageHandler(tornado.web.RequestHandler, TemplateRendering):
    def get(self):
        docs = {}
        self.write(self.render_template("index.html", {"docs":docs}))

class QueryPageHandler(tornado.web.RequestHandler, TemplateRendering):
    def post(self):
        context = {}
        if self.get_argument('user_type') == 'admin':
            if self.get_argument('operation') == 'create-paper':
                authors = self.get_argument('arguments[author]').split(',')
                title = self.get_argument('arguments[title]')
                abstract = self.get_argument('arguments[abstract]')
                topics = self.get_argument('arguments[topic]').split(',')
                result = self.get_argument('arguments[result]')
                context['operation'] = sql.createPaper(engine,authors,title,abstract,topics,result)
            elif self.get_argument('operation') == 'update-paper':
                old_title = self.get_argument('arguments[old-title]')
                authors = self.get_argument('arguments[author]').split(',')
                title = self.get_argument('arguments[title]')
                abstract = self.get_argument('arguments[abstract]')
                topics = self.get_argument('arguments[topic]').split(',')
                result = self.get_argument('arguments[result]')
                context['operation'] = sql.updatePaper(engine,old_title,authors,title,abstract,topics,result)
            elif self.get_argument('operation') == 'delete-paper':
                title = self.get_argument('arguments[title]')
                context['operation'] = sql.deletePaper(engine,title)
            elif self.get_argument('operation') == 'create-author':
                name = self.get_argument('arguments[name]')
                surname = self.get_argument('arguments[surname]')
                context['operation'] = sql.createAuthor(engine,name,surname)
            elif self.get_argument('operation') == 'update-author':
                old_name = self.get_argument('arguments[old-name]')
                old_surname = self.get_argument('arguments[old-surname]')
                name = self.get_argument('arguments[name]')
                surname = self.get_argument('arguments[surname]')
                context['operation'] = sql.updateAuthor(engine,name,surname,old_name,old_surname)
            elif self.get_argument('operation') == 'delete-author':
                name = self.get_argument('arguments[name]')
                surname = self.get_argument('arguments[surname]')
                context['operation'] = sql.deleteAuthor(engine,name,surname)
            elif self.get_argument('operation') == 'create-topic':
                topic = self.get_argument('arguments[name]')
                context['operation'] = sql.createTopic(engine,topic)
            elif self.get_argument('operation') == 'update-topic':
                old_topic = self.get_argument('arguments[old-name]')
                topic = self.get_argument('arguments[name]')
                context['operation'] = sql.updateTopic(engine,topic,old_topic)
            elif self.get_argument('operation') == 'delete-topic':
                topic = self.get_argument('arguments[name]')
                context['operation'] = sql.deleteTopic(engine,topic)
        if self.get_argument('operation') == 'view-authors':
            context['authors'] = sql.viewAuthors(engine)
        elif self.get_argument('operation') == 'view-papers':
            context['papers'] = sql.viewPapers(engine)
        elif self.get_argument('operation') == 'view-topics':
            context['topics'] = sql.viewTopics(engine)
        elif self.get_argument('operation') == 'author-name':
            author = self.get_argument('arguments[name]')
            name, surname = author.split()[0], author.split()[1]
            context['papers'] = sql.paperOfAuthor(engine, name, surname)
        elif self.get_argument('operation') == 'topic-name':
            topic = self.get_argument('arguments[topic]')
            context['papers'] = sql.paperOfTopic(engine,topic)
        elif self.get_argument('operation') == 'search-word':
            word = self.get_argument('arguments[word]')
            context['papers'] = sql.searchWord(engine,word)
        elif self.get_argument('operation') == 'rank-by-sota':
            context['authors'] = sql.rankBySota(engine)
        elif self.get_argument('operation') == 'co-author-name':
            author = self.get_argument('arguments[name]')
            name, surname = author.split()[0], author.split()[1]
            context['authors'] = sql.findCoAuthors(engine,name,surname)

        self.write(self.render_template("result.html", {"result":context}))
    
        
class Application(tornado.web.Application):
    def __init__(self):
        handlers = [
            (r'/', IndexPageHandler),
            (r'/makeQuery', QueryPageHandler),
        ]
        settings = {
            "template_path": "templates",
            "static_path": "static"
        }
        tornado.web.Application.__init__(self, handlers, debug=True, **settings)
 
if __name__ == "__main__":
    engine = create_engine("mysql+pymysql://root:45904590@localhost:3306",echo = False)
    sql.createTables(engine)
    ws_app = Application()
    server = tornado.httpserver.HTTPServer(ws_app)
    server.listen(8080)
    tornado.ioloop.IOLoop.instance().start()

