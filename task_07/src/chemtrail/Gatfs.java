package chemtrail;

import java.io.InputStream;
import java.util.ArrayList;

import org.gridlab.gat.URI;



public interface Gatfs {
	void createFile(URI FileName) throws Exception; //check
	void createDir(URI DirName) throws Exception; //check 
	//Lieber stream als parameter
	void readFile(URI FileName, InputStream Data) throws Exception; 
	ArrayList<URI> readDir(URI DirName) throws Exception; //check
	void deleteFile(URI FileName) throws Exception;
	void updateFile(URI Filename, InputStream Data) throws Exception;
	void renameDir(URI OldDirName, URI NewDirName) throws Exception;
	void renameFile(URI OldFileName, URI NewFileName) throws Exception;
	void deleteDir(URI DirName, boolean recursive) throws Exception;
	
}
