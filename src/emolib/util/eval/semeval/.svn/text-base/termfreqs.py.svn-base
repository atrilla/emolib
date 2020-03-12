"""
    File    : termfreqs.py
    Created : 14-Sep-2011
    By      : atrilla
    
    Emolib - Emotional Library
    
    Copyright (c) 2011 Alexandre Trilla &
    2007-2011 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
    
    This file is part of Emolib.
    
    You should have received a copy of the rights granted with this
    distribution of EmoLib. See COPYING.
"""

import sys

f = open(sys.argv[1], "r")
lines = f.readlines()
multipleterms = 0
avglen = 0
for i in lines:
    thisline = i.split(" ")
    avglen = avglen + len(thisline)
    for t in thisline:
        if i.count(" " + t + " ") > 1:
            multipleterms = multipleterms + 1
            print i
            break
print multipleterms, "headlines have multiple terms\n"
print "the average sentence length is: ", float(avglen) / len(lines), "words"
