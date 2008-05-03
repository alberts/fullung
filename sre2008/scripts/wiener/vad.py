#!/usr/bin/env python

import re
import sys

SAMPLE_RATE = 8000.0

### XXX sph2pipe -f raw -u tgihs.wiener.sph wiener.raw

buf = open(sys.argv[1],'rb').read()
vadbuf = []
lines = open(sys.argv[2]).readlines()
for line in lines:
    parts = re.split('\\s+', line.strip())
    starttime = float(parts[2])
    endtime = float(parts[4])
    assert starttime >= 0
    assert endtime > starttime
    startsample = int(starttime * SAMPLE_RATE)
    endsample = int(endtime * SAMPLE_RATE)
    assert startsample >= 0
    assert startsample < endsample
    assert endsample <= len(buf)
    vadbuf += buf[startsample:endsample]
assert len(vadbuf) <= len(buf)
fp = open('vad.raw', 'wb')
fp.write(''.join(vadbuf))
fp.close()
