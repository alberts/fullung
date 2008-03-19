#!/usr/bin/env python

from glob import glob
import os.path
import re
import sys

def read_train(filename, models, gender, condition):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        id, train = re.split('\\s+', line)
        id = id.strip().lower()
        assert not models.has_key(id)
        models[id] = {}
        models[id]['train'] = set()
        train = train.split(',')
        for trn in train:
            # XXX this doesn't work with 2w
            sph, channel = trn.split(':', 2)
            sph = sph.split('.')[0]
            channel = channel.lower()
            assert channel in ('a', 'b')
            trainkey = sph, channel
            assert trainkey not in models[id]['train']
            models[id]['train'].add(trainkey)
        assert gender in ('m', 'f')
        models[id]['gender'] = gender
        models[id]['condition'] = condition
        models[id]['trials'] = {}
    return models

def remove_model_errors(filename, models):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        if line.find('#') == 0: continue
        id = line.strip().lower()
        if not models.has_key(id): continue
        print >>sys.stderr, 'removing bad model %s' % id
        del models[id]

def read_trials(filename, models, traincond, testcond):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        id, gender, testseg, channel = re.split('\\s+', line)
        id = id.strip().lower()
        assert models.has_key(id)
        assert models[id]['condition'] == traincond
        gender = gender.strip().lower()
        assert gender in ('m', 'f')
        testseg = testseg.strip().lower()
        channel = channel.strip().lower()
        assert channel in ('a', 'b')
        assert gender == models[id]['gender']
        testkey = testseg, channel
        assert testkey not in models[id]['trials']
        trial = {'type' : 'unk', 'condition' : testcond, 'gender' : gender}
        models[id]['trials'][testkey] = trial

def remove_trial_errors(filename, models):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        if line.find('#') == 0: continue
        testseg, testsegmod = re.split('\\s+', line, 1)
        testsegmod = re.split('\\s+', testsegmod)
        badids = []
        for id in testsegmod:
            # remove for all models
            if id.strip().lower() == 'm0000':
                badids = models.keys()
                break
            else:
                badids.append(id.strip().lower())
        for id in badids:
            if not models.has_key(id): continue
            for channel in ('a', 'b'):
                testkey = testseg, channel
                if testkey not in models[id]['trials']: continue
                print >> sys.stderr, 'removing bad trial %s channel %s for model %s' % (testkey + (id,))
                del models[id]['trials'][testkey]

def read_model_key(filename, models):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        parts = re.split('\\s+', line)
        id = parts[0]
        speakerid = parts[2]
        gender = parts[3]
        if not models.has_key(id): continue
        assert models[id]['gender'] == gender
        models[id]['pin'] = speakerid

def read_key(filename, models):
    lines = open(filename).readlines()
    key = {}
    for line in lines:
        line = line.strip()
        parts = re.split('\\s+', line)
        seg = parts[0]
        speakerid = parts[4]
        gender = parts[5]
        channel = parts[6].lower()
        if channel not in ('a', 'b'): continue
        segkey = seg, channel
        if not key.has_key(segkey):
            key[segkey] = speakerid
        else:
            assert speakerid == key[segkey]

    badmodels = set()
    for id in models:
        modelspeaker = models[id]['pin']
        trials = models[id]['trials']
        badtrials = set()
        for testkey in trials:
            if not key.has_key(testkey):
                badtrials.add(testkey)
                continue
            testspeaker = key[testkey]
            trial = trials[testkey]
            if testspeaker == modelspeaker:
                trial['type'] = 'targ'
            else:
                trial['type'] = 'non'
            trial['pin'] = testspeaker
        for badtrial in badtrials:
            condition = trials[badtrial]['condition']
            print >>sys.stderr, 'removing unkeyed trial %s channel %s, condition %s' % \
                (badtrial + (condition,))
            del trials[badtrial]
    for badmodel in badmodels:

        print models[badmodel]['train']

        condition = models[badmodel]['condition']
        print >>sys.stderr, 'removing unkeyed model %s, condition %s' % \
            (badmodel, condition)
        del models[badmodel]

def check_models(models):
    for id in models:
        train = models[id]['train']
        trials = models[id]['trials']
        badtrials = set()
        for testkey, info in trials.iteritems():
            assert info['type'] in ('targ', 'non')

def print_eval(models):
    print '<?xml version="1.0" encoding="UTF-8"?>'
    print '<eval name="SRE2006">'
    modelkeys = models.keys()
    modelkeys.sort()
    for id in modelkeys:
        model = models[id]
        gender = model['gender']
        pin = model['pin']
        print '<model id="%s" gender="%s" pin="%s">' % (id, gender, pin)
        print '<train condition="%s">' % model['condition']
        for sph, channel in model['train']:
            print '<segment name="%s" channel="%s" />' % (sph, channel)
        print '</train>'
        print '<trials>'
        trialkeys = model['trials'].keys()
        trialkeys.sort()
        for trialkey in trialkeys:
            sph, channel = trialkey
            trial = model['trials'][trialkey]
            condition = trial['condition']
            trialtype = trial['type']
            gender = trial['gender']
            pin = trial['pin']
            print '<trial name="%s" channel="%s" condition="%s" type="%s" gender="%s" pin="%s" />' % \
                (sph, channel, condition, trialtype, gender, pin)
        print '</trials>'
        print '</model>'
    print '</eval>'

def main():
    models = {}
    conditions = ['10sec4w', '1conv4w', '3conv4w', '8conv4w']
    for condition in conditions:
        filename = '%s.trn' % condition
        read_train(os.path.join('train', 'male', filename), models, 'm', condition)
        read_train(os.path.join('train', 'female', filename), models, 'f', condition)

    for filename in glob(os.path.join('trials', '*.ndx')):
        traincond, testcond = os.path.splitext(os.path.basename(filename))[0].split('-')
        if traincond not in conditions: continue
        read_trials(filename, models, traincond, testcond)
    remove_model_errors('model-errors-v2-DAR.txt', models)
    remove_trial_errors('testseg-errors-v5.txt', models)
    read_model_key('sre06_model_key_v9-DAR.txt', models)
    read_key('sre06_test_seg_key_v11-small-DAR.txt', models)
    check_models(models)
    print_eval(models)

if __name__ == '__main__':
    main()
