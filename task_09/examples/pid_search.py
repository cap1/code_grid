import httplib2, pprint, urllib,sys

def args():
    for arg in sys.argv:
        print sys.argv[0]

        try: 
            print sys.argv[1]
            generate_dir_md5(sys.argv[1])
        except IndexError:
            print "No arguments - generate MD5s for the CWD"
            try:
                generate_dir_md5(os.getcwd())
            except:
                print"(Warning) Could not generate MD5 for CWD"

	except:
            print"(Warning) Could not generate MD5"
#        finally:
#            print "Number of warnings :"
#            print"- = Fin = -"
    

# Creates a new HTPP Object
http = httplib2.Http()

# User login 
delicious_user = 'demo'
delicious_pass = '****'
http.add_credentials(delicious_user, delicious_pass)

# Handle URL for "CREATE" service
#url = 'http://handle.gwdg.de:8080/pidservice/read/search'
url = 'http://handle.gwdg.de:8080/pidservice/read/view'

# PID Infos
PID_encoding = "xml"

# PID to URL  (pid=11858%2F00-ZZZZ-0000-0001-4743-4?showmenu=yes)
params3 = urllib.urlencode({
    'pid':"11858/00-ZZZZ-0000-0001-4743-4",
    'showmenu':"yes",
    'encoding': PID_encoding,
    'proxyview':"yes"		### shows the content of the *HANDLE* via Handle proxy site
#    'redirect':"yes"		### shows the original content (redirected to the url)
})
#  Connect to the Service
print "---Request URL---"
print 'Service URL:', url
print "---Request parameters---"
print 'Encoded parameters:', params3
#response, content = http.request(url, method="GET", body=params3,
#    headers={'Content-type': 'application/x-www-form-urlencoded'})
response, content = http.request(url+'?'+params3, method="GET")
#print urllib.urlopen(url+params3).read()

print "---Status---"
if response['status'] == "200":
    try:
        print "All O.k. ..."
        print "PID Location: ", response['location']
    except: 
        print "(Error) No PID Redirect"

if response['status'] == "403": 
        print "(Error 403) Forbidden (Maybe there exists already a PID for this object?) " 
        
if response['status'] == "404":
        print "(Error 404) Not Found" 
        
if response['status'] == "405":
        print "(Error 405) Not Allowed" 
        
#print "Status: ",response['status']

# Print response to screen
print "---Response Header---"
pprint.pprint(response)
print "---Response Content---"
pprint.pprint(content)
