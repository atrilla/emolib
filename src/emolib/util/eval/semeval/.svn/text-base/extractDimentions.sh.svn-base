#!/bin/bash
#
# File    : extractDimentions.sh
# Created : 17-Mar-2009
# By      : atrilla
#
# Shell script that extract the emotional dimentions from an input results file
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2009 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

# SYNOPSIS
#       extractDimentions RESULTS_FILE NUMBER_OF_DIMENTIONS

cat $1 | grep paragraph | grep num > aux.txt

if [ $2 -eq 2 ]
then
	cat aux.txt | grep paragraph | grep num | sed -e "s/[a-zA-Z\"\'<>=]//g" | sed -e "s/^[0-9]\+//g" | awk 'BEGIN { FS = " " }
		{
			print $2" "$3" "
		}'
fi

if [ $2 -eq 3 ]
then
	cat aux.txt | grep paragraph | grep num | sed -e "s/[a-zA-Z\"\'<>=]//g" | sed -e "s/^[0-9]\+//g" | awk 'BEGIN { FS = " " }
		{
			print $2" "$3" "$4" "
		}'
fi

rm aux.txt
