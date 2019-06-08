#!/usr/bin/env python3

import sys
import re

CMD_PATTERN = "\n*(\.LS [0-9]+)|(\.LW [0-9]+)|(\.FT (off|on))|(\.LM [0-9]+)\n*"

def parse_cmd(line):
    tmp = line.strip('\n').split()
    c = tmp[0][1:] #Strip '.' from command
    v = tmp[1]
    if c == 'LW':
        cmd['LW'] = int(v)
        cmd['FT'] = 1 # LW imples FT, as per the spec
    elif c == 'FT':
        cmd['FT'] = 1 if v == 'on' else 0
    elif c == 'LM':
        if v[0] == '+':
            cmd['LM'] += int(v[1:])
            if cmd['LM'] > cmd['LW'] - 20:
                cmd['LM'] = cmd['LW'] - 20
        elif v[0] == '-':
            cmd['LM'] -= int(v[1:])
            if cmd['LM'] < 0:
                cmd['LM'] = 0
        else:
            cmd['LM'] = int(v)
    elif c == 'LS':
        cmd['LS'] = int(v)

def main():
    if len(sys.argv) > 1:
        instream = open(sys.argv[1])
    else:
        instream = sys.stdin

    l = instream.read().split('\n')
    c = []

    for i in range(len(l)):
        if l[i] == '':
            continue
        elif l[i][0] == '.':
            c.append((i, l[i]))

    print(c)
    for i in range(len(c)):
        try:
            print(str(c[i][0])+', '+str(c[i+1][0]))
            print(l[c[i][0]:c[i+1][0]])
        except IndexError:
            pass
            # print(l[c[i][0]:])

    cmd = {'FT':0, 'LM':0, 'LW':0, 'LS':0}

if __name__=='__main__':
    main()
