#!/bin/bash
#
# File    : custom_path.sh
# Created : 07-Oct-2010
# By      : atrilla
#
# Shell script that customises the EmoLib config file with the 
# current project path
#
# Copyright (c) 2010 Alexandre Trilla &
# 2007-2011 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
#
# This file is part of Emolib.
#
# You should have received a copy of the rights granted with this
# distribution of EmoLib. See COPYING.

PROJ_PATH=`cd ..; pwd`
cat emolib.config.xml.base | sed "s|EMOLIB_PATH|$PROJ_PATH|g" > emolib.config.xml
