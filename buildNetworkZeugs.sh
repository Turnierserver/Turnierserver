#!/bin/bash -e

if [ -d build ]
then
    rm -rf build
fi
mkdir -p build

mvn -N install
projects="Utils NetworkingLib CompilerLib Worker Game-Logic Backend"
for project in $projects
do
    cd $project
    #mvn clean
    mvn package install dependency:copy-dependencies
    cp target/*.jar ../build/
    cp target/dependency/* ../build/
    cd ..
done

echo -e "#!/bin/sh
cd \`dirname \$0\`
java -cp '*' org.pixelgaffer.turnierserver.backend.BackendMain \${@}" > build/backend.sh
echo -e "#!/bin/sh
cd \`dirname \$0\`
java -cp '*' org.pixelgaffer.turnierserver.worker.WorkerMain \${@}" > build/worker.sh

projects="Sandbox"
for project in $projects
do
	cd $project
	mkdir -p build
	cd build
	qmake ../$project.pro CONFIG+=debug MAKE='make -j3'
	make -j3
	for file in *
	do
		if [ -x $file -a -r $file -a ! -d $file ]
		then
			cp $file ../../build
		fi
	done
	cd ../..
done

echo -e "#!/bin/sh
if [ \$UID != 0 ]; then
  echo Die Sandbox benötigt root-Rechte
  echo \"sudo \$0 \${@}\"
  sudo \$0 \${@}
  exit \$?
fi
PATH=\`dirname \$0\`:\$PATH
echo -e \"run \${@}\nbt\nq\ny\n\" | gdb sandboxd" > build/sandbox.sh

chmod +x build/*.sh
