#!/usr/bin/env python

import sys

def main():
    lines = open(sys.argv[1]).readlines()

    evals = {}
    for line in lines:
        line = line.strip()
        parts = line.split(',')
        eval = parts[0]
        if not eval in evals:
            evals[eval] = []
        evals[eval].append((parts[1],int(parts[3])))

    del evals['SRE04']
    files = []
    go = True
    while go:
        for eval,evalfiles in evals.iteritems():
            assert len(evalfiles) > 0
            filename, filecount = evalfiles.pop(0)
            files.append(filename)
            if len(files) >= 300:
                go = False
                break
    files.sort()
    for filename in files:
        print filename

if __name__ == '__main__':
    main()
