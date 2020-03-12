#!/bin/bash
#
# File    : extractCategory.sh
# Created : 13-Feb-2009
# By      : atrilla
#
# Shell script that removes the body of the sentences and leaves the
# resulting emotions in a plain text format.
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2009 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

cat $1 | grep paragraph | grep cat | sed 's/>//g' | sed 's/cat\=/>/g' | sed 's/[\ ]\+<[a-z\ .=\"0-9]\+>//g' | sed 's/\"//g'
