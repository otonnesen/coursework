#!/usr/bin/env python3

import sys

def parse_cmd(line, cmd):
    tmp = line.strip('\n').split()
    c = tmp[0][1:] # Strip '.' from command
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

    cmd = {'FT':0, 'LM':0, 'LW':0, 'LS':0}
    linecount = 0
    ends_with_newline = False

    for line in instream.readlines():
        if line[0] == '.': # Line contains a command
            parse_cmd(line, cmd)
        elif cmd['FT'] == 0 or cmd['LW'] == 0: # no formatting is done
            sys.stdout.write(line)
            ends_with_newline = True
        elif line == '\n':
            if ends_with_newline: # Two or more consecutive empty lines in the input require one less newline in the output
                sys.stdout.write('\n')
                sys.stdout.write('\n\n'*cmd['LS'])
            else:
                sys.stdout.write('\n\n')
                sys.stdout.write('\n\n'*cmd['LS'])
            ends_with_newline = True
            linecount = 0
        else:
            for word in line.strip('\n').split():
                if linecount == 0: # Assume no word is longer than the allowed line width
                    sys.stdout.write(' '*cmd['LM']+word)
                    linecount = len(word)
                elif linecount + len(word) + 1 <= cmd['LW'] - cmd['LM']: # Current line length + length of word + one space fits in the specified line width
                    sys.stdout.write(' '+word)
                    linecount += len(word)+1
                else: # Length of word + one space does not fit -> start new line 
                    sys.stdout.write('\n'*(cmd['LS']+1)+' '*cmd['LM']+word)
                    linecount = len(word)
            ends_with_newline = False

    if not ends_with_newline: # Append newline if needed
        sys.stdout.write('\n')

if __name__ == '__main__':
    main()
