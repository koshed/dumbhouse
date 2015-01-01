package WlanClientScanner;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jibble.simplewebserver.SimpleWebServer;
import org.jsoup.*;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.*;
import org.jsoup.select.*;


public class WlanClientScanner implements IWlanClientScanner {

	@Override
	public String getRawHTMLFromRouter(String routerIP, String url) throws IOException {
		String fullUrl = "http://10.10.10.1/DEV_redirect.htm";

//		Connection.Response res = Jsoup.connect(fullUrl)
//				.data("user", "admin", "password", "ubp2smr4easo")
//		        .method(Method.POST)
//		        
//		        .execute();
//		
//		@SuppressWarnings("unused")
//		Connection connection = Jsoup.connect(fullUrl)
//				.data("user", "admin", "password", "ubp2smr4easo")
//				;		
		Connection.Request req = null;
		HttpURLConnection conn=(HttpURLConnection)req.url().openConnection();
		  conn.setRequestMethod(req.method().name());
		  conn.setInstanceFollowRedirects(false);
		  conn.setConnectTimeout(req.timeout());
		  conn.setReadTimeout(req.timeout());
		  if (req.method() == Method.POST)   conn.setDoOutput(true);
		
//		Connection con = Jsoup.connect(fullUrl);
//		Response exeResponse = con.execute();
		
		Document doc = Jsoup.connect(fullUrl).get();
		doc.select("");
		
		Elements archived = doc.select("div.archived strong");

		for (Element element: archived){
		    System.out.println("KEY: " + element.text());
		    System.out.println("VALUE: " + element.nextSibling());
		}
		
//		Document htmlDoc = connection.get();
//		Document doc = Jsoup.parse(fullUrl);
//		
		//HTML form id
		Elements allElements = doc.getAllElements();
		Element loginform = doc.getElementById("your_form_id");
			
	 
		Elements inputElements = loginform.getElementsByTag("input");
		List<String> paramList = new ArrayList<String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");
			System.out.println(key + ":" + value);
		}
		
		String title = doc.title();
		return title;
	}

	@Override
	public void startupWebServer() throws IOException {
		File file = new File("./");
		SimpleWebServer sms = new SimpleWebServer(file, 80);
	}

}
