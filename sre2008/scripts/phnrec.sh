#!/bin/sh
[ -z $1 ] && exit 1
[ -z $2 ] && exit 1;
[ ! -e $1 ] && exit 1;
echo "$1 -> $2"
hostname 1>&2
WORKINGDIR=`pwd`
PHNRECHOME=/opt/phnrec
PHNRECEXE=$PHNRECHOME/phnrec
SPH2PIPE=$PHNRECHOME/sph2pipe
PHNRECCFG=$PHNRECHOME/PHN_HU_SPDAT_LCRC_N1500
cp -a "$PHNRECCFG" /tmp
RAW="/tmp/temp.raw"
MLF="/tmp/temp.rec"
$SPH2PIPE -c 1 -p -f raw $1 $RAW
$PHNRECEXE -v -c "/tmp/PHN_HU_SPDAT_LCRC_N1500" -i $RAW -m $MLF -w lin16 -s wf -t str
cp -v $MLF $2
