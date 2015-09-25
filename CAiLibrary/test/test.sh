#!/bin/bash

cd "`dirname "$0"`"
export "LD_LIBRARY_PATH=`realpath "../build"`:$LD_LIBRARY_PATH"
for file in *.c
do
	echo -e "\e[1mBuilding $file ...\e[0m"
	gcc -g -o "$file.bin" "$file" -L"`realpath ../build`" -lailib -lm || exit 1
	echo -e "\e[1mExecuting $file ...\e[0m"
	"./$file.bin" || echo -e "run\nbt full\nq\ny" | gdb "./$file.bin"
	
	rm -f "$file.bin" &>/dev/null
done
