#!/usr/bin/env python3
import sys
a = set([1,2,3,5,6,9,11])
k = 0
d = {i:0 for i in range(1,19)}

for i in a:
    for j in a:
        if i == j:
            continue
        k += 1
        sys.stdout.write('&{}-{}={}\qquad'.format(i,j,(i-j)%15))
        d[(i-j)%15] += 1
        if k%5 == 0:
            sys.stdout.write('\\\\\n')
sys.stdout.write('\n')
