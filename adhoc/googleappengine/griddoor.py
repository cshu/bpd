import webapp2
import jinja2
import os
import re
import urllib2

jinja_environment = jinja2.Environment(loader=jinja2.FileSystemLoader(os.path.dirname(__file__)))
jinja_environment.add_extension('jinja2.ext.loopcontrols')
jinja_environment.globals.update(str=str)




class MainPage(webapp2.RequestHandler):
	def get(self):
		template = jinja_environment.get_template('griddoor.html')
		self.response.out.write(template.render({'redirectedLinks':[]}))
	
	def post(self):
		redirectedLinks=[]
		matchLines=re.compile('http.*?$|http.*?\\n').findall(self.request.get('links'))
		
		for iterVar in matchLines:
			url=str(iterVar)
			#raise Exception(str(iterVar))
			req = urllib2.Request(url)
			req.add_header('User-Agent', 'Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3')
			response = urllib2.urlopen(req)
			link=response.read()
			response.close()
			match=re.compile('[\\\\\'"]https?:.*?[\\\\\'"]').findall(link)
			output=str(match[0])
			outputr=output[1:-1]
			#raise Exception(output[1:-1])
			redirectedLinks.append(outputr)
		template = jinja_environment.get_template('griddoor.html')
		self.response.out.write(template.render({'redirectedLinks':redirectedLinks}))

application = webapp2.WSGIApplication([
	('/', MainPage)
], debug=False)
