if [ $# -eq 1 ]; then
	options=$1
else
	options=""
fi

export CC=gcc
export AR=gcc-ar
export RANLIB=gcc-ranlib
export CFLAGS="-I/usr/include/x86_64-linux-gnu"
export LIBS=-ldl
cd libxml2
sed -i 's/1.16.3/1.15/g' configure.ac
./autogen.sh $options --prefix="$(pwd)/out"
make && make install
