from flask.ext.script import prompt_bool, prompt, prompt_pass
from database import db, populate, AI, User, GameType, ftp

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


def manage(manager, app):
	@manager.command
	def clean_db():
		"Löscht die DB, und füllt sie mit Beispieldaten."
		if prompt_bool("Sicher, die DB zu leeren?"):
			with app.app_context():
				db.drop_all()
				if prompt_bool("Mit Fakedaten füllen"):
					populate(5)

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

			print("Games/"+game_id+"/info.pdf", "->", "tmp/info.pdf")
			ftp.ftp_host.download("Games/"+game_id+"/info.pdf", "tmp/info.pdf")

			zipf = zipfile.ZipFile('tmp/data_container.zip', 'w')
			os.chdir("tmp")
			zipdir('AiLibraries', zipf)
			zipdir('SimplePlayers', zipf)
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
