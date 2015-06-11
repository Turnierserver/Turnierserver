#echo "---Kompiliere Java Zeugs---"
#./buildNetworkZeugs.sh


tmux new-session -d -s turnierserver


tmux new-window -t turnierserver:1 -n 'Config' 'echo "turnierserver.prop:"; cat turnierserver.prop; echo "\n\nFrontend:"; python3 -m Frontend._cfg; read'
tmux new-window -t turnierserver:2 -n 'Backend' './build/backend.sh "../turnierserver.prop"; read'
tmux new-window -t turnierserver:3 -n 'Worker' './build/worker.sh "../turnierserver.prop"; read'
tmux new-window -t turnierserver:4 -n 'Sandbox 1' 'sleep 1; ./build/sandbox.sh SandboxMachine/SandboxMachine.ini; read'
tmux new-window -t turnierserver:5 -n 'Sandbox 2' 'sleep 2; ./build/sandbox.sh SandboxMachine/SandboxMachine.ini; read'
tmux new-window -t turnierserver:6 -n 'Frontend' 'cd Frontend; python3 app.py run; read'
# für gunicorn:
#tmux new-window -t turnierserver:6 -n 'Frontend' 'cd Frontend; gunicorn -b 0.0.0.0:80 app:app --log-level INFO; read'

tmux select-window -t turnierserver:1
tmux -2 attach-session -t turnierserver