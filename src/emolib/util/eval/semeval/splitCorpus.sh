#!/bin/bash
#
# File    : splitCorpus.sh
# Created : 13-Mar-2009
# By      : atrilla
#
# Shell script that splits a dataset into the different emotions accounted for in EmoLib
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2009 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

# SYNOPSIS
#	splitCorpus CORPUS_FILE

for category in anger fear sorrow neutral happiness surprise
do
	cat $1 | grep $category > $category.dat
done
