#!/usr/bin/env python
import httplib2, pprint, urllib

url = 'http://handle.gwdg.de:8080/pidservice/write/create'


# Creates a new HTPP Object
http = httplib2.Http()

# User login 
delicious_user = 'demo'
delicious_pass = '****'
http.add_credentials(delicious_user, delicious_pass)

# 
PID_url = "http://handle.gwdg.de/javadocs/"
PID_suffix = ""
PID_encoding = "xml"

# PID to URL 
params = urllib.urlencode({
    'url': PID_url,
    'suffix': PID_suffix,
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
