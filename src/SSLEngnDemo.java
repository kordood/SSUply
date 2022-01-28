import java.util.Scanner;

public class SSLEngnDemo {
	
	SSLEngnServerRunnable serverRunnable;
	
	public SSLEngnDemo() {
		serverRunnable = new SSLEngnServerRunnable();
		Thread server = new Thread(serverRunnable);
		server.start();
	}
	
	public void runDemo() throws Exception {
		Scanner scanner = new Scanner(System.in);
		
		// System.setProperty("javax.net.debug", "all");
		
		SSLEngnClient client = new SSLEngnClient("TLSv1.2", "localhost", 9222);
		String str = "null";
		client.connect();
		while(!str.equals("quit")) {
		str = scanner.nextLine();
		client.write(str);
		client.read();
		}
		client.shutdown();
		
		SSLEngnClient client2 = new SSLEngnClient("TLSv1.2", "localhost", 9222);
		SSLEngnClient client3 = new SSLEngnClient("TLSv1.2", "localhost", 9222);
		SSLEngnClient client4 = new SSLEngnClient("TLSv1.2", "localhost", 9222);

		client2.connect();
		client2.write("Hello! I am another client!");
		client2.read();
		client2.shutdown();

		client3.connect();
		client4.connect();
		client3.write("Hello from client3!!!");
		client4.write("Hello from client4!!!");
		client3.read();
		client4.read();
		client3.shutdown();
		client4.shutdown();

		serverRunnable.stop();
	}
	
	public static void main(String[] args) throws Exception {
		SSLEngnDemo demo = new SSLEngnDemo();
		Thread.sleep(1000);	// Give the server some time to start.
		demo.runDemo();
	}
	
}