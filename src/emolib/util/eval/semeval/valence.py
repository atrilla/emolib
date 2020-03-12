"""
    File    : valence.py
    Created : 16-Jun-2011
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
for i in lines:
    thisline = i.split(" ")
    if (int(thisline[1]) <= -50):
        print "NEG"
    elif (int(thisline[1]) >= 50):
        print "POS"
    else:
        print "NEU"
