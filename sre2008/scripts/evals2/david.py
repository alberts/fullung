import re

__all__ = [
    'read_david_models',
    'read_david_trials'
    ]

def read_david_models(filename, models):
    lines = open(filename).readlines()
    for line in lines:
        line = line.strip()
        id, train = re.split('\\s+', line)
        id = id.lower()
        assert not models.has_key(id)
        models[id] = {}
        model = models[id]
        model['train'] = set()
        train = train.split(',')
        for trn in train:
            sph, channel = trn.split('-')
            sph = sph.split('.')[0]
            channel = channel.lower()
            assert channel in ('a', 'b')
            trainkey = sph, channel
            assert trainkey not in model['train']
            model['train'].add(trainkey)
        model['trials'] = {}

def read_david_trials(filename, models):
    lines = open(filename).readlines()
    badmodels = set()
    for line in lines:
        line = line.strip()
        modelid, gender, trial = re.split('\\s+', line)
        name, channel = trial.split('-')
        assert channel in ('a', 'b')
        trialkey = name, channel
        trial = {'gender' : gender}
        if not modelid in models:
            badmodels.add(modelid)
            continue
        model = models[modelid]
        assert model['gender'] == gender, \
            'gender mismatch: %s->%s vs %s->%s' % \
            (modelid, model['gender'], ':'.join(trialkey), gender)
        model['trials'][trialkey] = trial
    for modelid in badmodels:
        print >>sys.stderr, 'found trials for bad model %s' % modelid
