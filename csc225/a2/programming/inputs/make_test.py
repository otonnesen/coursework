#!/usr/bin/env python3

import sys
import random

max_val = 65536
max_lines = 100000
max_time = 40
priority = 100
used = [0 for i in range(max_val)]
sys.stdout.write(str(max_val)+'\n')
for time_step in range(max_lines):
    num = random.randint(0, max_val-1)
    tmp = used[num]
    if used[num] != 0:
        used[num] -= 1
        r = random.random()
        if r < 0.33:
            sys.stdout.write('%s renice %s %s\n' % (time_step, num, random.randint(-priority, priority)))
            sys.stderr.write('%s renice %s %s' % (time_step, num, random.randint(-priority, priority)))
        elif r > 0.67:
            sys.stdout.write('%s kill %s\n' % (time_step, num))
            sys.stderr.write('%s kill %s' % (time_step, num))
            used[num] = 0
        else:
            sys.stderr.write('\t')
    else:
        used[num] = random.randint(1, max_time)
        sys.stdout.write('%s add %s %s\n' % (time_step, num, used[num]))
        sys.stderr.write('%s add %s %s' % (time_step, num, used[num]))
    sys.stderr.write('\tused[%s] = %s' % (num, tmp))
    sys.stderr.write('\tused[%s] = %s\n' % (num, used[num]))
