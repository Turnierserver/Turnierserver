#!/bin/bash

#./buildNetworkZeugs.sh

tmux new-session -d -s turnierserver-backend

tmux new-window   -t turnierserver-backend:1 -n 'Config' 'cat ../Turnierserver-Config/turnierserver.prop; read'
tmux new-window   -t turnierserver-backend:2 -n 'Backend' 'cd build; ./backend.sh ../../Turnierserver-Config/turnierserver.prop; read'
tmux split-window -t turnierserver-backend:2 -v 'cd build; ./worker.sh ../../Turnierserver-Config/turnierserver.prop; read'
tmux new-window   -t turnierserver-backend:3 -n 'Sandbox' 'sleep 3s; cd build; ./sandbox.sh ../../Turnierserver-Config/SandboxMachine/SandboxMachine.ini; read'
tmux new-window   -t turnierserver-backend:4 -n 'Frontend' 'javac FrontendFake.java && java FrontendFake; read'

tmux select-window -t turnierserver-backend:4
tmux -2 attach-session -t turnierserver-backend
