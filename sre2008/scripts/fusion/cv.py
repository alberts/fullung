#!/usr/bin/env python

import numpy as np
import re
import tables

GENDERS = {
    'm' : (0, 1),
    'f' : (1, 0)
    }

CHANNELS = ('a', 'b')

ANSWERS = ('target', 'nontarget')

LANGTYPES = {
    'eng:eng' : (0, 0, 1),
    'oth:eng' : (0, 1, 0),
    'eng:oth' : (0, 1, 0),
    'oth:oth' : (1, 0, 0)
    }

CHNTYPES = {
    'phn:phn' : (0, 0, 1),
    'mic:phn' : (0, 1, 0),
    'phn:mic' : (0, 1, 0),
    'mic:mic' : (1, 0, 0)
    }

def read_model_key(filename):
    lines = open(filename).readlines()
    models = {}
    for line in lines:
        parts = re.split('\\s+', line.strip())
        id, pin = parts[0], parts[2]
        assert id not in models
        models[id] = pin
    return models

def read_model_seg(filename):
    lines = open(filename).readlines()
    modelseg = {}
    for line in lines:
        parts = re.split('\\s+', line.strip())
        pin = parts[2]
        segs = parts[5].split(',')
        for seg in segs:
            segment, channel = seg.split(':')
            segment = segment.split('.')[0]
            trial = segment, channel
            assert channel in CHANNELS
            modelseg[trial] = pin
    return modelseg

def read_test_seg_key(filename):
    lines = open(filename).readlines()
    testseg = {}
    for line in lines:
        parts = re.split('\\s+', line.strip())
        segment, pin, channel = parts
        assert channel in CHANNELS
        trial = segment, channel
        assert trial not in testseg
        testseg[trial] = pin
    return testseg

def main():
    models = read_model_key('sre06_model_key_v2.txt')
    modelseg = read_model_seg('sre06_model_key_v2.txt')
    testseg = read_test_seg_key('sre06_test_seg_key_v11a_short.txt')
    # merge model segments with test segments
    for trial, pin in modelseg.iteritems():
        assert trial not in testseg
        testseg[trial] = pin

    speakers, scores = {}, []
    lines = open('scores.txt').readlines()
    for line in lines:
        key, gender, langtype, chntype, answer, trialscores = re.split('\\s+', line.strip(), 5)

        id, segchn = key.split('_')
        if id in models:
            pin = models[id]
        else:
            # handle short-short models
            pin = testseg[(id[:-2], id[-1])]

        segment, channel = segchn.split(':')
        assert channel in CHANNELS

        gender = GENDERS[gender.lower()]
        langtype = LANGTYPES[langtype.lower()]
        chntype = CHNTYPES[chntype.lower()]
        assert answer in ANSWERS

        trialscores = map(lambda x: float(x), re.split('\\s+', trialscores))
        trialscores.extend(chntype)
        trialscores.extend(langtype)
        trialscores.extend(gender)
        scores.append(trialscores)
        # calculate index afterwards so that they are 1-based, which
        # is what MATLAB expects
        scoreindex = len(scores)

        if not speakers.has_key(pin):
            speaker = {}
            speaker[ANSWERS[0]] = []
            speaker[ANSWERS[1]] = []
            speaker['trials'] = set()
            speakers[pin] = speaker
        trial = segment, channel
        speaker = speakers[pin]
        speaker[answer].append(trial + (scoreindex,))
        speaker['trials'].add(trial)

    h5file = tables.openFile('cvscores.h5', mode='w')
    all_scores = {ANSWERS[0] : [], ANSWERS[1] : []}
    for pin, speaker in speakers.iteritems():
        train_scores = {ANSWERS[0] : [], ANSWERS[1] : []}
        test_scores = {ANSWERS[0] : [], ANSWERS[1] : []}

        # set of pins this speaker is evaluated against
        ignore_pins = set()
        for trial in speaker['trials']:
            trial_pin = testseg[trial]
            ignore_pins.add(trial_pin)
        # explicitly add this speaker so that any trials involving
        # him/her are also excluded
        ignore_pins.add(pin)

        for pin2, speaker2 in speakers.iteritems():
            if pin == pin2: continue
            for answer in ANSWERS:
                answer_scores = train_scores[answer]
                for segment, channel, scoreindex in speaker2[answer]:
                    trial = segment, channel
                    trial_pin = testseg[trial]
                    if trial_pin in ignore_pins: continue
                    answer_scores.append(scoreindex)

        for answer in ANSWERS:
            for segment, channel, scoreindex in speaker[answer]:
                test_scores[answer].append(scoreindex)
                # subtract 1 from scoreindex for Python
                all_scores[answer].append(scores[scoreindex - 1])

        print 'writing scores for speaker %s' % pin
        group = h5file.createGroup('/', 'spk%s' % pin)
        traingroup = h5file.createGroup(group, 'train')
        testgroup = h5file.createGroup(group, 'test')
        for answer in ANSWERS:
            indices = np.array(train_scores[answer], dtype=np.int32)
            h5file.createArray(traingroup, answer, indices)
            if len(test_scores[answer]) > 0:
                indices = np.array(test_scores[answer], dtype=np.int32)
                h5file.createArray(testgroup, answer, indices)

    for answer in ANSWERS:
        scoresarr = np.array(all_scores[answer], dtype=np.float32)
        h5file.createArray('/', answer, scoresarr)
    h5file.createArray('/', 'scores', np.array(scores, dtype=np.float32))
    h5file.close()

if __name__ == '__main__':
    main()
