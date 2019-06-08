#!/usr/bin/env python3
import sys
from copy import deepcopy
import itertools
import functools

a = set()
orbits = []
for i in range(56):
    if i in a:
        continue
    # b = set()
    b = []
    j = i
    while j not in b:
        a.add(j)
        # b.add(j)
        b.append(j)
        j *= 3
        j %= 56
    orbits.append(b)
k = 0
for i in orbits:
    sys.stdout.write('&\{')
    s = ''
    for j in i:
        sys.stdout.write(s+'{}'.format(j))
        s = ','
    sys.stdout.write('\}\qquad')
    k += 1
    if k%3 == 0:
        sys.stdout.write('\\\\\n')

    # sys.stdout.write('&\{')
    # for j in sorted(b):
    #     if j == sorted(b)[-1]:
    #         sys.stdout.write('{}'.format(j))
    #     else:
    #         sys.stdout.write('{},'.format(j))
    # sys.stdout.write('\}\\\\\n')

# def check_diff_set(c, n):
#     d = {i:0 for i in range(1,56)}
#     # for i in range(len(orbits)):
#     #     print('{}: {}'.format(i, orbits[i]))
#     # print(c)
#     # print(len(c))
#     for i in c:
#         for j in c:
#             if i == j:
#                 continue
#             d[(i-j)%n] += 1

#     for i in d:
#         # print('{}: {}'.format(i, d[i]))
#         if d[i] != d[1]:
#             return False
#     return True

# def powerset(iterable):
#     s = list(iterable)
#     return itertools.chain.from_iterable(itertools.combinations(s, r) for r in range(len(s)+1))

# for i in powerset(orbits):
#     if len(i) == 0:
#         continue
#     s = functools.reduce(lambda x,y: x+y, i)
#     if len(s) == 11:
#         print(check_diff_set(s, 56))
# # for i in itertools.product(orbits):
# #     s = functools.reduce(lambda x,y: x.union(y), i)
# #     if len(s) == 11:
# #         print(check_diff_set(s, 56))
