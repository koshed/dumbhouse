package WlanClientScanner;

import java.io.IOException;

public interface IWlanClientScanner {
	public String getRawHTMLFromRouter(String routerIP, String url) throws IOException;
}
