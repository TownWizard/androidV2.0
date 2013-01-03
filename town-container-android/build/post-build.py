#!/usr/bin/python

from os import remove
from os.path import isfile
from shutil import copyfile

resources = [
  'res/values/strings.xml'
]

def main():
    isError = False
    try:
        restoreResources()
    except RuntimeError as e:
        print e
        isError = True
        
    if(isError): return 1
    
def restoreResources():
    for r in resources:
      fname = 'build/' + r.split('/')[-1] + '.bak'
      if isfile(fname):
        copyfile(fname, r)
        remove(fname)

main()