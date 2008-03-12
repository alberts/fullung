#!/usr/bin/env python

from glob import glob
from subprocess import Popen, PIPE
import os.path
import sys

def visit(arg, dirname, names):
    for name in names:
        path = os.path.join(dirname, name)
        if name.endswith('.sph'):
            arg.append(path)

filenames = []
os.path.walk('/net/nist/albert/tmp', visit, filenames)

jobname = 'mfcc'
jobs = []
jobsize = 2
for i in xrange(0, len(filenames), jobsize):
    jobs.append(filenames[i: i+jobsize])

jobscript = """#$ -N %(jobname)s
#$ -j n
#$ -S /bin/bash
#$ -cwd
hostname 1>&2
JAVA_HOME=/opt/jdk1.6.0_04
PATH=$JAVA_HOME/bin:$PATH
cd XXXX CHANGE TO SGE TMP DIR
JAR=/net/nist/albert/scripts/sre2008.jar
MAINCLASS=net.lunglet.features.mfcc.MFCCBuilder
java -server -ea -Xmx256m -cp "$JAR" $MAINCLASS <<EOF
%%(filenames)s
EOF
"""  % {'jobname' : jobname}

jobscripts = []

for job in jobs:
    jobscripts.append(jobscript % {'filenames' : '\n'.join(job)})

for jobscript in jobscripts:
    p = Popen('qsub', stdin=PIPE, stdout=PIPE, stderr=PIPE)
    stdout, stderr = p.communicate(jobscript)
    if p.returncode != 0:
        print 'job submission failed:'
        print stderr
    print stdout,
    break
