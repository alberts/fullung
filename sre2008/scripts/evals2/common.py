__all__ = [
    'print_xml',
    'check_language',
    'print_models',
    'print_trials'
    ]

def print_xml(models):
    print '<?xml version="1.0" encoding="UTF-8"?>'
    print '<eval name="SRE2006">'
    modelkeys = models.keys()
    modelkeys.sort()
    for id in modelkeys:
        model = models[id]
        gender = model['gender']
        pin = model['pin']
        print '<model id="%s" gender="%s" pin="%s">' % (id, gender, pin)
        print '<train condition="%s">' % model['condition']
        for sph, channel in model['train']:
            print '<segment name="%s" channel="%s" />' % (sph, channel)
        print '</train>'
        print '<trials>'
        trialkeys = model['trials'].keys()
        trialkeys.sort()
        for trialkey in trialkeys:
            sph, channel = trialkey
            trial = model['trials'][trialkey]
            condition = trial['condition']
            trialtype = trial['type']
            gender = trial['gender']
            pin = trial['pin']
            print '<trial name="%s" channel="%s" condition="%s" type="%s" gender="%s" pin="%s" />' % \
                (sph, channel, condition, trialtype, gender, pin)
        print '</trials>'
        print '</model>'
    print '</eval>'

def print_models(models, fp):
    for modelid, model in models.iteritems():
        modelparts = [
            modelid,
            model['gender'],
            ':'.join(list(model['train'])[0]),
            model['language'],
            model['speech_type'],
            model['channel_type']
            ]
        print >>fp, ' '.join(modelparts)

def print_trials(models, fp):
    for modelid, model in models.iteritems():
        trials = model['trials']
        for trialkey, trial in trials.iteritems():
            trialparts = [
                modelid,
                trial['gender'],
                ':'.join(trialkey),
                trial['language'],
                trial['speech_type'],
                trial['channel_type']
                ]
            if trial.has_key('answer'):
                trialparts.append(trial['answer'])
            print >>fp, ' '.join(trialparts)

def check_language(lang):
    assert True
    return lang
