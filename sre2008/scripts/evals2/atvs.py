import re

__all__ = [
    'read_atvs_models',
    'read_atvs_trials'
    ]

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
            sph, channel = trn.split(':')
            sph = sph.split('.')[0]
            channel = channel.lower()
            assert channel in ('a', 'b')
            trainkey = sph, channel
            assert trainkey not in model['train']
            model['train'].add(trainkey)
        model['trials'] = {}

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
