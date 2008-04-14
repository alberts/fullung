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
                assert os.path.exists('%s.0.mlf' % path)
                arg.append(path)
    filenames = []
    inputpath = 'Z:\\%s' % eval
    os.path.walk(inputpath, visit, filenames)
    filemap = {}
    for filename in filenames:
        basename = os.path.splitext(os.path.basename(filename))[0]
        filemap[basename] = filename
    return filemap    

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

def read_metadata(filemap, filelist, eval, gender):
    lines = open(filelist).readlines()
    for line in lines:
        line = line.strip()
        filename = filemap[line]
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
        print '%s,%s,%s,%d,%d,%d,%.8f' % \
              (eval, filename, gender, sample_count, valid, invalid, r)

def main():
    evals = ['SRE00', 'SRE02', 'SRE03', 'SRE04']
    genders = ['male', 'female']
    for eval in evals:
        filemap = map_files(eval)
        for gender in genders:
            filelist = '%s_%s_1side.txt' % (eval, gender)
            read_metadata(filemap, filelist, eval, gender)

if __name__ == '__main__':
    main()
