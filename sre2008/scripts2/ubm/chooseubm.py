#!/usr/bin/env python

import sys

def main():
    # input should be sorted according to valid phonemes in descending
    # order and invalid phonemes in ascending order
    lines = open(sys.argv[1]).readlines()
    evals = {}
    for line in lines:
        line = line.strip()
        parts = line.split(',')
        eval = parts[0]
        if not eval in evals:
            evals[eval] = []
        evals[eval].append((parts[1],int(parts[3])))

    # adjust this to change composition of data
    evalkeys = ['SRE00', 'SRE02', 'SRE03', 'SRE04']
    required = 8000 * 175 * 60 * 60
    count = 0
    files = []

    evalcounts = {}
    for eval in evals.iterkeys():
        evalcounts[eval] = 0
    while count < required:
        for evalkey in evalkeys:
            evalfiles = evals[evalkey]
            assert len(evalfiles) > 0
            filename, filecount = evalfiles.pop(0)
            count += filecount
            evalcounts[evalkey] += filecount
            files.append(filename)
    for eval, count in evalcounts.iteritems():
        print >>sys.stderr, '%s -> %f hours' % (eval, count/8000.0/60.0/60.0)
    files.sort()
    for filename in files:
        print filename

if __name__ == '__main__':
    main()
