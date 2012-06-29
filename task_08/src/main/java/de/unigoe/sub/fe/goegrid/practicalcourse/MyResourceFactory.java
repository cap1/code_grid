package de.unigoe.sub.fe.goegrid.practicalcourse;

import java.net.URISyntaxException;

import org.gridlab.gat.URI;

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import chemtrail.GAThandler;

/**
 *
 */
public class MyResourceFactory implements ResourceFactory {

	private GAThandler gatfs;
	String protocol = "gsiftp://";
	String gridhost = "lima";

	public MyResourceFactory() {
		System.out.println("Setting up GAT handler");
		gatfs = new GAThandler("/tmp/x509up_u1013", "gridftp");
	}

	/**
	 * Get a new Gridresource.
	 * 
	 * @param host host of the requested resource
	 * @param path path to the requested resource
	 * 
	 * @return the resource described by the parameters.
	 * 
	 * @see com.bradmcevoy.http.ResourceFactory#getResource(java.lang.String,
	 * java.lang.String)
	 */
	public Resource getResource(String host, String path) {
		System.out.println("Host " + host + " Path " + path);

		URI target = null;
		try {
			target = new URI(protocol + gridhost + "/" + path);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		Resource gfs = null;
		if (gatfs.exists(target)) {
			if (gatfs.isDirectory(target)) {
				gfs = new GridFolderResource(gatfs, target);

			} else if (gatfs.isFile(target)) {
				gfs = new GridFileResource(gatfs, target);
			}
		}

		return gfs;
	}

}