#!/usr/bin/env python

from glob import glob
import os.path
import re
import sys

def read_testseg_errors(filename):
    lines = open(filename).readlines()
    global_errors = set()
    specific_errors = {}
    for line in lines:
        line = line.strip()
        if len(line) == 0: continue
        if line.find('#') == 0: continue
        testseg, models = re.split('\\s+', line, 1)
        models = re.split('\\s+',  models)
        for id in models:
            id = id.strip().lower()
            if id == 'x': continue
            if id == 'm0000':
                global_errors.add(testseg)
                break
            if not specific_errors.has_key(testseg):
                specific_errors[testseg] = set()
            specific_errors[testseg].add(id)
    for testseg in global_errors:
        if not specific_errors.has_key(testseg):
            continue
        print >>sys.stderr, '%s has specific errors and global errors' % testseg
        del specific_errors[testseg]
        assert not specific_errors.has_key(testseg)
    return global_errors, specific_errors

def read_model_errors(filename):
    lines = open(filename).readlines()
    model_errors = set()
    for line in lines:
        line = line.strip()
        if len(line) == 0: continue
        if line.find('#') == 0: continue
        model_errors.add(line)
    return model_errors

def read_key(filename):
    lines = open(filename).readlines()
    valid_states = 'target', 'nontarget', 'bad'
    key = {}
    for line in lines:
        line = line.strip()
        parts = re.split('\\s+', line)
        testseg = parts[0].strip().lower()
        model = parts[9].strip().lower()
        state = parts[15].strip().lower()
        assert state in valid_states
        key[testseg,model] = state != 'bad'
    return key

def main():
    global_errors, specific_errors = read_testseg_errors('testseg-errors.txt')
    model_errors = read_model_errors('model-errors.txt')
    key = read_key('sre06_test_seg_key_v11a.txt')
    for k, v in key.iteritems():
        testseg, model = k
        if model in model_errors:
            assert not v
            continue
        if testseg in global_errors:
            assert not v
            assert testseg not in specific_errors
            continue
        if not specific_errors.has_key(testseg):
            continue
        if model not in specific_errors[testseg]:
            continue
        # there is a specific error for this testseg and model, so the
        # trial must be marked as bad
        if v:
            print >>sys.stderr, '%s for model %s should be bad' % k

if __name__ == '__main__':
    main()
