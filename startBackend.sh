#!/bin/bash

#./buildNetworkZeugs.sh

tmux new-session -d -s turnierserver-backend

tmux new-window   -t turnierserver-backend:1 -n 'Config' 'cat ../Turnierserver-Config/turnierserver.prop; read'
tmux new-window   -t turnierserver-backend:2 -n 'Backend' 'build/backend.sh ../../Turnierserver-Config/turnierserver.prop; read'
tmux new-window   -t turnierserver-backend:3 -n 'Worker' 'build/worker.sh ../../Turnierserver-Config/turnierserver.prop; read'
tmux new-window   -t turnierserver-backend:4 -n 'Sandbox' 'sleep 2; build/sandbox.sh ../Turnierserver-Config/SandboxMachine.ini; read'
tmux split-window -t turnierserver-backend:4 -v 'sleep 2; build/sandbox.sh ../Turnierserver-Config/SandboxMachine.ini; read'
tmux new-window   -t turnierserver-backend:5 -n 'Frontend' 'javac FrontendFake.java && java FrontendFake; read'

tmux select-window -t turnierserver-backend:5
tmux -2 attach-session -t turnierserver-backend
