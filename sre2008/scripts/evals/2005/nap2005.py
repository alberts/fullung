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
        #print >>sys.stderr, 'removing bad model %s' % id
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
                #print >> sys.stderr, 'removing bad trial %s channel %s for model %s' % (testkey + (id,))
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
            #print >>sys.stderr, 'removing unkeyed trial %s channel %s, condition %s' % \
            #    (badtrial + (condition,))
            del trials[badtrial]
    for badmodel in badmodels:
        condition = models[badmodel]['condition']
        #print >>sys.stderr, 'removing unkeyed model %s, condition %s' % \
        #    (badmodel, condition)
        del models[badmodel]

def check_models(models):
    for id in models:
        train = models[id]['train']
        trials = models[id]['trials']
        badtrials = set()
        for testkey, info in trials.iteritems():
            assert info['type'] in ('targ', 'non')

def get_speakers(models):
    speakers = {}
    for modelid, model in models.iteritems():
        def initpin(pin, gender):
            if pin in speakers:
                if gender != speakers[pin]['gender']:
                    speakers[pin]['gender'] = 'x'
                return
            speakers[pin] = {}
            speaker = speakers[pin]
            speaker['gender'] = gender
            speaker['1conv4w'] = set()
            speaker['1convmic'] = set()
        pin = model['pin']
        initpin(pin, model['gender'])
        for segment in model['train']:
            speakers[pin]['1conv4w'].add(segment)
        for segment, trial in model['trials'].iteritems():
            pin = trial['pin']
            initpin(pin, model['gender'])
            speakers[pin][trial['condition']].add(segment)
    return speakers

def print_speakers(speakers, fp):
    lines = []
    for pin, info in speakers.iteritems():
        parts = [info['gender'], str(pin)]
        klass = ''
        def join_segments(segments):
            segments = list(segments)
            segments.sort()
            return ','.join(map(lambda x: '%s:%s' % x, segments))
        if len(info['1conv4w']) > 0:
            klass = klass + 'phn'
            #parts.append(str(len(info['1conv4w'])))
        if len(info['1convmic']) > 0:
            klass = klass + 'mic'
            #parts.append(str(len(info['1convmic'])))
        parts.insert(1, klass)
        if len(info['1conv4w']) > 0:
            parts.append(join_segments(info['1conv4w']))
        if len(info['1convmic']) > 0:
            parts.append(join_segments(info['1convmic']))
        lines.append(' '.join(parts))
    lines.sort()
    print >>fp, '\n'.join(lines)

def main():
    models = {}
    conditions = ['1conv4w', '3conv4w', '8conv4w']
    for condition in conditions:
        filename = '%s.trn' % condition
        read_train(os.path.join('train', 'male', filename), models, 'm', condition)
        read_train(os.path.join('train', 'female', filename), models, 'f', condition)
    trialfiles = glob(os.path.join('trials', '*-1convmic-v2.ndx')) + \
        glob(os.path.join('trials', '*-1conv4w-v2.ndx'))
    for filename in trialfiles:
        parts = os.path.splitext(os.path.basename(filename))[0].split('-')
        traincond, testcond = parts[:2]
        if traincond not in conditions: continue
        read_trials(filename, models, traincond, testcond)
    remove_model_errors('model-errors-v0.txt', models)
    remove_trial_errors('testseg-error-v1.txt', models)
    read_key('sre05-key-v7c.txt', models)
    check_models(models)

    # additional pruning of bad data
    valid2005 = set([x.strip() for x in open('valid2005.txt').readlines()])
    badmodels = set()
    for modelid, model in models.iteritems():
        train = list(model['train'])[0][0]
        if train not in valid2005:
            badmodels.add(modelid)
            continue
        badtrials = set()
        for trialkey in model['trials']:
            if trialkey[0] not in valid2005:
                badtrials.add(trialkey)
        for trialkey in badtrials:
            del model['trials'][trialkey]
        if len(model['trials']) == 0:
            badmodels.add(modelid)
    for modelid in badmodels:
        del models[modelid]

    speakers = get_speakers(models)
    print_speakers(speakers, open('nap2005.txt', 'w'))

if __name__ == '__main__':
    main()
