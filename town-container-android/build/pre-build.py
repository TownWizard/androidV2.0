#!/usr/bin/python

from os import environ
from os import remove
from os.path import isfile
from shutil import copyfile
from xml.dom import minidom
import json
import os

PARTNERS_FILE = 'build/partners/partners.json'

resources = [
  'res/values/strings.xml'
]

def main():
    isError = False
    try:
        idToJson = loadPartnerData()
        backupResources()
        replaceStrings(idToJson)
    except RuntimeError as e:
        print e
        isError = True
            
    if(isError): return 1
    
def loadPartnerData():
    idToJson = {}
    with open(PARTNERS_FILE) as f:
        partnerList = json.loads(f.read())
        for p in partnerList:
            idToJson[int(p['id'])] = p
    
    return idToJson
    
def backupResources():    
    for r in resources:
      fname = 'build/' + r.split('/')[-1] + '.bak'
      if isfile(fname): remove(fname)
      copyfile(r, fname)

def replaceStrings(idToJson):
    file = 'res/values/strings.xml'
    xmldoc = minidom.parse(file)
    itemlist = xmldoc.getElementsByTagName('string')
    for s in itemlist :
        if s.attributes['name'].value == 'app_name':
            partnerId = int(environ['PARTNER_ID'])
            appName = idToJson[partnerId]['name']
            print "Replacing app_name with %s" % appName
            s.childNodes[0].data = appName
        
    with open(file, 'w') as f:
        print "strings.xml file content modified as:"
        print xmldoc.toxml()
        f.write(xmldoc.toxml())
    
main()