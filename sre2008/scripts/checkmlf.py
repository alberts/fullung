#!/usr/bin/env python

from StringIO import StringIO
import os
import re
import sys

def read_sphere_header(sph):
    fp = open(sph, 'rb')
    magic = fp.read(8).strip()
    if magic != 'NIST_1A':
        raise IOError, 'invalid SPHERE header'
    size = int(fp.read(8).strip())
    if size <= 0 or size > 8192:
        raise IOError, 'invalid size in SPHERE header'
    fp.seek(0)
    buf = fp.read(size)
    fp.close()
    buf = buf.strip()
    lines = StringIO(buf).readlines()
    if lines[-1] != 'end_head':
        raise IOError, 'end_head is missing'
    header = {}
    for line in lines[2:-1]:
        parts = re.split('\\s+', line.strip(), 2)
        name, dtype, value = parts
        if name == 'sample_count':
            header['sample_count'] = int(value)
            continue
        if name == 'channel_count':
            header['channel_count'] = int(value)
            continue
        if name == 'sample_rate':
            header['sample_rate'] = int(value)
            continue
    return header

def main():
    def check(sph):
        print sph
        header = read_sphere_header(sph)
        channels = header['channel_count']
        samples = header['sample_count']
        rate = float(header['sample_rate'])
        length = samples / rate
        for channel in xrange(channels):
            mlfname = '%s.%d.mlf' % (sph, channel)
            if not os.path.isfile(mlfname):
                print >>sys.stderr, 'ERROR: %s is missing' % mlfname
                continue
            lines = open(mlfname).readlines()
            if len(lines) == 0:
                print >>sys.stderr, 'ERROR: %s is empty' % mlfname
                continue
            # performance improvement assumption: all lines are okay
            # if last line is
            lines = [lines[-1]]
            incomplete = False
            for line in lines:
                parts = re.split('\\s+', line.strip())
                if len(parts) != 4:
                    incomplete = True
                    break
            if incomplete:
                print >>sys.stderr, 'ERROR: %s is incomplete' % mlfname
                continue
            endtime = long(parts[1]) / 1e7
            delta = length - endtime
            if delta > 0.025:
                print >>sys.stderr, 'ERROR: %s is truncated [delta=%f]' % (mlfname, delta)
                continue

    def visit(arg, dirname, names):
        for name in names:
            path = os.path.join(dirname, name)
            path = os.path.normpath(path)
            name = name.lower()
            if name.endswith('.sph') or name.endswith('.wav'):
                check(path)
    filenames = []
    os.path.walk(sys.argv[1], visit, filenames)

if __name__ == '__main__':
    main()
