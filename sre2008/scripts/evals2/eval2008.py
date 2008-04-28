#!/usr/bin/env python

from common import *

from glob import glob
import os.path
import re
import sys

def read_train(filename, gender, condition, models):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        id, train = re.split('\\s+', line)
        id = id.strip().lower()
        assert not models.has_key(id)
        models[id] = {}
        model = models[id]
        assert gender in ('m', 'f')
        model['gender'] = gender
        model['train'] = set()
        train = train.split(',')
        for trn in train:
            trn = trn.lower()
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
        model['condition'] = condition
        model['trials'] = {}
    return models

def read_trials(filename, trialcond, models):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip().lower()
        modelid, gender, trial = re.split('\\s+', line)
        name, channel = trial.split(':')
        assert channel in ('a', 'b')
        name = name.split('.')[0]
        trialkey = name, channel
        trial = {
            'condition' : trialcond,
            'gender' : gender,
            }
        model = models[modelid]
        assert model['gender'] == gender
        model['trials'][trialkey] = trial
    return models

def read_header_info(filename):
    info = {}
    for line in open(filename).readlines():
        line = line.strip().lower()
        filename, lang, speech_type, channel_type = line.split(',')
        filename = filename.split('.')[0]
        # INE in conjunction with other languages should be assumed
        # not to be in English
        lang = check_language(lang)
        info[filename] = {
            'language' : lang,
            'speech_type' : speech_type,
            'channel_type' : channel_type
            }
    return info

def append_header_info(models, info):
    for modelid, model in models.iteritems():
        assert len(model['train']) == 1
        name, channel = list(model['train'])[0]
        model.update(info[name])
        trials = model['trials']
        for trialid, trial in trials.iteritems():
            name, channel = trialid
            trial.update(info[name])

def main():
    models = {}
    read_train('male_short2.trn', 'm', 'short2', models)
    read_train('female_short2.trn', 'f', 'short2', models)
    read_trials('short2-short3.ndx', 'short3', models)
    info = read_header_info('NIST_SRE08_header_info.all.csv')
    append_header_info(models, info)
    print_models(models, open('sre2008.trn','w'))
    print_trials(models, open('sre2008.ndx','w'))

if __name__ == '__main__':
    main()
