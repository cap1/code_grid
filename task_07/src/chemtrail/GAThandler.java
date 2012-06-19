package chemtrail;

import java.io.*;
import java.util.ArrayList;


import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.Preferences;
import org.gridlab.gat.URI;
import org.gridlab.gat.security.CredentialSecurityContext;

public class GAThandler implements gatfs {

	private byte[] proxyCredentialBytes = null;
	private Preferences prefs = null;

	public GAThandler() {
		//set proxy credential bytes here
		
		Preferences prefs = new Preferences();
		prefs.put("File.adaptor.name", "gridftp");
		
	}
	
	public GAThandler(String certFileName, String adaptorname)
	{
		Preferences prefs = new Preferences();
		prefs.put("File.adaptor.name", adaptorname);
		this.proxyCredentialBytes = readCertificate(certFileName);
		
	
	}
	
	
	//reads the certificate file and returns it as byte datastream
	private byte[] readCertificate(String FileName) {
		

		File file = new File(FileName);
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			
			while((text = reader.readLine()) != null ) {
				contents.append(text).append(System.getProperty("line.separator"));
				
			} 
			
		} catch (FileNotFoundException e) {
			System.out.println("Could not read Certificate file. Is the path correct?");
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not read Certificate file. Is the path correct?");
			//e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return contents.toString().getBytes();
	}
	
	
	public byte[] getProxyCredentialBytes() {
		return proxyCredentialBytes;
	}


	public void setProxyCredentialBytes(byte[] proxyCredentialBytes) {
		this.proxyCredentialBytes = proxyCredentialBytes;
	}


	public String getAdaptorname() {
		return (String) this.prefs.get("File.adaptor.name");
	}


	public void setAdaptorname(String adaptorname) {
		this.prefs.put("File.adaptor.name", adaptorname);
	}	
	
	
	private GATContext generateGATcontext()
	{
		GATContext gatContext = new GATContext();
		gatContext.addPreferences(prefs);
		
		CredentialSecurityContext securityContext = new 
				CredentialSecurityContext(this.getProxyCredentialBytes());
		
		gatContext.addSecurityContext(securityContext);
		
		return gatContext;
		
	}
	
	
	@Override
	public void createFile(URI FileName) {
		GATContext context = this.generateGATcontext();
		try {
			File file = GAT.createFile(context,FileName);
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		}
		GAT.end();
	}

	/* (non-Javadoc)
	 * @see chemtrail.gatfs#createDir(org.gridlab.gat.URI)
	 */
	@Override
	public void createDir(URI DirName) throws Exception {
		GATContext context = this.generateGATcontext();
		try {
			File file = GAT.createFile(context,DirName);
			file.mkdir();
		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		}
		GAT.end();
	}

	/* (non-Javadoc)
	 * @see chemtrail.gatfs#deleteDir(org.gridlab.gat.URI)
	 */
	@Override
	public void deleteDir(URI DirName) throws Exception {
		GATContext context = this.generateGATcontext();
		try {
			File file = GAT.createFile(context,DirName);
			if(file.isDirectory() ) {
				file.delete();
				
			}
		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		}
		GAT.end();
	}

	/* (non-Javadoc)
	 * @see chemtrail.gatfs#deleteFile(org.gridlab.gat.URI)
	 */
	@Override
	public void deleteFile(URI FileName) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see chemtrail.gatfs#readDir(org.gridlab.gat.URI)
	 */
	@Override
	public ArrayList<URI> readDir(URI DirName) throws Exception {
		GATContext context = this.generateGATcontext();
		ArrayList<URI> result = new ArrayList<URI>();
		try {
			File dir = GAT.createFile(context, DirName);
			if(dir.isDirectory()) {
				for (int i = 0; i < dir.list().length; i++) {
					result.add(new URI(dir.list()[i]));
				}
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		GAT.end();
		return result;
	}

	/* (non-Javadoc)
	 * @see chemtrail.gatfs#readFile(org.gridlab.gat.URI)
	 */
	@Override
	public Object readFile(URI FileName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see chemtrail.gatfs#renameDir(org.gridlab.gat.URI, org.gridlab.gat.URI)
	 */
	@Override
	public void renameDir(URI OldDirName, URI NewDirName) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see chemtrail.gatfs#renameFile(org.gridlab.gat.URI, org.gridlab.gat.URI)
	 */
	@Override
	public void renameFile(URI OldFileName, URI NewFileName) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see chemtrail.gatfs#updateFile(org.gridlab.gat.URI, java.lang.Object)
	 */
	@Override
	public void updateFile(URI Filename, Object Data) throws Exception {
		// TODO Auto-generated method stub
		
	}

}