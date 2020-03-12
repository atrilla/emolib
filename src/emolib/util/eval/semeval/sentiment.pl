#!/usr/bin/perl
#
# File    : sentiment.pl
# Created : 17-Apr-2009
# By      : atrilla
#
# Perl script that assigns the correspondent sentiment tag
# (N or P) according to the input valence. If valence < 0
# then N, P otherwise.
#
# Copyright (c) 2009 Alexandre Trilla &
# 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

# SYNOPSIS
#        perl sentiment.pl DATASET_FILE

open(DATASETFILE, $ARGV[0]);

while ($line = <DATASETFILE>) {
        @line_elements = split(" ", $line);
        if ($line_elements[1] < 0) {
                print "N\n";
        } else {
                print "P\n";
        }
}
