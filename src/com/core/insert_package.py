#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os
import tempfile

def getParent(inFile):
    #print os.path.abspath(inFile)
    p=os.path.dirname(inFile).split(os.sep)
    print p
    return '.' + p[1] + '.' + p[2] + ';'

def insertPackageStatement(inFile, insertStr):
    f = open(inFile, 'r+w')
    f.seek(os.SEEK_SET, 0)
    firstLine=f.readline()
    if firstLine.startswith('package'):
        return
    f.seek(os.SEEK_SET, 0)

    temp=tempfile.NamedTemporaryFile('r+w',dir=os.getcwd())
    #temp.write(insertStr+os.linesep)
    temp.write(insertStr + "\r\n")
    temp.write("\r\n")
    for line in f:
        temp.write(line)
    temp.flush()

    f.close()
    src=os.path.join(os.getcwd(),temp.name)
    dst=os.path.abspath(inFile)
    print src,dst
    os.rename(src,dst)
    #temp.close()

def insertStatement(fileList):
    print fileList
    packages='package com.core'
    fin = open(fileList, 'r')
    for eachFile in fin:
        eachFile=eachFile.strip()
        parent=getParent(eachFile)
        if parent:
            insertPackageStatement(eachFile,packages+parent)

    fin.close()


if __name__ == "__main__":
    print os.getcwd()
    insertStatement(sys.argv[1])