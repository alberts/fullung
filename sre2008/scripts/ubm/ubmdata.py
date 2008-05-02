#!/usr/bin/env python

from StringIO import StringIO
import os.path
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

def map_files(eval):
    def visit(arg, dirname, names):
        for name in names:
            #if name.lower().endswith('.sph'):
            if name[-4:] == '.sph':
                path = os.path.join(dirname, name)
                arg.append(path)
    filenames = []
    inputpath = 'Z:\\%s' % eval
    os.path.walk(inputpath, visit, filenames)
    filemap = {}
    for filename in filenames:
        basename = os.path.splitext(os.path.basename(filename))[0]
        filemap[basename] = filename
    return filemap

def read_mlf(mlffile, header):
    channels = header['channel_count']
    samples = header['sample_count']
    rate = float(header['sample_rate'])
    length = samples / rate

    lines = open(mlffile).readlines()
    valid, invalid = 0, 0
    for line in lines:
        parts = re.split('\\s+', line.strip())
        if len(parts) != 4:
            raise IOError, 'ERROR: %s is incomplete' % mlffile

        if parts[2] in ('spk', 'oth', 'int', 'pau'):
            invalid += 1
        else:
            valid += 1

    endtime = long(parts[1]) / 1e7
    delta = length - endtime
    if delta > 0.025:
        raise IOError, 'ERROR: %s is truncated [delta=%f]' % (mlffile, delta)

    return valid, invalid

def read_metadata(filemap, filelist, eval, gender):
    lines = open(filelist).readlines()
    for line in lines:
        line = line.strip()
        segparts = line.strip().split(':')
        if len(segparts) == 1:
            segment = segparts[0]
            channel = 'a'
        else:
            assert len(segparts) == 2
            segment, channel = segparts
        filename = filemap[segment]

        print >>sys.stderr, filename

        header = read_sphere_header(filename)
        sample_count = header['sample_count']
        channel_count = header['channel_count']

        parts = [
            eval,
            filename,
            segment,
            channel,
            gender,
            str(sample_count)
            ]
        valids = []
        invalids = []
        for channel in xrange(channel_count):
            valid, invalid = read_mlf('%s.%d.mlf' % (filename, channel), header)
            valids.append(valid)
            invalids.append(invalid)
            # phoneme ratio
            r = float(valid) / float(invalid)
            parts += [str(valid), str(invalid), '%.8f' % r]
        if len(valids) == 2:
            validsum = sum(valids)
            invalidsum = sum(invalids)
            try:
                validratio = float(max(valids))/float(min(valids))
            except ZeroDivisionError:
                validratio = 1000000.0
            parts += [str(validsum), str(invalidsum), '%.8f' % validratio]
        print ','.join(parts)

def main():
    #evals = ['SRE00', 'SRE02', 'SRE03', 'SRE04', 'SRE05', 'SRE06']
    evals = ['SRE05']
    genders = ['male', 'female']
    for eval in evals:
        filemap = map_files(eval)
        for gender in genders:
            filelist = '%s_%s_1side.txt' % (eval, gender)
            read_metadata(filemap, filelist, eval, gender)

if __name__ == '__main__':
    main()
