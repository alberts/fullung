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
            parts = trn.split(':')
            if len(parts) == 1:
                sph, channel = parts[0], 'a'
            else:
                sph, channel = parts
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

def read_key(filename, models):
    lines = open(filename).readlines()
    lines = [re.split('\\s+', line.strip()) for line in lines]
    for parts in lines:
        state = parts[15]
        assert state in ('target', 'nontarget', 'bad')
        if state == 'bad': continue
        if state == 'target':
            state = 'targ'
        elif state == 'nontarget':
            state = 'non'
        else: assert False
        trialcond = parts[1].lower().strip()
        id = parts[9].lower().strip()
        model = models[id]
        gender = parts[5]
        language = parts[7]
        name = parts[0].lower().strip()
        channel = parts[6].lower().strip()
        if channel not in ('a', 'b'): continue
        trialkey = name, channel
        pin = parts[4].lower().strip()
        trial = {
            'condition' : trialcond,
            'gender' : gender,
            'type' : state,
            'pin' : pin
            }
        model['trials'][trialkey] = trial

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

def main():
    models = {}
    train_conditions = set(['10sec4w', '1conv4w', '3conv2w', '3conv4w', '8conv4w'])
    for condition in train_conditions:
        filename = '%s.trn' % condition
        read_train(os.path.join('train', 'male', filename), models, 'm', condition)
        read_train(os.path.join('train', 'female', filename), models, 'f', condition)
    read_model_key('sre06_model_key.txt', models)
    read_key('sre06_test_seg_key_v11a.txt', models)
    print_eval(models)

if __name__ == '__main__':
    main()
