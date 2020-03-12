//
// File    : CorpusSentimentPlotter.sci
// Created : 07-Apr-2090
// By      : atrilla
// 
// Emolib - Emotional Library
//
// Copyright (c) 2009 Alexandre Trilla &
// 2007-2012 Enginyeria i Arquitectura La Salle (Universitat Ramon Llull)
//
// This file is part of Emolib.
//
// You should have received a copy of the rights granted with this
// distribution of EmoLib. See COPYING.

// CorpusSentimentPlotter is a Scilab function that once placed in a folder with
// the two sentiment corpus files {P,N}.dat, it represents the points of the files
// in a plane emotion space.
// The function automatically loads the six files named "<sentiment>.dat"
// and builds the appropriate figure. Each file contains the emotional
// dimentions (valence and activation in this order) of all the instances
// pertaining to an sentiment, one instance per line. The sentiment evaluations
// correspond to the ones accounted for in EmoLib: N and P.

function CorpusSentimentPlotter()

        // N
        fid = file ('open','N.dat','unknown');
        data = read (fid, -1, 2);

        for i=1:(length(data)/2)
                val(i)=data(i);
        end
        for i=(length(data)/2 + 1):length(data)
                act(i-length(data)/2)=data(i);
        end

        plot(val, act, '+r');

        val=0;
        act=0;

        // P
        fid = file ('open','P.dat','unknown');
        data = read (fid, -1, 2);

        for i=1:(length(data)/2)
                val(i)=data(i);
        end
        for i=(length(data)/2 + 1):length(data)
                act(i-length(data)/2)=data(i);
        end

        plot(val, act, '+b');

        legend(['Negative','Positive'],4);

endfunction
