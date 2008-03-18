from xml.etree.cElementTree import XMLTreeBuilder
import gzip
import sys

def main():
    if len(sys.argv) != 4:
        print >>sys.stderr, 'usage: %s xml traincond testcond' % sys.argv[0]
        sys.exit(1)

    xml, traincond, testcond = sys.argv[1:]
    if xml.lower().endswith('.gz'):
        fp = gzip.open(xml, 'r')
    else:
        fp = open(xml, 'r')
    builder = XMLTreeBuilder()
    builder.feed(fp.read())
    fp.close()
    tree = builder.close()

    lines = []
    for model in tree:
        id = model.get('id')
        gender = model.get('gender')
        assert gender in ('m', 'f')

        train = model[0]
        if train.get('condition') != traincond:
            continue
        trainline = []
        for seg in train:
            name = seg.get('name')
            channel = seg.get('channel')
            trainline.append(':'.join([name, channel]))
        trainline.sort()
        trainline = ','.join(trainline)

        trialline = []
        trials = model[1]
        for trial in trials:
            assert trial.get('gender') == gender
            if trial.get('condition') != testcond:
                continue
            name = trial.get('name')
            channel = trial.get('channel')
            trialtype = trial.get('type')
            trialline.append(':'.join([name,channel,trialtype]))
        trialline.sort()
        trialline = ','.join(trialline)

        line = ' '.join([id, gender, trainline, trialline])
        lines.append(line)
    lines.sort()
    print '\n'.join(lines)

if __name__ == '__main__':
    main()
