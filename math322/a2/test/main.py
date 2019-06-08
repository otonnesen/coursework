#!/usr/bin/env python3
def distinct(s1, s2):
    for i in s1:
        for j in s1-set([i]):
            if i in s2 and j in s2:
                return False
    return True

a = [set(x) for x in [[2,3,4],[1,4,8],[6,7,8],[2,6,9],[3,7,10],[3,5,9],[1,5,7],[1,2,10],[8,9,10],[1,3,6],[4,7,9],[4,5,6],[2,5,8]]]

k = 0
for i in range(len(a)):
    for j in range(i):
        k += 1
        print('{} and {}: {}'.format(a[i], a[j], distinct(a[i], a[j])))
print(k)
