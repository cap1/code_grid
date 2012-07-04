package de.unigoe.sub.fe.goegrid.practicalcourse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gridlab.gat.URI;

import chemtrail.GAThandler;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.FolderResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * Represents a Folder in a Grid.
 * 
 * @author ralph.krimmel, christian.mueller
 */
public class GridFolderResource implements FolderResource {
	
	//path to the directory
	URI dirName;
	//handler to communicate with JavaGAT
	GAThandler gatfs;
	//determine if verbose output is desired
	private static final boolean verbose = true;
	
	/**
	 * Construct a new Folder Ressource on the Grid
	 * 
	 * @param gatfs JavaGAT handler to communicate with the grid
	 * @param dirName Path to the directory to work with
	 */
	GridFolderResource(	GAThandler gatfs, URI dirName) {
		this.dirName = dirName;
		this.gatfs = gatfs;
		if(verbose) System.out.println("Creating Folder Resource: " + dirName);
	}

	/**
	 * Create a new GridFolderRessource.
	 * 
	 * @param name name of the new resource
	 * @param content 
	 * @param length
	 * @param type
	 * 
	 * @see com.bradmcevoy.http.PutableResource#createNew(java.lang.String,
	 * java.io.InputStream, java.lang.Long, java.lang.String)
	 */
	public Resource createNew(String name, InputStream content, Long length,
			String type) throws IOException, ConflictException,
			NotAuthorizedException, BadRequestException {
		GridFolderResource result = null;
		//TODO
		try {
			 result = new GridFolderResource(gatfs,new URI(name));
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Create a new Directory.
	 * 
	 * @param name Name of the new Directory
	 * 
	 * @see com.bradmcevoy.http.MakeCollectionableResource#createCollection(java.
	 * lang.String)
	 */
	public CollectionResource createCollection(String name)
			throws NotAuthorizedException, ConflictException,
			BadRequestException {
		// TODO: implement
		//throw new UnsupportedOperationException("not implemented yet");
		CollectionResource result = null;
		return result;
	}

	/**
	 * Get the resource described by the given name.
	 *
	 * @param name name of the resource to obtain
	 * @return the resource described by the given name. 
	 *  
	 * @see com.bradmcevoy.http.CollectionResource#child(java.lang.String)
	 */
	public Resource child(String name) {
		if(verbose) System.out.println("Trying to get item " + name);
		URI item = null;
		try {
			item = new URI(name);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		//check if file or directory
		if( gatfs.isDirectory(item)) {
			GridFolderResource gfs = new GridFolderResource(gatfs,item);
			return gfs;
		}
		else {
			GridFileResource gfs = new GridFileResource(gatfs,item);
			return gfs;
		}
	}

	/**
	 * Returns a list of all children in the GridfolderResource.
	 * 
	 * @return List of the Children.
	 * 
	 * @see com.bradmcevoy.http.CollectionResource#getChildren()
	 */
	public List<? extends Resource> getChildren() {
		
		ArrayList<URI> content;
		List<Resource> result = new ArrayList<Resource>();
		if(verbose) System.out.println("Trying to read directory " + this.dirName);
		try {
			content = gatfs.readDir(this.dirName);
			for(Iterator<URI> it = content.iterator();it.hasNext();)
			{
				URI item = it.next();
				
				URI absoluteItem = new URI(this.dirName.toString() + item);
				
				if( gatfs.isDirectory(absoluteItem)) {
					GridFolderResource gfs = new GridFolderResource(gatfs,absoluteItem);
					result.add(gfs);
				}
				else if (gatfs.isFile(absoluteItem)) {
					GridFileResource gfs = new GridFileResource(gatfs,absoluteItem);
					result.add(gfs);
				}
				else {
						System.out.println("Problem with " + absoluteItem);
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;		
	}

	/**
	 * Get the basename of the Directory.
	 * 
	 * @return basename of the Directory
	 * 
	 * @see com.bradmcevoy.http.Resource#getName()
	 */
	public String getName() {
		return gatfs.getBaseName(this.dirName);		
	}

	/**
	 * Get the unique ID of the resource
	 * 
	 * @return the unique ID of the resource, described by the path.
	 * 
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	public String getUniqueId() {
		return this.dirName.getPath();	
	}

	/**
	 * Copy the directory to a another directory and change its name.
	 * 
	 * @param destination New path for the directory
	 * @param new name of the directory
	 * 
	 * @seecom.bradmcevoy.http.CopyableResource#copyTo(com.bradmcevoy.http.
	 * CollectionResource, java.lang.String)
	 */
	public void copyTo(CollectionResource destination, String newName) {
		//TODO: implement
		//throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * Move the directory to a another directory and change its name.
	 * 
	 * @param destination New path for the directory
	 * @param new name of the directory
	 *  
	 * @seecom.bradmcevoy.http.MoveableResource#moveTo(com.bradmcevoy.http.
	 * CollectionResource, java.lang.String)
	 */
	public void moveTo(CollectionResource destination, String newName)
			throws ConflictException {
		// TODO: implement
		//throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * Delete the directory.
	 * 
	 * @see com.bradmcevoy.http.DeletableResource#delete()
	 */
	public void delete() throws ConflictException {
		// TODO: implement
		//throw new UnsupportedOperationException("not implemented yet");
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.GetableResource#getContentLength()
	 */
	public Long getContentLength() {
		return 0l;
	}

	/** 
	 * Return the MIME-Type of the GridFolderResource.
	 * 
	 * @return not implemented, returning always "text/html"
	 * 
	 * @see com.bradmcevoy.http.GetableResource#getContentType(java.lang.String)
	 */
	public String getContentType(String arg0) {
		return "text/html";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bradmcevoy.http.GetableResource#sendContent(java.io.OutputStream,
	 * com.bradmcevoy.http.Range, java.util.Map, java.lang.String)
	 */
	public void sendContent(OutputStream out, Range range,
			Map<String, String> arg2, String arg3) throws IOException,
			NotAuthorizedException, BadRequestException {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.PropFindableResource#getCreateDate()
	 */
	public Date getCreateDate() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.Resource#getModifiedDate()
	 */
	public Date getModifiedDate() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bradmcevoy.http.GetableResource#getMaxAgeSeconds(com.bradmcevoy.http
	 * .Auth)
	 */
	public Long getMaxAgeSeconds(Auth arg0) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.Resource#getRealm()
	 */
	public String getRealm() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.Resource#authenticate(java.lang.String,
	 * java.lang.String)
	 */
	public Object authenticate(String arg0, String arg1) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.Resource#authorise(com.bradmcevoy.http.Request,
	 * com.bradmcevoy.http.Request.Method, com.bradmcevoy.http.Auth)
	 */
	public boolean authorise(Request arg0, Method arg1, Auth arg2) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bradmcevoy.http.Resource#checkRedirect(com.bradmcevoy.http.Request)
	 */
	public String checkRedirect(Request arg0) {
		return null;
	}
}