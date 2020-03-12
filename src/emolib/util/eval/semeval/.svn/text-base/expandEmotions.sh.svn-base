#!/bin/bash
#
# File    : expandEmotions.sh
# Created : 14-Feb-2009
# By      : atrilla
#
# Shell script that expands the most significant emotion
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2009 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

cat $1 | sed 's/^[0-9]\+\ //g' | awk 'BEGIN { FS = " " }
	{
		present = $1
		position = 1
		for (i = 2; i <= 6; i = i + 1) {
			if ($i > present) {
				present = $i
				position = i
			}
		}
		if (position == 1) print "anger"
		if (position == 2) print "anger"
		if (position == 3) print "fear"
		if (position == 4) print "happiness"
		if (position == 5) print "sorrow"
		if (position == 6) print "surprise"
	}'
