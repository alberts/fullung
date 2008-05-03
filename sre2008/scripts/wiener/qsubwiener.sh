#!/bin/bash
[ -z $1 ] || [ ! -e $1 ] && exit 1
JOBNAME=`basename $1` 
qsub <<EOF
#!/bin/bash
#$ -N $JOBNAME
#$ -j y
#$ -o /dev/null
#$ -S /bin/bash
#$ -cwd
hostname 1>&2
echo \$TMPDIR
cd "\$TMPDIR"
export AURORACALC=/opt/phnrec/qio
export PATH=/opt/phnrec/bin:\$PATH
/usr/bin/python /net/nist/albert/scripts/wiener.py $*
EOF
