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


def manage(manager, app, db, populate, AI, ftp, **_):
	@manager.command
	def clean_db():
		with app.app_context():
			db.drop_all()
			populate(5)

	@manager.command
	def sync_ftp():
		for ai in AI.query.all():
			print("Syncing:", ai.name)
			try:
				ai.ftp_sync()
			except ftp.err:
				print("Failed to sync", ai.name)

	@manager.command
	def create_simple_players(game_id):
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
