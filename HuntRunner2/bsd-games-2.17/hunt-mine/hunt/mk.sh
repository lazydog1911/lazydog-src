DEFS_LINUX="-DINTERNET -DBSD_RELEASE=44 -DUSE_CURSES -DSIGNAL_TYPE=void"
GAME_PARAMS="-DRANDOM -DREFLECT -DMONITOR -DOOZE -DFLY -DVOLCANO -DBOOTS" # -DOTTO"
INCDIR="-I../huntd/ -I../../../ncurses-5.2/include"
FLAGS="-DHUNTD=\"/usr/games/huntd\" -DINFTIM=-1"

#Configured with: ../src/configure -v --with-pkgversion='Debian 4.6.3-14' --with-bugurl=file:///usr/share/doc/gcc-4.6/README.Bugs --enable-languages=c,c++,fortran,objc,obj-c++ --prefix=/usr --program-suffix=-4.6 --enable-shared --enable-linker-build-id --with-system-zlib --libexecdir=/usr/lib --without-included-gettext --enable-threads=posix --with-gxx-include-dir=/usr/arm-linux-gnueabihf/include/c++/4.6.3 --libdir=/usr/lib --enable-nls --enable-clocale=gnu --enable-libstdcxx-debug --enable-libstdcxx-time=yes --enable-gnu-unique-object --enable-plugin --enable-objc-gc --disable-sjlj-exceptions --with-arch=armv7-a --with-fpu=vfpv3-d16 --with-float=hard --with-mode=thumb --enable-checking=release --build=x86_64-linux-gnu --host=x86_64-linux-gnu --target=arm-linux-gnueabihf --program-prefix=arm-linux-gnueabihf- --includedir=/usr/arm-linux-gnueabihf/include --with-headers=/usr/arm-linux-gnueabihf/include --with-libs=/usr/arm-linux-gnueabihf/lib
 
#CC="gcc --target=arm-linux-gnueabihf -I/usr/include/arm-linux-gnueabihf/ -D__ARM_PCS_VFP"

#CC=/root/arm-linux-gnueabihf-gcc-4.6
#CC=gcc-4.6

#CC=/usr/bin/arm-linux-gnueabihf-gcc
CC=gcc

#For X86/X64:
#$CC $DEFS_LINUX $GAME_PARAMS $INCDIR -c -o connect.o connect.c
#$CC -DINFTIM=-1 $DEFS_LINUX $GAME_PARAMS $INCDIR -c -o playit.o playit.c
#$CC $DEFS_LINUX $GAME_PARAMS $INCDIR -c -o hunt.o hunt.c
#$CC -DHUNTD=\"/usr/games/huntd\" $DEFS_LINUX $GAME_PARAMS $INCDIR -c -o pathname.o pathname.c
##gcc-4.6 $DEFS_LINUX $GAME_PARAMS $INCDIR -c -o otto.o otto.c
#$CC -lcurses -o hunt *.o

#for ARM:
$CC  $INCDIR -static $FLAGS $DEFS_LINUX $GAME_PARAMS connect.c playit.c pathname.c hunt.c -o hunt-arm -L../../../ncurses-5.2/lib -lncurses 

