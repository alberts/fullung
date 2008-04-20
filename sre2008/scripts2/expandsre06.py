#!/usr/bin/env/python

import os.path
import sys

for line in open(sys.argv[1]).readlines():
    line = line.strip()
    path = os.path.join('Z:\\SRE06\\train\\data', '%s.sph' % line)
    path = os.path.normpath(path)
    if os.path.exists(path):
        print path
        continue
    path = os.path.join('Z:\\SRE06\\test\\data', '%s.sph' % line)
    path = os.path.normpath(path)
    if os.path.exists(path):
        print path
        continue
    assert False

