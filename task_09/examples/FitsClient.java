/**
 * Client for the API of the File Information Tool Set
 * For details see: http://code.google.com/p/fits/wiki/developer_documentation
 */

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.math.BigInteger;
import edu.harvard.hul.ois.fits.*;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;

/**
 * @author Tibor [dot] Kalman [at] gwdg [dot] de, Daniel [dot] Kurzawe [at] gwdg [dot] de
 */

public class FitsClient {
	//the necessary FITS libs are located in FitsHome
	public static String FitsHome = "/home/... (directory of FITS)";
	//this file is analyzed by FITS
	public static String InputFile = "/home/ ... (path to inputfile)";

	public static void main(String[] args) throws NullPointerException,
			FitsException, IOException, FitsConfigurationException,
			FileNotFoundException, NoSuchAlgorithmException {
		// Initializing fits
		File testfile = new File(InputFile);
		Fits fits = new Fits(FitsHome);
		FitsOutput fitsOut = null;

		// Examining Testfile
		fitsOut = fits.examine(testfile);

		// Save fits results to Disk
		fitsOut.saveToDisk("test.xml");

		// Extract Metadata of interest
		FitsMetadataElement author_element = fitsOut.getMetadataElement("author");
		FitsMetadataElement title_element = fitsOut.getMetadataElement("title");

		System.out.println("Autor: " + author_element.getValue());
		System.out.println("Titel: " + title_element.getValue());
		System.out.println(md5(TargetFile));
	}

	//function to calculate MD5 checksum
	public static String md5(String filename) throws NoSuchAlgorithmException,
			FileNotFoundException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		// File f = new File("c:\\myfile.txt");
		InputStream is = new FileInputStream(filename);
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			// System.out.println("MD5: " + output);
			return output;
		} catch (IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(
						"Unable to close input stream for MD5 calculation", e);
			}
		}
	}
}
