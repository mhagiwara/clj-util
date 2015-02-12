#! /usr/bin/python

import sys

files = []
for fn in sys.argv[1:]:
    if fn == "-":
        files.append( None )
    else:
        files.append( open(fn) )
    
while True:
    first = True
    b = False
    for f in files:
        if f:
            line = f.readline()
            if not line:
                b = True
                break
            if not first: sys.stdout.write('\t')
            sys.stdout.write( line.rstrip('\r\n') )
        else:
            if not first: sys.stdout.write('\t')
        first = False
            
    print ''
    if b: break
