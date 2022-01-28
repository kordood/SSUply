import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server {

	ExecutorService executorService;
	ServerSocketChannel serverSocketChannel;
	List<Client> connections = new Vector<Client>();
	int clientNum = -1;
	FileManage fm;
	RequestTableImpl remoteRequestTable;
	String address = "localhost";
//	String address = "192.168.0.98";
	String port = "1099";

	void startServer() {
		 fm = new FileManage();
		 System.setProperty("java.rmi.server.hostname", address);
		 try {
			 remoteRequestTable = new RequestTableImpl();
			 remoteRequestTable.setRequestTable(fm);
			 String rmiURL = "rmi://" + address + ":" + port + "/RequestRemote";
			 try {
				 java.rmi.Naming.rebind(rmiURL, remoteRequestTable);
				 System.out.println(rmiURL);
				 System.out.println("RequestTable Instance send");
			 } catch(Exception e) {
				 System.out.println("rmi error(" + rmiURL + ")" + e);
				 e.printStackTrace();
			 }
		 } catch(RemoteException re) {
			 System.out.println("Server start error: " + re);
			 re.printStackTrace();
		 }
		// 스레드 풀 생성
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(true);
			serverSocketChannel.bind(new InetSocketAddress(5001));

		} catch (Exception e) {
			if (serverSocketChannel.isOpen()) {
				stopServer();
			}
		}

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				System.out.println("[연결 기다림]");

				while (true) {
					try {
						SocketChannel socketChannel = serverSocketChannel.accept();
						InetSocketAddress isa = (InetSocketAddress) socketChannel.getRemoteAddress();
						System.out.println("[연결 수락함] " + isa.getHostName());

						Client client = new Client(socketChannel);
						connections.add(client);

						System.out.println("[연결 개수: " + connections.size() + "]");
					} catch (Exception e) {
						if (serverSocketChannel.isOpen()) {
							stopServer();
						}

						break;
					}
				}
			}
		};

		executorService.submit(runnable);

	}

	void stopServer() {
		try {
			Iterator<Client> iterator = connections.iterator();

			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.socketChannel.close();
				iterator.remove();
			}

			if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
				serverSocketChannel.close();
			}

			if (executorService != null && executorService.isShutdown()) {
				executorService.shutdown();
			}

			fm.loginFileSave();
			fm.userFileSave();
			System.out.println("서버 종료");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	class Client {
		SocketChannel socketChannel;
		int ownClientNum = -1;
		boolean isAuthed = false;

		public Client(SocketChannel socketChannel) {
			this.socketChannel = socketChannel;
			ownClientNum = ++clientNum;
			receive();
		}

		void receive() {
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					String[] arg = null;
					StringSlice ss = new StringSlice();
					String cID = null;
					String clientID = Client.this.toString();
					String err;
					int processResult = 0;
					boolean isRun = true;
					boolean isFirstConnection = true;
					ByteBuffer byteBuffer;
					
					while (isRun) {
						try {
							byteBuffer = ByteBuffer.allocate(150);

							// 클라이언트가 비정상 종료를 했을 경우 IOException 발생
							int byteCount = socketChannel.read(byteBuffer);

							// 클라이언트가 정상적으로 SocketChannel의 close()를 호출헀을 경우
							if (byteCount == -1) {
								throw new IOException();
							}

							String msg = "[요청 처리: " + socketChannel.getRemoteAddress() + ": "
									+ Thread.currentThread().getName() + "]";

							System.out.println(msg);

							byteBuffer.flip();
							Charset charset = Charset.forName("UTF-8");
							String data = charset.decode(byteBuffer).toString();

							if (isFirstConnection == false) {
								ss.setRaw(data);
								arg = ss.getSlice();
								for (int i = 0; i < arg.length; i++) {
									if ("cID".equals(arg[i])) {
										cID = arg[i + 1];
										break;
									}
								}
							}

							/*
							 * for (Client client : connections) { client.send(data); }
							 */
							
							// ================== process between client ======================

							if(isFirstConnection) {
								/*
								 * System.out.println("%cID?" + clientID + data);
								 * System.out.println(ownClientNum);
								 */
								isFirstConnection = false;
								connections.get(ownClientNum).send("%cID?" + clientID + data);
							}
							else {
								if(cID.equals(clientID)){
										processResult = process(ownClientNum, clientID, arg);
									switch(processResult) {
										case -2:
											err = "\nProcess didn't accepted(Invalid format): send again.\n";
											connections.get(ownClientNum).send("%cID?" + clientID + "%err?" + err);
											break;
										case -1:
											err = "\nProcess didn't accepted.(Unknown error): send again.\n";
											connections.get(ownClientNum).send("%cID?" + clientID + "%err?" + err);
											break;
										case 1:
											System.out.println("[" + ownClientNum + "]process completed.");
											break;
										case 2:				// quit
											System.out.println("ByeBye~!");
											connections.remove(Client.this);
											socketChannel.close();
											clientNum--;
											isRun = false;
											break;
									}
								}
								else {
									System.out.println(data);
									System.out.println("compare\n" + clientID + "\n" + cID);
									connections.get(ownClientNum).send("You send wrong connectionID!" + cID);
									/*
									 * connections.remove(Client.this);
									 * socketChannel.close();
									 */
								}
							}
							
							// ================================================================
							
							byteBuffer.clear();
						} catch (Exception e) {
							try {
								isRun = false;
								connections.remove(Client.this);
								clientNum--;

								String msg = "[클라이언트 통신 안됨: " + socketChannel.getRemoteAddress() + ": "
										+ Thread.currentThread().getName() + "]";
								e.printStackTrace();

								System.out.println(msg);
								socketChannel.close();
							} catch (IOException e2) {
								System.out.println(e2);
								e2.printStackTrace();
							}

							break;
						}
					}
				}

			};

			executorService.submit(runnable);
		}

		void send(String data) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						Charset charset = Charset.forName("UTF-8");
						ByteBuffer byteBuffer = charset.encode(data);
						socketChannel.write(byteBuffer);
						byteBuffer.clear();
					} catch (Exception e) {
						try {
							String msg = "[클라이언트 통신 안됨: " + socketChannel.getRemoteAddress() + ": "
									+ Thread.currentThread().getName() + "]";

							System.out.println(msg);
							connections.remove(Client.this);
							socketChannel.close();

						} catch (IOException e2) {
							System.out.println(e2);
						}
					}
				}
			};
			executorService.submit(runnable);
		}

		int process(int ownClientNum, String clientID, String [] arg) throws IOException {
			String data = "%cID?" + clientID;
			String isRegister = null, ID = null, PW = null;
			String rmiURL = new String();
			
			if(ownClientNum > -1 && clientID != null && arg != null) {
				System.out.println("ownClientNum: " + ownClientNum + ", clientID: " + clientID + "\n<args>");
				
				try {
					for(int i = 2; i < arg.length; i++) {		// except %cID? + clientID
						if((i % 2) == 0) {
							data += "%" + arg[i];
						}
						else {
							data += "?" + arg[i];
						}
						System.out.println(arg[i]);
					}
					for(int i = 2; i < arg.length; i+=2) {		// except %cID? + clientID
						switch(arg[i]) {
							case "isQuit":
								try {
									java.rmi.Naming.unbind(rmiURL);
								} catch(NotBoundException nbe) {
									System.out.println("rmi unbind error: " + nbe);
									nbe.printStackTrace();
								}
								return 2;
							case "isRegister":
								isRegister = arg[i+1];
							case "ID":
								ID = arg[i+1];
								break;
							case "PW":
								PW = arg[i+1];
								break;
						}
					}
				} catch(NullPointerException ne) {
					System.out.println("[Method]process error");
					ne.printStackTrace();
					return -2;		// null argument
				}
				
				// =============== login or register ===============
				if(isAuthed != true) {
					if(ID != null || PW != null) {
						boolean isLoginwell = loginProcess(isRegister, ID, PW);
						if(isLoginwell) {
							if(isRegister.equals("false")) {						// when login
								/* ==== send User Instance ==== */
								rmiURL = "rmi://" + address + ":" + port + "/UserRemote" + Integer.toString(clientNum);		// UserRemote0, UserRemote1, UserRemote2, ...
								System.out.println("URL: " + rmiURL);
								try {
									UserImpl remoteObj = new UserImpl(ID);
									String[] user = fm.getUser(ID);
									if(user != null) {
										remoteObj.loadUser(user);
									}
									java.rmi.Naming.rebind(rmiURL, remoteObj);
									System.out.println("User Instance send");
								} catch(Exception e) {
									System.out.println("rmi error" + e);
									e.printStackTrace();
									return -1;
								}
								data = "%cID?" + clientID +"%rmiURL?" + rmiURL;
							}
							else {
								data = "%cID?" + clientID +"%message?Register success!";
							}
							connections.get(ownClientNum).send(data);
							return 1;
						}
					}
				}
				// =================== after login ====================
				else {
				}
				
				System.out.println();
				System.out.println(data);
		
				connections.get(ownClientNum).send(data);
				return 1;
			}
			else {
				return -1;
			}
		}
		
		boolean loginProcess(String isRegister, String ID, String PW) {
			int loginResult = -1;		// -1: cannot login, 0: register success, 1: login success, 2: register failed(same ID exists), 3: login failed
			Login login = new Login(isRegister, ID, PW);
			
			loginResult = login.loginRegister(fm);
			switch(loginResult) {
				case -1:
					System.out.println(ownClientNum + ": cannout login");
					return false;
				case 0:
					System.out.println(ownClientNum + ": register success");
					fm.loginFileSave();
					return true;
				case 1:
					System.out.println(ownClientNum + ": login success");
					isAuthed = true;
					return true;
				case 2:
					System.out.println(ownClientNum + ": register failed(same ID exists)");
					return false;
				case 3:
					System.out.println(ownClientNum + ": login failed");
					return false;
			}
			return false;
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.startServer();
	}

}