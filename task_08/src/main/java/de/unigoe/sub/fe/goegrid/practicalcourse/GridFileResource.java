package de.unigoe.sub.fe.goegrid.practicalcourse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.channels.GatheringByteChannel;
import java.util.Date;
import java.util.Map;

import org.gridlab.gat.URI;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.FileItem;
import com.bradmcevoy.http.FileResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

import chemtrail.GAThandler;

/**
 * Represents a File in the Grid.
 * 
 * @author ralph.krimmel, christian.mueller6
 */

public class GridFileResource implements FileResource {
	
	//Path to the represented file
	URI fileName;
	//Handler to communicate with the JavaGAT
	GAThandler gatfs;
	//determine if verbose output is desired
	private final static boolean verbose = true;

	/**
	 * Construct a new file resource.
	 * 
	 * @param gatfs Handler to communicate with JavaGAT
	 * @param fileName Basename of the file
	 */
	GridFileResource(GAThandler gatfs, URI fileName) {
		this.fileName = fileName;
		this.gatfs = gatfs;
		if(verbose) System.out.println("Creating File Resource: " + fileName);		
	}

	/**
	 * Get the basename of the file.
	 * 
	 * @return basename of the file.
	 * 
	 * @see com.bradmcevoy.http.Resource#getName()
	 */
	public String getName() {
		if (verbose) System.out.println("Getting Name of file " + fileName);
		return gatfs.getBaseName(this.fileName);
	}

	/**
	 * Get the unique ID of the resource.
	 * 
	 * @return unique ID of the resource, describend by the path.
	 * 
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	public String getUniqueId() {
		return fileName.getPath();
	}

	/**
	 * Copy this file to the destination under the given name.
	 * 
	 * @param destination the path to the copy of the resource
	 * @param newName the name of the copy
	 * 
	 * @seecom.bradmcevoy.http.CopyableResource#copyTo(com.bradmcevoy.http.
	 * CollectionResource, java.lang.String)
	 */
	public void copyTo(CollectionResource destination, String newName) {
		URI source = null;
 		URI target = null;
		if (verbose) System.out.println("Copy " + this.getName() + " to new Name: " +  newName);
		try {
			source = new URI(this.getName());
			target = new URI(destination.getName());
			gatfs.copyFile(source, target);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Move the file to the destination with the given name.
	 * NOT transaction safe, as the GATHandler implements it by copy and delete.
	 * 
	 * @see chemtrail.GATHandler
	 * 
	 * @seecom.bradmcevoy.http.MoveableResource#moveTo(com.bradmcevoy.http.
	 * CollectionResource, java.lang.String)
	 */
	public void moveTo(CollectionResource destination, String newName)
			throws ConflictException {
		URI source = null;
 		URI target = null;
		if (verbose) System.out.println("Move " + this.getName() + " to new Name: " +  newName);
		try {
			source = new URI(this.getName());
			target = new URI(destination.getName());
			gatfs.moveFile(source, target);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the resource.
	 * 
	 * @see com.bradmcevoy.http.DeletableResource#delete()
	 */
	public void delete() throws ConflictException {
		if(verbose) System.out.println("Deleting file " + this.fileName);
		try {
			gatfs.deleteFile(fileName);
			//TODO: ConflictException
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the Size of the resource.
	 * 
	 * @return size of the resource in bytes
	 * 
	 * @see com.bradmcevoy.http.GetableResource#getContentLength()
	 */
	public Long getContentLength() {
		if(verbose) System.out.println("Getting size of file " + this.fileName);
		return gatfs.getSize(this.fileName);
	}

	/**
	 * Read the contents of the resource.
	 * 
	 * @see
	 * com.bradmcevoy.http.GetableResource#sendContent(java.io.OutputStream,
	 * com.bradmcevoy.http.Range, java.util.Map, java.lang.String)
	 */
	public void sendContent(OutputStream out, Range arg1,
			Map<String, String> arg2, String arg3) throws IOException,
			NotAuthorizedException, BadRequestException {
		//gatfs.readFile(, data)
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
	 * @see com.bradmcevoy.http.PostableResource#processForm(java.util.Map,
	 * java.util.Map)
	 */
	public String processForm(Map<String, String> arg0,
			Map<String, FileItem> arg1) throws BadRequestException,
			NotAuthorizedException, ConflictException {
		throw new UnsupportedOperationException();
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