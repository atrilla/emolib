#!/bin/bash
#
# File    : removeTags.sh
# Created : 13-Feb-2009
# By      : atrilla
#
# Shell script that removes the tags from an input XML file
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2009 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

cat $1 | sed 's/<[\/a-z\ =\"0-9]\+>//g' | grep "^[+\\\t\(\)|.,:\'&%a-zA-Z0-9\ -?!\$\"\`\']\+$"
