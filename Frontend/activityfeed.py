import arrow

class ActivityFeed:
	feed = []

	def add(self, act):
		self.feed.append(act)
		self.feed = self.feed[-50:]

activity_feed = ActivityFeed()

class Activity:
	def __init__(self, msg, img=None, extratext=None):
		self.created = arrow.utcnow()
		self.msg = msg
		self.type = "simple"
		self.img = img
		self.extratext = extratext
		if self.extratext:
			self.extratext = self.extratext.replace("\n", "<br />")
		activity_feed.add(self)

	def time(self):
		return str(self.created.humanize(locale="de_DE"))

Activity("Feed angefangen", extratext="Dieser Feed zeigt viele 'wichtige' Aktivitäten, die seit dem letzten Neustart passiert sind.")
