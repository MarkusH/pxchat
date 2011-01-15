#!/usr/bin/python
# -*- coding: utf-8 -*-
"""
    serverctl.py
    ~~~~~~~

    Controll script for handling the pxchat server

    :copyright: 2011 by the Markus Holtermann
    :license: `Creative Commons BY-NC-SA 3.0 <http://creativecommons.org/licenses/by-nc-sa/3.0/de/>`_
"""

import sys
import subprocess
from xml.dom.minidom import getDOMImplementation, parse

DEFAULT_CONFIG_FILE = './../data/config/server.xml'

def printhelp():
    print "HELP"
    exit()

def load(filename=DEFAULT_CONFIG_FILE):
    dom = parse(filename)

    try:
        port = dom.getElementsByTagName('port')[0].getAttribute('number')
    except:
        port = None

    try:
        serverlist = dom.getElementsByTagName('serverlist')[0].getAttribute('url')
    except:
        serverlist = None

    try:
        auth = dom.getElementsByTagName('auth')[0] or None
        users = {}
        if auth:
            dusers = auth.getElementsByTagName('user')
            for duser in dusers:
                users[duser.getAttribute('name')] = duser.getAttribute('password')
    except:
        users = None

    return port, serverlist, users


def save(port=None, serverlist=None, users=None, filename=DEFAULT_CONFIG_FILE):
    impl = getDOMImplementation()
    dom = impl.createDocument(None, "server", impl.createDocumentType('server', None, "../xml/server.dtd"))

    dserver = dom.documentElement

    dconfig = dom.createElement('config')

    if port:
        dport = dom.createElement('port')
        dport.setAttribute('number', port)
        dconfig.appendChild(dport)

    if serverlist:
        dserverlist = dom.createElement('serverlist')
        dserverlist.setAttribute('url', serverlist)
        dconfig.appendChild(dserverlist)

    dserver.appendChild(dconfig)

    dauth = dom.createElement('auth')
    if users:
        for user in users:
            if not users.get(user):
                print "[WARNING] User %s has no password" % user
            duser = dom.createElement('user')
            duser.setAttribute('name', user)
            duser.setAttribute('password', users.get(user))
            dauth.appendChild(duser)
    dserver.appendChild(dauth)

    print dom.toprettyxml(indent='    ', encoding='UTF-8')


def start(pidfilename='server.pid'):
    instance = subprocess.Popen('java pxchat/server/ServerMain', shell=True, stdout=subprocess.PIPE)
    pidfile = file(pidfilename, 'w')
    pidfile.write('%d' % instance.pid)
    pidfile.close()


def stop(sig=15, pidfilename='server.pid'):
    try:
        pidfile = file(pidfilename, 'r')
        pid = pidfile.read()
        pidfile.close()
        subprocess.Popen('kill -%d %s' % (sig, pid), shell=True, stdout=subprocess.PIPE)
        subprocess.Popen('rm -f "%s"' % pidfilename, shell=True, stdout=subprocess.PIPE)
    except:
        print "No running instance found"


param = sys.argv[1:]
count = len(param)
if param:
    port, serverlist, users = load()
    print port, serverlist, users

    if count == 1:
        if param[0] == "start":
            print "start"
            start()
            exit()
        elif param[0] == "stop":
            print "stop"
            stop()
            exit()
        elif param[0] == "restart":
            print "restart"
            stop()
            start()
            exit()
        elif param[0] == "reload":
            print "reload"
            stop(12)
            exit()
        else:
            printhelp()
    elif count == 2:
        if param[0] == "port":
            port = param[1]
        elif param[0] == "serverlist":
            serverlist = param[1]
        elif param[0] == "userdel":
            if users.has_key(param[1]):
                del users[param[1]]
            else:
                print "user %s does not exists" % param[1]
                exit()
        else:
            printhelp()
    elif count == 3:
        if param[0] == "useradd":
            if not users.has_key(param[1]):
                users[param[1]] = param[2]
            else:
                print "user %s exists" % param[1]
                exit()
        elif param[0] == "usermod":
            if users.has_key(param[1]):
                users[param[1]] = param[2]
            else:
                print "user %s does not exists" % param[1]
                exit()
        else:
            printhelp()
    else:
        printhelp()

    save(port=port, serverlist=serverlist, users=users)
else:
    printhelp()