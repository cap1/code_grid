package de.unigoe.sub.fe.goegrid.practicalcourse;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import chemtrail.GAThandler;

import com.bradmcevoy.http.MiltonServlet;

/**
 * 
 */
public class WebDAVServer {
	/**
   *
   */
	public static void main(String[] args) {
		// TODO: implement other initialization
		System.getProperties().setProperty("gat.adaptor.path",
		"resources/lib/adaptors");
		
		int Port = 8091;
		Server server = new Server(Port);
		// server.setAttribute("Host","127.0.0.1");
		Context root = new Context(server, "/", Context.SESSIONS);
		System.out.println("Running Webserver on port " + Port);

		try {
			MiltonServlet servlet = new MiltonServlet();
			ServletHolder servletHolder = new ServletHolder(servlet);

			servletHolder.setInitParameter("resource.factory.class",
					MyResourceFactory.class.getName());

			root.addServlet(servletHolder, "/*");

			server.start();
			server.join();
		} catch (Exception e) {
			System.err.println("could not start server");
			e.printStackTrace();
		}
	}

}
