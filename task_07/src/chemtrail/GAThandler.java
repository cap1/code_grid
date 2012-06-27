package chemtrail;


import chemtrail.DirectoryNotEmptyException;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.gridlab.gat.io.File;
import org.gridlab.gat.io.FileInputStream;
import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.Preferences;
import org.gridlab.gat.URI;
import org.gridlab.gat.security.CredentialSecurityContext;

public class GAThandler implements Gatfs {

	private byte[] proxyCredentialBytes = null;
	private Preferences prefs = null;
	private final static boolean verbose = true;

	public GAThandler() {
		// set proxy credential bytes here

		Preferences prefs = new Preferences();
		prefs.put("File.adaptor.name", "gridftp");

	} 
	
	/**
	 * @return the verbose
	 */
	public static boolean isVerbose() {
		return verbose;
	}

	public GAThandler(String certFileName, String adaptorname) {
		Preferences prefs = new Preferences();
		prefs.put("File.adaptor.name", adaptorname);
		this.proxyCredentialBytes = readCertificate(certFileName);

	}

	// reads the certificate file and returns it as byte datastream
	private byte[] readCertificate(String FileName) {

		
		java.io.File file = new java.io.File(FileName);
		try {
			java.io.FileInputStream  proxyFileReader = new java.io.FileInputStream(file);
			byte [] proxyCredentialBytes = new byte[(int)file.length()];
			try {
				proxyFileReader.read(proxyCredentialBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return proxyCredentialBytes;
		 
	}

	

	private static int pipeData(InputStream in, OutputStream out)
			throws IOException {
		int buffer = 0;
		int bytecount = 0;
		while ((buffer = in.read()) != -1) {
			out.write(buffer);
			bytecount++;
		}
		return bytecount;
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

	private GATContext generateGATcontext() {
		GATContext gatContext = new GATContext();
		gatContext.addPreferences(prefs);

		CredentialSecurityContext securityContext = new CredentialSecurityContext(
				this.getProxyCredentialBytes());
		gatContext.addSecurityContext(securityContext);

		return gatContext;

	}

	
	public void createFile(URI FileName) {
		
		if (verbose) System.out.println("Trying to create file " + FileName);
		GATContext context = this.generateGATcontext();
		
		try {
			File file = GAT.createFile(context, FileName);
			try {
				boolean result = file.createNewFile();
				if(verbose && result) {
					System.out.println("Successfully created file " + FileName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		}
		GAT.end();
	}
	
	public void createFile(URI FileName, InputStream data) throws IOException {
		//create the new File
		createFile(FileName);
		
		//add content to the file
		GATContext context = this.generateGATcontext();
		File file = null;
		try {
			file = GAT.createFile(context, FileName);
		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		}
		org.gridlab.gat.io.FileOutputStream fileOutput = null;
		try {
			if (!file.exists())
				throw new IOException("File does not exist: " + FileName);
			fileOutput = GAT.createFileOutputStream(context, FileName);
			int count = pipeData(data, fileOutput);
			if (verbose) System.out.println("Copied " + count + " bytes into " + FileName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileOutput != null) {
				fileOutput.close();
			}
			GAT.end();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chemtrail.gatfs#createDir(org.gridlab.gat.URI)
	 */
	public void createDir(URI DirName) throws Exception {
		GATContext context = this.generateGATcontext();
		if (verbose) System.out.println("Trying to create dir " + DirName);
		try {
			File file = GAT.createFile(context, DirName);
			boolean result = file.mkdir();
			if(verbose && result) {
				System.out.println("Successfully created dir " + DirName);
			}
		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		GAT.end(); 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chemtrail.gatfs#deleteDir(org.gridlab.gat.URI)
	 */
	//@Override
	public void deleteDir(URI DirName, boolean recursive) throws Exception {
		if (verbose ) System.out.println("Trying to delete dir: " + DirName);
		GATContext context = this.generateGATcontext();
		try {
			File file = GAT.createFile(context, DirName);
			if (!file.exists()) {
				throw new FileNotFoundException();
			} else {
				if (file.isDirectory()) {
					if (recursive) {
						file.recursivelyDeleteDirectory();
					} else {
						if (file.list().length == 0) {
							file.delete();
						} else {
							throw new DirectoryNotEmptyException(file.getPath());
						}
					}
				}
			}
		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		}
		GAT.end();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chemtrail.gatfs#deleteFile(org.gridlab.gat.URI)
	 */
	//@Override
	public void deleteFile(URI FileName) throws Exception {
		GATContext context = this.generateGATcontext();
		if (verbose) System.out .println("Trying to delete file: " + FileName);

		try {
			File file = GAT.createFile(context, FileName);
			if (!file.exists()) {
				throw new FileNotFoundException();
			}
			file.delete();
		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		}
		GAT.end();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chemtrail.gatfs#readDir(org.gridlab.gat.URI)
	 */
	//@Override
	public ArrayList<URI> readDir(URI DirName) throws Exception {
		if (verbose) System.out.println("Reading content of dir: " + DirName);
		GATContext context = this.generateGATcontext();
		ArrayList<URI> result = new ArrayList<URI>();
		try {
			File dir = GAT.createFile(context, DirName);
			if (dir.isDirectory()) {
				for (int i = 0; i < dir.list().length; i++) {
					result.add(new URI(dir.list()[i]));
					if (verbose) System.out.println(dir.list()[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		GAT.end();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chemtrail.gatfs#renameDir(org.gridlab.gat.URI, org.gridlab.gat.URI)
	 */
	//@Override
	public void renameDir(URI OldDirName, URI NewDirName) throws Exception {
		GATContext context = this.generateGATcontext();

		try {
			File oldFile = GAT.createFile(context, OldDirName);
			if (!oldFile.exists()) {
				throw new FileNotFoundException();
			} else {any://" + host + "/tmp
				// rename != move !!
				if (oldFile.isDirectory()
						&& OldDirName.getHost().equals(NewDirName.getHost())) {
					File newFile = GAT.createFile(context, NewDirName);
					oldFile.renameTo(newFile);
				}
			}

		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		}
		GAT.end();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chemtrail.gatfs#renameFile(org.gridlab.gat.URI, org.gridlab.gat.URI)
	 */
	//@Override
	public void renameFile(URI OldFileName, URI NewFileName) throws Exception {
		GATContext context = this.generateGATcontext();
		try {
			File file = GAT.createFile(context, OldFileName);
			if (!file.exists()) {
				throw new FileNotFoundException("File not found");
			}
			file.copy(NewFileName);
		} catch (GATObjectCreationException e) {
			e.printStackTrace();
		}
		GAT.end();
	}

	//@Override
	public void readFile(URI FileName, OutputStream data) throws Exception {
		GATContext context = this.generateGATcontext();

		File file = GAT.createFile(context, FileName);

		FileInputStream fileInput = null;
		try {
			if (!file.exists())
				throw new IOException("File does not exist: " + FileName);
			fileInput = GAT.createFileInputStream(file);
			pipeData(fileInput, data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileInput != null) {
				fileInput.close();
			}
			GAT.end();
		}
	}

	//@Override
	public void updateFile(URI FileName, InputStream data) throws Exception {

		deleteFile(FileName);
		createFile(FileName, data);
	}
}