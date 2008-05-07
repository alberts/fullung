#!/usr/bin/env python

import os.path
import re
import sys

def map_files(inputpath):
    def visit(arg, dirname, names):
        for name in names:
            path = os.path.join(dirname, name)
            name = name.lower()
            if name.endswith('.sph'):
                arg.append(path)
    filenames = []
    os.path.walk(inputpath, visit, filenames)
    filemap = {}
    for filename in filenames:
        if filename.find('.wiener.') >= 0:
            channel_type = 'mic'
        else:
            channel_type = 'phn'
        basename = os.path.basename(filename).split('.')[0]
        key = basename, channel_type
        assert key not in filemap
        filemap[key] = filename
    return filemap

def map_segments(filemap, lines):
    for line in lines:
        segchn, channel_type = re.split(' ', line.strip())
        segment, channel = segchn.split(':')
        key = segment, channel_type
        if key not in filemap:
            print '%s is missing' % str(key)
        continue
        filename = filemap[key]
        mfccfilename = '%s.mfcc.h5' % filename
        assert os.path.isfile(mfccfilename)
        print '%s:%s' % (mfccfilename, channel)

def main():
    filemap = map_files(sys.argv[1])
    map_segments(filemap, open(sys.argv[2]).readlines())

if __name__ == '__main__':
    main()
