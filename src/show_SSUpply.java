
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
      System.out.println("* SSUpply �ŷ� *              \n");
      System.out.println("��û���� ������ ��ٸ��� ���Դϴ�.");
      System.out.println("------------------------------------------------------");
      System.out.println("'q' �ߴ��ϱ�");
      System.out.println("------------------------------------------------------");
   }

   void showSupplyR() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply �ŷ� *              \n");
      System.out.println("�����ڿ� �ŷ��� �Ͻðڽ��ϱ�?");
      System.out.println("------------------------------------------------------");
      System.out.println("'y' �ŷ��ϱ� | 'n' �ߴ��ϱ�");
      System.out.println("------------------------------------------------------");
   }

   void showMatching() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply �ŷ� *              \n");
      System.out.println(show_SSUpply.rqst[client.num - 1].getRequestString());
      System.out.println("�ŷ��� �Դϴ�.");
      System.out.println("------------------------------------------------------");
      System.out.println("'y' �ŷ��Ϸ� | 'n' �ŷ����");
      System.out.println("------------------------------------------------------");
   }

   void showEnd() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply �ŷ� ���� *              \n");
      // System.out.println("�ŷ� ");
      System.out.println("------------------------------------------------------");
      // System.out.println("'y' �ŷ��Ϸ� | 'n' �ŷ����");
      // System.out.println("------------------------------------------------------");
   }

   void showRequest() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply ��û�ϱ� *              \n");
      System.out.println("'q' �������� ���ư���");
      System.out.println("------------------------------------------------------");
   }

   void showLogin() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply Login *              \n");
      System.out.println("'q' �������� ���ư���");
      System.out.println("------------------------------------------------------");
   }

   void showSign() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply ȸ������ *              \n");
      System.out.println("'q' �������� ���ư���");
      System.out.println("------------------------------------------------------");
   }

   void showSelect() {
      System.out.println();
      System.out.println(show_SSUpply.rqst[client.num - 1].getRequestString());
      System.out.println();
      System.out.println("------------------------------------------------------");
      System.out.println("'y' �����ϱ� | 'c' �ݱ�");
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

         // ���� ���� ���
         String uRmi = SSLEngnClient.rmiURL;
         User remoteUser = (User) Naming.lookup(uRmi);

         System.out.println(remoteUser.getID() + " ���� ĳ������ " + remoteUser.getCashunut() + " �� �Դϴ�.");
         System.out.println("------------------------------------------------------\n");
         System.out.println(String.format("%-5s%-14s%-14s%-5s", "��ȣ", "��û��ǰ", "��û���", "ĳ����") + "\n");

         // ��� ���
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
      System.out.println("���ڸ� ���� �󼼺���         ");
      System.out.println("'r' ��û�ϱ� | 'q' ����");
      System.out.println("------------------------------------------------------");
   }

   void showGuest() {
      System.out.println("------------------------------------------------------");
      System.out.println("* SSUpply *              ");
      System.out.println("�α��� ���ּ���.");
      System.out.println("------------------------------------------------------\n");
      System.out.println(String.format("%-5s%-14s%-14s%-5s", "��ȣ", "��û��ǰ", "��û���", "ĳ����") + "\n");
      // System.out.println("\n\n\n\n\n��� ����Ʈ\n\n\n\n\n ");

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
      // System.out.println("���ڸ� ���� �󼼺��� ");
      System.out.println("'l' �α��� | 's' ȸ������ | 'q' ����");
      System.out.println("------------------------------------------------------");
   }
}