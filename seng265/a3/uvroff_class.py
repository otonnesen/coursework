#!/usr/bin/env python3

import sys


class UVroff:

    def __init__(self, filename, lines):
        self._out_lines = []    # Internal array to return from get_lines
        self._cmd = {'FT': False, 'LM': 0, 'LW': 0, 'LS': 0}
        if filename is not None:
            try:
                self._in = sys.stdin if filename is 'stdin' else open(filename)
            except OSError as e:
                if isinstance(e, FileNotFoundError):
                    print('Invalid filename, aborting.')
                elif isinstance(e, IsADirectoryError):
                    print('File is a directory, aborting.')
                else:
                    print(e)
                sys.exit(-1)
            try:
                self._lines = self._in.readlines()
            except UnicodeDecodeError:
                print('File contains non UTF-8 characters, aborting.')
                sys.exit(-1)
        else:
            self._lines = lines
        self._format_text()

    # Places n empty lines in _out_lines
    def _newline(self, n=1):
        for i in range(int(n)):
            self._out_lines.append('')

    # Appends line to the last index of _out_lines
    def _write_out(self, line):
        if len(self._out_lines) is 0:
            self._out_lines = [line]
        else:
            self._out_lines[-1] += line

    # Satisfies specification
    def get_lines(self):
        return self._out_lines

    # To be called by constructor to format its given text
    def _format_text(self):
        ends_with_newline = False
        ftoff = False
        linecount = 0

        for line in self._lines:
            if line[0] == '.':
                self._parse_cmd(line)
            elif not self._cmd['FT'] or self._cmd['LW'] == 0:
                if line is '\n':
                    self._newline()
                else:
                    if ftoff:
                        self._newline()
                    self._write_out(line.strip('\n'))
                    ends_with_newline = False
                    ftoff = True
            elif line == '\n':
                if ftoff:
                    self._newline()
                if ends_with_newline:
                    self._newline(1+2*self._cmd['LS'])
                else:
                    self._newline(2*(self._cmd['LS']+1))
                    ends_with_newline = True
                    linecount = 0
                ftoff = False
            else:
                if ftoff:
                    self._newline()
                for word in line.strip('\n').split():
                    if linecount == 0:
                        self._write_out(' '*self._cmd['LM']+word)
                        linecount = len(word)
                    elif linecount + len(word) + 1 <= self._cmd['LW'] - \
                            self._cmd['LM']:
                        self._write_out(' '+word)
                        linecount += len(word)+1
                    else:
                        self._newline(self._cmd['LS']+1)
                        self._write_out(' '*self._cmd['LM']+word)
                        linecount = len(word)
                ends_with_newline = False
                ftoff = False
        if len(self._out_lines) > 2\
                and self._out_lines[-1] is ''\
                and self._out_lines[-2] is '':
            del self._out_lines[-1]

        # if not ends_with_newline:
        #     self._newline()

    def _parse_cmd(self, line):
        tmp = line.strip('\n').split()
        c = tmp[0][1:]  # Strip '.' from command
        v = tmp[1]
        if c == 'LW':
            try:
                self._cmd['LW'] = int(v)
            except ValueError:
                print('Illegal value for LW, aborting.')
                sys.exit(-1)
            self._cmd['FT'] = True  # LW imples FT, as per the spec
        elif c == 'FT':
            if v == 'on':
                self._cmd['FT'] = True
            elif v == 'off':
                self._cmd['FT'] = False
            else:
                print('Illegal value for FT, aborting.')
                sys.exit(-1)
        elif c == 'LM':
            if v[0] == '+':
                try:
                    self._cmd['LM'] += int(v[1:])
                except ValueError:
                    print('Illegal value for LM, aborting.')
                    sys.exit(-1)
                if self._cmd['LM'] > self._cmd['LW'] - 20:
                    self._cmd['LM'] = self._cmd['LW'] - 20
            elif v[0] == '-':
                try:
                    self._cmd['LM'] -= int(v[1:])
                except ValueError:
                    print('Illegal value for LM, aborting.')
                    sys.exit(-1)
                if self._cmd['LM'] < 0:
                    self._cmd['LM'] = 0
            else:
                try:
                    self._cmd['LM'] = int(v)
                except ValueError:
                    print('Illegal value for LM, aborting.')
                    sys.exit(-1)
        elif c == 'LS':
            try:
                self._cmd['LS'] = int(v)
            except ValueError:
                print('Illegal value for LS, aborting.')
                sys.exit(-1)
        else:
            print('Invalid command, aborting.')
            sys.exit(-1)


def main():
    pass


if __name__ == '__main__':
    main()
