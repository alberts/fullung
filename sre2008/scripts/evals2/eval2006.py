#!/usr/bin/env python

from atvs import *
from common import *
from david import *
import re

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
        badtrials = set()
        for trialkey, trial in model['trials'].iteritems():
            if trialkey not in key:
                badtrials.add(trialkey)
                continue
            trial.update(key[trialkey])
            if pin == trial['pin']:
                trial['answer'] = 'target'
            else:
                trial['answer'] = 'nontarget'
        for trialkey in badtrials:
            del model['trials'][trialkey]

def filter_models(models):
    valid2006 = set([x.strip() for x in open('valid2006.txt').readlines()])
    badmodels = set()
    for modelid, model in models.iteritems():
        train = list(model['train'])[0][0]
        if train not in valid2006:
            badmodels.add(modelid)
            continue
        badtrials = set()
        for trialkey in model['trials']:
            if trialkey[0] not in valid2006:
                badtrials.add(trialkey)
        for trialkey in badtrials:
            del model['trials'][trialkey]
        if len(model['trials']) == 0:
            badmodels.add(modelid)
    for modelid in badmodels:
        del models[modelid]

def atvs(key):
    models = {}
    read_atvs_models('dev_test_male_NIST08_vacios_eliminados.trn', 'm', models)
    read_atvs_models('dev_test_female_NIST08_vacios_eliminados.trn', 'f', models)
    read_atvs_trials('dev_test_NIST08_vacios_eliminados.ndx', models)
    append_key_info(models, key)
    #filter_models(models)
    print_models(models, open('atvs.trn','w'))
    print_trials(models, open('atvs.ndx','w'))

def david(key):
    models = {}
    train_conditions = set(['1conv4w', '1convmic'])
    for condition in train_conditions:
        filename = '%s-2006.trn' % condition
        read_david_models(filename, models)
    # append here to get gender
    append_key_info(models, key)
    read_david_trials('1conv4w-1conv4w-2006.ndx', models)
    read_david_trials('1conv4w-1convmic-2006.ndx', models)
    read_david_trials('1convmic-1conv4w-2006.ndx', models)
    read_david_trials('1convmic-1convmic-2006.ndx', models)
    append_key_info(models, key)
    #filter_models(models)
    print_models(models, open('david.trn','w'))
    print_trials(models, open('david.ndx','w'))

def main():
    key = {}
    read_short_key('sre06_test_seg_key_short.txt', key)
    read_model_key('sre06_model_key_v9.txt', key)
    atvs(key)
    david(key)

if __name__ == '__main__':
    main()
