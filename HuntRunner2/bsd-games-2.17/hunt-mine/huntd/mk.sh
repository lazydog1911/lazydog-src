DEFS_LINUX="-DINTERNET  -DBSD_RELEASE=44 -DUSE_CURSES -DSIGNAL_TYPE=void" # -DLOG
GAME_PARAMS="-DRANDOM -DREFLECT -DMONITOR -DOOZE -DFLY -DVOLCANO -DBOOTS" # -DOTTO"
INCDIR="-I../huntd/ -I../../..//ncurses-5.2/include"
FLAGS="-DHUNTD=\"/usr/games/huntd\" -DINFTIM=-1"
#CC=/root/arm-linux-gnueabihf-gcc-4.6
#CC=/usr/bin/arm-linux-gnueabihf-gcc
CC=gcc



$CC $INCDIR -static $FLAGS $DEFS_LINUX $GAME_PARAMS  *.c -o huntd-arm

