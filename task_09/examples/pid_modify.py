#!/usr/bin/env python
import httplib2, pprint, urllib

url = 'http://handle.gwdg.de:8080/pidservice/write/modify'


# Creates a new HTPP Object
http = httplib2.Http()

# User login 
delicious_user = 'demo'
delicious_pass = '****'
http.add_credentials(delicious_user, delicious_pass)

# 
new_PID_url = "http://handle.gwdg.de/javadocs/"
old_PID_title = "PIDService Documentation page"
new_PID_title = "PIDService Documentation"
PID_encoding = "xml"

# PID to URL 
params = urllib.urlencode({
    'pid':"11858/00-ZZZZ-0000-0001-4743-4",
    'url': new_PID_url,
    'oldtitle': old_PID_title,
    'newtitle': new_PID_title,
    'encoding': PID_encoding
})
print "---Request---"
print 'Encoded parameters:', params

response, content = http.request(url, 'POST', params,
    headers={'Content-type': 'application/x-www-form-urlencoded'}
)

# Print response to screen
print "---Content---"
pprint.pprint(content)
print "---Response---"
pprint.pprint(response)
	
