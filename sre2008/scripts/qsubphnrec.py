#!/usr/bin/env python

from subprocess import Popen, PIPE
import re
import os.path
import subprocess
import sys

def get_channels(filename):
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
        'mlffilename' : mlffilename
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
PHNRECCFG=$PHNRECHOME/PHN_HU_SPDAT_LCRC_N1500
cp -a "$PHNRECCFG" "$TMPDIR"
RAW="$TMPDIR/temp.raw"
MLF="$TMPDIR/temp.rec"
/usr/bin/sox -V -t sph %(filename)s -t sw -c 1 $RAW %(soxeffect)s
$PHNRECEXE -v -c "$TMPDIR/PHN_HU_SPDAT_LCRC_N1500" -i $RAW -m $MLF -w lin16 -s wf -t str
cp -v $MLF %(mlffilename)s
"""  % params
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
            submit_job(*job)

if __name__ == '__main__':
    main()
