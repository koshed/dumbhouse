package WlanClientScannerTest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import WlanClientScanner.IWlanClientScanner;
import WlanClientScanner.WlanClientScanner;

public class HTMLScannerTest {

	@Test
	public void getRawDataTest() throws IOException
	{
		IWlanClientScanner iWlanClientScanner = new WlanClientScanner();
		String routerIP = null;
		String url = null;
		String result = iWlanClientScanner.getRawHTMLFromRouter(routerIP, url);
		assertNotNull(result);
		assertTrue("html should contain mac, but was just: '" + result + "'", result.contains("test"));
	}
}
