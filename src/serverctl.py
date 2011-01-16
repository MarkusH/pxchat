#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
    serverctl.py
    ~~~~~~~

    control script for handling the pxchat server

    :copyright: 2011 by the Markus Holtermann
    :license: `Creative Commons BY-NC-SA 3.0 <http://creativecommons.org/licenses/by-nc-sa/3.0/de/>`_
"""

import sys
import subprocess
from xml.dom.minidom import getDOMImplementation, parse

DEFAULT_CONFIG_FILE = './../data/config/server.xml'
CONFIG_ITEMS = [('name', 'value'), ('port', 'number'), ('serverlist', 'url')]
CONFIG_ITEM_SECS = [sec for sec,key in CONFIG_ITEMS]

def printhelp():
    print "HELP"
    exit()

def load(filename=DEFAULT_CONFIG_FILE):
    dom = parse(filename)
    result = {}

    for sec,key in CONFIG_ITEMS:
        try:
            result[sec] = dom.getElementsByTagName(sec)[0].getAttribute(key)
        except:
            result[sec] = None

    try:
        auth = dom.getElementsByTagName('auth')[0] or None
        users = {}
        if auth:
            dusers = auth.getElementsByTagName('user')
            for duser in dusers:
                users[duser.getAttribute('name')] = duser.getAttribute('password')
    except:
        users = None
        
    result['users'] = users

    return result

def save(config={}, filename=DEFAULT_CONFIG_FILE):
    impl = getDOMImplementation()
    dom = impl.createDocument(None, "server", impl.createDocumentType('server', None, "../xml/server.dtd"))

    dserver = dom.documentElement

    dconfig = dom.createElement('config')
    
    for sec,key in CONFIG_ITEMS:
        if config[sec]:
            ditem = dom.createElement(sec)
            ditem.setAttribute(key, config[sec])
            dconfig.appendChild(ditem)
    
    dserver.appendChild(dconfig)

    dauth = dom.createElement('auth')
    users = config['users']
    if users:
        for user in users:
            if not users.get(user):
                print "[WARNING] User %s has no password" % user
            duser = dom.createElement('user')
            duser.setAttribute('name', user)
            duser.setAttribute('password', users.get(user))
            dauth.appendChild(duser)
    dserver.appendChild(dauth)

    configFile = file(DEFAULT_CONFIG_FILE, 'w')
    dom.writexml(configFile, addindent='    ', newl='\n', encoding='UTF-8')
    configFile.close()

def start(pidfilename='server.pid'):
    print "Starting the server"
    instance = subprocess.Popen('java pxchat/server/ServerMain', shell=True, stdout=subprocess.PIPE)
    pidfile = file(pidfilename, 'w')
    pidfile.write('%d' % instance.pid)
    pidfile.close()

def stop(sig=15, pidfilename='server.pid'):
    print "Stopping the server"
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
    config = load()
    if count == 1:
        if param[0] == "start":
            start()
        elif param[0] == "stop":
            stop()
        elif param[0] == "restart":
            stop()
            start()
        elif param[0] == "reload":
            print "Reload the server config"
            stop(12)
        elif param[0] == "showconfig":
            print config
        else:
            printhelp()
        exit()
    elif count == 2:
        if param[0] in ["deluser", "userdel"]:
            if config['users'].has_key(param[1]):
                del config['users'][param[1]]
            else:
                print "user '%s' does not exists" % param[1]
                exit()
        elif param[0] in CONFIG_ITEM_SECS:
            for sec,key in CONFIG_ITEMS:
                if param[0] == sec:
                    config[sec] = param[1]
        else:
            printhelp()
    elif count == 3:
        if param[0] in ["adduser", "useradd"]:
            if not config['users'].has_key(param[1]):
                config['users'][param[1]] = param[2]
            else:
                print "user '%s' exists" % param[1]
                exit()
        elif param[0] in ["moduser", "usermod"]:
            if config['users'].has_key(param[1]):
                config['users'][param[1]] = param[2]
            else:
                print "user '%s' does not exists" % param[1]
                exit()
        else:
            printhelp()
    else:
        printhelp()

    save(config=config)
else:
    printhelp()