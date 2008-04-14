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

    if False:
        # collect 101 hours of samples
        required = 8000 * 101 * 60 * 60
        count = 0
        files = []
        while count < required:
            for eval,evalfiles in evals.iteritems():
                assert len(evalfiles) > 0
                filename, filecount = evalfiles.pop(0)
                count += filecount
                files.append(filename)
        files.sort()
        for filename in files:
            print filename
    else:
        del evals['SRE04']
        files = []
        go = True
        while go:
            for eval,evalfiles in evals.iteritems():
                assert len(evalfiles) > 0
                filename, filecount = evalfiles.pop(0)
                files.append(filename)
                if len(files) >= 200:
                    go = False
                    break
        files.sort()
        for filename in files:
            print filename

if __name__ == '__main__':
    main()
