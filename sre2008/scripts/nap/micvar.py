import re
valid2005 = set([x.strip() for x in open('valid2005.txt').readlines()])
lines = open('2005-auxmic-variation.table').readlines()
for line in lines:
    parts = re.split('\\s+', line.strip())
    validparts = set()
    for part in parts[2:]:
        if part in valid2005: validparts.add(part)
    if len(validparts) < 3: continue
    validparts = ['%s:a' % x for x in validparts]
    print 'x x x %s' % ','.join(validparts)
