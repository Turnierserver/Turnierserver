#echo "---Kompiliere Java Zeugs---"
#./buildNetworkZeugs.sh


tmux new-session -d -s turnierserver


tmux new-window -t turnierserver:1 -n 'Config' 'echo "turnierserver.prop:"; cat turnierserver.prop; echo "\n\nFrontend:"; python3 -m Frontend._cfg; read'
tmux new-window -t turnierserver:2 -n 'Backend' './build/backend.sh "../turnierserver.prop"; read'
tmux new-window -t turnierserver:3 -n 'Worker' './build/worker.sh "../turnierserver.prop"; read'
tmux new-window -t turnierserver:4 -n 'Sandbox' 'sleep 1; ./build/sandbox.sh SandboxMachine/SandboxMachine.ini; read'
tmux new-window -t turnierserver:5 -n 'Frontend' 'cd Frontend; python3 app.py run; read'

tmux select-window -t turnierserver:1
tmux -2 attach-session -t turnierserver