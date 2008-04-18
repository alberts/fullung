import re
import sys

def main():
    models = {}
    lines = open(sys.argv[1]).readlines()
    for line in lines:
        line = line.strip()
        parts = re.split('\\s+', line)
        model, segment, channel, label = parts
        if not models.has_key(model):
            models[model] = []
        models[model].append((label, segment, channel))
    for model, trials in models.iteritems():
        nontargetLimit = 0
        for label, segment, channel in trials:
            if label == 'target':
                nontargetLimit += 1
        nontargetLimit = min([nontargetLimit, 10])
        nontargetCount = 0
        for label, segment, channel in trials:
            if label == 'nontarget':
                if nontargetCount >= nontargetLimit:
                    continue
                nontargetCount += 1
            print '%s %s %s %s' % (model, segment, channel, label)

if __name__ == '__main__':
    main()
