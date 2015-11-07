#echo "---Kompiliere Java Zeugs---"
#./buildNetworkZeugs.sh


tmux new-session -d -s turnierserver


tmux new-window -t turnierserver:1 -n 'Config' 'echo -e "\e[1mturnierserver.prop\e[0m"; cat turnierserver.prop; echo -e "\n\n\e[1msandbox.prop\e[0m"; cat SandboxManager/sandbox.prop; echo -e "\n\n\e[1m_cfg.py\e[0m"; python3 -m Frontend._cfg; read'
tmux new-window -t turnierserver:2 -n 'Backend' './build/backend.sh "../turnierserver.prop"; read'
tmux new-window -t turnierserver:3 -n 'Worker 1' './build/worker.sh "../worker1.prop"; read'
tmux new-window -t turnierserver:4 -n 'Sandbox 1 1' './build/sandbox.sh ../sandbox1.prop; read'
tmux new-window -t turnierserver:5 -n 'Sandbox 1 2' './build/sandbox.sh ../sandbox1.prop; read'
tmux new-window -t turnierserver:6 -n 'Worker 2' './build/worker.sh "../worker2.prop"; read'
tmux new-window -t turnierserver:7 -n 'Sandbox 2 1' './build/sandbox.sh ../sandbox2.prop; read'
tmux new-window -t turnierserver:8 -n 'Sandbox 2 2' './build/sandbox.sh ../sandbox2.prop; read'
tmux new-window -t turnierserver:9 -n 'Frontend' 'cd Frontend; python3 app.py run; read'
# für gunicorn:
#tmux new-window -t turnierserver:9 -n 'Frontend' 'cd Frontend; gunicorn -b 0.0.0.0:80 app:app --log-level INFO; read'

tmux select-window -t turnierserver:1
tmux -2 attach-session -t turnierserver
