from xml.etree.cElementTree import XMLTreeBuilder
import gzip
import sys

def main():
    if len(sys.argv) != 2:
        print >>sys.stderr, 'usage: %s xml' % sys.argv[0]
        sys.exit(1)

    xml = sys.argv[1]
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
        if train.get('condition') != '1conv4w': continue

        trialline = []
        trials = model[1]
        for trial in trials:
            assert trial.get('gender') == gender
            if trial.get('condition') != '1conv4w': continue
            name = trial.get('name')
            channel = trial.get('channel')
            channel = channel.upper()
            assert channel in ('A', 'B')
            trialtype = trial.get('type')
            line = '%s %s%s' % (id, name, channel)
            lines.append(line)
    lines.sort()
    print '\n'.join(lines)

if __name__ == '__main__':
    main()
