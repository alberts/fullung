import re

lines = open('2004_1s.txt').readlines()
output = []
for line in lines:
    parts = re.split('\\s+', line.strip())
    gender = parts[0]
    pin = parts[1]
    parts = parts[2:]
    #count = len(parts)
    parts = ','.join(map(lambda x: '%s:a' % x, parts))
    #output.append(' '.join([gender,'phn',pin,str(count),parts]))
    output.append(' '.join([gender,'phn',pin,parts]))
output.sort()
print >>open('nap2004.txt', 'w'), '\n'.join(output)
