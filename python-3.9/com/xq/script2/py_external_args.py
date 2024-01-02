#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys

file_name = sys.argv[0]
arg1 = sys.argv[1]
arg2 = sys.argv[2]

print("len:", len(sys.argv))

for i in sys.argv:
    print(i)

print("File name:", file_name)
print("First argument:", arg1)
print("Second argument:", arg2)

print("@@@@@@@@@@@@@")

import argparse

parser = argparse.ArgumentParser(description='Process some integers.')
parser.add_argument('integers', metavar='N', type=int, nargs='+',
                    help='an integer for the accumulator')
parser.add_argument('--sum', dest='accumulate', action='store_const',
                    const=sum, default=max,
                    help='sum the integers (default: find the max)')

args = parser.parse_args()

# for i in args:
#     print(i)

print(args.accumulate(args.integers))
