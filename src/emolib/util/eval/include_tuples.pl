#!/usr/bin/perl
#
# File    : include_tuples.pl
# Created : 01-Mar-2010
# By      : atrilla
#
# Appends the tuples to a given textual dataset.
#
# Copyright (c) 2010 Alexandre Trilla &
# 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

# SYNOPSIS
#        perl include_tuples.pl DATASET_FILE

open(DATASETFILE, $ARGV[0]);

while ($line = <DATASETFILE>) {
        @line_elements = split(" ", $line);
        $label = pop(@line_elements);
        $linelen = @line_elements;
        print "@line_elements", " ";
        for ($i = 2; $i < $linelen; $i++) {
                print $line_elements[$i - 1], "_", $line_elements[$i], " ";
        }
        print $label, "\n";
}
