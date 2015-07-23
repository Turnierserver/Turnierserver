from flask.ext.script import prompt_bool, prompt, prompt_pass
from database import *
from backend import backend
from unittest.mock import Mock

import os
import shutil
import zipfile


def clean_tmp():
	print("Cleaning tmp")
	if os.path.isdir("tmp"):
		shutil.rmtree("tmp")
	os.mkdir("tmp")


def zipdir(path, ziph):
	# ziph is zipfile handle
	for root, dirs, files in os.walk(path):
		for file in files:
			ziph.write(os.path.join(root, file))


def _make_data_container(game_id):
	clean_tmp()
	@ftp.safe
	def f():
		langs = [d for d in ftp.ftp_host.listdir("Games/"+game_id) if ftp.ftp_host.path.isdir("Games/"+game_id+"/"+d)]

		os.mkdir("tmp/AiLibraries")
		for lang in langs:
			path = "Games/{}/{}/ailib".format(game_id, lang)
			os.mkdir("tmp/AiLibraries/"+lang)
			for root, dirs, files in ftp.ftp_host.walk(path):
				new_path = root.replace(path, "tmp/AiLibraries/"+lang)
				#make dirs
				for dir in dirs:
					print("MKDIR:", new_path + "/" + dir)
					os.mkdir(new_path + "/" + dir)

				#load files
				for file in files:
					print(root+"/"+file, "->", new_path+"/"+file)
					ftp.ftp_host.download(root+"/"+file, new_path+"/"+file)


		os.mkdir("tmp/SimplePlayers")
		for lang in langs:
			path = "Games/{}/{}/example_ai".format(game_id, lang)
			os.mkdir("tmp/SimplePlayers/"+lang)
			for root, dirs, files in ftp.ftp_host.walk(path):
				if root.endswith("/example_ai"):
					new_path = root.replace(path, "tmp/SimplePlayers/"+lang+"/")
				else:
					new_path = root.replace(path, "tmp/SimplePlayers/"+lang)
				#make dirs
				for dir in dirs:
					print("MKDIR:", new_path + "/" + dir)
					os.mkdir(new_path + "/" + dir)

				#load files
				for file in files:
					print(root+"/"+file, "->", new_path+"/"+file)
					ftp.ftp_host.download(root+"/"+file, new_path+"/"+file)

		if ftp.ftp_host.path.exists("Games/"+game_id+"/info.pdf"):
			print("Games/"+game_id+"/info.pdf", "->", "tmp/info.pdf")
			ftp.ftp_host.download("Games/"+game_id+"/info.pdf", "tmp/info.pdf")
		else:
			print("[W] info.pdf not found")

		zipf = zipfile.ZipFile('tmp/data_container.zip', 'w')
		os.chdir("tmp")
		zipdir('AiLibraries', zipf)
		zipdir('SimplePlayers', zipf)
		if ftp.ftp_host.path.exists("Games/"+game_id+"/info.pdf"):
			zipf.write("info.pdf")
		os.chdir("..")
		zipf.close()

		ftp.ftp_host.upload("tmp/data_container.zip", "Games/"+game_id+"/data_container.zip")
		print("Uploaded ZIP to 'Games/"+game_id+"/data_container.zip'")


	try:
		f()
	except ftp.err:
		print("Failed...")

	gt = GameType.query.get(game_id)
	if not gt:
		print("Invalid ID.")

	gt.updated()


def manage(manager, app):
	@manager.command
	def clean_db():
		"Löscht die DB, und füllt sie mit Beispieldaten."
		if prompt_bool("Sicher, die DB zu leeren?"):
			with app.app_context():
				db.drop_all()
				if prompt_bool("Mit Daten füllen"):
					populate()

	@manager.command
	def sync_ftp():
		"Updated die Information von allen KIs zum FTP"
		for ai in AI.query.all():
			print("Syncing:", ai.name)
			try:
				ai.ftp_sync()
			except ftp.err:
				print("Failed to sync", ai.name)

	@manager.command
	def add_admin():
		"Fügt einen Admin hinzu und zeigt alle aktuellen Admins."
		print("Aktuelle Admins:")
		for admin in User.query.filter(User.admin == True).all():
			print(admin)
		print()
		if prompt_bool("Sicher, dass du einen neuen Admin hinzufügen willst?"):
			name = prompt("Name?")
			pw = prompt_pass("Passwort?")
			admin = User(name=name, admin=True)
			admin.set_pw(pw)
			db.session.add(admin)
			print(admin)

	@manager.command
	def make_data_container(game_id):
		"Packt die Beispiel-KIs und AILibs in einen data_container.zip zusammen"
		_make_data_container(game_id)


	@manager.command
	def compile_quali_ai():
		for gt in GameType.query.all():
			if not prompt_bool("Compile for '" + gt.name + "'?"):
				continue
			ai = Mock()
			ai.id = -gt.id
			ai.lang = Lang.query.filter(Lang.name == "Java").first()
			ai.type = gt
			ai.name = "QualiKi-"+gt.name
			v = ai.latest_version()
			v.version_id = 1
			v.qualified, v.compiled, v.frozen = True, True, True
			v.extras.return_value = []
			ai.version_list = [v]
			ai.ftp_sync = lambda: AI.ftp_sync(ai)
			ai.copy_example_code = lambda: AI.copy_example_code(ai)
			ai.copy_example_code()
			for data, event in backend.compile(ai):
				print(event, ":", data)


	@manager.command
	def recompile_ais():
		all = prompt_bool("Compile all?")
		for ai in AI.query.all():
			if all or prompt_bool("Compile '"+ai.name + "' by " + ai.user.name):
				if ai.latest_version().frozen:
					print("AI_Version is frozen")
					continue
				ai.latest_version().compiled = True
				print("Compiling", ai.name)
				for data, event in backend.compile(ai):
					print(event, ":", data)

	@manager.command
	def add_gametype():
		name = prompt("Name")
		default_viz = "vizs/" + name.lower() + ".html"
		viz = prompt("Visualisierungsdatei", default=default_viz)
		if not prompt_bool("Spieltyp " + name + " erstellen?"):
			return
		gt = GameType(name=name, viz=viz)
		db.session.add(gt)
		db.session.commit()


		@ftp.safe
		def f():
			paths = ["Games/"+str(gt.id)]
			for l in Lang.query:
				b = "Games/{}/{}".format(gt.id, l.name)
				paths.append(b)
				paths.append(b+"/ailib")
				paths.append(b+"/example_ai")
			for p in paths:
				print("MKDIR:", p)
				ftp.ftp_host.mkdir(p)

		f()
