#!/usr/bin/env python

import re

systems06 = [
    'david_meta.txt',
    'david.f5agmm.txt',
    'david.bothgsv1024.txt',
    'david.femalegsv512.txt',
    'david.malegsv512.txt',
    'short-short-2006-t.ml1-eval',
    'short-short-2006-t.mlf-eval',
    'short-short-2006-t.mlm-eval'
    ]

systems08 = [
    'sre2008_meta.txt',
    'sre2008.f5agmm.txt',
    'sre2008.bothgsv1024.txt',
    'sre2008.femalegsv512.txt',
    'sre2008.malegsv512.txt',
    'short2-short3-2008-t.ml1-eval',
    'short2-short3-2008-t.mlf-eval',
    'short2-short3-2008-t.mlm-eval'
    ]

def merge(systems):
    trials = {}
    meta = systems[0]
    for line in open(meta).readlines():
        key, gender, lang, chntype, answer = re.split('\\s+', line.strip())
        assert key not in trials
        trials[key] = {
            'gender' : gender,
            'lang' : lang,
            'chntype' : chntype,
            'answer' : answer,
            'scores' : []
            }
    systems = systems[1:]

    # Albert's systems
    for system in systems[0:4]:
        for line in open(system).readlines():
            key, gender, lang, chntype, answer, score = re.split('\\s+', line.strip())
            trial = trials[key]
            assert gender == trial['gender']
            assert chntype == trial['chntype']
            assert lang == trial['lang']
            assert answer == trial['answer']
            trial['scores'].append(float(score))

    # David's systems
    for system in systems[4:]:
        for line in open(system).readlines():
            parts = re.split('\\s+', line.strip())
            model = parts[4]
            segment = parts[5]
            channel = parts[6]
            score = parts[8]
            gender = parts[3]
            key = '%s_%s:%s' % (model, segment, channel)
            trial = trials[key]
            assert gender == trial['gender']
            trial['scores'].append(float(score))

    for key, trial in trials.iteritems():
        assert len(trial['scores']) == 7
        assert gender in ('m','f')
        assert chntype in ('phn:mic','mic:mic','mic:phn','phn:phn')
        assert lang in ('eng:oth','oth:oth','oth:eng','eng:eng')

    return trials

def format(trials):
    lines = []
    for key, trial in trials.iteritems():
        lines.append('%s %s %s %s %s %s\n' % \
                         (key, trial['gender'], trial['lang'], trial['chntype'],
                          trial['answer'], ' '.join(map(lambda x: '%.15E' % x, trial['scores']))))
    lines.sort()
    return lines

def main():
    fp = open('scores.txt','w')
    fp.writelines(format(merge(systems06)))
    fp.close()

    fp = open('scores08.txt','w')
    fp.writelines(format(merge(systems08)))
    fp.close()

if __name__ == '__main__':
    main()
