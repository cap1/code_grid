package chemtrail;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import org.gridlab.gat.URI;

import chemtrail.GAThandler;

public class TinFoilHat {

	public static void main(String args[]){
		System.getProperties().setProperty("gat.adaptor.path", 
											"resources/lib/adaptors");
		
		GAThandler handler = new GAThandler("cert.pem","gridftp");
		
		
		//Testing creation of a new, empty file
		try {
			handler.createFile(new URI("foo"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		//Testing creation of a new, empty dir
		try {
			handler.createDir(new URI("blarg"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		//Testing directory read
		try {
			ArrayList<URI> filelist = handler.readDir(new URI("."));
			for (Iterator<URI> it = filelist.iterator(); it.hasNext();) {
				URI file = it.next();
				if (file != null) {
				System.out.println(file.getPath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
}

