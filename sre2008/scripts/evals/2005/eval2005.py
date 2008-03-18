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
        testseg, testsegmod = re.split('\\s+', line, 1)
        testsegmod = re.split('\\s+', testsegmod)
        testsegmod = [m.lower() for m in testsegmod]
        for id in testsegmod:
            if not models.has_key(id): continue
            for channel in ('a', 'b'):
                testkey = testseg, channel
                if testkey not in models[id]['trials']: continue
                print >> sys.stderr, 'removing bad trial %s channel %s for model %s' % (testkey + (id,))
                del models[id]['trials'][testkey]

def read_key(filename, models):
    lines = open(filename).readlines()
    key = {}
    for line in lines:
        line = line.strip()
        parts = re.split('\\s+', line)
        modelid = parts[0]
        testseg = parts[1]
        channel = parts[4].lower()
        speakerid = parts[5]
        if speakerid == 'xxxx': continue
        speakerid = int(speakerid)
        gender = parts[8]
        if key.has_key(modelid):
            assert key[modelid] == speakerid
        key[modelid] = speakerid

        # XXX ignore 2w with regards to trials
        if channel not in ('a', 'b'): continue

        testkey = testseg, channel
        if key.has_key(testkey):
            assert key[testkey] == speakerid
        key[testkey] = speakerid

    badmodels = set()
    for id in models:
        if not key.has_key(id):
            badmodels.add(id)
            continue
        modelspeaker = key[id]
        models[id]['pin'] = modelspeaker
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
    print '<eval name="SRE2005">'
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
        parts = os.path.splitext(os.path.basename(filename))[0].split('-')
        traincond, testcond = parts[:2]
        if traincond not in conditions: continue
        read_trials(filename, models, traincond, testcond)
    remove_model_errors('model-errors-v0.txt', models)
    remove_trial_errors('testseg-error-v1.txt', models)
    read_key('sre05-key-v7c.txt', models)
    check_models(models)
    print_eval(models)

if __name__ == '__main__':
    main()
