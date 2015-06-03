#!/bin/bash -e

if [ -d build ]
then
    rm -rf build
fi
mkdir -p build

projects="NetworkingLib CompilerLib Worker Game-Logic Backend"
for project in $projects
do
    cd $project
    #mvn clean
    mvn package install dependency:copy-dependencies
    cp target/*.jar ../build/
    cp target/dependency/* ../build/
    cd ..
done

echo -e "#!/bin/sh\ncd \`dirname \$0\`\njava -cp '*' org.pixelgaffer.turnierserver.backend.BackendMain \${@}" > build/backend.sh
echo -e "#!/bin/sh\ncd \`dirname \$0\`\njava -cp '*' org.pixelgaffer.turnierserver.worker.WorkerMain \${@}" > build/worker.sh

projects="SandboxHelper SandboxMachine"
for project in $projects
do
	cd $project
	mkdir -p build
	cd build
	qmake ../$project.pro CONFIG+=debug
	make
	for file in *
	do
		if [ -x $file -a -r $file -a ! -d $file ]
		then
			cp $file ../../build
		fi
	done
	cd ../..
done

echo -e "#!/bin/sh\nif [ \$UID != 0 ]; then\n  echo Die Sandbox benötigt root-Rechte\n  echo \"su -c '\$0 \${@}'\"\n  su -c \"\$0 \${@}\"\n  exit \$?\nfi\nPATH=\`dirname \$0\`:\$PATH\nsandboxd \${@}" > build/sandbox.sh

chmod +x build/*.sh
