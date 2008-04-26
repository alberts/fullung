#!/usr/bin/env python

from common import *

from glob import glob
import os.path
import re
import sys

def read_atvs_models(filename, gender, models):
    assert gender in ('m', 'f')
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        id, train = re.split('\\s+', line)
        id = id.lower()
        assert not models.has_key(id)
        models[id] = {}
        model = models[id]
        model['gender'] = gender
        model['train'] = set()
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
            assert trainkey not in model['train']
            model['train'].add(trainkey)
        model['trials'] = {}
    return models

def read_atvs_trials(filename, models):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        modelid, gender, name, channel = re.split('\\s+', line)
        assert channel in ('a', 'b')
        trialkey = name, channel
        trial = {'gender' : gender}
        model = models[modelid]
        assert model['gender'] == gender
        model['trials'][trialkey] = trial

def read_david_models(filename, models):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        id, train = re.split('\\s+', line)
        id = id.lower()
        assert not models.has_key(id)
        models[id] = {}
        models[id]['train'] = set()
        train = train.split(',')
        for trn in train:
            parts = trn.split('-')
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
        models[id]['trials'] = {}
    return models

def read_david_trials(filename, models, trials):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        modelid, gender, trial = re.split('\\s+', line)
        name, channel = trial.split('-')
        assert channel in ('a', 'b')
        trialkey = name, channel
        trial = {'gender' : gender}
        model = models[modelid]
        assert model['gender'] == gender
        model['trials'][trialkey] = trial
    return models, trials

def read_key(filename, key):
    valid_trialconds = '1conv4w', '1convmic'
    lines = open(filename).readlines()
    for line in lines:
        parts = re.split('\\s+', line.strip())

        # segment name and channel
        name = parts[0].lower()
        channel = parts[6].lower()
        if channel not in ('a', 'b'): continue
        assert channel in ('a', 'b')
        trialkey = name, channel
        if trialkey in key: continue

        # trial condition
        trialcond = parts[1].lower()
        if trialcond not in valid_trialconds: continue

        # answer
        answer = parts[15].lower()
        if answer == 'bad': continue
        assert answer in ('target', 'nontarget')

        gender = parts[5].lower()
        assert gender in ('m', 'f')
        language = check_language(parts[7].lower())
        pin = parts[4].lower()
        if trialcond == '1conv4w' :
            channel_type = 'phn'
        elif trialcond == '1convmic':
            channel_type = 'mic'
        else:
            assert False
        key[trialkey] = {
            'gender' : gender,
            'pin' : pin,
            'language' : language,
            'speech_type' : 'phonecall',
            'channel_type' : channel_type
            }
    return key

def read_short_key(filename, key):
    lines = open(filename).readlines()
    for line in lines:
        parts = re.split('\\s+', line.strip())
        name = parts[0].lower()
        channel = parts[6].lower()
        if channel not in ('a', 'b'): continue
        assert channel in ('a', 'b')
        trialkey = name, channel
        assert trialkey not in key
        gender = parts[5].lower()
        assert gender in ('m', 'f')
        language = check_language(parts[7].lower())
        pin = parts[4].lower()
        trialcond = parts[1].lower()
        if trialcond == '1conv4w' :
            channel_type = 'phn'
        elif trialcond == '1convmic':
            channel_type = 'mic'
        else:
            assert False
        key[trialkey] = {
            'gender' : gender,
            'pin' : pin,
            'language' : language,
            'speech_type' : 'phonecall',
            'channel_type' : channel_type
            }
    return key

def read_model_key(filename, key):
    lines = open(filename).readlines()
    for line in lines:
        parts = re.split('\\s+', line.strip())
        traincond = parts[1]
        if traincond.find('conv4w') != 1: continue
        pin = parts[2].lower()
        gender = parts[3].lower()
        language = check_language(parts[4].lower())
        for part in parts[5].split(','):
            name, channel = part.split(':')
            name = name.split('.')[0]
            assert channel in ('a', 'b')
            trialkey = name, channel
            if trialkey in key:
                assert gender == key[trialkey]['gender']
                continue
            assert trialkey not in key
            key[trialkey] = {
                'gender' : gender,
                'pin' : pin,
                'language' : language,
                'speech_type' : 'phonecall',
                'channel_type' : 'phn',
                }

def append_key_info(models, key):
    for modelid, model in models.iteritems():
        assert len(model['train']) == 1
        trainkey = list(model['train'])[0]
        model.update(key[trainkey])
        pin = model['pin']
        for trialkey, trial in model['trials'].iteritems():
            trial.update(key[trialkey])
            if pin == trial['pin']:
                trial['answer'] = 'target'
            else:
                trial['answer'] = 'nontarget'

def main():
    key = {}
    read_short_key('sre06_test_seg_key_short.txt', key)
    read_model_key('sre06_model_key_v9.txt', key)
    models = {}
    read_atvs_models('dev_test_male_NIST08_vacios_eliminados.trn', 'm', models)
    read_atvs_models('dev_test_female_NIST08_vacios_eliminados.trn', 'f', models)
    read_atvs_trials('dev_test_NIST08_vacios_eliminados.ndx', models)
    append_key_info(models, key)
    print_models(models, open('atvs_models.txt','w'))
    print_trials(models, open('atvs_trials.txt','w'))

    #read_key('sre06_test_seg_key_v11a.txt', models)
    #print_eval(models)
    #'language' : lang,
    #'speech_type' : speech_type,
    #'channel_type' : channel_type
    """
    models = {}
    train_conditions = set(['1conv4w', '1convmic'])
    for condition in train_conditions:
        filename = '%s-2006.trn' % condition
        read_train(os.path.join(filename), models, condition)
    read_trials('1conv4w-1conv4w-2006.ndx', models, '1conv4w')
    read_trials('1conv4w-1convmic-2006.ndx', models, '1convmic')
    read_trials('1convmic-1conv4w-2006.ndx', models, '1conv4w')
    read_trials('1convmic-1convmic-2006.ndx', models, '1convmic')
    """

if __name__ == '__main__':
    main()
