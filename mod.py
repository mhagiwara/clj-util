#! /usr/bin/python

import sys

def optparse(args):
    dict = {}
    prev = None
    for arg in args[1:]:
        if arg[0:1] == "-":
            if prev:
                dict[prev] = True
            prev = arg
        else:
            if prev:
                dict[prev] = arg
                prev = None
            else:
                # error
                pass
    if prev:
        dict[prev] = True

    return dict

opts = optparse( sys.argv )

if '--base' in opts:
    l = int( opts['--base'] )
else:
    l = 0

mod = int( opts['--mod'] )
n   = int( opts['-n'] )

for line in sys.stdin:
    if l % mod == n:
        sys.stdout.write( line )
    l += 1
