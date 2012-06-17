package chemtrail;

import java.net.URISyntaxException;

import org.gridlab.gat.URI;

import chemtrail.GAThandler;

public class TinFoilHat {

	public static void main(String args[]){
		System.getProperties().setProperty("gat.adaptor.path", 
											"src/resources/lib/adaptors");
		
		GAThandler handler = new GAThandler("cert.pem","gridftp");
		try {
			handler.createFile(new URI("foo"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

