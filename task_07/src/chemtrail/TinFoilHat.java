package chemtrail;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import org.gridlab.gat.URI;
import org.gridlab.gat.io.FileInputStream;
import org.gridlab.gat.io.FileOutputStream;

import chemtrail.GAThandler;

public class TinFoilHat {

	public static void main(String args[]){
		System.getProperties().setProperty("gat.adaptor.path", 
											"resources/lib/adaptors");
		
		GAThandler handler = new GAThandler("cert.pem","gridftp");
		
		
		//Testing creation of a new, empty dir
		System.out.println("Creating directory 'blarg'");
		try {
			handler.createDir(new URI("blarg"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Testing creation of a new, empty file
		System.out.println("Creating file 'blarg/blubb'");
		try {
			handler.createFile(new URI("blarg/blubb"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		//Testing directory read
		System.out.println("Reading content of dir: 'blarg'");
		try {
			ArrayList<URI> filelist = handler.readDir(new URI("blarg"));
			for (Iterator<URI> it = filelist.iterator(); it.hasNext();) {
				URI file = it.next();
				if (file != null) {
				System.out.println(file.getPath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			FileOutputStream data = null;
			
			handler.updateFile(new URI("blarg/blubb"), data );
			System.out.println("meh" + data);
			
			data.write(new String("foobar!").getBytes());
			data.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Trying to delete dir: 'blarg'");
		try {
			handler.deleteDir(new URI("blarg"), false);
		} catch (DirectoryNotEmptyException e)
		{
			System.out.println(e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Trying to delete file: 'blarg/blubb:'");
		try {
			handler.deleteFile(new URI("blarg/blubb"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Trying to delete dir: 'blarg' again");
		try {
			handler.deleteDir(new URI("blarg"), false);
		} catch (DirectoryNotEmptyException e)
		{
			System.out.println(e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}

