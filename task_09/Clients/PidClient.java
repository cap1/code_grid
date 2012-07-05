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

import java.util.regex.*;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.DecoderException; 

public class PidClient {
	static String serviceUser = "griduser9";
	static String servicePwd = "doLhj10En4";
	final static boolean verbose = false;

	public static void main(String[] args) throws Exception {
		//check for PID
		if (args.length == 1) {
			try {
				searchPid(args[0]);
			}
			catch (IOException e) {
				System.out.println("Could not find info for PID \"" + args[0] + "\".");
				if (verbose) e.printStackTrace();
			}
		}
		//create
		else if (args.length == 3) {
			serviceUser = args[1];
			servicePwd = args[2];
			createPid(args[0]);
		}
		//update
		else if (args.length == 4) {
			serviceUser = args[2];
			servicePwd = args[3];
			modifyPid(args[0], args[1]);
		}
		//info
		else {
			String info = "Number of arguemnts determines function:\n";
			info += "\tjava PidClient $SomePid  -- Querry for $SomePid\n";
			info += "\tjava PidClient $uri $user $pw -- Create new Handle from $uri with $user and $pw\n";
			info += "\tjava PidClient $pid $newUri $user $ $pw -- Modify Handle $pid to new $uri with $user and $pw\n";

			System.out.println(info);
		}

		//String pid = "11858/00-STUD-0000-0000-13D9-3"; //christians own CID
		//createPid("https://github.com/cap1/pke-presentation/blob/master/presentation.pdf");
		//searchPid(pid);
		//modifyPid(pid);
		//searchPid(pid);
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

	public static String getPidValue(String pid, String field) throws IOException {
		String serviceUrl = "http://hdl-test.gwdg.de:8080/pidservice/read/search";
		String serviceParam = URLEncoder.encode(pid, "UTF-8");
		String serviceParam1 = URLEncoder.encode(field, "UTF-8");

		URL url = new URL(serviceUrl + "?" + "pid=" + serviceParam + "&encoding=xml");
		URLConnection connection = url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String decodedString;
		Pattern p = Pattern.compile("[ \t\n\f\r]*<" + field + ">(.*)</" + field +">");
		String value = null;
		while ((decodedString = in.readLine()) != null) {
			System.out.println(decodedString);
			if (Pattern.matches("[ \t\n\f\r]*<" + field + ">.*", decodedString)) {
				Matcher m = p.matcher(decodedString);
				if (m.find( )) {
				value = m.group(1);
				}
			}
		}

		in.close();
		return value;
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

	public static void modifyPid(String pid, String newUri) throws IOException {
		String serviceUrl = "http://hdl-test.gwdg.de:8080/pidservice/write/modify";
		
		String oldtitle = getPidValue(pid, "title");

		System.out.println(oldtitle);

		String serviceParam1 = URLEncoder.encode(pid, "UTF-8");
		String serviceParam2 = URLEncoder.encode(newUri, "UTF-8");
		String serviceParam3 = URLEncoder.encode(oldtitle, "UTF-8");


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
			out.write("&url=" + serviceParam2);
			out.write("&oldtitle=" + serviceParam3);
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