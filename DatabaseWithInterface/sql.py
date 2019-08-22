def createTables(engine):
    engine.execute('CREATE DATABASE IF NOT EXISTS project3db;')
    engine.execute('USE project3db;')
    engine.execute('CREATE TABLE IF NOT EXISTS Author(Name varchar(255) NOT NULL, Surname varchar(255) NOT NULL)')
    engine.execute('CREATE TABLE IF NOT EXISTS Topic(Topic varchar(255) UNIQUE NOT NULL, Sota INT NOT NULL DEFAULT 0)')
    engine.execute('CREATE TABLE IF NOT EXISTS Paper(Title varchar(255) UNIQUE NOT NULL, Abstract varchar(255) NOT NULL, Result INT NOT NULL)')
    engine.execute('CREATE TABLE IF NOT EXISTS AuthorToPaper(Name varchar(255) NOT NULL, Surname varchar(255) NOT NULL, Title varchar(255) NOT NULL)')
    engine.execute('CREATE TABLE IF NOT EXISTS TopicToPaper(Topic varchar(255) NOT NULL, Title varchar(255) NOT NULL)')
    engine.execute('DROP PROCEDURE IF EXISTS find_co_authors')
    engine.execute('CREATE PROCEDURE find_co_authors ( IN name varchar(255), IN surname varchar(255)) \
                    SELECT DISTINCT R2.Name, R2.Surname FROM AuthorToPaper AS R1 INNER JOIN AuthorToPaper AS R2 \
                    ON R1.Title=R2.Title WHERE NOT R1.Name = R2.Name AND R1.Name=name AND R1.Surname=surname;')
    engine.execute('DROP TRIGGER IF EXISTS update_sota_insertion')
    engine.execute('DROP TRIGGER IF EXISTS update_sota_deletion')
    engine.execute('DROP TRIGGER IF EXISTS update_sota_update')
    engine.execute('CREATE TRIGGER update_sota_insertion AFTER INSERT ON TopicToPaper \
                    FOR EACH ROW \
                    UPDATE Topic SET Sota=(SELECT IFNULL(MAX(Paper.Result),0) FROM Paper INNER JOIN TopicToPaper \
                    ON Paper.Title=TopicToPaper.Title WHERE TopicToPaper.Topic=NEW.Topic) WHERE Topic=NEW.Topic;')
    engine.execute('CREATE TRIGGER update_sota_deletion AFTER DELETE ON TopicToPaper \
                    FOR EACH ROW \
                    UPDATE Topic SET Sota=(SELECT IFNULL(MAX(Paper.Result),0) FROM Paper INNER JOIN TopicToPaper \
                    ON Paper.Title=TopicToPaper.Title WHERE TopicToPaper.Topic=OLD.Topic) WHERE Topic=OLD.Topic;')
    engine.execute('CREATE TRIGGER update_sota_update AFTER UPDATE ON TopicToPaper \
                    FOR EACH ROW \
                    UPDATE Topic SET Sota=(SELECT IFNULL(MAX(Paper.Result),0) FROM Paper INNER JOIN TopicToPaper \
                    ON Paper.Title=TopicToPaper.Title WHERE TopicToPaper.Topic=NEW.Topic) WHERE Topic=NEW.Topic;')

def createPaper(engine,authors, title, abstract, topics, result):
    engine.execute('INSERT INTO Paper VALUES("{}","{}","{}")'.format(title,abstract,result))
    for author in authors:
        name, surname = author.split()[0], author.split()[1]
        engine.execute('INSERT INTO AuthorToPaper VALUES("{}","{}","{}")'.format(name,surname,title))
    for topic in topics:
        engine.execute('INSERT INTO TopicToPaper VALUES("{}","{}")'.format(topic.strip(), title))
    return 'PAPER CREATED'

def updatePaper(engine,old_title, authors, title, abstract, topics, result):
    engine.execute('UPDATE Paper SET Title="{}", Abstract="{}", Result="{}" WHERE Title="{}"'.format(title, abstract, result, old_title))
    engine.execute('DELETE FROM AuthorToPaper WHERE Title="{}"'.format(old_title))
    for author in authors:
        name, surname = author.split()[0], author.split()[1]
        engine.execute('INSERT INTO AuthorToPaper VALUES("{}","{}","{}")'.format(name, surname, title))
    engine.execute('DELETE FROM TopicToPaper WHERE Title="{}"'.format(old_title))
    for topic in topics:
        engine.execute('INSERT INTO TopicToPaper SET Title="{}", Topic="{}"'.format(title, topic.strip()))   
    return 'PAPER UPDATED'

def deletePaper(engine,title):
    engine.execute('DELETE FROM Paper WHERE Title="{}"'.format(title))
    engine.execute('DELETE FROM AuthorToPaper WHERE Title="{}"'.format(title))
    engine.execute('DELETE FROM TopicToPaper WHERE Title="{}"'.format(title))
    all_topics = engine.execute('SELECT Topic FROM TopicToPaper WHERE Title="{}"'.format(title)).fetchall()
    return 'PAPER DELETED'

def createAuthor(engine,name, surname):
    engine.execute('INSERT INTO Author VALUES("{}","{}")'.format(name,surname))
    return 'AUTHOR CREATED'

def updateAuthor(engine,name, surname, old_name, old_surname):
    engine.execute('UPDATE Author SET Name="{}", Surname="{}" WHERE Name="{}" AND Surname="{}"'.format(name, surname, old_name, old_surname))
    engine.execute('UPDATE AuthorToPaper SET Name="{}", Surname="{}" WHERE Name="{}" AND Surname="{}"'.format(name, surname, old_name, old_surname))
    return 'AUTHOR UPDATED'

def deleteAuthor(engine,name, surname):
    engine.execute('DELETE FROM Author WHERE Name="{}" AND Surname="{}"'.format(name,surname))
    return 'AUTHOR DELETED'

def createTopic(engine,topic):
    engine.execute('INSERT INTO Topic (Topic) VALUES("{}")'.format(topic))
    return 'TOPIC CREATED'

def updateTopic(engine,topic, old_topic):
    engine.execute('UPDATE Topic SET Topic="{}" WHERE Topic="{}"'.format(topic, old_topic))
    engine.execute('UPDATE TopicToPaper SET Topic="{}" WHERE Topic="{}"'.format(topic,old_topic))
    return 'TOPIC UPDATED'

def deleteTopic(engine,topic):
    engine.execute('DELETE FROM Topic WHERE Topic="{}"'.format(topic))
    return 'TOPIC DELETED'

def viewAuthors(engine):
    all_authors = engine.execute('SELECT * FROM Author').fetchall()
    authors = []
    for author in all_authors:
        authors.append({'name':author[0],'surname':author[1]})
    return authors

def viewPapers(engine):
    all_papers = engine.execute('SELECT * FROM Paper').fetchall()
    papers = []
    for paper in all_papers:
        authors = engine.execute('SELECT Name, Surname FROM AuthorToPaper WHERE Title="{}"'.format(paper[0])).fetchall()
        topics = engine.execute('SELECT Topic FROM TopicToPaper WHERE Title="{}"'.format(paper[0])).fetchall()
        papers.append({'title':paper[0],'abstract':paper[1],'result':paper[2],'authors':authors,'topics':topics})
    return papers

def viewTopics(engine):
    all_topics = engine.execute('SELECT * FROM Topic').fetchall()
    topics = []
    for topic in all_topics:
        topics.append({'topic':topic[0],'sota':topic[1]})
    return topics

def paperOfAuthor(engine,name, surname):
    all_papers = engine.execute('SELECT Paper.* FROM Paper INNER JOIN AuthorToPaper \
                    ON Paper.title=AuthorToPaper.title \
                    WHERE AuthorToPaper.Name="{}" AND AuthorToPaper.Surname="{}"'.format(name,surname)).fetchall()
    papers = []
    for paper in all_papers:
        authors = engine.execute('SELECT Name, Surname FROM AuthorToPaper WHERE Title="{}"'.format(paper[0])).fetchall()
        topics = engine.execute('SELECT Topic FROM TopicToPaper WHERE Title="{}"'.format(paper[0])).fetchall()
        papers.append({'title':paper[0],'abstract':paper[1],'result':paper[2],'authors':authors,'topics':topics})
    return papers

def paperOfTopic(engine,topic):
    all_papers = engine.execute('SELECT Paper.* FROM Paper INNER JOIN TopicToPaper \
                    ON Paper.title=TopicToPaper.title \
                    WHERE TopicToPaper.Topic="{}"'.format(topic)).fetchall()
    papers = []
    for paper in all_papers:
        authors = engine.execute('SELECT Name, Surname FROM AuthorToPaper WHERE Title="{}"'.format(paper[0])).fetchall()
        topics = engine.execute('SELECT Topic FROM TopicToPaper WHERE Title="{}"'.format(paper[0])).fetchall()
        papers.append({'title':paper[0],'abstract':paper[1],'result':paper[2],'authors':authors,'topics':topics})
    return papers

def searchWord(engine,word):
    all_papers = engine.execute('SELECT * FROM Paper WHERE Title LIKE "%%'+word+'%%" OR Abstract LIKE "%%'+word+'%%"').fetchall()
    papers = []
    for paper in all_papers:
        authors = engine.execute('SELECT Name, Surname FROM AuthorToPaper WHERE Title="{}"'.format(paper[0])).fetchall()
        topics = engine.execute('SELECT Topic FROM TopicToPaper WHERE Title="{}"'.format(paper[0])).fetchall()
        papers.append({'title':paper[0],'abstract':paper[1],'result':paper[2],'authors':authors,'topics':topics})
    return papers

def rankBySota(engine):
    all_authors = engine.execute('SELECT Name, Surname FROM \
                                (Topic INNER JOIN Paper ON Topic.Sota = Paper.Result) \
                                INNER JOIN \
                                AuthorToPaper ON AuthorToPaper.Title = Paper.Title GROUP BY Name, Surname \
                                ORDER BY Count(*) DESC;')
    authors = []
    for author in all_authors:
        authors.append({'name':author[0],'surname':author[1]})
    return authors

def findCoAuthors(engine,name,surname):
    all_authors = engine.execute('CALL find_co_authors("{}","{}")'.format(name,surname));
    authors = []
    for author in all_authors:
        authors.append({'name':author[0],'surname':author[1]})
    return authors
    