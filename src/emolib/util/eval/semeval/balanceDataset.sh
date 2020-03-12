#!/bin/bash
#
# File    : balanceDataset.sh
# Created : 11-Mar-2009
# By      : atrilla
#
# Shell script that distributes a dataset according to a goven number of folds
# in order to prevent the folds from missing a category.
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2009 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

# SYNOPSIS
#	balanceDataset NUMBER_OF_FOLDS DATASET_FILE

for nfold in `seq 1 $1`
do

	for category in anger fear sorrow neutral happiness surprise
	do
		size=`cat $2 | grep $category | wc -l`
		size=`echo $size / $1 | bc`
		cat $2 | grep $category | head -n `echo $nfold \* $size | bc` | tail -n $size
	done

done
