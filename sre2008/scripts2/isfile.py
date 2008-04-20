import sys
import os.path
for line in open(sys.argv[1]).readlines():
    line = line.strip()
    if not os.path.isfile(line): print line
