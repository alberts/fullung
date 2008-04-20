#!/usr/bin/env python

def read_csv(filename):
    lines = open(filename).readlines()
    evals = {}
    for line in lines:
        line = line.strip()
        parts = line.split(',')
        eval = parts[0]
        if not eval in evals:
            evals[eval] = {}
        evals[eval][parts[1]] = line
    return evals

def main():
    male = read_csv('male.csv')
    female = read_csv('female.csv')

    output = []

    for first, second in [(male, female), (female, male)]:
        validlines = []
        for evalkey, lines in first.iteritems():
            for filename, line in lines.iteritems():
                if filename in second[evalkey]:
                    continue
                validlines.append('%s\n' % line)
        output.append(validlines)

    open('male_fixed.csv', 'w').writelines(output[0])
    open('female_fixed.csv', 'w').writelines(output[1])

if __name__ == '__main__':
    main()

