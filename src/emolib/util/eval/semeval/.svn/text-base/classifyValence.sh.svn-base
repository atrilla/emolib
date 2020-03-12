#!/bin/bash
#
# File    : classifyValence.sh
# Created : 19-Feb-2009
# By      : atrilla
#
# Shell script that determines the category (emotion) according to the
# input valence.
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2009 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

cat $1 | awk 'BEGIN { FS = " " }
	{
		value = $2 + 100
		value = value / 20
		if (value >= 8.5) {
			emo = "surprise"
		} else if (value >= 6.445) {
			emo = "happiness"
		} else if (value <=2) {
			emo = "fear"
		} else if (value <= 3) {
			emo = "anger"
		} else if (value <= 4) {
			emo = "sorrow"
		} else {
			emo = "neutral"
		}
		print emo
	}'
