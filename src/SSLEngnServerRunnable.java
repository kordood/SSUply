import java.io.IOException;

/**
 * This class provides a runnable that can be used to initialize a
 * {@link NioSslServer} thread.
 * <p/>
 * Run starts the server, which will start listening to the configured IP
 * address and port for new SSL/TLS connections and serve the ones already
 * connected to it.
 * <p/>
 * Also a stop method is provided in order to gracefully close the server and
 * stop the thread.
 * 
 * @author <a href="mailto:alex.a.karnezis@gmail.com">Alex Karnezis</a>
 */
public class SSLEngnServerRunnable implements Runnable {

	SSLEngnServer server;

	@Override
	public void run() {
		try {
			server = new SSLEngnServer("TLSv1.2", "127.0.0.1", 9222);
			server.start();
			System.out.println("Stopped wait2");
			stop();
		} catch (Exception e) {
			e.printStackTrace();
			stop();
		}
	}

	/**
	 * Should be called in order to gracefully stop the server.
	 */
	public void stop() {
		server.stop();
	}

	public static void main(String[] args) throws Exception {
		SSLEngnServerRunnable serverRunnable = new SSLEngnServerRunnable();
		Thread server = new Thread(serverRunnable);
		server.start();
		server.sleep(1000);
		System.out.println("sleeped");
		server.interrupt();
		System.out.println("interrupted");
		server.start();
	}
}
