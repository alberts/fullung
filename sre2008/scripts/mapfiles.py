#!/usr/bin/env python

from StringIO import StringIO
import os.path
import sys

def map_files(eval):
    def visit(arg, dirname, names):
        for name in names:
            path = os.path.join(dirname, name)
            name = name.lower()
            if name.endswith('.sph'):
                #assert os.path.exists('%s.0.mlf' % path)
                #assert os.path.exists('%s.1.mlf' % path)
                arg.append(path)
    filenames = []
    inputpath = 'Z:\\%s' % eval
    os.path.walk(inputpath, visit, filenames)
    filemap = {}
    for filename in filenames:
        basename = os.path.splitext(os.path.basename(filename))[0]
        filemap[basename] = filename
    return filemap

def read_metadata(filemap, filelist):
    lines = open(filelist).readlines()
    for line in lines:
        line = line.strip()
        filename = filemap[line]
        print filename

def main():
    filemap = map_files('SRE06')
    read_metadata(filemap, 'sre06_names.txt')

if __name__ == '__main__':
    main()
