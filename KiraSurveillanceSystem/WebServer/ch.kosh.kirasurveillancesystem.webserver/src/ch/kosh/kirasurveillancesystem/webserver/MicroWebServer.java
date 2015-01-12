package ch.kosh.kirasurveillancesystem.webserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.kosh.kirasurveillancesystem.phonestates.PhoneScanner;
import ch.kosh.kirasurveillancesystem.phonestates.PhoneStateList;

public class MicroWebServer implements Runnable {

	private static final Logger log4j = LogManager
			.getLogger(MicroWebServer.class.getName());

	private PhoneStateList phoneStateList;

	private final PhoneScanner phoneScanner;

	public MicroWebServer(PhoneStateList phoneStateList,
			PhoneScanner phoneScanner) {
		this.phoneStateList = phoneStateList;
		this.phoneScanner = phoneScanner;
	}

	private void printHTMLBody(PrintWriter out) {
		out.println("<table>");
		printCamStatusTable(out);
		printPhoneStatesTable(out);
		printExtendedAwayLogTable(out);
		printDetailLogTable(out);
		out.println("</table>");
	}

	private void printCamStatusTable(PrintWriter out) {
		out.println("<tr><td>");
		out.println(phoneScanner.getCamSwitchLogAsHTML());
		out.println("</td></tr>");
	}

	private void printExtendedAwayLogTable(PrintWriter out) {
		out.println("<tr><td><b>");
		out.println(phoneStateList.getExtendedAwayLogAsHTML());
		out.println("</b></td></tr>");
	}

	private void printDetailLogTable(PrintWriter out) {
		out.println("<tr><td>");
		out.println(phoneStateList.getLogAsHtML());
		out.println("</td></tr>");
	}

	private void printPhoneStatesTable(PrintWriter out) {
		out.println("<tr><td>Elvis status: "
				+ this.phoneStateList.getAll().get(0).toHTMLWebString()
				+ "</td></tr>" + "<tr><td>Kahlan status: "
				+ this.phoneStateList.getAll().get(1).toHTMLWebString()
				+ "</td></tr>");
	}

	/**
	 * WebServer constructor.
	 */
	@SuppressWarnings("resource")
	public void run() {
		ServerSocket s;

		int port = 8080;
		log4j.info("Webserver starting up on port " + port);
		try {
			// create the main server socket
			s = new ServerSocket(port);
		} catch (Exception e) {
			log4j.error("Error: " + e);
			return;
		}

		// System.out.println("Waiting for connection");
		for (;;) {
			try {
				// wait for a connection
				Socket remote = s.accept();
				// remote is now the connected socket
				log4j.trace("Connection: " + remote.getInetAddress()
						+ ", sending data.");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						remote.getInputStream()));
				PrintWriter out = new PrintWriter(remote.getOutputStream());

				// read the data sent. We basically ignore it,
				// stop reading once a blank line is hit. This
				// blank line signals the end of the client HTTP
				// headers.
				String str = ".";
				while (!str.equals(""))
					str = in.readLine();

				// Send the response
				// Send the headers
				out.println("HTTP/1.1 200 OK");
				out.println("Content-Type: text/html");
				out.println("Server: Bot");
				// out.println("<meta http-equiv=\"refresh\" content=\"1\" >");
				// this blank line signals the end of the headers
				out.println("");
				out.println("<html><head><meta http-equiv=\"refresh\" content=\"5\" ></head>");
				// Send the HTML page
				out.print("<body>");
				printHTMLBody(out);
				out.println("</body></html>");
				out.flush();
				remote.close();
			} catch (Exception e) {
				log4j.error("Error: " + e);
				e.printStackTrace();
			}
		}
	}
}
