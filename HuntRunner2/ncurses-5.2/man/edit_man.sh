#! /bin/sh
# this script is generated by the configure-script
prefix="/usr/local"
datadir="${prefix}/share"
TERMINFO="/usr/local/share/terminfo"
MKDIRS="/root/HuntRunner2/ncurses-5.2/mkinstalldirs"
INSTALL="/usr/bin/install -c"
INSTALL_DATA="${INSTALL} -m 644"
transform="s,x,x,"

TMP=${TMPDIR-/tmp}/man$$
trap "rm -f $TMP" 0 1 2 5 15

verb=$1
shift

mandir=$1
shift

srcdir=$1
shift

for i in $* ; do
case $i in #(vi
*.orig|*.rej) ;; #(vi
*.[0-9]*)
	section=`expr "$i" : '.*\.\([0-9]\)[xm]*'`;
	if test $verb = installing ; then
	if test ! -d $mandir/man${section} ; then
		$MKDIRS $mandir/man$section
	fi
	fi
	aliases=
	source=`basename $i`
	inalias=$source
	test ! -f $inalias && inalias="$srcdir/$inalias"
	if test ! -f $inalias ; then
		echo .. skipped $source
		continue
	fi
	aliases=`sed -f $srcdir/manlinks.sed $inalias | sort -u`
	target=`grep "^$source" /root/HuntRunner2/ncurses-5.2/man/man_db.renames | mawk '{print $2}'`
	if test -z "$target" ; then
		echo '? missing rename for '$source
		target="$source"
	fi
	target="$mandir/man$section/$target"
	test $verb = installing && sed -e "s,@DATADIR@,$datadir," < $i | sed -f edit_man.sed >$TMP
	if test $verb = installing ; then
	if ( gzip -f $TMP )
	then
		mv $TMP.gz $TMP
	fi
	fi
	target="$target.gz"
	suffix=`basename $target | sed -e 's/^[^.]*//'`
	if test $verb = installing ; then
		echo $verb $target
		$INSTALL_DATA $TMP $target
		test -n "$aliases" && (
			cd $mandir/man${section} && (
				target=`basename $target`
				for cf_alias in $aliases
				do
					if test $section = 1 ; then
						cf_alias=`echo $cf_alias|sed "${transform}"`
					fi

					if test -f $cf_alias${suffix} ; then
						if ( cmp -s $target $cf_alias${suffix} )
						then
							:
						else
							echo .. $verb alias $cf_alias${suffix}
							rm -f $cf_alias${suffix}
							ln -s $target $cf_alias${suffix}
						fi
					else
						echo .. $verb alias $cf_alias${suffix}
						rm -f $cf_alias${suffix}
						ln -s $target $cf_alias${suffix}
					fi
				done
			)
		)
	elif test $verb = removing ; then
		echo $verb $target
		rm -f $target
		test -n "$aliases" && (
			cd $mandir/man${section} && (
				for cf_alias in $aliases
				do
					if test $section = 1 ; then
						cf_alias=`echo $cf_alias|sed "${transform}"`
					fi

					echo .. $verb alias $cf_alias${suffix}
					rm -f $cf_alias${suffix}
				done
			)
		)
	else
#		echo ".hy 0"
		cat $TMP
	fi
	;;
esac
done
exit 0
