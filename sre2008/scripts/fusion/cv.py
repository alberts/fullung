#!/usr/bin/env python

import re
import tables

CHANNELS = ('a', 'b')
ANSWERS = ('targ', 'non')

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
    lines = open('eval.txt').readlines()
    for line in lines:
        traincond, adapt, testcond, gender, id, \
            segment, channel, decision, score, answer = \
            re.split('\\s+', line.strip())
        pin = models[id]
        score = float(score)
        assert channel in CHANNELS
        if not speakers.has_key(pin):
            speaker = {}
            speaker['targ'] = []
            speaker['non'] = []
            speaker['trials'] = set()
            speakers[pin] = speaker
        trial = segment, channel
        speaker = speakers[pin]
        speaker[answer].append(trial + (score,))
        speaker['trials'].add(trial)

    h5file = tables.openFile('cvscores.h5', mode='w')
    all_scores = {'targ' : [], 'non' : []}
    for pin, speaker in speakers.iteritems():
        train_scores = {'targ' : [], 'non' : []}
        test_scores = {'targ' : [], 'non' : []}
        for pin2, speaker2 in speakers.iteritems():
            if pin == pin2: continue
            for answer in ANSWERS:
                answer_scores = train_scores[answer]
                for segment, channel, score in speaker2[answer]:
                    # ignore trials on which the target speaker was
                    # also evaluated
                    trial = segment, channel
                    if trial in speaker['trials']: continue

                    # ignore trials with the target speaker
                    trial_pin = testseg[trial]
                    if pin == trial_pin: continue

                    answer_scores.append(score)

        for answer in ANSWERS:
            for segment, channel, score in speaker[answer]:
                test_scores[answer].append(score)
                all_scores[answer].append(score)

        print 'writing scores for speaker %s' % pin
        group = h5file.createGroup('/', 'spk%s' % pin)
        traingroup = h5file.createGroup(group, 'train')
        testgroup = h5file.createGroup(group, 'test')
        for answer in ANSWERS:
            h5file.createArray(traingroup, answer, train_scores[answer])
            if len(test_scores[answer]) > 0:
                h5file.createArray(testgroup, answer, test_scores[answer])

    h5file.createArray('/', 'targ', all_scores['targ'])
    h5file.createArray('/', 'non', all_scores['non'])
    h5file.close()

if __name__ == '__main__':
    main()
