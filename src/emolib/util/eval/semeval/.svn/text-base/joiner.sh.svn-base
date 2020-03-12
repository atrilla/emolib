#!/bin/bash
#
# File    : joiner.sh
# Created : 18-Mar-2009
# By      : atrilla
#
# Shell script that joind the dimentions from one file with the categories from
# another.
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2009 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

# SYNOPSIS
#	joiner DIMENTIONS_FILE REFERENCE_CATEGORIES_FILE


len=`cat $1 | wc -l`

for i in `seq 1 $len`; do cat $1 | head -n $i | tail -n 1; cat $2 | head -n $i | tail -n 1 | sed -e s/[0-9]//g | sed -e s/\.\ //g; done
