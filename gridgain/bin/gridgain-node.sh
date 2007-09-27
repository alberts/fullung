#!/bin/sh

if [ -z "$GRIDGAIN_HOME" ] ; then
  ## resolve links - $0 may be a link to activemq's home
  PRG="$0"
  progname=`basename "$0"`
  saveddir=`pwd`

  # need this for relative symlinks
  dirname_prg=`dirname "$PRG"`
  cd "$dirname_prg"

  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
    else
    PRG=`dirname "$PRG"`"/$link"
    fi
  done

  GRIDGAIN_HOME=`dirname "$PRG"`/..

  cd "$saveddir"

  # make it fully qualified
  GRIDGAIN_HOME=`cd "$GRIDGAIN_HOME" && pwd`
fi

$GRIDGAIN_HOME/bin/wrapper-linux-x86-32 -c $GRIDGAIN_HOME/config/wrapper-node.conf $@
