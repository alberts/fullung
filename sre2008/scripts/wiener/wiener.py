#!/usr/bin/env python

from StringIO import StringIO
import re
import os
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

def mlf2silenceflags(filename, channel):
    header = read_sphere_header(filename)
    sample_count = header['sample_count']
    assert sample_count > 0
    frames = int(sample_count/8000.0/10.0e-3)-1
    mlffilename = '%s.%d.mlf' % (filename, channel)
    lines = open(mlffilename).readlines()
    silflags = ''
    for line in lines:
        line = line.strip()
        start, stop, phoneme, score = re.split('\\s+', line)
        start, stop = long(start), long(stop)
        stop = stop - start
        start = 0
        if phoneme in ('spk', 'int', 'pau', 'oth'):
            flag = '1'
        else:
            flag = '0'
        silflags += (flag * len(xrange(start, stop, 100000)))

    if len(silflags) > frames:
        silflags = silflags[:frames]
    elif len(silflags) < frames:
        delta = frames - len(silflags)
        silflags += silflags[-1] * delta

    assert len(silflags) == frames

    return silflags

def wiener(filename, channel, keep_other):
    silflags = mlf2silenceflags(filename, channel)
    fp = open('input.sil','w')
    fp.write(silflags)
    fp.close()

    channel_p1 = channel + 1
    status = os.system('sph2pipe -c %d -p -f raw %s input.raw' % (channel_p1, filename))
    assert status == 0

    if keep_other:
        other_channel = abs(channel - 1)
        other_channel_p1 = other_channel + 1
        status = os.system('sph2pipe -c %d -p -f raw %s other.raw' % (other_channel_p1, filename))
        assert status == 0

    status = os.system('nr -Shift 10 -Length 20 -fs 8000 -S 1 -Ssilfile input.sil -swapin 0 -swapout 0 -i input.raw -o wiener.raw')
    assert status == 0

    output = '%s.wiener.sph' % os.path.splitext(filename)[0]
    if keep_other:
        # maximize volume
        status = os.system("sox -r 8000 -s -2 wiener.raw -e stat -v 2>vc")
        status = os.system("sox -V -v `cat vc` -r 8000 -s -2 wiener.raw wiener2.raw")
        if channel == 0:
            soxargs = ('wiener2.raw', 'other.raw')
        else:
            soxargs = ('other.raw', 'wiener2.raw')
        status = os.system('sox -V --combine merge -r 8000 -s -2 %s -r 8000 -s -2 %s -U %s' % (soxargs + (output,)))
        assert status == 0
    else:
        status = os.system('sox -r 8000 -s -2 wiener.raw -U %s' % (output,))
        assert status == 0

def main():
    assert len(sys.argv) in (3,4)
    filename = sys.argv[1]
    if len(sys.argv) == 3:
        assert sys.argv[2] == 'interview'
        channel = 'a'
        keep_other = False
    elif len(sys.argv) == 4:
        assert sys.argv[2] == 'auxmic'
        channel = sys.argv[3]
        keep_other = True
    assert os.path.isfile(filename)
    assert channel in ('a', 'b')
    channel = ord(channel) - ord('a')
    wiener(filename, channel, keep_other)

if __name__ == '__main__':
    main()
