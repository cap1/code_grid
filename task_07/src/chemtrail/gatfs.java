package chemtrail;

import org.gridlab.gat.URI;



public interface gatfs {
	void createFile(URI FileName);
	void createDir();
	void readFile();
	void readDir();
	void deleteFile();
	void deleteDir();
	void updateFile();
	void renameDir();
	void renameFile();
}
