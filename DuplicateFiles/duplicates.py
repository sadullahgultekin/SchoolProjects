# Author : Sadullah Gultekin
# Date : 24 April 2017
#############################################################################################
# Python program that can be invoked with the following options and arguments:
# duplicates < -c <command> | -p > [-f | -d] [“...”] [<dir1> <dir2> ..]
# The duplicates program will traverse the directories and look for files or
# directories that are exact duplicates of each other. It will then carry out an action
# on the files or directories in question. The explanations of the options and
# arguments are given below:
#
# < -c <command> | -p >
# 
# If -c is given, a command will be passed the list of files/directories which are duplicates.
# If -p is given , then the duplicates will be printed (a new line should be printed between the 
# sets of duplicates)
# 
# [-f | -d ] 
# 
# -f means look for duplicate files, -d means look for duplicate directories. The default is
# duplicate files.
# 
# [ “...” ] 
# 
# Consider only the filenames or directory names that match the Python pattern given in “...“.
# The default is all files or all directories
# 
# [<dir1> <dir2> ..] 
# 
# The list of directories to traverse (note that the directories will be traversed recursively,
# i.e. directories and their subdirectories and their subdirectories etc. etc.). The default
# is current directory.
#############################################################################################
import os
import sys
import subprocess
import hashlib
import re
from collections import Counter

firstArg, secArg, thirdArg = '', '', ''
numOfArgs = 1
dirsToTraverse = []
dic = {}

# returns the hash value of the given directory
def get_hash(dir, pat):
    os.chdir(dir)  # change work. dir. to curr. dir. each time it enters a recursive function, or leaves one
    allincurr = os.listdir(dir)  # all files in the current directory
    if len(allincurr) == 0:  # if the current directory is empty, returns the hash value of ''
        if re.search(pat, dir.split('/')[-1], flags=0):
            dic[dir] = hashlib.sha256(b'').hexdigest()
        return hashlib.sha256(b'').hexdigest()
    else:  # if it is not empty
        hashes = []  # to save all hashes
        for item in allincurr:  # iterate all hashes in the directory
            if not os.path.isdir(item):  # if it is not a directory
                cmd = "shasum " + item + " | cut -d' ' -f1"  # command that will be run in terminal
                hashes.append(
                    subprocess.check_output(cmd, shell=True).decode('utf-8'))  # result of the command is saved
                os.chdir(dir)  # change work. dir. to curr. dir. each time it enters a recursive function, or leaves one
            else:  # if it is a directory
                hashes.append(get_hash(os.getcwd() + "/" + item, pat))  # hash of the subdirectory
                os.chdir(dir)  # change work. dir. to curr. dir. each time it enters a recursive function, or leaves one
        temp = ''.join(sorted(hashes))  # command that will used in sha function
        if re.search(pat, dir.split('/')[-1], flags=0):
            dic[dir] = hashlib.sha256(temp.encode()).hexdigest()
        return hashlib.sha256(temp.encode()).hexdigest()


if numOfArgs < len(sys.argv) and re.search('-(c|p)$', sys.argv[numOfArgs], flags=0):
    if sys.argv[numOfArgs] == '-c':
        firstArg = sys.argv[numOfArgs + 1]
        numOfArgs += 2
    elif sys.argv[numOfArgs] == '-p':
        firstArg = sys.argv[numOfArgs]
        numOfArgs += 1
    else:
        firstArg = '-p'
else:
    firstArg = '-p'

if numOfArgs < len(sys.argv) and re.search('^-(f|d)$', sys.argv[numOfArgs], flags=0):
    if sys.argv[numOfArgs] == '-f':
        secArg = '-f'
        numOfArgs += 1
    elif sys.argv[numOfArgs] == '-d':
        secArg = '-d'
        numOfArgs += 1
    else:
        secArg = '-d'
else:
    secArg = '-d'

if numOfArgs < len(sys.argv) and re.search('^\".*\"$', sys.argv[numOfArgs], flags=0):
    thirdArg = sys.argv[numOfArgs]
    numOfArgs += 1
else:
    thirdArg = '.*?'

while numOfArgs < len(sys.argv):
    dirsToTraverse.append(sys.argv[numOfArgs])
    numOfArgs += 1

if not len(dirsToTraverse):
    dirsToTraverse.append(os.getcwd())

if secArg == '-f':
    while dirsToTraverse:
        fullpathname = dirsToTraverse.pop()
        curlist = os.listdir(fullpathname)
        for fdname in curlist:
            newpath = fullpathname + "/" + fdname
            if os.path.isdir(newpath):
                dirsToTraverse.append(newpath)
            elif os.path.isfile(newpath) and re.search(thirdArg, fdname, flags=0):
                dic[newpath] = subprocess.check_output('shasum -a 256 ' + newpath + " | cut -d' ' -f1", shell=True)

else:
    for i, val in enumerate(dirsToTraverse):
        get_hash(val, thirdArg)

listofhashes = []
for key, val in dic.items():
    listofhashes.append(val)

q = {k: v for k, v in Counter(listofhashes).items() if v != 1}

if len(q):
    if firstArg == '-p':
        for myhash, times in q.items():
            for itemkey, itemval in dic.items():
                if itemval == myhash:
                    print(itemkey)
            print()
    else:
        for myhash, times in q.items():
            for itemkey, itemval in dic.items():
                if itemval == myhash:
                    print(subprocess.check_output(firstArg + ' ' + itemkey, shell=True))
            print()

