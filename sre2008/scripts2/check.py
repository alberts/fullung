#!/usr/bin/env python

from StringIO import StringIO
import os.path
import sys

for line in open(sys.argv[1]).readlines():
    filename = line.strip()
    if not os.path.isfile(filename):
        print >>sys.stderr, '%s doesn\'t exist' % filename
        continue
    header = StringIO(open(filename).read(4096)).readlines()
    channel_count = 0
    for line in header:
        if not line.startswith('channel_count'):
            continue
        channel_count = int(line.split(' ')[2])
        break
    assert channel_count > 0
    for i in xrange(channel_count):
        mlffilename = '%s.%d.mlf' % (filename, i)
        if not os.path.isfile(mlffilename):
            print >>sys.stderr, '%s doesn\'t exist' % mlffilename
