#!/bin/bash

# $Revision: 23 $

rrdtool graphv $TMP_DIR/classes.png   \
        --start `./convDate $1 00:00:00` \
        --end   `./convDate $1 23:59:59` \
        --step  4                                \
        --title "Classes - $1" \
		-w 600 -h 150                    \
        DEF:AAA=$TMP_DIR/jvm.rrd:ClassLoad:MAX	 \
        AREA:AAA#00AA00BB			 \
