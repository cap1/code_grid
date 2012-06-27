package chemtrail;

public class DirectoryNotEmptyException extends Exception {

	private String dirname;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DirectoryNotEmptyException(String DirectoryName)
	{
		this.dirname = DirectoryName;
	}
	
	public String toString(){
        return "Directory " + this.getDirname() + " is not empty, cannot be deleted without the recursive flag";
    }

	/**
	 * @return the dirname
	 */
	public String getDirname() {
		return dirname;
	}

	/**
	 * @param dirname the dirname to set
	 */
	public void setDirname(String dirname) {
		this.dirname = dirname;
	}

}
