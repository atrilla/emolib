//
// File    : CorpusSpacePlotter.sci
// Created : 18-Mar-2090
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

// CorpusSpacePlotter is a Scilab function that once placed in a folder with
// the six emotional corpus files, it represents the points of the files
// in a 3D emotion space.
// The function automatically loads the six files named "<emotion>.dat"
// and builds the appropriate figure. Each file contains the emotional
// dimentions (valence and activation in this order) of all the instances
// pertaining to an affective state, one instance per line. The emotions
// correspond to the ones accounted for in EmoLib.

function CorpusSpacePlotter()

        // Anger
        fid = file ('open','anger.dat','unknown');
        data = read (fid, -1, 3);

        for i=1:(length(data)/3)
                val(i)=data(i);
        end
        for i=(length(data)/3 + 1):(2*length(data)/3)
                act(i-length(data)/3)=data(i);
        end
        for i=(2*length(data)/3 + 1):length(data)
                con(i-2*length(data)/3)=data(i);
        end

        param3d(val,act,con);
        prm=get("hdl");
        prm.mark_style=1;
        prm.line_mode="off";
        prm.mark_foreground=5;

        val=0;
        act=0;
        con=0;

        // Fear
        fid = file ('open','fear.dat','unknown');
        data = read (fid, -1, 3);

        for i=1:(length(data)/3)
                val(i)=data(i);
        end
        for i=(length(data)/3 + 1):(2*length(data)/3)
                act(i-length(data)/3)=data(i);
        end
        for i=(2*length(data)/3 + 1):length(data)
                con(i-2*length(data)/3)=data(i);
        end

        param3d(val,act,con);
        prm=get("hdl");
        prm.mark_style=1;
        prm.line_mode="off";
        prm.mark_foreground=7;

        val=0;
        act=0;
        con=0;

        // Sorrow
        fid = file ('open','sorrow.dat','unknown');
        data = read (fid, -1, 3);

        for i=1:(length(data)/3)
                val(i)=data(i);
        end
        for i=(length(data)/3 + 1):(2*length(data)/3)
                act(i-length(data)/3)=data(i);
        end
        for i=(2*length(data)/3 + 1):length(data)
                con(i-2*length(data)/3)=data(i);
        end

        param3d(val,act,con);
        prm=get("hdl");
        prm.mark_style=1;
        prm.line_mode="off";
        prm.mark_foreground=3;

        val=0;
        act=0;
        con=0;

        // Neutral
        fid = file ('open','neutral.dat','unknown');
        data = read (fid, -1, 3);

        for i=1:(length(data)/3)
                val(i)=data(i);
        end
        for i=(length(data)/3 + 1):(2*length(data)/3)
                act(i-length(data)/3)=data(i);
        end
        for i=(2*length(data)/3 + 1):length(data)
                con(i-2*length(data)/3)=data(i);
        end

        param3d(val,act,con);
        prm=get("hdl");
        prm.mark_style=1;
        prm.line_mode="off";
        prm.mark_foreground=4;

        val=0;
        act=0;
        con=0;

        // Happiness
        fid = file ('open','happiness.dat','unknown');
        data = read (fid, -1, 3);

        for i=1:(length(data)/3)
                val(i)=data(i);
        end
        for i=(length(data)/3 + 1):(2*length(data)/3)
                act(i-length(data)/3)=data(i);
        end
        for i=(2*length(data)/3 + 1):length(data)
                con(i-2*length(data)/3)=data(i);
        end

        param3d(val,act,con);
        prm=get("hdl");
        prm.mark_style=1;
        prm.line_mode="off";
        prm.mark_foreground=2;

        val=0;
        act=0;
        con=0;

        // Surprise
        fid = file ('open','surprise.dat','unknown');
        data = read (fid, -1, 3);

        for i=1:(length(data)/3)
                val(i)=data(i);
        end
        for i=(length(data)/3 + 1):(2*length(data)/3)
                act(i-length(data)/3)=data(i);
        end
        for i=(2*length(data)/3 + 1):length(data)
                con(i-2*length(data)/3)=data(i);
        end

        param3d(val,act,con);
        prm=get("hdl");
        prm.mark_style=1;
        prm.line_mode="off";
        prm.mark_foreground=6;

        val=0;
        act=0;
        con=0;

        legend(['anger','fear','sorrow','neutral','happiness','surprise'],4);

endfunction
