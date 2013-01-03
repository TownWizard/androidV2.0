#!/usr/bin/python

import sys
import os
import json

PARTNERS_FILE = 'build/partners/partners.json'

def main(argv):
    message = "\nTo build a partner apk file use\n\n ./build <partner_id>\n\nTo build all partner files, use\n\n./build -all\n"
    
    buildAll = False
    partnerId = 0
    install = False
    
    try:
        partnerId = int(argv[0])
    except:
        pass
        
    if partnerId != 0:
        try:
            installStr = argv[1]
            install = (installStr == '-install')
        except:
            pass
    else:
        try:
            buildAllStr = argv[0]
            buildAll = (buildAllStr == '-all')
        except:
            pass
            
    if not buildAll and partnerId == 0:
        print message
        return
                
    if buildAll: print "Building all partners ..."    

    partnerIds = loadPartnerIds()

    if partnerId != 0:
        if not partnerId in partnerIds:
            print "No partner id %d found in %s." % (partnerId, PARTNERS_FILE)
            return
        partnerIds = []        
        partnerIds.append(partnerId)
    else:
        partnerIds = loadPartnerIds()
    
    if install and partnerId != 0:
        command = 'ant -q clean release install'
    else:
        command = 'ant -q clean release'    
    
    for id in partnerIds:
        print "Building partner %s ..." % id
        os.environ['PARTNER_ID'] = str(id)
        os.system(command)
        

def loadPartnerIds():
    ids = []
    with open(PARTNERS_FILE) as f:
        partnerList = json.loads(f.read())
        for p in partnerList:
            ids.append(int(p['id']))
    return ids

if __name__ == '__main__':
    main(sys.argv[1:])