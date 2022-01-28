import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

/**
 * An SSL/TLS client that connects to a server using its IP address and port.
 * <p/>
 * After initialization of a {@link SSLEngnClient} object,
 * {@link SSLEngnClient#connect()} should be called, in order to establish
 * connection with the server.
 * <p/>
 * When the connection between the client and the object is established,
 * {@link SSLEngnClient} provides a public write and read method, in order to
 * communicate with its peer.
 *
 * @author <a href="mailto:alex.a.karnezis@gmail.com">Alex Karnezis</a>
 */
public class SSLEngnClient extends NioSslPeer {
   static SSLEngnClient client = null;
   //static String url = "localhost";
    static String url = "127.20.10.7";//"192.168.78.1";//"10.27.24.39";
   static show_SSUpply show = new show_SSUpply();
   // SocketChannel socketChannel;// = null;
   private static Scanner sc = new Scanner(System.in);
   PasswordAuthentication PA = new PasswordAuthentication();
   boolean isFirstConnection = true; // cID 받기 위한 변수
   StringSlice ss = new StringSlice();
   String traderID = null;

   String data = null;
   static String[] request = null;
   boolean login = false;
   static int switchNum = 0;
   static int num;
   static String cID = null;

   static String rmiURL = null;
   static String userID = null;
   static boolean selected = false;

   /**
    * The remote address of the server this client is configured to connect to.
    */
   private String remoteAddress;

   /**
    * The port of the server this client is configured to connect to.
    */
   private int port;

   /**
    * The engine that will be used to encrypt/decrypt data between this client and
    * the server.
    */
   private SSLEngine engine;

   /**
    * The socket channel that will be used as the transport link between this
    * client and the server.
    */
   private SocketChannel socketChannel;

   /**
    * Initiates the engine to run as a client using peer information, and allocates
    * space for the buffers that will be used by the engine.
    *
    * @param protocol
    *            The SSL/TLS protocol to be used. Java 1.6 will only run with up to
    *            TLSv1 protocol. Java 1.7 or higher also supports TLSv1.1 and
    *            TLSv1.2 protocols.
    * @param remoteAddress
    *            The IP address of the peer.
    * @param port
    *            The peer's port that will be used.
    * @throws Exception
    */
   


	String keyFilePath1 = "C:\\Users\\msec\\eclipse-workspace\\SSUpply\\bin\\main\\resources\\client.jks";
	String trustFilePath1 = "C:\\Users\\msec\\eclipse-workspace\\07주차2\\bin\\main\\resources\\trustedCerts.jks";
	
	String keyFilePath2 = "C:\\Users\\Liberty\\Documents\\eclipse-workspace\\RealSSUpply\\SSUpply\\bin\\main\\resources\\client.jks";
	String trustFilePath2 = "C:\\Users\\Liberty\\Documents\\eclipse-workspace\\RealSSUpply\\SSUpply\\bin\\main\\resources\\trustedCerts.jks";
	
   public SSLEngnClient(String protocol, String remoteAddress, int port) throws Exception {

      this.remoteAddress = remoteAddress;
      this.port = port;

      SSLContext context = SSLContext.getInstance(protocol);

      try {
      context.init(
            createKeyManagers(keyFilePath1, "storepass", "keypass"),
            createTrustManagers(trustFilePath1, "storepass"),
            new SecureRandom());
      } catch(FileNotFoundException fnfe) {
          context.init(
                  createKeyManagers(keyFilePath2, "storepass", "keypass"),
                  createTrustManagers(trustFilePath2, "storepass"),
                  new SecureRandom());
      }
      engine = context.createSSLEngine(remoteAddress, port);
      engine.setUseClientMode(true);

      SSLSession session = engine.getSession();
      myAppData = ByteBuffer.allocate(1024);
      myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
      peerAppData = ByteBuffer.allocate(1024);
      peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());
   }

   /**
    * Opens a socket channel to communicate with the configured server and tries to
    * complete the handshake protocol.
    *
    * @return True if client established a connection with the server, false
    *         otherwise.
    * @throws Exception
    */
   public boolean connect() throws Exception {

      socketChannel = SocketChannel.open();

      socketChannel.configureBlocking(false);

      socketChannel.connect(new InetSocketAddress(remoteAddress, port));
      
      while (!socketChannel.finishConnect()) {
         // can do something here...
      }
            System.out.println("tq");

      engine.beginHandshake();

      return doHandshake(socketChannel, engine);
      
   }

   /**
    * Public method to send a message to the server.
    *
    * @param message
    *            - message to be sent to the server.
    * @throws IOException
    *             if an I/O error occurs to the socket channel.
    */
   public void write(String message) throws IOException {
      write(socketChannel, engine, message);
   }

   /**
    * Implements the write method that sends a message to the server the client is
    * connected to, but should not be called by the user, since socket channel and
    * engine are inner class' variables. {@link SSLEngnClient#write(String)} should
    * be called instead.
    *
    * @param message
    *            - message to be sent to the server.
    * @param engine
    *            - the engine used for encryption/decryption of the data exchanged
    *            between the two peers.
    * @throws IOException
    *             if an I/O error occurs to the socket channel.
    */
   @Override
   protected void write(SocketChannel socketChannel, SSLEngine engine, String message) throws IOException {
      // System.out.println(message);
      if (!client.isFirstConnection)
         message = "%cID?" + cID + message;
      // System.out.println(message);
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
            System.out.println("Message sent to the server: " + message);
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
    * Public method to try to read from the server.
    *
    * @throws Exception
    */
   public void read() throws Exception {
      read(socketChannel, engine);
   }

   /**
    * Will wait for response from the remote peer, until it actually gets
    * something. Uses {@link SocketChannel#read(ByteBuffer)}, which is
    * non-blocking, and if it gets nothing from the peer, waits for
    * {@code waitToReadMillis} and tries again.
    * <p/>
    * Just like {@link SSLEngnClient#read(SocketChannel, SSLEngine)} it uses inner
    * class' socket channel and engine and should not be used by the client.
    * {@link SSLEngnClient#read()} should be called instead.
    * 
    * @param message
    *            - message to be sent to the server.
    * @param engine
    *            - the engine used for encryption/decryption of the data exchanged
    *            between the two peers.
    * @throws Exception
    */
   @Override
   protected void read(SocketChannel socketChannel, SSLEngine engine) throws Exception {
      StringSlice ss;
      String[] arg;

      peerNetData.clear();
      int waitToReadMillis = 50;
      boolean exitReadLoop = false;
      while (!exitReadLoop) {
         int bytesRead = socketChannel.read(peerNetData);
         if (bytesRead > 0) {
            peerNetData.flip();
            while (peerNetData.hasRemaining()) {
               peerAppData.clear();
               SSLEngineResult result = engine.unwrap(peerNetData, peerAppData);
               switch (result.getStatus()) {
               case OK:
                  peerAppData.flip();
                  Charset charset = Charset.forName("UTF-8");
                  data = charset.decode(peerAppData).toString();
                  System.out.println(data);

                  ss = new StringSlice(data);
                  arg = ss.getSlice();

                  for (int i = 0; i < arg.length; i++) {
                     System.out.println(arg[i]);
                     if (arg[i].equals("cID")) {
                        cID = arg[i + 1];
                     }
                  }

                  exitReadLoop = true;
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
         } else if (bytesRead < 0) {
            handleEndOfStream(socketChannel, engine);
            return;
         }
         Thread.sleep(waitToReadMillis);
      }
   }

   /**
    * Should be called when the client wants to explicitly close the connection to
    * the server.
    *
    * @throws IOException
    *             if an I/O error occurs to the socket channel.
    */
   public void shutdown() throws IOException {
      closeConnection(socketChannel, engine);
      executor.shutdown();
   }
   ///////////////////////////////////////////////////////////////

   void login() throws Throwable {

      String ID = null;
      String PW = null;
      show.clearScreen();
      show.showLogin();

      while (true) {
         System.out.print("ID : ");
         ID = sc.next();
         if (ID.equals("q")) {
            ID = null;
            show.clearScreen();
            if (client.login)
               show.showMain();
            else
               show.showGuest();
            return;
         }

         System.out.print("PW : ");
         PW = sc.next();
         if (PW.equals("q")) {
            PW = null;
            show.clearScreen();
            if (client.login)
               show.showMain();
            else
               show.showGuest();
            return;
         }
         /////////////////////////////////////
         PW = PA.hash(PW.toCharArray()); // hash
         String RIP = "%isRegister?false%ID?" + ID + "%PW?" + PW;
         System.out.println();
         while (true) {
            client.write(RIP);
            client.read();

            if (data.contains("%rmiURL?")) {
               show.clearScreen();
               System.out.println("+ 로그인에 성공했습니다. +\n");
               login = true;

               userID = ID;

               String[] arg = null;

               ss.setRaw(data);
               arg = ss.getSlice();
               for (int i = 0; i < arg.length; i++) {
                  if ("rmiURL".equals(arg[i])) {
                     rmiURL = arg[i + 1];
                     break;
                  }
               }

               if (client.login)
                  show.showMain();
               else
                  show.showGuest();
               return;
            } else {
               show.clearScreen();
               System.out.println("+ 로그인에 실패했습니다. +\n");
               ID = null;
               PW = null;
               show.showLogin();
               break;
            }
         }

      }
   }

   void sign() throws Exception {
      String ID = null;
      String PW = null;
      String rPW = null;

      show.clearScreen();
      show.showSign();
      while (true) {
         System.out.print("ID : ");
         ID = sc.next();
         while (true) {
            if (ID.equals("q")) {
               ID = null;
               show.clearScreen();
               if (client.login)
                  show.showMain();
               else
                  show.showGuest();
               return;
            } else if (ID.length() >= 2 && ID.length() <= 12 && !ID.contains("%") && !ID.contains("?"))
               break;
            else {
               show.clearScreen();
               System.out.println("+ 아이디로 적합하지 않습니다. (2 ~ 12글자, '%', '?' 사용불가) +\n");
               ID = null;
               show.showSign();
               System.out.print("ID : ");
               ID = sc.next();
            }
         }
         /*
          * // ID 중복 검사 while (true) { ssu.send("ID", ID); ssu.receive();
          * 
          * if (ssu.data.equals("isSame")) { // 서버 검사후 아이디가 중볼될 경우 clearScreen();
          * System.out.println("+ 중복된 아이디입니다. +\n"); ID = null; showSign();
          * System.out.print("ID : "); ID = sc.next(); } else data = null; break; }
          */
         while (true) {
            System.out.print("PW : ");
            PW = sc.next();
            // PW 적합성 검사
            while (true) {
               if (PW.equals("q")) {
                  PW = null;
                  show.clearScreen();
                  if (client.login)
                     show.showMain();
                  else
                     show.showGuest();
                  return;
               } else if (PW.length() >= 8 && PW.length() <= 12 && !PW.contains("%") && !PW.contains("?"))
                  break;
               else {
                  show.clearScreen();
                  System.out.println("+  패스워드로 적합하지 않습니다. (8 ~ 12글자, '%', '?' 사용불가) +\n");
                  PW = null;
                  show.showSign();
                  System.out.print("PW : ");
                  PW = sc.next();
               }
            }

            System.out.print("PW : ");
            rPW = sc.next();
            while (true) {
               if (rPW.equals("q")) {
                  PW = null;
                  show.clearScreen();
                  if (client.login)
                     show.showMain();
                  else
                     show.showGuest();
                  return;
               } else if (PW.compareTo(rPW) != 0) {
                  show.clearScreen();
                  System.out.println("+ 비밀번호가 일치하지 않습니다. +\n");
                  PW = null;
                  show.showSign();
                  break;
               } else if (PW.equals(rPW)) {
                  PW = PA.hash(PW.toCharArray()); // hash
                  String RIP = "%isRegister?" + true + "%ID?" + ID + "%PW?" + PW;

                  while (true) {
                     client.write(RIP);
                     client.read();

                     System.out.println(data);
                     // 실패했을떄
                     // if (!data.equals("isSaved")) { // !저장됨
                     if (!data.equals("isSaved")) {
                        show.clearScreen();
                        System.out.println("+ 회원가입에 실패했습니다. +\n");
                        ID = null;
                        PW = null;
                        if (client.login)
                           show.showMain();
                        else
                           show.showGuest();
                        return;
                     } else
                        break;
                  }
                  show.clearScreen();
                  System.out.println("+ 회원가입이 완료되었습니다. +\n");
                  ID = null;
                  PW = null;
                  if (client.login)
                     show.showMain();
                  else
                     show.showGuest();
                  return;
               }
            } // while

         }
      }

   } // sign

   private void get_request() {

      String requestID = null;
      String wantService = null;
      String location = null;
      String payCashunut = "1000";
      int timeOut = 10;
      boolean isRent = false;
      int rentTime = 0;
      // TODO Auto-generated method stub

      show.clearScreen();
      show.showRequest();

      System.out.print("요청 물품 : ");
      wantService = sc.next();
      if (wantService.equals("q")) {
         wantService = null;
         show.clearScreen();
         if (client.login)
            show.showMain();
         else
            show.showGuest();
         return;
      }
      System.out.print("요청 장소 : ");
      location = sc.next();
      if (location.equals("q")) {
         location = null;
         show.clearScreen();
         if (client.login)
            show.showMain();
         else
            show.showGuest();
         return;
      }
      /*
       * System.out.println("지불할 캐슈넛 : "); payCashunut = sc.next(); if
       * (payCashunut.equals("q")) { payCashunut = null; clearScreen(); showMain();
       * return; }
       */
      /*
       * System.out.println("타임아웃 : "); timeOut = sc.next(); if (timeOut.equals("q"))
       * { timeOut = null; clearScreen(); showMain(); return; }
       */
      /*
       * while (true) {
       * 
       * System.out.print("요청 종류(1 : 일반 요청, 2 : 빌리기) : "); // int get_isRent =
       * sc.nextInt(); while (!sc.hasNextInt()) { // 값이 숫자인지 판별 sc.next(); // 값이 숫자가
       * 아니면 버린다. System.err.println("잘못된 입력입니다. ");
       * System.out.print("요청 종류(1 : 일반 요청, 2 : 빌리기) : "); }
       * 
       * int get_isRent = sc.nextInt();
       * 
       * if (get_isRent == 1) { isRent = false; show.clearScreen();
       * ///////////////////////
       * 
       * try { String rmi = "rmi://" + SSUpply.url + ":1099/RequestRemote";
       * RequestTable remoteObj = (RequestTable) Naming.lookup(rmi);
       * 
       * remoteObj.addRequest(requestID, wantService, location, payCashunut, timeOut,
       * isRent, rentTime, userID);
       * 
       * } catch (MalformedURLException | RemoteException | NotBoundException e) { //
       * TODO Auto-generated catch block e.printStackTrace(); }
       * 
       * /////////////////////// System.out.println("+ 요청이 완료되었습니다. + "); if
       * (client.login) show_SSUpply.showMain(); else show_SSUpply.showGuest();
       * return; } else if (get_isRent == 2) { isRent = true; break; }
       * System.out.println("잘못된 입력입니다. "); }
       */
      /*
       * if (isRent) { while (true) { System.out.print("반납 시간 (0 ~ 23): "); //
       * rentTime = sc.nextInt(); // 수정사항 : Int아닐때 값 다시 받기 while (!sc.hasNextInt()) {
       * // 값이 숫자인지 판별 sc.next(); // 값이 숫자가 아니면 버린다.
       * System.err.println("잘못된 입력입니다. "); System.out.print("반납 시간 (0 ~ 23): "); }
       * 
       * rentTime = sc.nextInt(); if (rentTime >= 0 && rentTime <= 23) {
       * show.clearScreen(); try { String rmi = "rmi://" + SSUpply.url +
       * ":1099/RequestRemote"; RequestTable remoteObj = (RequestTable)
       * Naming.lookup(rmi);
       * 
       * remoteObj.addRequest(requestID, wantService, location, payCashunut, timeOut,
       * isRent, rentTime, userID);
       * 
       * } catch (MalformedURLException | RemoteException | NotBoundException e) { //
       * TODO Auto-generated catch block e.printStackTrace(); }
       * System.out.println("+ 요청이 완료되었습니다. + "); if (client.login)
       * show_SSUpply.showMain(); else show_SSUpply.showGuest(); return; }
       * System.out.println("잘못된 입력입니다. ");
       * 
       * }
       * 
       * }
       */

      /*
       * try { User remoteObj = (User)
       * Naming.lookup("rmi://localhost:1099/UserRemote");
       * 
       * //Request r = new Request("requestID", wantService, location, isRent,
       * rentTime); //remoteObj.addRequest(r); //System.out.println("Message : " +
       * msg);
       * 
       * } catch (MalformedURLException | RemoteException | NotBoundException e) { //
       * TODO Auto-generated catch block e.printStackTrace(); }
       */

      // Request r = new Request("requestID", wantService, location, isRent,
      // rentTime);
      show.clearScreen();
      try {
         String rmi = "rmi://" + client.url + ":1099/RequestRemote";
         RequestTable remoteObj = (RequestTable) Naming.lookup(rmi);

         remoteObj.addRequest(requestID, wantService, location, payCashunut, timeOut, isRent, rentTime, userID);

      } catch (MalformedURLException | RemoteException | NotBoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      System.out.println("+ 요청이 완료되었습니다. + ");
      if (client.login)
         show.showMain();
      else
         show.showGuest();
      return;
      /*
       * 추가 사항 : 서버에 전송하기
       */

   }

   public void supplySupplier() throws Exception {
      String select;
      // System.out.println(show.rqst[client.num - 1].getRequestID());
      client.write("%supplierYes?Yes%rID?" + show.rqst[client.num - 1].getRequestID() + "%traderID?" + traderID);// show.rqst[client.num
                                                                                       // -
                                                                                       // 1].getUserID());
      client.read(); // 요청자의 yes를 기다림

      // 대기 화면
      while (true) {
         /*
          * select = sc.next();
          * 
          * if (select.equals("q")) { show.clearScreen();
          * System.out.println("+ 거래 취소 +"); show.showMain(); return; } else
          */if (data.equals("%requesterYes?No")) {
            show.clearScreen();
            System.out.println("+ 거래 실패 +");
            show.showMain();
            return;
         } /*
             * else { show.clearScreen(); System.out.println("+  잘못된 입력입니다. +");
             * show.showSupply(); }
             */
         // 거래 화면
         while (true) {
            if (data.equals("%requesterYes?Yes") || data.equals("%reTrade?Yes")) {
               show.clearScreen();
               show.showMatching();
               while (true) {
                  select = sc.next();
                  if (select.equals("n")) {
                     client.write("%tradeSYes?No%traderID?" + client.traderID);
                     client.read();
                     if (data.equals("%isFinish?Yes")) {
                        // N N
                        show.clearScreen();
                        System.out.println("+ 거래 취소 +");
                        show.showMain();
                        return;
                     } else if (data.equals("%reTrade?Yes")) {
                        // N Y
                        System.out.println("+ 상대방이 수락하였습니다. 다시 입력해 주세요. +");
                        break;
                     }
                  } else if (select.equals("y")) {
                     client.write("%tradeSYes?Yes%traderID?" + client.traderID);
                     client.read();
                     if (data.equals("%authStart?Yes")) {
                        // Y Y
                        show.clearScreen();
                        System.out.println("+ 거래 인증번호를 입력해 주세요.  +");
                        show.showEnd();
                        while (true) {
                           System.out.print("인증 번호 : ");
                           String auth = sc.next();
                           client.write("%authTry?" + auth + "%traderID?" + traderID);
                           client.read();
                           if (data.equals("%isPass?Yes")) {
                              show.clearScreen();
                              System.out.println("+ 거래 완료 +");
                              show.showMain();
                              return;
                           } else {
                              show.clearScreen();
                              System.out.println("+ 인증코드 오류 +");
                              show.showEnd();
                           }
                        }
                     } else if (data.equals("%reTrade?Yes")) {
                        // N Y
                        System.out.println("+ 상대방이 거절하였습니다. 다시 입력해 주세요. +");
                        break;
                     }
                  } else {
                     show.clearScreen();
                     System.out.println("+  잘못된 입력입니다. +");
                     show.showMatching();
                  }
               }
            }
         }

      }
      // 요청자가 yes했을때
      // 거래화면

   }

   public void supplyRequester() throws Exception {
      show.clearScreen();
      show.showSupplyR();
      String select;

      while (true) {
         select = sc.next();
         if (select.equals("n")) {
            client.write("%requesterYes?No%traderID?" + client.traderID);
            client.read();

            show.clearScreen();
            System.out.println("+ 거래 취소 +");
            show.showMain();
            return;

         } else if (select.equals("y")) {
            client.write("%requesterYes?Yes%traderID?" + client.traderID);
            client.read();

            show.clearScreen();
            show.showMatching();
            while (true) {
               select = sc.next();
               if (select.equals("n")) {
                  client.write("%tradeRYes?No%traderID?" + client.traderID);
                  client.read();
                  if (data.equals("%isFinish?Yes")) {
                     // N N
                     show.clearScreen();
                     System.out.println("+ 거래 취소 +");
                     show.showMain();
                     return;
                  } else if (data.equals("%reTrade?Yes")) {
                     // N Y
                     System.out.println("+ 상대방이 수락하였습니다. 다시 입력해 주세요. +");
                     break;
                  }
               } else if (select.equals("y")) {
                  client.write("%tradeRYes?Yes%traderID?" + client.traderID);
                  client.read();
                  if (data.contains("%authCode?")) {
                     String[] arg;
                     String authCode = null;

                     client.ss.setRaw(data);
                     arg = ss.getSlice();
                     for (int i = 0; i < arg.length; i++) {
                        if (arg[i].equals("authCode"))
                           authCode = arg[i + 1];
                     }

                     System.out.println("인증코드 : "+authCode);

                     client.write("%isAuth?Yes");
                     client.read();

                     if (data.equals("isAuthed")) {
                        show.clearScreen();
                        System.out.println("+ 거래 완료 +");
                        show.showMain();
                        return;
                     }

                  } else if (data.equals("%reTrade?Yes")) {
                     // N Y
                     System.out.println("+ 상대방이 거절하였습니다. 다시 입력해 주세요. +");
                     break;
                  }
               } else {
                  show.clearScreen();
                  System.out.println("+  잘못된 입력입니다. +");
                  show.showMatching();
               }
            }
         }
      }
   }
   //////////////////////////////////////////////////////////////////

   public static void main(String[] args) throws Throwable {
      show.clearScreen();
      Scanner scanner = new Scanner(System.in);

      // System.setProperty("javax.net.debug", "all");

      try {
         client = new SSLEngnClient("TLSv1.2", client.url, 9222);

         String str = "null";
         client.connect();
         boolean run = true;

         if (client.login)
            show.showMain();
         else
            show.showGuest();
         while (run) {
            if (client.login) {
               client.write("%isYes?" + userID);
               client.read();
               // 내가 받은 값이 %traderID?__ 일때
               if (client.data.contains("%isYesReact?Yes")) {
                  // traderID 슬라이스 하세요.
                  String[] arg;
                  client.ss.setRaw(client.data);
                  arg = client.ss.getSlice();
                  for (int i = 0; i < arg.length; i++) {
                     if (arg[i].equals("traderID"))
                        client.traderID = arg[i + 1];
                  }
                  client.supplyRequester();
               }
            }
            String select = sc.next();

            // 첫 연결 후 메뉴 진입시 cID 받아오기
            if (client.isFirstConnection) {
               client.write("%?");
               client.read();
               client.isFirstConnection = false;
            }

            if (client.login) {
               switch (select) {
               case "q":
                  sc.close();
                  show.clearScreen();
                  System.out.println("+ SSUpply를 종료합니다. +\n");
                  client.write("%isQuit?true");
                  // ssu.receive();
                  show.showMain();
                  run = false;
                  break;
               case "r":
                  client.get_request();
                  break;

               default:
                  CheckNum a = new CheckNum();
                  if (a.isNumber(select)) {

                     num = Integer.parseInt(select);
                     if (num >= 0 && num <= switchNum) {
                        show.clearScreen();
                        selected = true;
                        show.showMain();
                        show.showSelect();
                        break;
                     }
                  }
                  if (selected) {
                     if (select.equals("c")) {
                        show.clearScreen();
                        
                        show.showMain();
                        break;
                     } else if (select.equals("y")) {
                        show.clearScreen();
                        show.showSupply();
                        client.traderID = show.rqst[client.num - 1].getUserID();
                        System.out.println("traderID = " + client.traderID);
                        client.supplySupplier();
                        break;
                     }
                  }
                  show.clearScreen();
                  System.out.println("+ 잘못된 입력입니다. +\n");
                  if (client.login)
                     show.showMain();
                  else
                     show.showGuest();
               }

            } else {
               switch (select) {
               case "q":
                  sc.close();
                  show.clearScreen();
                  System.out.println("+ SSUpply를 종료합니다. +\n");
                  client.write("%isQuit?true");
                  client.read();
                  // ssu.receive();
                  show.showMain();
                  run = false;
                  break;

               case "s":
                  client.sign();
                  break;
               case "l":
                  client.login();
                  break;
               default:
                  show.clearScreen();
                  System.out.println("+ 잘못된 입력입니다. +\n");
                  if (client.login)
                     show.showMain();
                  else
                     show.showGuest();
               }
            }

         }
         /*
          * try { while (!str.equals("quit")) { str = scanner.nextLine(); if (cID !=
          * null) { client.write("%cID?" + cID + str); } else { client.write(str); }
          * client.read(); } scanner.close(); } catch (Exception e) {
          * client.write("CLOSED"); scanner.close(); }
          */

         System.out.println("quit");
         sc.close();
         client.shutdown();
      } catch (Exception e) {
         System.out.println("Client close");
         client.shutdown();
      }

   }

}