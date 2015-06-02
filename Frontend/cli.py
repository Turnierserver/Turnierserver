from flask.ext.script import prompt_bool, prompt, prompt_pass
from database import db, populate, AI, User, ftp

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
	def create_simple_players(game_id):
		"Packt die Beispiel-KIs in eine SimplePlayers.zip zusammen"
		#in der ESU:
		#SimplePlayer/Java/
		#			/Python/
		#im FTP:
		#Games/1/Java/example_ai
		#		Python/example_ai
		clean_tmp()
		@ftp.safe
		def f():
			os.mkdir("tmp/SimplePlayer")
			for lang in ["Python", "Java"]:
				path = "Games/{}/{}/example_ai".format(game_id, lang)
				os.mkdir("tmp/SimplePlayer/"+lang)
				for root, dirs, files in ftp.ftp_host.walk(path):
					if root.endswith("/example_ai"):
						new_path = root.replace(path, "tmp/SimplePlayer/"+lang+"/")
					else:
						new_path = root.replace(path, "tmp/SimplePlayer/"+lang)
					#make dirs
					for dir in dirs:
						print("MKDIR:", new_path + "/" + dir)
						os.mkdir(new_path + "/" + dir)

					#load files
					for file in files:
						print(root+"/"+file, "->", new_path+"/"+file)
						ftp.ftp_host.download(root+"/"+file, new_path+"/"+file)

			zipf = zipfile.ZipFile('tmp/simple_players.zip', 'w')
			os.chdir("tmp")
			zipdir('SimplePlayer', zipf)
			os.chdir("..")
			zipf.close()

			ftp.ftp_host.upload("tmp/simple_players.zip", "Games/"+game_id+"/simple_players.zip")
			print("Uploaded ZIP to 'Games/"+game_id+"/simple_players.zip'")


		try:
			f()
		except ftp.err:
			print("Failed...")


	@manager.command
	def create_ai_library(game_id):
		"Packt die Beispiel-KIs in eine AiLibrary.zip zusammen"
		#in der ESU:
		#AiLibrary/Java/
		#			/Python/
		#im FTP:
		#Games/1/Java/example_ai
		#		Python/example_ai
		clean_tmp()
		@ftp.safe
		def f():
			os.mkdir("tmp/AiLibrary")
			for lang in ["Python", "Java"]:
				path = "Games/{}/{}".format(game_id, lang)
				os.mkdir("tmp/AiLibrary/"+lang)
				for root, dirs, files in ftp.ftp_host.walk(path):
					new_path = root.replace(path, "tmp/AiLibrary/"+lang)
					#make dirs
					for dir in dirs:
						print("MKDIR:", new_path + "/" + dir)
						os.mkdir(new_path + "/" + dir)

					#load files
					for file in files:
						print(root+"/"+file, "->", new_path+"/"+file)
						ftp.ftp_host.download(root+"/"+file, new_path+"/"+file)

			zipf = zipfile.ZipFile('tmp/AiLibrary.zip', 'w')
			os.chdir("tmp")
			zipdir('AiLibrary', zipf)
			os.chdir("..")
			zipf.close()

			ftp.ftp_host.upload("tmp/AiLibrary.zip", "Games/"+game_id+"/AiLibrary.zip")
			print("Uploaded ZIP to 'Games/"+game_id+"/AiLibrary.zip'")


		try:
			f()
		except ftp.err:
			print("Failed...")
