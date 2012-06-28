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

/**
 * Implements file and folder operations using JavaGAT by implementing Gatfs.
 * 
 * @author ralph.krimmel, christian.mueller6
 */

public class GAThandler implements Gatfs {
	//bytefield to handle the proxycertificate
	private byte[] proxyCredentialBytes = null;
	//preference object for the GAThandler
	private Preferences prefs = null;
	//enable verbose output during operations on System.out
	private final static boolean verbose = true;

	public GAThandler(String certFileName, String adaptorname) {
		Preferences prefs = new Preferences();
		prefs.put("File.adaptor.name", adaptorname);
		this.proxyCredentialBytes = readCertificate(certFileName);
	}	
	
	/**
	 * Read the proxy-certificate from the File to a byte field.
	 * JavaGat requires the certificate as a byte[] format
	 * 
	 * @param FileName Path to the certificate to read from
	 * 
	 * @return byte[] with the contents of FileName
	 */
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

	/**
	 * Pipes data from the in to out.
	 * Reads data from the InputStream and writes it to the output 
	 * and counts the bytes piped.
	 *  
	 * @param in InputStream to read from
	 * @param out OutputStream to write to
	 * 
	 * @return number of bytes piped
	 */
	private static int pipeData(InputStream in, OutputStream out) throws IOException {
		int buffer = 0;
		int bytecount = 0;
		while ((buffer = in.read()) != -1) {
			out.write(buffer);
			bytecount++;
		}
		return bytecount;
	}

	/**
	 * Gets the proxy certificate as a byte[].
	 * @return proxycertificate as byte[]
	 */
	public byte[] getProxyCredentialBytes() {
		return proxyCredentialBytes;
	}

	/**
	 * Sets the proxy certificate.
	 * @param proxyCredentialBytes proxy certificate as byte[]
	 */
	public void setProxyCredentialBytes(byte[] proxyCredentialBytes) {
		this.proxyCredentialBytes = proxyCredentialBytes;
	}

	/**
	 * Gets the name of the Adaptor as defined in the "File.adaptor.name".
	 * @return String containing the current adaptor name
	 */
	public String getAdaptorname() {
		return (String) this.prefs.get("File.adaptor.name");
	}

	/**
	 * Sets the "File.adaptor.name" in the Preferences object.
	 * @param adaptorname name of the adaptor to use
	 */
	public void setAdaptorname(String adaptorname) {
		this.prefs.put("File.adaptor.name", adaptorname);
	}

	/**
	 * Create a GATContext with the set proxy certificate and Preference.
	 * @return a GATContext with Certificate and Preferences
	 * 
	 */
	private GATContext generateGATcontext() {
		GATContext gatContext = new GATContext();
		//add prefrerences
		gatContext.addPreferences(prefs);

		//add proxy certificate
		CredentialSecurityContext securityContext = new CredentialSecurityContext(
				this.getProxyCredentialBytes());
		gatContext.addSecurityContext(securityContext);

		return gatContext;
	}

	/**
	 * Create an empty file.
	 * 
	 * @param FileName Path to the file to create
	 */
	public void createFile(URI FileName) {
		if (verbose) System.out.println("Trying to create file " + FileName);
		GATContext context = this.generateGATcontext();
		
		try {
			File file = GAT.createFile(context, FileName);
			try {
				//create the file and test if successfull
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
	
	/**
	 * Create a file and fill it with data.
	 * 
	 * @param FileName Path to the file to create
	 * @param data Data to write to the newly create file
	 * 
	 */
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
			if (!file.exists()) throw new IOException("File does not exist: " + FileName);
			
			//write to the file
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

	/**
	 * Create a new directory.
	 * 
	 * @param DirName Path to the directory to create
	 * @see chemtrail.gatfs#createDir(org.gridlab.gat.URI)
	 */
	public void createDir(URI DirName) throws Exception {
		GATContext context = this.generateGATcontext();
		if (verbose) System.out.println("Trying to create dir " + DirName);
		try {
			//create the directory and test for success
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

	/**
	 * Remove a directory.
	 * 
	 * @param DirName Path to the directory to remove
	 * @param recursive if true, behaves like "rm -r", otherwise like "rmdir"
	 * 
	 * @see chemtrail.gatfs#deleteDir(org.gridlab.gat.URI)
	 */
	public void deleteDir(URI DirName, boolean recursive) throws Exception {
		if (verbose ) System.out.println("Trying to delete dir: " + DirName);
		GATContext context = this.generateGATcontext();
		try {
			File file = GAT.createFile(context, DirName);
			if (!file.exists()) {
				throw new FileNotFoundException();
			} else {
				if (file.isDirectory()) {
					//rm -r
					if (recursive) {
						file.recursivelyDeleteDirectory();
					} else {
						if (file.list().length == 0) {
							//rmdir
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

	/**
	 * Delete a file.
	 * 
	 * @param FileName Path to the file to delete
	 * 
	 * @see chemtrail.gatfs#deleteFile(org.gridlab.gat.URI)
	 */
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

	/**
	 * Read the contents of a given directory.
	 * 
	 * @param DirName Directory whose contents shall be listed
	 * @return A list of the contents
	 * 
	 * @see chemtrail.gatfs#readDir(org.gridlab.gat.URI)
	 */
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

	/**
	 * Rename the given directory on the same host.
	 * This is a rename, not a move.
	 * 
	 * @param OldDirName Path to the directory that shall be renamed
	 * @param NewDirName Path to the directorys new name
	 * 
	 * @see chemtrail.gatfs#renameDir(org.gridlab.gat.URI, org.gridlab.gat.URI)
	 */
	public void renameDir(URI OldDirName, URI NewDirName) throws Exception {
		GATContext context = this.generateGATcontext();

		try {
			File oldFile = GAT.createFile(context, OldDirName);
			if (!oldFile.exists()) {
				throw new FileNotFoundException();
			} else {
				// rename != move
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

	/**
	 * Rename the given file.
	 * 
	 * @param OldFileName Path to the file to be renamed
	 * @param NewFileName Path the file shall be renamed to
	 * 
	 * @see chemtrail.gatfs#renameFile(org.gridlab.gat.URI, org.gridlab.gat.URI)
	 */
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

	/**
	 * Read the contents of a file.
	 * 
	 * @param FileName Path to the file to read
	 * @param data Stream to read the file contents to
	 */
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

	/**
	 * Update the content of a file.
	 * Dismisses the file content and sets the file content as given from the stream.
	 * 
	 * @param FileName Path to the file whose contents shall be updated
	 * @param data Stream of updated content for the file
	 */
	public void updateFile(URI FileName, InputStream data) throws Exception {
		deleteFile(FileName);
		createFile(FileName, data);
	}

	public boolean isDirectory(URI DirName) {
		GATContext context = this.generateGATcontext();
		File file = null;
		
		try {
			file = GAT.createFile(context, DirName);
		} catch (GATObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file.isDirectory();
	}

	public boolean isFile(URI FileName) {
	
		GATContext context = this.generateGATcontext();
		File file=null;
		try {
			file = GAT.createFile(context, FileName);
			
		} catch (GATObjectCreationException e) {
			// TODO Auto-generated catch block
		}
		return file.isFile();
	}
}