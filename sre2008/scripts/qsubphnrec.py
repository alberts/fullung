#!/usr/bin/env python

from StringIO import StringIO
from subprocess import Popen, PIPE
import re
import os.path
import subprocess
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

def get_channels(filename):
    return read_sphere_header(filename)['channel_count']

def get_channels_sox(filename):
    command = ['sox', '-V', '-t', 'sph', filename, '-e', 'stat']
    output = Popen(command, stdout=PIPE, stderr=PIPE).communicate()
    output = '\n'.join(output)
    result1 = re.search('(\d+) channel', output)
    result2 = re.search('Channels\s*:\s*(\d+)', output)
    if result1 is None and result2 is None:
        print output
    result = result1 or result2
    assert result is not None
    return int(result.group(1))

def really_submit(jobscript):
    p = Popen('qsub', stdin=PIPE, stdout=PIPE, stderr=PIPE)
    stdout, stderr = p.communicate(jobscript)
    if p.returncode != 0:
        print 'job submission failed:'
        print stderr
    print stdout,

def submit_job(filename, channel, channels):
    basefilename = os.path.basename(filename)
    jobname = 'phnrecjob.%s.%d' % (basefilename, channel)
    if channels == 1:
        assert channel == 0
        soxeffect = 'copy'
    elif channels == 2:
        if channel == 0:
            avgparam = '-l'
        else:
            avgparam = '-r'
        soxeffect = 'avg %s' % avgparam
    mlffilename = '%s.%d.mlf' % (filename, channel)
    params = {
        'soxeffect' : soxeffect,
        'jobname' : jobname,
        'filename' : filename,
        'mlffilename' : mlffilename,
        'channelp1' : channel + 1
        }
    jobscript = """#$ -N %(jobname)s
#$ -j y
#$ -o /dev/null
#$ -S /bin/bash
#$ -cwd
hostname 1>&2
WORKINGDIR=`pwd`
echo $TMPDIR
cd "$TMPDIR"
PHNRECHOME=/opt/phnrec
PHNRECEXE=$PHNRECHOME/phnrec
SPH2PIPE=$PHNRECHOME/sph2pipe
PHNRECCFG=$PHNRECHOME/PHN_HU_SPDAT_LCRC_N1500
cp -a "$PHNRECCFG" "$TMPDIR"
RAW="$TMPDIR/temp.raw"
MLF="$TMPDIR/temp.rec"
$SPH2PIPE -c %(channelp1)s -p -f raw %(filename)s $RAW
#/usr/bin/sox -V -t sph %(filename)s -t sw -c 1 $RAW %(soxeffect)s
$PHNRECEXE -v -c "$TMPDIR/PHN_HU_SPDAT_LCRC_N1500" -i $RAW -m $MLF -w lin16 -s wf -t str
cp -v $MLF %(mlffilename)s
"""  % params
    #print jobscript
    #import sys; sys.exit(1)
    really_submit(jobscript)

def main():
    assert len(sys.argv) == 2
    inputpath = sys.argv[1]
    assert os.path.exists(inputpath)

    def visit(arg, dirname, names):
        for name in names:
            path = os.path.join(dirname, name)
            name = name.lower()
            if name.endswith('.sph') or name.endswith('.wav'):
                arg.append(path)
    filenames = []
    os.path.walk(inputpath, visit, filenames)
    filenames.sort()
    for filename in filenames:
        filename = os.path.abspath(filename)
        filename = os.path.normpath(filename)
        channels = get_channels(filename)
        for i in xrange(channels):
            job = filename, i, channels
            mlffilename = '%s.%d.mlf' % (filename, i)
            if os.path.exists(mlffilename):
                print 'skipping %s, channel %d' % (filename, i)
                continue
            submit_job(*job)

if __name__ == '__main__':
    main()
