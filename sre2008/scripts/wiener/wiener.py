#!/usr/bin/env python

from StringIO import StringIO
import re
import os
import sys

def mlf2silenceflags(filename, channel):
    header = StringIO(open(filename).read(4096)).readlines()
    sample_count = 0
    for line in header:
        if not line.startswith('sample_count'):
            continue
        sample_count = int(line.split(' ')[2])
        break
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
    delta = frames - len(silflags)
    silflags += silflags[-1] * delta
    assert len(silflags) == frames
    return silflags

def main():
    lines = open(sys.argv[1]).readlines()
    for line in lines:
        line = line.strip()
        print line
        parts = re.split('\\s+', line)
        filename = parts[0]
        assert os.path.isfile(filename)
        for i in xrange(1, len(parts)):
            parts[i] = parts[i].lower()
        if len(parts) == 2:
            assert parts[1] == 'interview'
            channel = 'a'
            keep_other = False
        elif len(parts) == 3:
            assert parts[1] == 'auxmic'
            channel = parts[2]
            keep_other = True

        assert channel in ('a', 'b')
        channel = ord(channel) - ord('a')

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
        #status = os.system('copy input.raw wiener.raw');
        assert status == 0

        output = '%s.wiener.sph' % os.path.splitext(filename)[0]
        if keep_other:
            if channel == 0:
                soxargs = ('wiener.raw', 'other.raw')
            else:
                soxargs = ('other.raw', 'wiener.raw')
            status = os.system('sox -V --combine merge -r 8000 -s -2 %s -r 8000 -s -2 %s -U %s' % (soxargs + (output,)))
            assert status == 0
        else:
            status = os.system('sox -r 8000 -s -2 wiener.raw -U %s' % (output,))
            assert status == 0

if __name__ == '__main__':
    main()
