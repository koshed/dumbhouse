package ch.kosh.kirasurveillancesystem.webserver;

public class MicroWebServerThreadStarter {
	
	private MicroWebServer ws;

	public MicroWebServerThreadStarter(MicroWebServer ws)
	{
		this.ws = ws;
	}
	
	public void start()
	{
		Thread webserverThread = new Thread(this.ws);
		webserverThread.start();
	}
}
