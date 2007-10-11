from StringIO import StringIO
import glob
import os.path
import re
import sys

TEST_SPLITS = 10

BACKEND_SPLITS = 10

LANGUAGES = set([
        'bengali',
        'thai',
        'hindustani',
        'spanish',
        'english',
        'farsi',
        'korean',
        'chinese',
        'arabic',
        'japanese',
        'tamil',
        'vietnamese',
        'german',
        'french',
        'russian',
        'indonesian',
        'bportuguese',
        'italian',
        'czech',
        'swedish',
        'hungarian',
        'swahili',
        'polish'
        ])

# languages to use when training frontend
FRONTEND_LANGUAGES = set([
        'arabic',
        'bengali',
        'chinese',
        'english',
        'hindustani',
        'spanish',
        'farsi',
        'german',
        'japanese',
        'korean',
        'russian',
        'tamil',
        'thai',
        'vietnamese',
        ])

LANGUAGE_CODES = {
    'hi' : 'hindustani',
    'sp' : 'spanish',
    'en' : 'english',
    'fa' : 'farsi',
    'ko' : 'korean',
    'ma' : 'chinese',
    'ar' : 'arabic',
    'ja' : 'japanese',
    'ta' : 'tamil',
    'vi' : 'vietnamese',
    'ge' : 'german',
    'fr' : 'french',
    'ru' : 'russian'
    }

SRE_LANGUAGE_CODES = {
    'E' : 'english',
    'M' : 'chinese',
    'S' : 'spanish',
    'A' : 'arabic',
    'R' : 'russian',
    'eng' : 'english',
    'spa' : 'spanish',
    'rus' : 'russian',
    'chn' : 'chinese',
    'ara' : 'arabic',
    'ben' : 'bengali',
    'kor' : 'korean',
    'yuh' : 'chinese',
    'urd' : 'hindustani',
    'hin' : 'hindustani',
    'tha' : 'thai',
    'far' : 'farsi',
    'man' : 'chinese'
    }

# identified as invalid by BUT cz phoneme recognizer
INVALID_SEGMENTS = set([
    ('callfriend', '4379'),
    ('lid96e1', '000f'),
    ('lid96e1', '0140'),
    ('lid96e1', '016l'),
    ('lid96e1', '01ie'),
    ('lid96e1', '02yj'),
    ('lid96e1', '030j'),
    ('lid96e1', '0069'),
    ('lid96e1', '006d'),
    ('lid96e1', '0094'),
    ('lid96e1', '00ja'),
    ('lid96e1', '00ua'),
    ('lid96e1', '00xn'),
    ('lid96e1', '0150'),
    ('lid96e1', '01hf'),
    ('lid96e1', '025q'),
    ('lid96e1', '02ar'),
    ('lid96e1', '02t9'),
    ('lid96e1', '02tw'),
    ('lid96e1', '02y3'),
    ('lid96e1', '030p'),
    ('lid96e1', '037w'),
    ('lid96e1', '0296'),
    ('lid96e1', '02gx'),
    ('lid03e1', 'lid00511'),
    ('lid05e1', 'lid07506'),
    ('lid05e1', 'lid08023'),
    ('lid05e1', 'lid08712'),
    ('lid05e1', 'lid08944'),
    ('lid05e1', 'lid09552'),
    ('lid05e1', 'lid09680'),
    ('lid05e1', 'lid09825'),
    ('lid05e1', 'lid10552'),
    ('lid05e1', 'lid10910'),
    ])

# identified as invalid by folks on the LRE2007 group
INVALID_FILES = set([
        ('fisher', '20040430_224303_A000401_B000402', '20040430_224303_A000401_B000402.sph.1.1.3s.sph'),
        ('fisher', '20040502_170223_A000368_B000367', '20040502_170223_A000368_B000367.sph.1.1.3s.sph'),
        ('callhome', '0855', 'sp_0855.sph.1.1.3s.sph'),
        ('lid96d1', '00ja', '00ja.sph'),
        ('lid96d1', '02ar', '02ar.sph'),
        ('lid96d1', '00ua', '00ua.sph'),
        ('lid96d1', '0069', '0069.sph'),
        ('lid96e1', '037w', '037w.sph'),
        ('lid96e1', '030p', '030p.sph'),
        ('cslu22', 'JA-163', 'JA-163.story.1.10s.sph'),
        ('cslu22', 'JA-163', 'JA-163.story.1.30s.sph'),
        ('cslu22', 'JA-163', 'JA-163.story.1.3s.sph'),
        ('cslu22', 'RU-180', 'RU-180.story.1.10s.sph'),
        ('cslu22', 'RU-180', 'RU-180.story.1.30s.sph'),
        ('cslu22', 'RU-180', 'RU-180.story.1.3s.sph'),
        ('fisher', '20040519_163338_A001145_B901146', '20040519_163338_A001145_B901146.sph.1.1.10s.sph'),
        ('fisher', '20040601_202635_A001212_B001213', '20040601_202635_A001212_B001213.sph.0.1.10s.sph'),
        ('fisher', 'fsa_24882', 'fsa_24882.sph.0.1.10s.sph'),
        ('MIXER', 'tfcq', 'tfcq.sph.1.10s.sph'),
        ('fisher', '20040424_003157_A000220_B000221', '20040424_003157_A000220_B000221.sph.0.1.3s.sph'),
        ('fisher', '20040503_235349_A000341_B000342', '20040503_235349_A000341_B000342.sph.1.1.3s.sph'),
        ('fisher', '20040518_131850_A001060_B001059', '20040518_131850_A001060_B001059.sph.1.1.3s.sph'),
        ('fisher', '20040519_163338_A001145_B901146', '20040519_163338_A001145_B901146.sph.1.1.3s.sph'),
        ('fisher', '20040601_174105_A001125_B001124', '20040601_174105_A001125_B001124.sph.1.1.3s.sph'),
        ('fisher', '20040601_202635_A001212_B001213', '20040601_202635_A001212_B001213.sph.0.1.3s.sph'),
        ('fisher', 'fsa_21901', 'fsa_21901.sph.0.1.3s.sph'),
        ('fisher', 'fsa_24582', 'fsa_24582.sph.0.1.3s.sph'),
        ('fisher', 'fsa_24882', 'fsa_24882.sph.0.1.3s.sph'),
        ('fisher', 'fsh_115674', 'fsh_115674.sph.0.1.3s.sph'),
        ('fisher', 'fsh_22152', 'fsh_22152.sph.0.1.3s.sph'),
        ('fisher', 'fsh_74602', 'fsh_74602.sph.1.1.3s.sph'),
        ('callhome', '6162', 'ge_6162.sph.1.1.3s.sph'),
        ('callhome', '1032', 'ja_1032.sph.0.1.3s.sph'),
        ('callhome', '0848', 'ma_0848.sph.1.1.3s.sph'),
        ('callhome', '1918', 'sp_1918.sph.1.1.3s.sph'),
        ('MIXER', 'tahk', 'tahk.sph.1.3s.sph'),
        ('MIXER', 'tazw', 'tazw.sph.1.3s.sph'),
        ('MIXER', 'tcbi', 'tcbi.sph.1.3s.sph'),
        ('MIXER', 'tfcq', 'tfcq.sph.1.3s.sph'),
        ('MIXER', '15517', 'xaxk.sph.1.3s.sph'),
        ('MIXER', '15139', 'xbvx.sph.1.3s.sph'),
        ('MIXER', '16525', 'xdqd.sph.1.3s.sph'),
        ('lid96e1', '000f', '000f.sph'),
        ('lid96e1', '0140', '0140.sph'),
        ('lid96e1', '016l', '016l.sph'),
        ('lid96e1', '01ie', '01ie.sph'),
        ('lid96e1', '02yj', '02yj.sph'),
        ('lid96e1', '030j', '030j.sph'),
        ('lid96e1', '0069', '0069.sph'),
        ('lid96e1', '006d', '006d.sph'),
        ('lid96e1', '0094', '0094.sph'),
        ('lid96e1', '00ja', '00ja.sph'),
        ('lid96e1', '00ua', '00ua.sph'),
        ('lid96e1', '00xn', '00xn.sph'),
        ('lid96e1', '0150', '0150.sph'),
        ('lid96e1', '01hf', '01hf.sph'),
        ('lid96e1', '025q', '025q.sph'),
        ('lid96e1', '02ar', '02ar.sph'),
        ('lid96e1', '02t9', '02t9.sph'),
        ('lid96e1', '02tw', '02tw.sph'),
        ('lid96e1', '02y3', '02y3.sph'),
        ('lid96e1', '030p', '030p.sph'),
        ('lid96e1', '037w', '037w.sph'),
        ('lid96e1', '0296', '0296.sph'),
        ('lid96e1', '02gx', '02gx.sph'),
        ('lid03e1', 'lid00511', 'lid00511.sph'),
        ('lid05e1', 'lid07506', 'lid07506.sph'),
        ('lid05e1', 'lid08023', 'lid08023.sph'),
        ('lid05e1', 'lid08712', 'lid08712.sph'),
        ('lid05e1', 'lid08944', 'lid08944.sph'),
        ('lid05e1', 'lid09552', 'lid09552.sph'),
        ('lid05e1', 'lid09680', 'lid09680.sph'),
        ('lid05e1', 'lid09825', 'lid09825.sph'),
        ('lid05e1', 'lid10552', 'lid10552.sph'),
        ('lid05e1', 'lid10910', 'lid10910.sph'),
        ('cslu22', 'AR-612', 'AR-612.story.1.3s.sph'),
        ('fisher', '20040602_114846_A001253_B001252', '20040602_114846_A001253_B001252.sph.0.1.3s.sph'),
        ('fisher', 'fsh_115674', 'fsh_115674.sph.0.1.10s.sph'),
        ('lre07_tr', 'rus_008_b', 'lre07_tr_rus_008_b.2.3s.sph'),
        ('lre07_tr', 'rus_013_b', 'lre07_tr_rus_013_b.2.3s.sph'),
        ('lre07_tr', 'wuu_013_b', 'lre07_tr_wuu_013_b.2.3s.sph'),
        ('MIXER', 'kckx', 'kckx.sph.1.1.3s.sph'),
        ('MIXER', '15517', 'xaxk.sph.1.10s.sph'),
        ])

def read_lid03e1_key(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        line = line.strip()
        id, lang, convid, ign, duration, ign, gender, ign, corpus = re.split('\s+', line)
        duration = int(duration)
        assert duration in (3, 10, 30)
        id = id.lower()
        lang = lang.lower()
        if lang == 'hindi':
            lang = 'hindustani'
            dialect = 'hindi'
        elif lang == 'mandarin':
            lang = 'chinese'
            dialect = 'mandarin'
        if not lang in LANGUAGES:
            raise ValueError, 'invalid language: %s' % lang
        convid = convid.lower()
        if corpus == 'CFRND':
            convid = convid.split('_', 1)[1]
            assert len(convid) == 4
            corpus = 'callfriend'
        elif corpus == 'CHOME':
            corpus = 'callhome'
            lang, convid = convid.split('_', 1)
            assert len(convid) == 4
            lang = LANGUAGE_CODES[lang]
        elif corpus == 'SWB1':
            pass
        elif corpus == 'SWBCELL':
            pass
        else:
            raise ValueError, 'invalid corpus: %s' % corpus
        if segments.has_key(id):
            raise ValueError, 'duplicate id: %d' % id
        segments['lid03e1',id] = {
            'language' : lang,
            'overlap' : set([(corpus, convid)]),
            'duration' : duration,
            'files' : set(['%s.sph' % id])
            }
    return segments

def read_lid05d1_key(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        line = line.strip()
        filename, lang, convid, ign, ign = re.split('\s+', line)
        if lang == 'IE':
            lang = 'english'
            dialect = 'indian'
        else:
            raise ValueError, 'invalid code: %s' % lang
        ign, duration, id = filename.split('/', 2)
        id = id.split('.', 1)[0].lower()
        assert id.find('lid') == 0
        duration = int(duration)
        assert duration in (3, 10, 30)
        if not lang in LANGUAGES:
            raise ValueError, 'invalid language: %s' % lang
        corpus = 'OHSU'
        if segments.has_key(id):
            raise ValueError, 'duplicate id: %d' % id
        segments['lid05d1',id] = {
            'language' : lang,
            'overlap' : set([(corpus, convid)]),
            'duration' : duration,
            'files' : set(['%s.sph' % id])
            }
    return segments

def read_lid05e1_key(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        line = line.strip()
        id, lang, dialect, convid, channel, cut, duration, corpus, gender, location, ign = re.split('\s+', line)
        id = id.lower()
        lang = lang.lower()
        duration = int(duration)
        assert duration in (3, 10, 30)
        if lang == 'hindi':
            lang = 'hindustani'
            dialect = 'hindi'
        elif lang == 'mandarin':
            lang = 'chinese'
            dialect = 'mandarin'
        if not lang in LANGUAGES:
            raise ValueError, 'invalid language: %s' % lang
        if corpus == 'CF':
            convid = convid.split('_', 1)[1]
            corpus = 'callfriend'
        elif corpus == 'OHSU':
            pass
        elif corpus == 'MIXER':
            convid = convid.split('_', 1)[1]
        else:
            raise ValueError, 'invalid corpus: %s' % corpus
        if segments.has_key(id):
            raise ValueError, 'duplicate id: %d' % id
        segments['lid05e1',id] = {
            'language' : lang,
            'overlap' : set([(corpus, convid)]),
            'duration' : duration,
            'files' : set(['%s.sph' % id])
            }
    return segments

def read_lid96_key(fp, corpus):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        line = line.strip()
        parts = re.split('\s+', line)
        id, lang, duration = parts[0:3]
        id = id.lower()
        assert len(id) == 4
        lang = lang.split('.', 1)[0].lower()
        if lang == 'hindi':
            lang = 'hindustani'
            dialect = 'hindi'
        elif lang == 'mandarin':
            lang = 'chinese'
            dialect = 'mandarin'
        if not lang in LANGUAGES:
            raise ValueError, 'invalid language: %s' % lang
        duration = int(duration)
        assert duration in (3, 10, 30)
        if segments.has_key(id):
            raise ValueError, 'duplicate id: %d' % id
        if len(parts) == 4:
            overlap = set([tuple(x.split(',')) for x in parts[3].split('|')])
        else:
            overlap = set()
        segments[corpus,id] = {
            'language' : lang,
            'duration' : duration,
            'overlap' : overlap,
            'files' : set(['%s.sph' % id])
            }
    return segments

def read_lid96d1_key(fp):
    return read_lid96_key(fp, 'lid96d1')

def read_lid96e1_key(fp):
    return read_lid96_key(fp, 'lid96e1')

def read_callfriend_index(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        line = line.strip()
        parts = re.split('\s+', line, 1)
        langid = parts[0].lower()
        lang, id = langid.split('_', 1)
        lang = LANGUAGE_CODES[lang]
        info = {}
        if len(parts) > 1:
            parts = parts[1].split('|')
            for part in parts:
                if not part.find('=') >= 0: continue
                key, value = part.split('=', 1)
                info[key] = value
        if segments.has_key(id):
            raise ValueError, 'duplicate id: %d' % id
        segments['callfriend',id] = {
            'language' : lang,
            'overlap' : set(),
            'files' : set(['%s.sph' % langid]),
            }
    return segments

def read_callhome_index(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        parts = line.strip().split('/')
        lang = parts[0].lower()
        id = parts[-1].lower().split('.')[0].split('_')[1]
        if lang == 'mandarin':
            lang = 'chinese'
        segments['callhome',id] = {
            'language' : lang,
            'overlap' : set(),
            'files' : set([parts[-1].lower()])
            }
    return segments

def read_ohsu(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        id = line.strip()
        if id[:2] == 'AE':
            lang = 'english'
            dialect = 'american'
        elif id[:2] == 'GE':
            lang = 'german'
        elif id[:2] == 'HI':
            lang = 'hindustani'
            dialect = 'hindi'
        elif id[:2] == 'IE':
            lang = 'english'
            dialect = 'indian'
        elif id[:2] == 'JA':
            lang = 'japanese'
        elif id[:2] == 'KO':
            lang = 'korean'
        elif id[:2] == 'MM':
            lang = 'chinese'
            dialect = 'mandarin'
        elif id[:2] == 'MS':
            lang = 'spanish'
        elif id[:2] == 'TA':
            lang = 'tamil'
        elif id[:2] == 'TM':
            lang = 'chinese'
            dialect = 'taiwan'
        else:
            raise ValueError, 'invalid id: %s' % id
        files = set(['%s-A-con.nis' % id, '%s-B-con.nis' % id])
        segments['OHSU',id] = {
            'language' : lang,
            'overlap' : set(),
            'files' : files
            }
    return segments

def read_lre07_tr(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        langcode, id, channel, gender, age, line = line.strip().split(',')
        if langcode == 'wuu':
            lang = 'chinese'
            dialect = 'wu'
        elif langcode == 'arb':
            lang = 'arabic'
        elif langcode == 'ben':
            lang = 'bengali'
        elif langcode == 'cfr':
            lang = 'chinese'
            dialect = 'min'
        elif langcode == 'rus':
            lang = 'russian'
        elif langcode == 'tha':
            lang = 'thai'
        elif langcode == 'urd':
            lang = 'hindustani'
            dialect = 'urdu'
        elif langcode == 'yuh':
            lang = 'chinese'
            dialect = 'cantonese'
        else:
            raise ValueError, 'invalid language/dialect: %s' % langcode
        key = 'lre07_tr', '%s_%s_%s' % (langcode, id, channel)
        segments[key] = {
            'language' : lang,
            'overlap' : set(),
            'files' : set()
            }
    return segments

def read_sre04_key(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    key = {}
    for line in lines:
        segid, langcode, id, ign = re.split('\s+', line.strip(), 3)
        lang = SRE_LANGUAGE_CODES[langcode]
        key[segid] = {'language' : lang, 'id' : id}
    return key

def read_sre04_segments(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        parts = re.split('\s+', line.strip())
        segid, langcode, id = parts[0:3]
        duration = parts[4]
        # exclude summed conversations
        if duration not in ('10, 30, 1s'): continue
        lang = SRE_LANGUAGE_CODES[langcode]
        key = 'MIXER', id
        if duration == '1s':
            files = set(['sre04.1side.%s.sph' % segid])
        else:
            duration = int(duration)
            files = set(['sre04.%ds.%s.sph' % (duration, segid)])
        if key not in segments:
            segments[key] = {
                'language' : lang,
                'overlap' : set(),
                'files' : files
                }
        else:
            assert segments[key]['language'] == lang
            segments[key]['files'] |= files
    return segments

def read_sre05_key(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    key = {}
    for line in lines:
        parts = re.split('\s+', line.strip())
        segid = parts[1].lower()
        if segid == 'xxxx': continue
        langcode = parts[2].upper()
        lang = SRE_LANGUAGE_CODES[langcode.upper()]
        id = parts[3]
        values = {'language' : lang, 'id' : id}
        if key.has_key(segid):
            assert key[segid] == values
        else:
            key[segid] = values
    return key

def read_sre05_segments(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        parts = re.split('\s+', line.strip())
        segid, channel = parts[1].lower(), parts[4].upper()
        if segid == 'xxxx' or channel not in ('A', 'B'):
            continue
        langcode = parts[2].upper()
        lang = SRE_LANGUAGE_CODES[langcode.upper()]
        id = parts[3]
        key = 'MIXER', id
        if key not in segments:
            segments[key] = {
                'language' : lang,
                'overlap' : set(),
                'files' : set(['sre05.1side.%s.sph' % segid])
                }
        else:
            assert segments[key]['language'] == lang
            segments[key]['files'].add('sre05.1side.%s.sph' % segid)
    return segments

def read_sre06_key(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    key = {}
    for line in lines:
        parts = re.split('\s+', line.strip())
        segid = parts[0]
        id = parts[2]
        langcode = parts[7]
        key[segid] = {'id' : id}
        # only set language if we have a valid language code and pick
        # up any segments with a missing language later
        if langcode not in ('-', 'unk'):
            lang = SRE_LANGUAGE_CODES[langcode]
            key[segid]['language'] = lang
    return key

def read_sre06_segments(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        parts = re.split('\s+', line.strip())
        langcode = parts[7]
        if langcode in ('-', 'unk'): continue
        conv = parts[1].lower()
        if conv not in ('1conv4w', '10sec4w'): continue
        channel = parts[6].lower()
        if channel not in ('a', 'b'): continue
        segid, id = parts[0], parts[2]
        lang = SRE_LANGUAGE_CODES[langcode]
        if conv == '1conv4w':
            files = set(['sre06.1side.%s.sph' % segid])
        elif conv == '10sec4w':
            files = set(['sre06.10s.%s.sph' % segid])
        else:
            assert False
        key = 'MIXER', id
        if key not in segments:
            segments[key] = {
                'language' : lang,
                'overlap' : set(),
                'files' : files
                }
        else:
            assert segments[key]['language'] == lang
            segments[key]['files'] |= files
    return segments

def read_mit_key(fp):
    sre04key = read_sre04_key('sre04_key-v2.txt')
    sre05key = read_sre05_key('sre05-key-v7b.txt')
    sre06key = read_sre06_key('sre06_test_seg_key_v9.txt')
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        id, lang, dialect, origcorpus = re.split('\s+', line.strip())
        filename = id
        if id.find('.story.') >= 0:
            id = id.split('.')[0].upper()
            corpus = 'cslu22'
        elif origcorpus == 'fisher':
            id = id.split('.')[0]
            corpus = origcorpus
            key = corpus, id
        elif origcorpus == 'callhome':
            corpus = origcorpus
            id = id.split('.')[0].split('_')[1]
        elif id.startswith('lid05e1_'):
            id = id.split('_')[1].split('.')[0]
            corpus = 'lid05e1'
            filename = None
        elif id.startswith('lid'):
            id = id.split('.')[0]
            corpus = 'lid03e1'
            filename = None
        elif id.startswith('lre07_tr_'):
            id = id.split('_', 2)[-1].split('.')[0]
            corpus = 'lre07_tr'
        elif id.find('0') == 0 and len(id) == 8:
            id = id.split('.')[0]
            corpus = 'lid96e1'
            filename = None
        elif origcorpus == 'mixer':
            sre_id = id.split('.')[0]
            if sre04key.has_key(sre_id):
                id = sre04key[sre_id]['id']
                assert lang == sre04key[sre_id]['language']
            elif sre05key.has_key(sre_id):
                id = sre05key[sre_id]['id']
                assert lang == sre05key[sre_id]['language']
            elif sre06key.has_key(sre_id):
                id = sre06key[sre_id]['id']
                assert lang == sre06key[sre_id]['language']
            elif sre_id.find('t') == 0 or sre_id.find('j') == 0 or sre_id.find('k') == 0:
                # don't have source info for SRE200[46] training data yet
                id = sre_id.split('.')[0]
            else:
                assert False
            corpus = 'MIXER'
        else:
            assert False
        key = corpus, id
        if not segments.has_key(key):
            segments[key] = {
                'language' : lang, 'overlap' : set(), 'files' : set()
                }
        # check if file is something that doesn't exist already
        if filename is not None:
            segments[key]['files'].add(filename)
    return segments

def read_cslu22_index(fp):
    if isinstance(fp, basestring):
        fp = open(fp, 'r')
    lines = fp.readlines()
    fp.close()
    segments = {}
    for line in lines:
        if not line.find('.story.') >= 0: continue
        lang, stuff, id = line.strip().split('/')
        if lang == 'hindi':
            lang = 'hindustani'
        elif lang in ('mandarin', 'cantonese'):
            lang = 'chinese'
        id = id.split('.')[0].upper()
        key = 'cslu22', id
        segments[key] = {
            'language' : lang,
            'overlap' : set(),
            'files' : set()
            }
    return segments

def merge_segments(segs1, segs2):
    for k1, v1 in segs1.iteritems():
        if segs2.has_key(k1):
            v2 = segs2[k1]
            assert v1['language'] == v2['language']
            v1['files'] |= v2['files']
    for k2, v2 in segs2.iteritems():
        if segs1.has_key(k2):
            v1 = segs1[k2]
            assert v1['language'] == v2['language']
            v1['files'] |= v2['files']
        else:
            segs1[k2] = v2

def read_keys():
    segments = {}
    segments.update(read_cslu22_index('CSLU22Lang.txt'))
    segments.update(read_callhome_index('callhome.txt'))
    segments.update(read_callfriend_index('cf_callinfo.txt'))
    segments.update(read_lid96d1_key('lid96d1key.txt'))
    segments.update(read_lid96e1_key('lid96e1key.txt'))
    segments.update(read_lid03e1_key('lid03e1key.txt'))
    segments.update(read_lid05d1_key('lid05d1key.txt'))
    segments.update(read_lid05e1_key('lid05e1key.txt'))
    segments.update(read_ohsu('ohsu.txt'))
    segments.update(read_lre07_tr('lre07_tr_call_side_info.csv'))
    segments.update(read_sre04_segments('sre04_key-v2.txt'))
    segments.update(read_sre05_segments('sre05-key-v7b.txt'))
    segments.update(read_sre06_segments('sre06_test_seg_key_v9.txt'))
    merge_segments(segments, read_mit_key('key.lid07d1'))

    # create implied segments
    newsegments = {}
    for k1, v1 in segments.iteritems():
        for k2 in v1['overlap']:
            if not segments.has_key(k2):
                newsegments[k2] = {
                    'language' : v1['language'],
                    'overlap' : set(),
                    'files' : set()
                    }
    segments.update(newsegments)

    # update the overlap set of every segment contain every other
    # segment that it overlaps with
    changed = True
    while changed:
        changed = False
        for k1, v1 in segments.iteritems():
            for k2 in v1['overlap']:
                v2 = segments[k2]
                if k1 not in v2['overlap']:
                    v2['overlap'].add(k1)
                    changed = True
                newoverlap = v1['overlap'] - set([k2])
                if newoverlap - v2['overlap'] != set():
                    v2['overlap'] |= newoverlap
                    changed = True
    return segments

def check_segments(segments):
    for k, v in segments.iteritems():
        assert v.has_key('language')
        assert v.has_key('overlap')
        assert v.has_key('files')
        assert v['language'] in LANGUAGES
        if v.has_key('duration'):
            assert v['duration'] in (3, 10, 30)
        if v.has_key('duration'):
            assert len(v['files']) == 1
        for k2 in v['overlap']:
            assert segments[k2]['overlap'] - v['overlap'] == set([k])

def print_segments(segments):
    segmentkeys = segments.keys()
    segmentkeys.sort()
    for k in segmentkeys:
        v = segments[k]
        overlap = list(v['overlap'])
        overlap.sort()
        print k, v['language'], overlap

def write_split(fp, keys, files):
    lines = []
    for k in keys:
        v = files[id]
        fmt = {
            'key' : ' '.join(k),
            'lang' : v['language'],
            'dur' : v['duration']
            }
        lines.append('%(key)s %(lang)s %(dur)d\n' % fmt)
    lines.sort()
    if isinstance(fp, basestring):
        fp = open(fp, 'w')
    fp.writelines(lines)
    fp.close()

def segments_to_files(segments):
    files = {}
    for k, v in segments.iteritems():
        assert v.has_key('files')
        if len(v['files']) == 0: continue
        for f in v['files']:
            if v.has_key('duration'):
                duration = v['duration']
            else:
                if f.find('.3s.') >= 0:
                    duration = 3
                elif f.find('.10s.') >= 0:
                    duration = 10
                elif f.find('.30s.') >= 0:
                    duration = 30
                else:
                    duration = -1
            id = k + (f,)
            files[id] = {
                'language' : v['language'],
                'overlap' : set(),
                'duration' : duration
                }
            # add other files from this segment to the overlap of this
            # file from the segment
            for otherf in v['files']:
                # don't mark this file as overlapping with itself
                if f == otherf: continue
                otherid = k + (otherf,)
                files[id]['overlap'].add(otherid)
            # add files from other overlapping segments to the set of
            # overlap files for this file
            for otherk in v['overlap']:
                otherv = segments[otherk]
                assert otherv.has_key('files')
                for otherf in otherv['files']:
                    otherid = otherk + (otherf,)
                    files[id]['overlap'].add(otherid)

    return files

def check_splits(files, testsplits, besplits, fesplits):
    # TODO get rid of duplicated code in here
    for i, vi in testsplits.iteritems():
        for f in vi:
            overlap = files[f]['overlap']
            for j, vj in besplits.iteritems():
                if j[0] != i: continue
                assert f not in vj
                assert len(vj & overlap) == 0
            for j, vj in fesplits.iteritems():
                if j[0] != i: continue
                assert f not in vj
                assert len(vj & overlap) == 0
    for i, vi in besplits.iteritems():
        testsplit = testsplits[i[0]]
        for f in vi:
            overlap = files[f]['overlap']
            assert f not in testsplit
            assert len(testsplit & overlap) == 0
            for j, vj in fesplits.iteritems():
                if i != j: continue
                assert f not in vj
                assert len(vj & overlap) == 0
    for i, vi in fesplits.iteritems():
        testsplit = testsplits[i[0]]
        for f in vi:
            overlap = files[f]['overlap']
            assert f not in testsplit
            assert len(testsplit & overlap) == 0
            for j, vj in besplits.iteritems():
                if i != j: continue
                assert f not in vj
                assert len(vj & overlap) == 0

def write_key(fp, files):
    if isinstance(fp, basestring):
        fp = open(fp, 'w')
    keylines = []
    for k, v in files.iteritems():
        overlap = ['|'.join(x) for x in v['overlap']]
        overlap.sort()
        fmt = ' '.join(k), v['language'], v['duration'], ','.join(overlap)
        keylines.append('%s %s %d %s\n' % fmt)
    keylines.sort()
    fp.writelines(keylines)
    fp.close()

def debug(segments, files):
    print segments[('lid03e1', 'lid00381')]
    print '=' * 80
    print segments[('callhome', '1889')]
    print '=' * 80
    print files[('lid03e1', 'lid00381', 'lid00381.sph')]
    print '=' * 80
    print files[('callhome', '1889', 'ja_1889.sph.1.1.10s.sph')]
    print '=' * 80
    print files[('callhome', '1889', 'ja_1889.sph.1.1.30s.sph')]
    print '=' * 80

def files_to_groups(files):
    validfiles = set([x.strip() for x in open('validfiles.txt').readlines()])
    validcf = set([x.strip() for x in open('validcf.txt').readlines()])
    longfiles = set([x.strip() for x in open('longfiles.txt').readlines()])
    for badfile in INVALID_FILES: assert badfile in files
    groups = set()
    for k, v in files.iteritems():
        o = set((k,))
        o |= v['overlap']
        o -= INVALID_FILES
        def validfiles_filter(x):
            duration = files[x]['duration']
            if duration == -1 or x[2].find('sre') == 0:
                return True
            filekey = '%d/%s/%s' % (duration, x[0], x[2])
            return filekey in validfiles
        def valid_callfriend_filter(x):
            if x[0] != 'callfriend': return True
            return x[1] in validcf
        def longfiles_filter(x):
            duration = files[x]['duration']
            if duration == -1 or x[2].find('sre') == 0:
                return True
            filekey = '%d/%s/%s' % (duration, x[0], x[2])
            return filekey in longfiles
        o = filter(validfiles_filter,  o)
        o = filter(valid_callfriend_filter,  o)
        o = filter(longfiles_filter,  o)
        # group might become empty if it only contained invalid files,
        # so check len before adding
        if len(o) > 0:
            f = lambda x: x + (files[x]['language'],files[x]['duration'])
            groups.add(frozenset(map(f, o)))
    return groups

def print_counts(langgroups):
    langdur = {}

    for lang, v in langgroups.iteritems():
        for g in v:
            for x in g:
                assert lang == x[3]
                duration = x[4]
                key = lang, duration
                sd = langdur.setdefault(key, 0)
                langdur[key] += 1

    for lang in LANGUAGES:
        print lang,
        for dur in (3, 10, 30):
            key = lang, dur
            print langdur[key],
        print

def create_subsets(groups, n):
    langgroups = {}
    for g in groups:
        lang = tuple(g)[0][3]
        langg = langgroups.setdefault(lang, set())
        langg.add(g)

    subsets = [set() for i in xrange(n)]
    for lang, groups in langgroups.iteritems():
        langsubsets = [set() for i in xrange(len(subsets))]
        lens = [0] * len(langsubsets)
        for group in groups:
            smallest_idx = 0
            for i, subset in enumerate(langsubsets):
                if lens[i] < lens[smallest_idx]:
                    smallest_idx = i
            smallest_subset = subsets[smallest_idx]
            smallest_subset.add(group)
            lens[smallest_idx] += len(group)
        # add each language-specific subset to the global subset
        for x, y in zip(subsets, langsubsets): x |= y
    return subsets

def create_splits(groups):
    otherlang_filter = lambda x: x[3] not in FRONTEND_LANGUAGES
    frontend_filter = lambda x: x[3] in FRONTEND_LANGUAGES
    backend_filter = lambda x: x[4] in (3,10,30) and x[3] in FRONTEND_LANGUAGES and x[2].find('sre') == -1
    test_filter = lambda x: x[4] in (3,10,30) and x[3] in FRONTEND_LANGUAGES and x[2].find('sre') == -1

    test_splits, be_splits, fe_splits = {}, {}, {}
    testsubsets = create_subsets(groups, TEST_SPLITS)

    all_files = set()
    for g in groups:
        all_files |= g
    otherlang_files = frozenset(filter(otherlang_filter, all_files))

    for i, testsubset in enumerate(testsubsets):
        test_files = set()
        for x in testsubset: test_files |= x
        test_splits[i] = frozenset(filter(test_filter, test_files))
        be_groups = groups - testsubset
        besubsets = create_subsets(be_groups, BACKEND_SPLITS)
        for j, besubset in enumerate(besubsets):
            be_files = set()
            for x in besubset: be_files |= x
            be_splits[i,j] = frozenset(filter(backend_filter, be_files))
            fesubset = be_groups - besubset
            fe_files = set()
            for x in fesubset: fe_files |= x
            fesplit = frozenset(filter(frontend_filter, fe_files))
            fe_splits[i,j] = frozenset(fesplit | otherlang_files)
    return test_splits, be_splits, fe_splits

def check_splits(files, testsplits, besplits, fesplits):
    # TODO get rid of duplicated code in here
    for i, vi in testsplits.iteritems():
        for f in vi:
            f = f[0:3]
            overlap = files[f]['overlap']
            for j, vj in besplits.iteritems():
                if j[0] != i: continue
                assert f not in vj
                assert len(vj & overlap) == 0
            for j, vj in fesplits.iteritems():
                if j[0] != i: continue
                assert f not in vj
                assert len(vj & overlap) == 0
    for i, vi in besplits.iteritems():
        testsplit = testsplits[i[0]]
        for f in vi:
            f = f[0:3]
            overlap = files[f]['overlap']
            assert f not in testsplit
            assert len(testsplit & overlap) == 0
            for j, vj in fesplits.iteritems():
                if i != j: continue
                assert f not in vj
                assert len(vj & overlap) == 0
    for i, vi in fesplits.iteritems():
        testsplit = testsplits[i[0]]
        for f in vi:
            f = f[0:3]
            overlap = files[f]['overlap']
            assert f not in testsplit
            assert len(testsplit & overlap) == 0
            for j, vj in besplits.iteritems():
                if i != j: continue
                assert f not in vj
                assert len(vj & overlap) == 0

def write_split(fp, files):
    lines = []
    for f in files:
        lines.append('%s %s %s %s %d\n' % f)
    lines.sort()
    if isinstance(fp, basestring):
        fp = open(fp, 'w')
    fp.writelines(lines)
    fp.close()

def main():
    print >>sys.stderr, 'reading keys...'
    segments = read_keys()
    print >>sys.stderr, 'checking segments...'
    check_segments(segments)
    files = segments_to_files(segments)
    groups = files_to_groups(files)
    testsplits, besplits, fesplits = create_splits(groups)

    print >>sys.stderr, 'writing splits...'
    write_key(os.path.join('output', 'key.txt'), files)
    for k, v in testsplits.iteritems():
        filename = os.path.join('output', 'test_%d.txt' % k)
        write_split(filename, v)
    for k, v in besplits.iteritems():
        filename = os.path.join('output', 'backend_%d_%d.txt' % k)
        write_split(filename, v)
    for k, v in fesplits.iteritems():
        filename = os.path.join('output', 'frontend_%d_%d.txt' % k)
        write_split(filename, v)

    # XXX this check takes a long time
    print >>sys.stderr, 'checking splits...'
    check_splits(files, testsplits, besplits, fesplits)

if __name__ == '__main__':
    main()
