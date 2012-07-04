/**
 * Client for the REST API of the Persistent Identifier Service
 * For details see: http://www.pidconsortium.eu/index.php?page=process
 */
//package de.gwdg.pidservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import sun.misc.BASE64Encoder;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.DecoderException; 

public class PidClient {
	static final String serviceUser = "griduser9";
	static final String servicePwd = "doLhj10En4";

	public static void main(String[] args) throws Exception {
		//String pid = "11858/00-ZZZZ-0000-0000-0229-F";
		//String pid = "11858/00-ZZZZ-0000-0001-4743-4";
		String pid = "11858/00-STUD-0000-0000-13D9-3"; //christians own CID


		//createPid("https://github.com/cap1/pke-presentation/blob/master/presentation.pdf");
		searchPid(pid);
		modifyPid(pid);
		searchPid(pid);
	}

	public static void searchPid(String pid) throws IOException {
		String serviceUrl = "http://hdl-test.gwdg.de:8080/pidservice/read/search";
		String serviceParam = URLEncoder.encode(pid, "UTF-8");

		URL url = new URL(serviceUrl + "?" + "pid=" + serviceParam);
		URLConnection connection = url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String decodedString;
		while ((decodedString = in.readLine()) != null) {
			System.out.println(decodedString);
		}
		in.close();
	}

	public static void createPid(String fileURL) throws IOException {
		String serviceUrl = "http://hdl-test.gwdg.de:8080/pidservice/write/create";

		String title = fileURL;
		String serviceParam1 = URLEncoder.encode(fileURL, "UTF-8");

		URL url = new URL(serviceUrl);
		HttpURLConnection urlConnection = null;
		BufferedReader in = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.setRequestMethod("POST");

			String authCred = serviceUser + ":" + servicePwd;
			String encodedAuthCred = new sun.misc.BASE64Encoder()
					.encode(authCred.getBytes());
			urlConnection.setRequestProperty("Authorization", "Basic "
					+ encodedAuthCred);
			/*
			 * Do not use sun.misc.* , see: "Sun proprietary API" 
			 * instead, use "Commons Codec library" for Base64 Encoder
			 * (http://commons.apache.org/codec/)
			 * import org.apache.commons.codec.DecoderException; 
			 * import org.apache.commons.codec.binary.Base64;
			 */

			OutputStreamWriter out = new OutputStreamWriter(
					urlConnection.getOutputStream());
			out.write("url=" + serviceParam1);
			out.write("&encoding=xml");
			out.close();

			in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			String decodedString;

			while ((decodedString = in.readLine()) != null) {
				System.out.println(decodedString);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void modifyPid(String pid) throws IOException {
		String serviceUrl = "http://hdl-test.gwdg.de:8080/pidservice/write/modify";

//		String modifiedUrl = "http://handle.gwdg.de/javadocs/";
//		String oldTitle = "PIDService Documentation";
//		String modifiedTitle = "PIDService Documentations";
		String oldTitle = "";
		String modifiedTitle = "Presentation on PKES";
		String serviceParam1 = URLEncoder.encode(pid, "UTF-8");
//		String serviceParam2 = URLEncoder.encode(modifiedUrl, "UTF-8");
		String serviceParam3 = URLEncoder.encode(oldTitle, "UTF-8");
		String serviceParam4 = URLEncoder.encode(modifiedTitle, "UTF-8");

		URL url = new URL(serviceUrl);
		HttpURLConnection urlConnection = null;
		BufferedReader in = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.setRequestMethod("POST");

			String authCred = serviceUser + ":" + servicePwd;
			String encodedAuthCred = new sun.misc.BASE64Encoder()
					.encode(authCred.getBytes());
			urlConnection.setRequestProperty("Authorization", "Basic "
					+ encodedAuthCred);
			/*
			 * Do not use sun.misc.* , see: "Sun proprietary API" 
			 * instead, use "Commons Codec library" for Base64 Encoder
			 * (http://commons.apache.org/codec/)
			 * import org.apache.commons.codec.DecoderException; 
			 * import org.apache.commons.codec.binary.Base64;
			 */

			OutputStreamWriter out = new OutputStreamWriter(
					urlConnection.getOutputStream());
			out.write("pid=" + serviceParam1);
		//	out.write("&url=" + serviceParam2);
			out.write("&oldtitle=" + serviceParam3);
			out.write("&newtitle=" + serviceParam4);	//modify
			out.write("&encoding=xml");
			out.close();

			in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			String decodedString;

			while ((decodedString = in.readLine()) != null) {
				System.out.println(decodedString);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
