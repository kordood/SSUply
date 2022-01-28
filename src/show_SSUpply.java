
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class show_SSUpply {
   SSLEngnClient client;

   String[] request = null;
   static Request[] rqst = null;

   void clearScreen() {
      for (int i = 0; i < 80; i++)
         System.out.println("");
   }

   void showSupply() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply 거래 *              \n");
      System.out.println("요청자의 수락을 기다리는 중입니다.");
      System.out.println("------------------------------------------------------");
      System.out.println("'q' 중단하기");
      System.out.println("------------------------------------------------------");
   }

   void showSupplyR() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply 거래 *              \n");
      System.out.println("제공자와 거래를 하시겠습니까?");
      System.out.println("------------------------------------------------------");
      System.out.println("'y' 거래하기 | 'n' 중단하기");
      System.out.println("------------------------------------------------------");
   }

   void showMatching() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply 거래 *              \n");
      System.out.println(show_SSUpply.rqst[client.num - 1].getRequestString());
      System.out.println("거래중 입니다.");
      System.out.println("------------------------------------------------------");
      System.out.println("'y' 거래완료 | 'n' 거래취소");
      System.out.println("------------------------------------------------------");
   }

   void showEnd() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply 거래 인증 *              \n");
      // System.out.println("거래 ");
      System.out.println("------------------------------------------------------");
      // System.out.println("'y' 거래완료 | 'n' 거래취소");
      // System.out.println("------------------------------------------------------");
   }

   void showRequest() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply 요청하기 *              \n");
      System.out.println("'q' 메인으로 돌아가기");
      System.out.println("------------------------------------------------------");
   }

   void showLogin() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply Login *              \n");
      System.out.println("'q' 메인으로 돌아가기");
      System.out.println("------------------------------------------------------");
   }

   void showSign() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply 회원가입 *              \n");
      System.out.println("'q' 메인으로 돌아가기");
      System.out.println("------------------------------------------------------");
   }

   void showSelect() {
      System.out.println();
      System.out.println(show_SSUpply.rqst[client.num - 1].getRequestString());
      System.out.println();
      System.out.println("------------------------------------------------------");
      System.out.println("'y' 수락하기 | 'c' 닫기");
      System.out.println("------------------------------------------------------");

      /*
       * String selected = show_SSUpply.rqst[SSUpply.num - 1].getRequestString();
       * System.out.println(selected); StringSlice ss = new StringSlice(selected);
       * String[] arg = ss.getSlice(); //for(int i = 0; i < arg.length; i ++)
       * System.out.println(arg[i]); String selList[] = new String[arg.length];
       * for(int i = 0; i < arg.length; i++) { if("rID".equals(arg[i])) selList[i] =
       * arg[i + 1]; else if("want".equals(arg[i])) } for(int i = 0; i <
       * selList.length; i++) System.out.println(selList[i]);
       */
   }

   void showMain() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply *              ");

      try {

         // 유저 정보 출력
         String uRmi = SSLEngnClient.rmiURL;
         User remoteUser = (User) Naming.lookup(uRmi);

         System.out.println(remoteUser.getID() + " 님의 캐슈넛은 " + remoteUser.getCashunut() + " 개 입니다.");
         System.out.println("------------------------------------------------------\n");
         System.out.println(String.format("%-5s%-14s%-14s%-5s", "번호", "요청물품", "요청장소", "캐슈넛") + "\n");

         // 목록 출력
         String rmi = "rmi://" + client.url + ":1099/RequestRemote";

         RequestTable remoteObj = (RequestTable) Naming.lookup(rmi);
         // System.out.println(rmi);

         // Request[] requestArray = null; remoteObj.loadUser("asdf", 20000,
         // requestArray); System.out.println("msg");

         String[] msg = remoteObj.showOtherRequest(client.userID);

         rqst = remoteObj.getOtherRequest(client.userID);// (SSUpply.userID);
         request = new String[rqst.length];
         client.switchNum = rqst.length;
         for (int i = 0; i < msg.length; i++) {
            request[i] = rqst[i].getRequestString();
            System.out.println(String.format("%-1s%-6s%-44s", " ", i + 1, msg[i]));

         }

         // System.out.println("Message : " + msg);

      } catch (MalformedURLException | RemoteException | NotBoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      System.out.println("\n------------------------------------------------------");
      System.out.println("숫자를 눌러 상세보기         ");
      System.out.println("'r' 요청하기 | 'q' 종료");
      System.out.println("------------------------------------------------------");
   }

   void showGuest() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply *              ");
      System.out.println("로그인 해주세요.");
      System.out.println("------------------------------------------------------\n");
      System.out.println(String.format("%-5s%-14s%-14s%-5s", "번호", "요청물품", "요청장소", "캐슈넛") + "\n");
      // System.out.println("\n\n\n\n\n목록 프린트\n\n\n\n\n ");

      try {
         String rmi = "rmi://" + client.url + ":1099/RequestRemote";
         RequestTable remoteObj = (RequestTable) Naming.lookup(rmi);
         // System.out.println(rmi);

         // Request[] requestArray = null; remoteObj.loadUser("asdf", 20000,
         // requestArray); System.out.println("msg");

         String[] msg = remoteObj.showOtherRequest(client.userID);

         /*
          * Request[] rqst = remoteObj.getOtherRequest(SSUpply.userID);//
          * (SSUpply.userID);
          * 
          * String[] request = new String[rqst.length];
          */
         for (int i = 0; i < msg.length; i++) {
            // request[i] = request[i];
            // System.out.println(request[i]);
            // request[i] = rqst[i].getRequestString();
            // System.out.println(request[i]);
            System.out.println(String.format("%-1s%-6s%-44s", " ", i + 1, msg[i]));

         }
         // Request[] rqst = remoteObj.getOtherRequest(SSUpply.userID);

         // System.out.println("Message : " + msg);

      } catch (MalformedURLException | RemoteException | NotBoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      // System.out.println("");
      System.out.println("\n------------------------------------------------------");
      // System.out.println("숫자를 눌러 상세보기 ");
      System.out.println("'l' 로그인 | 's' 회원가입 | 'q' 종료");
      System.out.println("------------------------------------------------------");
   }
}