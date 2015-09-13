####################################################################################
# mail.py
#
# Copyright (C) 2015 Pixelgaffer
#
# This work is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as published by the
# Free Software Foundation; either version 2 of the License, or any later
# version.
#
# This work is distributed in the hope that it will be useful, but without
# any warranty; without even the implied warranty of merchantability or
# fitness for a particular purpose. See version 2 and version 3 of the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
####################################################################################
from _cfg import env

import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from jinja2 import Environment, PackageLoader
jinja_env = Environment(loader=PackageLoader('mail', 'templates'))

validation_template = jinja_env.get_template('mail_validation.html')
reset_template = jinja_env.get_template('mail_password_reset.html')


def send_validation(user):
	if user.validated:
		return False

	msg = MIMEMultipart('alternative')

	htmlpart = MIMEText(validation_template.render(user=user, env=env, html=True), 'html')
	plainpart = MIMEText(validation_template.render(user=user, env=env, html=False), 'plain')

	msg.attach(plainpart)
	msg.attach(htmlpart)

	msg['Subject'] = 'Turnierserver Account freischaltung'
	msg['From'] = env.mail_address
	msg['To'] = user.email
	print(msg.as_string())

	try:
		s = smtplib.SMTP(env.mail_server)
		s.starttls()
		s.login(env.mail_address, env.mail_password)
		s.sendmail(env.mail_address, [user.email], msg.as_string())
		s.quit()
		return True
	except smtplib.SMTPException as e:
		print(e)
		return False



def reset_password(user):

	msg = MIMEMultipart('alternative')

	htmlpart = MIMEText(reset_template.render(user=user, env=env, html=True), 'html')
	plainpart = MIMEText(reset_template.render(user=user, env=env, html=False), 'plain')

	msg.attach(plainpart)
	msg.attach(htmlpart)

	msg['Subject'] = 'Turnierserver - Passwortreset'
	msg['From'] = env.mail_address
	msg['To'] = user.email
	print(msg.as_string())

	try:
		s = smtplib.SMTP(env.mail_server)
		s.starttls()
		s.login(env.mail_address, env.mail_password)
		s.sendmail(env.mail_address, [user.email], msg.as_string())
		s.quit()
		return True
	except smtplib.SMTPException as e:
		print(e)
		return False
