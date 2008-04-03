#!/usr/bin/env python

import os.path
import re
import sys

if len(sys.argv) != 3:
    print >>sys.stderr, 'usage: %s sphfilelist evaltxt' % sys.argv[0]
    sys.exit(1)

sphs = open(sys.argv[1]).readlines()
lines = open(sys.argv[2]).readlines()

requiredsph = {}
for line in lines:
    line = line.strip()
    parts = re.split('\\s+', line)
    for seg in ','.join(parts[2:]).split(','):
        segname = seg.split(':')[0].lower()
        requiredsph[segname] = False

for sph in sphs:
    sph = sph.strip()
    name = os.path.splitext(os.path.basename(sph))[0]
    name = name.lower()
    if name in requiredsph:
        print sph
        requiredsph[name] = True

for key, found in requiredsph.iteritems():
    assert found
