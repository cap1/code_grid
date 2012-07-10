package chemtrail;

import org.gridlab.gat.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.gridlab.gat.URI;


/**
 * Interface to communicate with JavaGAT.
 * @author ralph.krimmel, christian.mueller6
 *
 */
public interface Gatfs {
	void createFile(URI FileName) throws Exception; //check
	void createFile(URI FileName, InputStream data) throws Exception;
	void createDir(URI DirName) throws Exception; //check 
	ArrayList<URI> readDir(URI DirName) throws Exception; //check
	void deleteFile(URI FileName) throws Exception; //check
	void updateFile(URI FileName, InputStream data) throws Exception;
	void readFile(URI FileName, OutputStream Data) throws Exception; 
	void deleteDir(URI DirName, boolean recursive) throws FileNotFoundException,DirectoryNotEmptyException,Exception; //check
	void renameDir(URI OldDirName, URI NewDirName) throws FileNotFoundException,DirectoryNotEmptyException,Exception; //check
	void renameFile(URI OldFileName, URI NewFileName) throws Exception; //check
	void copyFile(URI FromFile, URI ToFile) throws Exception;
	void moveFile(URI FromFile, URI ToFile) throws Exception;
	void copyDir(URI FromDir, URI ToDir) throws Exception;
	void moveDir(URI FromDir, URI ToDir) throws Exception;
	boolean isFile(URI FileName);
	boolean isDirectory(URI DirName);
	boolean exists(URI path);
	long getSize(URI FileName);
	String getBaseName(URI FileName);
	
}
