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
        header = StringIO(open(filename).read(4096)).readlines()
        sample_count = 0
        for line in header:
            if not line.startswith('sample_count'):
                continue
            sample_count = int(line.split(' ')[2])
            break
        channel_count = 0
        for line in header:
            if not line.startswith('channel_count'):
                continue
            channel_count = int(line.split(' ')[2])
            break
        assert channel_count in (1,2)
        assert sample_count > 0

        parts = [
            eval,
            filename,
            gender,
            str(sample_count)
            ]
        for channel in xrange(channel_count):
            valid, invalid = read_mlf('%s.%d.mlf' % (filename, channel))
            # phoneme ratio
            r = float(valid) / float(invalid)
            parts += [str(valid), str(invalid), '%.8f' % r]
        print ','.join(parts)

def main():
    evals = ['SRE00', 'SRE02', 'SRE03', 'SRE04', 'SRE05', 'SRE06']
    genders = ['male', 'female']
    for eval in evals:
        filemap = map_files(eval)
        for gender in genders:
            filelist = '%s_%s_1side.txt' % (eval, gender)
            read_metadata(filemap, filelist, eval, gender)

if __name__ == '__main__':
    main()
