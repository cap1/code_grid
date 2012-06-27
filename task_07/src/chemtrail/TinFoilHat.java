package chemtrail;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import org.gridlab.gat.URI;

import chemtrail.GAThandler;

public class TinFoilHat {

	public static void main(String args[]) {
		System.getProperties().setProperty("gat.adaptor.path",
				"resources/lib/adaptors");
		// System.getProperties().setProperty("gat.adaptor.path",
		// "/afs/informatik.uni-goettingen.de/user/r/ralph.krimmel/workspace/gruenewolken/src/resources/lib/adaptors");

		System.getProperties().setProperty("log4j.rootLogger", "off");

		GAThandler handler = new GAThandler("/tmp/x509up_u1013", "gridftp");

		String host = "lima";
		String adaptor = "gsiftp://";
		
		// Testing creation of file with content
		

		// Testing creation of a new, empty dir
		try {
			handler.createDir(new URI(adaptor + host + "//tmp/griduser9blarg"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Testing creation of a new, empty file
		try {
			handler.createFile(new URI(adaptor + host
					+ "//tmp/griduser9blarg/blubb"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// Testing creation of non empty file
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream data;
		try {
			data = new PipedInputStream(pos);
			byte[] b = "Toller Content einer Datei\n".getBytes();
			pos.write(b, 0, b.length);
			pos.close();
			handler.createFile(new URI(adaptor + host
					+ "//tmp/griduser9blarg/bla"), data);
			data.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		
		// Testing directory read
		try {
			ArrayList<URI> filelist = handler.readDir(new URI(adaptor + host
					+ "//tmp/griduser9blarg"));
			for (Iterator<URI> it = filelist.iterator(); it.hasNext();) {
				URI file = it.next();
				if (file != null) {
					System.out.println(file.getPath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//trying to read from file
		try {
			handler.readFile(new URI(adaptor + host + "//tmp/griduser9blarg/bla"), System.out);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//trying to update file
		pos = new PipedOutputStream();
		try {
			data = new PipedInputStream(pos);
			byte[] b = "bla".getBytes();
			pos.write(b, 0, b.length);
			pos.close();
			handler.updateFile(new URI(adaptor + host + "//tmp/griduser9blarg/blubb"), data );
		 } catch (Exception e) { 
			 e.printStackTrace(); 
		 }

		
		//try to delete directory with content
		try {
			handler.deleteDir(new URI(adaptor + host + "//tmp/griduser9blarg"),
					false);
		} catch (DirectoryNotEmptyException e) {
			System.out.println(e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//try to delete file
		try {
			handler.deleteFile(new URI(adaptor + host
					+ "//tmp/griduser9blarg/blubb"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//try to delete directory recursively
		try {
			handler.deleteDir(new URI(adaptor + host + "//tmp/griduser9blarg"),
					true);
		} catch (DirectoryNotEmptyException e) {
			System.out.println(e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
