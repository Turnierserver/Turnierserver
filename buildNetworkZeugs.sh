#!/bin/bash -e

if [ -d build ]
then
    rm -rf build
fi
mkdir -p build

mvn -N install
projects="Utils NetworkingLib CompilerLib Worker Game-Logic Backend SandboxManager"
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
echo -e "#!/bin/sh
cd \`dirname \$0\`
java -cp '*' org.pixelgaffer.turnierserver.sandboxmanager.SandboxMain \${@}" > build/sandbox.sh

chmod +x build/*.sh
