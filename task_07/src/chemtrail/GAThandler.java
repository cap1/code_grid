package chemtrail;

import java.io.*;


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
		// TODO Auto-generated method stub
		GATContext context = this.generateGATcontext();
		try {
			File file = GAT.createFile(context,FileName.getPath());
		} catch (GATObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	@Override
	public void createDir() {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFile() {
		// TODO Auto-generated method stub

	}

	@Override
	public void readDir() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFile() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteDir() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFile() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renameDir() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renameFile() {
		// TODO Auto-generated method stub

	}

}
