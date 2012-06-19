package chemtrail;

import java.util.ArrayList;

import org.gridlab.gat.URI;



public interface gatfs {
	void createFile(URI FileName) throws Exception; //check
	void createDir(URI DirName) throws Exception; //check 
	Object readFile(URI FileName) throws Exception; 
	ArrayList<URI> readDir(URI DirName) throws Exception; //check
	void deleteFile(URI FileName) throws Exception;
	void deleteDir(URI DirName) throws Exception;
	void updateFile(URI Filename, Object Data) throws Exception;
	void renameDir(URI OldDirName, URI NewDirName) throws Exception;
	void renameFile(URI OldFileName, URI NewFileName) throws Exception;
}
