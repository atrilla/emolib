//
// File    : CorpusPlotter.sci
// Created : 13-Mar-2090
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

// CorpusPlotter is a Scilab function that once placed in a folder with
// the six emotional corpus files, it represents the points of the files
// in a plane emotion space.
// The function automatically loads the six files named "<emotion>.dat"
// and builds the appropriate figure. Each file contains the emotional
// dimentions (valence and activation in this order) of all the instances
// pertaining to an affective state, one instance per line. The emotions
// correspond to the ones accounted for in EmoLib.

function CorpusPlotter()

        // Anger
        fid = file ('open','anger.dat','unknown');
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

        // Fear
        fid = file ('open','fear.dat','unknown');
        data = read (fid, -1, 2);

        for i=1:(length(data)/2)
                val(i)=data(i);
        end
        for i=(length(data)/2 + 1):length(data)
                act(i-length(data)/2)=data(i);
        end

        plot(val, act, '+y');

        val=0;
        act=0;

        // Sorrow
        fid = file ('open','sorrow.dat','unknown');
        data = read (fid, -1, 2);

        for i=1:(length(data)/2)
                val(i)=data(i);
        end
        for i=(length(data)/2 + 1):length(data)
                act(i-length(data)/2)=data(i);
        end

        plot(val, act, '+g');

        val=0;
        act=0;

        // Neutral
        fid = file ('open','neutral.dat','unknown');
        data = read (fid, -1, 2);

        for i=1:(length(data)/2)
                val(i)=data(i);
        end
        for i=(length(data)/2 + 1):length(data)
                act(i-length(data)/2)=data(i);
        end

        plot(val, act, '+c');

        val=0;
        act=0;

        // Happiness
        fid = file ('open','happiness.dat','unknown');
        data = read (fid, -1, 2);

        for i=1:(length(data)/2)
                val(i)=data(i);
        end
        for i=(length(data)/2 + 1):length(data)
                act(i-length(data)/2)=data(i);
        end

        plot(val, act, '+b');

        val=0;
        act=0;

        // Surprise
        fid = file ('open','surprise.dat','unknown');
        data = read (fid, -1, 2);

        for i=1:(length(data)/2)
                val(i)=data(i);
        end
        for i=(length(data)/2 + 1):length(data)
                act(i-length(data)/2)=data(i);
        end

        plot(val, act, '+m');

        val=0;
        act=0;

        legend(['anger','fear','sorrow','neutral','happiness','surprise'],4);

endfunction
