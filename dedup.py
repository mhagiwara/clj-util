#! /usr/bin/python

import sys, codecs, re, math
from collections import deque, defaultdict

# enable utf-8 input/output
sys.stdin = codecs.getreader('utf_8')(sys.stdin)
sys.stdout = codecs.getwriter('utf_8')(sys.stdout)

def hamming_dist(x,y):
    count,z = 0,x^y
    while z:
        count += 1
        z &= z-1
    return count

def cosine_dist(x, y):
    inner_prod = 0
    for k in x.iterkeys():
        if k in y:
            inner_prod += x[k] * y[k]
    lenx = math.sqrt(sum(v * v for v in x.itervalues()))
    leny = math.sqrt(sum(v * v for v in y.itervalues()))
    return inner_prod / (lenx * leny)

def line2bow(line):
    bow = defaultdict(int)

    cp = "^"
    for c0 in line:
        bow[c0] += 1
        bow[cp+c0] += 1
        cp = c0
    bow[cp+"$"] = 1
    return bow


HASH_SIZE = 128
BUF_SIZE = 20

buf = deque(maxlen=BUF_SIZE)

for line in sys.stdin:

    line = line.strip()

    bow = line2bow(line)

    h = 0
    for g in bow.iterkeys():
        h |= (1 << (hash(g) % HASH_SIZE))

    dup_detected = False
    for x in buf:
        if hamming_dist(x[0], h) < HASH_SIZE * 0.15:
            if cosine_dist(bow, x[2]) > 0.9:
                dup_detected = True
                break

    if dup_detected:
        print "*\t",
    else:
        print " \t",
    print line
    buf.append((h, line, bow))
