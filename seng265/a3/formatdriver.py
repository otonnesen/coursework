#!/usr/bin/env python3

import sys
import argparse
from textformatter import UVroff

def main():
    p = argparse.ArgumentParser()
    p.add_argument('filename', nargs='?', help='File to be processed')
    args = p.parse_args()

    if args.filename:
        filename = args.filename
    else:
        filename = "stdin"

    # Instantiate a UVroff object with the name of a file to be
    # opened; therefore no list of strings is provided, so the second
    # parameter is None. (If we wanted to format a list of strings
    # instead, the first parameter would be None and the second
    # parameter would be a reference to some list.
    #
    f = UVroff(filename, None)
    lines = f.get_lines()

    for l in lines:
        print (l)


if __name__ == "__main__":
    main()
