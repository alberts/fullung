#!/usr/bin/env python

# TODO instead of repeating train scores the whole time, just write
# indexes into a big score matrix

import numpy as np
import re
import tables

GENDERS = {'m' : 0, 'f' : 1}

CHANNELS = ('a', 'b')

ANSWERS = ('target', 'nontarget')

LANGTYPES = {
    'eng:eng' : (1, 0, 0),
    'oth:eng' : (0, 1, 0),
    'eng:oth' : (0, 1, 0),
    'oth:oth' : (0, 0, 1)
    }

CHNTYPES = {
    'phn:phn' : (1, 0, 0),
    'mic:phn' : (0, 1, 0),
    'phn:mic' : (0, 1, 0),
    'mic:mic' : (0, 0, 1)
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
    testseg = read_test_seg_key('sre06_test_seg_key_v11a_short.txt')

    speakers = {}
    lines = open('scores.txt').readlines()
    for line in lines:
        key, gender, langtype, chntype, answer, scores = re.split('\\s+', line.strip(), 5)

        id, segchn = key.split('_')
        pin = models[id]
        segment, channel = segchn.split(':')
        assert channel in CHANNELS

        gender = GENDERS[gender.lower()]
        langtype = LANGTYPES[langtype.lower()]
        chntype = CHNTYPES[chntype.lower()]
        assert answer in ANSWERS

        scores = map(lambda x: float(x), re.split('\\s+', scores))
        #scores.append(gender)
        scores.extend(langtype)
        #scores.extend(chntype)

        if not speakers.has_key(pin):
            speaker = {}
            speaker[ANSWERS[0]] = []
            speaker[ANSWERS[1]] = []
            speaker['trials'] = set()
            speakers[pin] = speaker
        trial = segment, channel
        speaker = speakers[pin]
        speaker[answer].append(trial + (scores,))
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
                for segment, channel, scores in speaker2[answer]:
                    trial = segment, channel
                    trial_pin = testseg[trial]
                    if trial_pin in ignore_pins: continue
                    answer_scores.append(scores)

        for answer in ANSWERS:
            for segment, channel, scores in speaker[answer]:
                test_scores[answer].append(scores)
                all_scores[answer].append(scores)

        print 'writing scores for speaker %s' % pin
        group = h5file.createGroup('/', 'spk%s' % pin)
        traingroup = h5file.createGroup(group, 'train')
        testgroup = h5file.createGroup(group, 'test')
        for answer in ANSWERS:
            scoresarr = np.array(train_scores[answer], dtype=np.float32)
            h5file.createArray(traingroup, answer, scoresarr)
            if len(test_scores[answer]) > 0:
                scoresarr = np.array(test_scores[answer], dtype=np.float32)
                h5file.createArray(testgroup, answer, scoresarr)

    print 'writing all scores'
    for answer in ANSWERS:
        scoresarr = np.array(all_scores[answer],dtype=np.float32)
        h5file.createArray('/', answer, scoresarr)

    h5file.close()

if __name__ == '__main__':
    main()
