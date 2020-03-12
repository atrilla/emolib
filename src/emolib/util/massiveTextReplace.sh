#!/bin/bash
#
# File    : massiveTextReplace.sh
# Created : 02-Jul-2009
# By      : atrilla
#
# Shell script that searches and replaces text in the files contained in folder
# Before use, the script has to be manually modified.
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

for i in `find ./src/ -name *.java`
do 
        cp $i "$i.mybak"
        cat "$i.mybak" | awk '{gsub("OLD_PATTERN","NEW_PATTERN");print}' > $i
        rm "$i.mybak"
done

for i in `find ./src/ -name *.html`
do 
        cp $i "$i.mybak"
        cat "$i.mybak" | awk '{gsub("OLD_PATTERN","NEW_PATTERN");print}' > $i
        rm "$i.mybak"
done

for i in `find ./src/ -name *.pl`
do 
        cp $i "$i.mybak"
        cat "$i.mybak" | awk '{gsub("OLD_PATTERN","NEW_PATTERN");print}' > $i
        rm "$i.mybak"
done

for i in `find ./src/ -name *.sh`
do 
        cp $i "$i.mybak"
        cat "$i.mybak" | awk '{gsub("OLD_PATTERN","NEW_PATTERN");print}' > $i
        rm "$i.mybak"
done

for i in `find ./src/ -name *.sci`
do 
        cp $i "$i.mybak"
        cat "$i.mybak" | awk '{gsub("OLD_PATTERN","NEW_PATTERN");print}' > $i
        rm "$i.mybak"
done

