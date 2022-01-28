import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

/**
 * An SSL/TLS server, that will listen to a specific address and port and serve
 * SSL/TLS connections compatible with the protocol it applies.
 * <p/>
 * After initialization {@link SSLEngnServer#start()} should be called so the
 * server starts to listen to new connection requests. At this point, start is
 * blocking, so, in order to be able to gracefully stop the server, a
 * {@link Runnable} containing a server object should be created. This runnable
 * should start the server in its run method and also provide a stop method,
 * which will call {@link SSLEngnServer#stop()}.
 * </p>
 * NioSslServer makes use of Java NIO, and specifically listens to new
 * connection requests with a {@link ServerSocketChannel}, which will create new
 * {@link SocketChannel}s and a {@link Selector} which serves all the
 * connections in one thread.
 *
 * @author <a href="mailto:alex.a.karnezis@gmail.com">Alex Karnezis</a>
 */
public class SSLEngnServer extends NioSslPeer {

	FileManage fm;
	RequestTableImpl remoteRequestTableImpl;
	RequestTable remoteRequestTable;
	User[] remoteUserTable;
	String rmiAddress = "127.0.0.1";
	String rmiPort = "1099";
	String cID = null;
	String[] cIDs = null;
	String[][] requestIsYes = null;
	String[] loginedcID = null;
	Object[][] supplierNet = null;
	Object[][] requesterNet = null;
	String[][] payList = null;
	int clientCount = 0;
	boolean isAuthed = false;
	int k = 0, trade = 0;;

	/**
	 * Declares if the server is active to serve and create new connections.
	 */
	private boolean active;

	/**
	 * The context will be initialized with a specific SSL/TLS protocol and will
	 * then be used to create {@link SSLEngine} classes for each new connection that
	 * arrives to the server.
	 */
	private SSLContext context;

	/**
	 * A part of Java NIO that will be used to serve all connections to the server
	 * in one thread.
	 */
	private Selector selector;

	/**
	 * Server is designed to apply an SSL/TLS protocol and listen to an IP address
	 * and port.
	 *
	 * @param protocol    - the SSL/TLS protocol that this server will be configured
	 *                    to apply.
	 * @param hostAddress - the IP address this server will listen to.
	 * @param port        - the port this server will listen to.
	 * @throws Exception
	 */

	String keyFilePath1 = "C:\\Users\\msec\\eclipse-workspace\\SSUpply\\bin\\main\\resources\\server.jks";
	String trustFilePath1 = "C:\\Users\\msec\\eclipse-workspace\\07주차2\\bin\\main\\resources\\trustedCerts.jks";
	
	String keyFilePath2 = "C:\\Users\\Liberty\\Documents\\eclipse-workspace\\RealSSUpply\\SSUpply\\bin\\main\\resources\\server.jks";
	String trustFilePath2 = "C:\\Users\\Liberty\\Documents\\eclipse-workspace\\RealSSUpply\\SSUpply\\bin\\main\\resources\\trustedCerts.jks";
	public SSLEngnServer(String protocol, String hostAddress, int port) throws Exception {

		context = SSLContext.getInstance(protocol);
		try {
		context.init(
				createKeyManagers(keyFilePath1, "storepass", "keypass"),
				createTrustManagers(trustFilePath1,"storepass"), new SecureRandom());
		} catch(FileNotFoundException fnfe) {
			context.init(
					createKeyManagers(keyFilePath2, "storepass", "keypass"),
					createTrustManagers(trustFilePath2,"storepass"), new SecureRandom());
		}
		
		SSLSession dummySession = context.createSSLEngine().getSession();
		myAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		myNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		peerAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		peerNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		dummySession.invalidate();

		selector = SelectorProvider.provider().openSelector();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(hostAddress, port));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		active = true;

	}

	/**
	 * Should be called in order the server to start listening to new connections.
	 * This method will run in a loop as long as the server is active. In order to
	 * stop the server you should use {@link SSLEngnServer#stop()} which will set it
	 * to inactive state and also wake up the listener, which may be in blocking
	 * select() state.
	 *
	 * @throws Exception
	 */
	public void start() throws Exception {
		fm = new FileManage();
		System.setProperty("java.rmi.server.hostname", rmiAddress);

		try {
			remoteRequestTableImpl = new RequestTableImpl();
			remoteRequestTableImpl.setRequestTable(fm);
			String rmiURL = "rmi://" + rmiAddress + ":" + rmiPort + "/RequestRemote";
			try {
				java.rmi.Naming.rebind(rmiURL, remoteRequestTableImpl);
				remoteRequestTable = (RequestTable) Naming.lookup(rmiURL);
				System.out.println(rmiURL);
				System.out.println("RequestTable Instance open");
				requestIsYes = invalidateRequestIsYes(remoteRequestTable, requestIsYes);
			} catch (Exception e) {
				System.out.println("rmi error(" + rmiURL + ")" + e);
				e.printStackTrace();
			}

			while (isActive()) {
				System.out.println("selected");
				selector.select();
				System.out.println("selected");
				Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = selectedKeys.next();
					selectedKeys.remove();
					if (!key.isValid()) {
						continue;
					}
					if (key.isAcceptable()) {
						accept(key);
						System.out.println("accepted");
					} else if (key.isReadable()) {
						System.out.println("read\n");
						read((SocketChannel) key.channel(), (SSLEngine) key.attachment());
						requestIsYes = invalidateRequestIsYes(remoteRequestTable, requestIsYes);
					}
					k++;
					if (k % 10 == 1) {
						fm.loginFileSave();
						fm.userFileSave();
						fm.requestTableFileSave(remoteRequestTable.getRequestArray());
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Server start error: " + e);
			e.printStackTrace();
			System.out.println("Stopped wait1");
		} finally {
			System.out.println("stop server");
			stop();
		}

	}

	/**
	 * Sets the server to an inactive state, in order to exit the reading loop in
	 * {@link SSLEngnServer#start()} and also wakes up the selector, which may be in
	 * select() blocking state.
	 */
	public void stop() {
		active = false;
		fm.loginFileSave();
		fm.userFileSave();
		try {
			fm.requestTableFileSave(remoteRequestTable.getRequestArray());
		} catch (RemoteException e) {
			e.printStackTrace();
		} finally {
			System.out.println("서버 종료");
			executor.shutdown();
			selector.wakeup();
		}
	}

	/**
	 * Will be called after a new connection request arrives to the server. Creates
	 * the {@link SocketChannel} that will be used as the network layer link, and
	 * the {@link SSLEngine} that will encrypt and decrypt all the data that will be
	 * exchanged during the session with this specific client.
	 *
	 * @param key - the key dedicated to the {@link ServerSocketChannel} used by the
	 *            server to listen to new connection requests.
	 * @throws Exception
	 */
	private void accept(SelectionKey key) throws Exception {

		SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
		socketChannel.configureBlocking(false);

		SSLEngine engine = context.createSSLEngine();
		engine.setUseClientMode(false);
		engine.beginHandshake();

		if (doHandshake(socketChannel, engine)) {
			socketChannel.register(selector, SelectionKey.OP_READ, engine);
			cID = addClient(Integer.toString(clientCount));
			clientCount++;
		} else {
			socketChannel.close();
		}
	}

	private String addClient(String cID) {
		if (cIDs == null) { // first client
			cIDs = new String[1];
			cIDs[0] = cID;
			return cID;
		}

		String[] newcIDs = new String[cIDs.length + 1];
		for (int i = 0; i < cIDs.length; i++) { // copy origin to new
			newcIDs[i] = cIDs[i];
		}

		newcIDs[cIDs.length] = cID;

		this.cIDs = newcIDs;

		return cID;
	}

	/**
	 * Will be called by the selector when the specific socket channel has data to
	 * be read. As soon as the server reads these data, it will call
	 * {@link SSLEngnServer#write(SocketChannel, SSLEngine, String)} to send back a
	 * trivial response.
	 *
	 * @param socketChannel - the transport link used between the two peers.
	 * @param engine        - the engine used for encryption/decryption of the data
	 *                      exchanged between the two peers.
	 * @throws IOException if an I/O error occurs to the socket channel.
	 */
	@Override
	protected void read(SocketChannel socketChannel, SSLEngine engine) throws IOException {

		String err;
		String reflectcID = null;
		int bytesRead;

		peerNetData.clear();
		try {
			bytesRead = socketChannel.read(peerNetData);
		} catch (IOException ioe) {
			bytesRead = -1; // accept close connection
		}

		if (bytesRead > 0) {
			peerNetData.flip();
			while (peerNetData.hasRemaining()) {
				peerAppData.clear();
				SSLEngineResult result = engine.unwrap(peerNetData, peerAppData);
				switch (result.getStatus()) {
				case OK:
					String data;
					String[] arg = null;
					StringSlice ss = new StringSlice();
					peerAppData.flip();
					Charset charset = Charset.forName("UTF-8");
					data = charset.decode(peerAppData).toString();
//                    data = new String(peerAppData.array());
					System.out.println("Incoming message: " + data);

					ss.setRaw(data);
					arg = ss.getSlice();
					for (int i = 0; i < arg.length; i++) {
						System.out.println(arg[i]);
						if (arg[i].equals("cID")) {
							reflectcID = arg[i + 1];
						}
					}

					int processResult = process(socketChannel, engine, arg);
					switch (processResult) {
					case -2:
						err = "\nProcess didn't accepted(Invalid format): send again.\n";

						write(socketChannel, engine, "%err?" + err);
						break;
					case -1:
						err = "\nProcess didn't accepted.(Unknown error): send again.\n";
						write(socketChannel, engine, "%err?" + err);
						break;
					case 1:
						System.out.println("process completed.");
						break;
					case 2: // quit
						System.out.println("ByeBye~!");
						closeConnection(socketChannel, engine);
						break;
					}
					break;
				case BUFFER_OVERFLOW:
					peerAppData = enlargeApplicationBuffer(engine, peerAppData);
					break;
				case BUFFER_UNDERFLOW:
					peerNetData = handleBufferUnderflow(engine, peerNetData);
					break;
				case CLOSED:
					closeConnection(socketChannel, engine);
					return;
				default:
					throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
				}
			}

			if (reflectcID != null) {
				// write(socketChannel, engine, "Hello I'm server");
			}

			else {
				write(socketChannel, engine, "%cID?" + cID + "%msg?Hello I'm server");
			}

		} else if (bytesRead < 0) {
			handleEndOfStream(socketChannel, engine);
		}
	}

	int process(SocketChannel socketChannel, SSLEngine engine, String[] arg) throws IOException {
		String data = new String();
		String clientcID = new String();
		String isRegister = null, ID = null, PW = null;
		String rmiURL = new String();
		String isYes = null;
		String[] tradeArgs = new String[15];

		if (arg != null) {
			System.out.println("\n<args>");

			try {
				for (int i = 2; i < arg.length; i++) { // except %cID? + clientID
					if ((i % 2) == 0) {
						data += "%" + arg[i];
					} else {
						data += "?" + arg[i];
					}
					System.out.println(arg[i]);
				}
				for (int i = 0; i < arg.length; i += 2) { // except %cID? + clientID
					switch (arg[i]) {
					case "isQuit":
						/*
						 * try { java.rmi.Naming.unbind(rmiURL); } catch (NotBoundException nbe) {
						 * System.out.println("rmi unbind error: " + nbe); nbe.printStackTrace(); }
						 */
						return 2;
					case "cID":
						clientcID = arg[i + 1];
						System.out.println(clientcID + "\n\n");
						break;
					case "isRegister":
						isRegister = arg[i + 1];
						break;
					case "ID":
						ID = arg[i + 1];
						break;
					case "PW":
						PW = arg[i + 1];
						break;
					case "isYes":
						isYes = arg[i + 1];
						break;
					case "rID":
						tradeArgs[0] = arg[i + 1];
						break;
					case "isYesReact":
						tradeArgs[1] = arg[i + 1];
						break;
					case "supplierYes":
						tradeArgs[2] = arg[i + 1];
						break;
					case "requesterYes":
						tradeArgs[3] = arg[i + 1];
						break;
					case "tradeSYes":
						tradeArgs[4] = arg[i + 1];
						break;
					case "tradeRYes":
						tradeArgs[5] = arg[i + 1];
						break;
					case "reTrade":
						tradeArgs[6] = arg[i + 1];
						break;
					case "isFinish":
						tradeArgs[7] = arg[i + 1];
						break;
					case "authCode":
						tradeArgs[8] = arg[i + 1];
						break;
					case "authStart":
						tradeArgs[9] = arg[i + 1];
						break;
					case "isAuth":
						tradeArgs[10] = arg[i + 1];
						break;
					case "authTry":
						tradeArgs[11] = arg[i + 1];
						break;
					case "isPass":
						tradeArgs[12] = arg[i + 1];
						break;
					case "isAuthed":
						tradeArgs[13] = arg[i + 1];
						break;
					case "traderID":
						tradeArgs[14] = arg[i + 1];
						break;
					}
				}
			} catch (NullPointerException ne) {
				System.out.println("[Method]process error");
				ne.printStackTrace();
				return -2; // null argument
			}

			// =============== login or register ===============
			if (!isAuthedClient(clientcID)) {
				if (ID != null || PW != null) {
					boolean isLoginwell = loginProcess(isRegister, ID, PW);
					if (isLoginwell) {
						if (isRegister.equals("false")) { // when login
							addAuthedClient(clientcID);
							/* ==== send User Instance ==== */
							if (clientcID != null) {
								rmiURL = "rmi://" + rmiAddress + ":" + rmiPort + "/UserRemote" + clientcID; // UserRemote0,
																											// UserRemote1,
																											// UserRemote2,
																											// ...
								System.out.println("URL: " + rmiURL);
								
								try {
									User userRmi;
									userRmi = (User) Naming.lookup(rmiURL);
									addUserTable(userRmi);
								} catch (NotBoundException e1) {
									System.out.println("[Method] read error: User not added\n" + e1);
								}
								
								try {
									UserImpl remoteObj = new UserImpl(ID);
									String[] user = fm.getUser(ID);
									if (user != null) {
										remoteObj.loadUser(user);
									}
									java.rmi.Naming.rebind(rmiURL, remoteObj);
									System.out.println("User Instance send");
								} catch (Exception e) {
									System.out.println("rmi error" + e);
									e.printStackTrace();
									return -1;
								}
								data = "%rmiURL?" + rmiURL;
							}
						} else {
							data = "isSaved";
						}
						write(socketChannel, engine, data);
						return 1;
					}
				}
			}
			// =================== after login ====================
			else {
				if (isYes != null) {
					String result = responseIsYes(isYes);
					System.out.println(result);
					if (result != null) {
						String msg = "%isYesReact?Yes%rID?" + result + "%traderID?" + supplierNet[trade];
						trade++;
					}
				}
				for (int i = 0; i < tradeArgs.length; i++) {
					if (tradeArgs[i] != null) {
						Object[] supplier;
						Object[] requester;
						String randomNumber = "0";
						int status = tradeProcess(ID, tradeArgs);
						switch (status) {
						case -1: // trade error
							setNoRequest(tradeArgs[0]);
							String msg = "%error?tradeProcessError";
							write(socketChannel, engine, msg);
							break;
						case 0:
						case 1: // supplierYes: yes
							setYesRequest(tradeArgs[0]);
							addSupplierNet(clientcID, socketChannel, engine);
							addPayList(tradeArgs[0], remoteRequestTable.getRequestID2Request(tradeArgs[0]).getPayCashunut());
							break;
						case 2: // requesterYes: yes
							addRequesterNet(clientcID, socketChannel, engine);
							supplier = getSupplier(tradeArgs[14]);
							msg = "%requesterYes?Yes";
							write((SocketChannel) supplier[1], (SSLEngine) supplier[2], msg);
							break;
						case -2: // requesterYes: no
							setNoRequest(tradeArgs[0]);
							supplier = getSupplier(tradeArgs[14]);
							msg = "%requesterYes?No";
							write((SocketChannel) supplier[1], (SSLEngine) supplier[2], msg);
							int ind = delSupplierNet(tradeArgs[14]);
							delRequesterNet(ID);
							delPayList(ind);
							break;
						case 3: // tradeSYes: yes
							requester = getRequester(tradeArgs[14]);
							msg = "%tradeSYes?Yes";
							write((SocketChannel) requester[1], (SSLEngine) requester[2], msg);
							break;
						case -3: // tradeSYes: no
							setNoRequest(tradeArgs[0]);
							requester = getRequester(tradeArgs[14]);
							msg = "%tradeSYes?No";
							write((SocketChannel) requester[1], (SSLEngine) requester[2], msg);
							break;
						case 4: // tradeRYes: yes
							Random random = new Random(status);
							randomNumber = Integer.toString(random.nextInt() % 999999);
							String authCode = randomNumber;
							requester = getRequester(tradeArgs[14]);
							msg = "%authCode?" + authCode;
							write((SocketChannel) requester[1], (SSLEngine) requester[2], msg);
							supplier = getSupplier(tradeArgs[14]);
							msg = "%authStart?Yes";
							write((SocketChannel) supplier[1], (SSLEngine) supplier[2], msg);
							break;
						case -4: // tradeRYes: no
							requester = getRequester(tradeArgs[14]);
							msg = "%isFinish?Yes";
							write((SocketChannel) requester[1], (SSLEngine) requester[2], msg);
							supplier = getSupplier(tradeArgs[14]);
							msg = "%isFinish?Yes";
							write((SocketChannel) supplier[1], (SSLEngine) supplier[2], msg);
							int ind2 = delSupplierNet(tradeArgs[14]);
							delRequesterNet(ID);
							delPayList(ind2);
							break;
						case 5:	// isAuth: yes
							break;
						case -5:	// isAuth: no
							write(socketChannel, engine, data);
							break;
						case 6: // authTry from supplier
							supplier = getSupplier(ID);
							if (tradeArgs[11].equals(randomNumber)) {
								msg = "%isPass?Yes";
								requester = getRequester(tradeArgs[14]);
								msg = "%isAuthed?Yes";
								write((SocketChannel) requester[1], (SSLEngine) requester[2], msg);
								int index = delSupplierNet(ID);
								delRequesterNet(tradeArgs[14]);

								String pay = payList[index][1];
								User cashunutPayer = getID2User(tradeArgs[14]);
								User cashunutEarner = getID2User(ID);
								cashunutPayer.subCashunut(cashunutPayer.getCashunut(), Integer.getInteger(pay));
								cashunutEarner.addCashunut(cashunutEarner.getCashunut(), Integer.getInteger(pay));
								
							} else {
								msg = "%isPass?No";
							}
							write((SocketChannel) supplier[1], (SSLEngine) supplier[2], msg);
							
							break;
						// ========================ToDo: in case 6: cashunut add, sub process===================

						case 10: // reTrade(cause by SYes RYes not equal)
							supplier = getSupplier(tradeArgs[14]);
							msg = "%reTrade?Yes";
							write((SocketChannel) supplier[1], (SSLEngine) supplier[2], msg);
							break;

						}
						break;
					}
					return 1;
				}
			}

			System.out.println();
			System.out.println(data);

			write(socketChannel, engine, data);
			return 1;
		} else {
			return -1;
		}
	}
	
	private void addUserTable(User newUser) {
		if(newUser == null) {
			return;
		}
		User[] newUserTable = new User[remoteUserTable.length + 1];
		
		for(int i = 0; i < remoteUserTable.length; i++) {
			if(remoteUserTable[i] != null) {
				newUserTable[i] = remoteUserTable[i];
			}
		}
		
		newUserTable[remoteUserTable.length] = newUser;
		this.remoteUserTable = newUserTable;
	}
	
	private User getID2User(String ID) {
		if(ID == null) {
			return null;
		}
		
		for(int i = 0; i < remoteUserTable.length; i++) {
			try {
				if(remoteUserTable[i].getID().equals(ID)) {
					return remoteUserTable[i];
				}
			} catch (RemoteException e) {
				System.out.println("[Method] getID2User error: remoteException");
			}
		}
		
		return null;
		
	}

	private Object[] getSupplier(String supplierClientID) {
		if (supplierNet == null) {
			return null;
		}

		for (int i = 0; i < supplierNet.length; i++) {
			if (supplierNet[i][0].equals(supplierClientID)) {
				return supplierNet[i];
			}
		}
		return null;
	}

	private Object[] getRequester(String requesterClientID) {
		if (requesterNet == null) {
			return null;
		}

		for (int i = 0; i < requesterNet.length; i++) {
			if (requesterNet[i][0].equals(requesterClientID)) {
				return requesterNet[i];
			}
		}
		return null;
	}

	private void addPayList(String rID, String payCashunut) {
		if(payList == null) {
			payList = new String[1][2];
			payList[0][0] = rID;
			payList[0][1] = payCashunut;
		}
		
		String[][] newPayList = new String[payList.length + 1][2];
		
		for(int i = 0; i < payList.length; i++) {
			newPayList[i] = payList[i];
		}
		
		newPayList[payList.length][0] = rID;
		newPayList[payList.length][1] = payCashunut;
		
		this.payList = newPayList;
	}
	
	private void delPayList(String rID) {
		if ((payList == null) || (payList.length - 1) < 1) {
			return;
		}

		String[][] newPayList = new String[payList.length - 1][2];
		for (int i = 0; i < payList.length; i++) {
			if (!rID.equals(payList[i][0])) {
				newPayList[i] = payList[i];
			}
		}
		this.payList = newPayList;
	}
	
	private void delPayList(int index) {
		if ((payList == null) || (payList.length - 1) < 1) {
			return;
		}

		String[][] newPayList = new String[payList.length - 1][2];
		for (int i = 0; i < newPayList.length; i++) {
			if (i != index) {
				newPayList[i] = payList[i];
			}
		}
		this.payList = newPayList;
	}
	
	private void addSupplierNet(String clientcID, SocketChannel socketChannel, SSLEngine engine) {
		try {
			if (supplierNet == null) {
				supplierNet = new Object[1][3];
				supplierNet[0][0] = (String) clientcID;
				supplierNet[0][1] = (SocketChannel) socketChannel;
				supplierNet[0][2] = (SSLEngine) engine;

				return;
			}
			Object[][] newTable = new Object[supplierNet.length + 1][3];
			for (int i = 0; i < supplierNet.length; i++) {
				newTable[i] = supplierNet[i];
			}
			newTable[supplierNet.length][0] = (String) clientcID;
			newTable[supplierNet.length][1] = (SocketChannel) socketChannel;
			newTable[supplierNet.length][2] = (SSLEngine) engine;

			this.supplierNet = newTable;
		} catch (Exception e) {
			System.out.println("appSupplierNet error: " + e);
			e.printStackTrace();
			return;
		}
	}

	private void addRequesterNet(String clientcID, SocketChannel socketChannel, SSLEngine engine) {
		try {
			if (requesterNet == null) {
				requesterNet = new Object[1][3];
				requesterNet[0][0] = (String) clientcID;
				requesterNet[0][1] = (SocketChannel) socketChannel;
				requesterNet[0][2] = (SSLEngine) engine;

				return;
			}
			Object[][] newTable = new Object[requesterNet.length + 1][3];
			for (int i = 0; i < requesterNet.length; i++) {
				newTable[i] = requesterNet[i];
			}
			newTable[requesterNet.length][0] = (String) clientcID;
			newTable[requesterNet.length][1] = (SocketChannel) socketChannel;
			newTable[requesterNet.length][2] = (SSLEngine) engine;

			this.requesterNet = newTable;
		} catch (Exception e) {
			System.out.println("appRequesterNet error: " + e);
			e.printStackTrace();
			return;
		}
	}

	private int delSupplierNet(String supplierClientID) {
		if ((supplierNet == null) || (supplierNet.length - 1) < 1) {
			return -1;
		}
		int index = 0;

		Object[][] newTable = new Object[supplierNet.length - 1][3];
		for (int i = 0; i < supplierNet.length; i++) {
			if (!supplierClientID.equals(supplierNet[i][0])) {
				newTable[i] = supplierNet[i];
			}
			else {
				index = i;
			}
		}
		this.supplierNet = newTable;
		trade--;
		return index;
	}

	private void delRequesterNet(String requesterClientID) {
		if ((requesterNet == null) || (requesterNet.length - 1) < 1) {
			return;
		}

		Object[][] newTable = new Object[requesterNet.length - 1][3];
		for (int i = 0; i < requesterNet.length; i++) {
			if (!requesterClientID.equals(requesterNet[i][0])) {
				newTable[i] = requesterNet[i];
			}
		}
		this.requesterNet = newTable;
	}

	boolean loginProcess(String isRegister, String ID, String PW) {
		int loginResult = -1; // -1: cannot login, 0: register success, 1: login success, 2: register
								// failed(same ID exists), 3: login failed
		Login login = new Login(isRegister, ID, PW);

		loginResult = login.loginRegister(fm);
		switch (loginResult) {
		case -1:
			System.out.println(": cannout login");
			return false;
		case 0:
			System.out.println(": register success");
			fm.loginFileSave();
			return true;
		case 1:
			System.out.println(": login success");
			isAuthed = true;
			return true;
		case 2:
			System.out.println(": register failed(same ID exists)");
			return false;
		case 3:
			System.out.println(": login failed");
			return false;
		}
		return false;
	}

	int tradeProcess(String ID, String[] tradeArgs) { // Maybe about 16 case will return
		String supplierID = new String();
		String yes = "Yes", no = "No";
		try {
			String requesterID = remoteRequestTable.getRequestID2Request(tradeArgs[0]).getUserID();
		} catch (RemoteException e) {
			System.out.println("[Trade] error: getUserID(from RequestID) error");
			e.printStackTrace();
			return -1;
		}
		if (tradeArgs[0] != null || tradeArgs[3] != null || tradeArgs[5] != null || tradeArgs[10] != null) { // process
																												// from
																												// requester
			supplierID = ID;

			if (tradeArgs[3].equals(yes)) { // requesterYes
				return 2;
			} else if (tradeArgs[3].equals(no)) { // requesterYes
				return -2;
			}
			if (tradeArgs[5].equals(yes)) { // tradeRYes YY
				if (!tradeArgs[5].equals(tradeArgs[4])) { // YN
					return 10;
				}
				return 4;
			} else if (tradeArgs[5].equals(no)) { // tradeRYes NN
				if (!tradeArgs[5].equals(tradeArgs[4])) { // NY
					return 10;
				}
				return -4;
			}
			if (tradeArgs[10].equals(yes)) { // isAuth
				return 5;
			} else if (tradeArgs[10].equals(no)) { // isAuth
				return -5;
			}

		} else if (tradeArgs[2] != null || tradeArgs[4] != null || tradeArgs[11] != null) { // process from supplier
			supplierID = ID;

			if (tradeArgs[2].equals(yes)) { // supplierYes
				return 1;
			}
			if (tradeArgs[4].equals(yes)) { // tradeSYes
				return 3;
			} else if (tradeArgs[4].equals(no)) { // tradeSYes
				return -3;
			}
			if (tradeArgs[11] != null) { // authTry
				return 6;
			}
		}

		return -1;
	}

	void addAuthedClient(String cID) {
		if (loginedcID == null) {
			loginedcID = new String[1];
			loginedcID[0] = cID;
			return;
		}
		String[] newTable = new String[loginedcID.length + 1];
		for (int i = 0; i < loginedcID.length; i++) {
			newTable[i] = loginedcID[i];
		}
		this.loginedcID = newTable;
	}

	boolean isAuthedClient(String cID) {
		if (cID == null || loginedcID == null) {
			return false;
		}
		for (int i = 0; i < loginedcID.length; i++) {
			if (cID.equals(loginedcID[i])) {
				return true;
			}
		}
		return false;
	}

	String responseIsYes(String userID) throws RemoteException {
		Request[] myRequests = remoteRequestTable.getMyRequest(userID);
		if (myRequests == null) {
			return null;
		}
		for (int i = 0; i < requestIsYes.length; i++) {
			for (int j = 0; j < myRequests.length; j++) {
				if (requestIsYes[i][0].equals(myRequests[j].getRequestID()) && requestIsYes[i][1] == "Y") {
					return requestIsYes[i][0];
				}
			}
		}
		return null;
	}

	void setYesRequest(String requestID) {
		if (requestIsYes == null) {
			return;
		}

		for (int i = 0; i < requestIsYes.length; i++) {
			if (requestIsYes[i][0].equals(requestID)) {
				requestIsYes[i][1] = "Y";
				return;
			}
		}

		System.out.println("there is not exist request: " + requestID);
	}

	void setNoRequest(String requestID) {
		if (requestIsYes == null) {
			return;
		}

		for (int i = 0; i < requestIsYes.length; i++) {
			if (requestIsYes[i][0].equals(requestID)) {
				requestIsYes[i][1] = "N";
				return;
			}
		}

		System.out.println("there is not exist request: " + requestID);
	}

	String[][] invalidateRequestIsYes(RequestTable remoteObj, String[][] requestIsYes) throws RemoteException {
		if (remoteObj.getIsChanged()) {
			String[][] originTable = requestIsYes;
			try {
				@SuppressWarnings("unused")
				int lengthGap = requestIsYes.length - remoteObj.getRequestArray().length;
			} catch (Exception e) {
				return requestIsYes;
			}
			requestIsYes = new String[remoteObj.getRequestArray().length][2];

			String[] rID = new String[requestIsYes.length];
			int i = 0;
			for (; i < requestIsYes.length; i++) { // compare string table
				rID[i] = remoteObj.getRequestArray()[i].getRequestID();
			}
			for (i = 0; i < requestIsYes.length; i++) {
				for (int j = 0; j < originTable.length; j++) { // copy origin to new
					if (rID[j] != null && originTable[j][0].equals(rID[j])) {
						rID[j] = "Deleted";
						requestIsYes[i] = originTable[j];
						break;
					}
				}
			}

			for (; i < requestIsYes.length; i++) { // add new
				for (int j = 0; j < rID.length; j++) {
					if (!rID[j].equals("Deleted")) {
						requestIsYes[i][0] = rID[j];
						requestIsYes[i][1] = "N";
						rID[j] = "Deleted";
						break;
					}
				}
			}

			remoteObj.setIsChanged(false);
		}

		return requestIsYes;
	}

	/**
	 * Will send a message back to a client.
	 *
	 * @param key     - the key dedicated to the socket channel that will be used to
	 *                write to the client.
	 * @param message - the message to be sent.
	 * @throws IOException if an I/O error occurs to the socket channel.
	 */
	@Override
	protected void write(SocketChannel socketChannel, SSLEngine engine, String message) throws IOException {

		myAppData.clear();
		myAppData.put(message.getBytes());
		myAppData.flip();
		while (myAppData.hasRemaining()) {
			// The loop has a meaning for (outgoing) messages larger than 16KB.
			// Every wrap call will remove 16KB from the original message and send it to the
			// remote peer.
			myNetData.clear();
			SSLEngineResult result = engine.wrap(myAppData, myNetData);
			switch (result.getStatus()) {
			case OK:
				myNetData.flip();
				while (myNetData.hasRemaining()) {
					socketChannel.write(myNetData);
				}
				break;
			case BUFFER_OVERFLOW:
				myNetData = enlargePacketBuffer(engine, myNetData);
				break;
			case BUFFER_UNDERFLOW:
				throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
			case CLOSED:
				closeConnection(socketChannel, engine);
				return;
			default:
				throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
			}
		}
	}

	/**
	 * Determines if the the server is active or not.
	 *
	 * @return if the server is active or not.
	 */
	private boolean isActive() {
		return active;
	}

	public static void main(String[] args) {
		SSLEngnServerRunnable serverRunnable = new SSLEngnServerRunnable();
		Thread server = new Thread(serverRunnable);
		server.start();
	}
}