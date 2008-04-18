#!/usr/bin/env python

import re
import sys

def main():
    lines = open(sys.argv[1]).readlines()
    trials = {}
    for line in lines:
        line = line.strip()
        modelid, gender, train, trialslist = re.split('\\s+', line)
        trialslist = trialslist.split(',')
        for trial in trialslist:
            segment, channel, label = trial.split(':')
            key = segment, channel
            if not trials.has_key(key):
                trials[key] = []
            trials[key].append((modelid, label))
    keys = trials.keys()
    keys.sort()
    for key in keys:
        modeltrials = trials[key]
        trialstr = []
        for modelid, label in modeltrials:
            trialstr.append(':'.join([modelid, label]))
        trialstr = ','.join(trialstr)
        segment, channel = key
        print '%s:%s %s' % (segment, channel, trialstr)

if __name__ == '__main__':
    main()
