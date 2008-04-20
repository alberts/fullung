#!/usr/bin/env python

# TODO support stereo data like SRE05

from StringIO import StringIO
import os.path
import sys

def read_mlf(mlffile):
    lines = open(mlffile).readlines()
    valid, invalid = 0, 0
    for line in lines:
        line = line.strip()
        parts = line.split(' ');
        if parts[2] in ('spk', 'oth', 'int', 'pau'):
            invalid += 1
        else:
            valid += 1
    return valid, invalid

def main():
    lines = open(sys.argv[1]).readlines()
    for line in lines:
        filename = line.strip()
        valid, invalid = read_mlf('%s.0.mlf' % filename)
        header = StringIO(open(filename).read(4096)).readlines()
        sample_count = 0
        for line in header:
            if not line.startswith('sample_count'):
                continue
            sample_count = int(line.split(' ')[2])
            break
        assert sample_count > 0
        # phoneme ratio
        r = float(valid) / float(invalid)
        print '%s,%d,%d,%d,%.8f' % \
              (filename, sample_count, valid, invalid, r)

if __name__ == '__main__':
    main()
