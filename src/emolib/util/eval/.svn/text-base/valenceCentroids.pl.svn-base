#!/usr/bin/perl
#
# File    : valenceCentroids.pl
# Created : 17-Apr-2009
# By      : atrilla
#
# Perl script that finds the two centroids (P and N) of a corpus
# for sentiment classification. The dataset file must contain, for
# each example, a valence and the correspondent sentiment category (P or N).
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

# SYNOPSIS
#        perl sentimnetCentroids.pl DATASET_FILE

open(DATASETFILE, $ARGV[0]);

$total_val_N = 0;
$examples_N = 0;

$total_val_P = 0;
$examples_P = 0;

while ($line = <DATASETFILE>) {
        @line_elements = split(" ", $line);
        if ($line_elements[1] eq "N") {
                $examples_N += 1;
                $total_val_N += $line_elements[0];
        } else {
                $examples_P += 1;
                $total_val_P += $line_elements[0];
        }
}

print $total_val_N / $examples_N, " N\n";
print $total_val_P / $examples_P, " P\n";
