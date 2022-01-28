import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RequestTableImpl extends UnicastRemoteObject implements RequestTable{
   private static final long serialVersionUID = 1L;
   private Request[] requestArray = null;
   
   private boolean isChanged = false;

   protected RequestTableImpl() throws RemoteException {
      super();
   }
   
   public Request getRequestID2Request(String requestID) throws RemoteException{
	   Request request = null;
	   Request[] requestArr = getRequestArray();
	   for(int i = 0; i < requestArr.length; i++) {
		   if(requestArr[i].getRequestID().equals(requestID)) {
			   request = requestArr[i];
			   return request;
		   }
	   }
	   return null;
   }
   
   public Request[] getRequestArray()  throws RemoteException{
      return this.requestArray;
   }
   
   public Request[] getOtherRequest(String ID) throws RemoteException{
      if(ID == null) {
         return getRequestArray();
      }
      Request[] requestArray = getRequestArray();
      int otherLength = 0;
      for(int i = 0; i < requestArray.length; i++) {
         if(requestArray[i].getUserID().equals(ID) == false) {
            otherLength++;
         }
      }
      
      Request[] otherRequest = new Request[otherLength];
      int j = 0;
      for(int i = 0; i < requestArray.length; i++) {
            if(requestArray[i] != null && (requestArray[i].getUserID().equals(ID) == false)) {
               otherRequest[j] = requestArray[i];
               j++;
            }
      }
      return otherRequest;
   }
   
   public Request[] getMyRequest(String ID) throws RemoteException{
      if(ID == null) {
         return null;
      }
      Request[] requestArray = getRequestArray();
      Request[] myRequest = new Request[requestArray.length];
      int j = 0;
      for(int i = 0; i < requestArray.length; i++) {
            if(requestArray[i] != null && (requestArray[i].getUserID().equals(ID) == true)) {
               myRequest[j] = requestArray[i];
               j++;
            }
      }
      return myRequest;
   }
   
   public void setRequestTable(FileManage fm) throws RemoteException{
      this.requestArray = fm.getRequestTable();
      //isChanged = true;
   }

   public void addRequest(Request request) {
      if(request.getRequestID() == null) {
         int count = 0;
         try {
            count = getMyRequest(request.getUserID()).length;
         } catch (RemoteException e) {
            e.printStackTrace();
         }
         String newID = request.getUserID() + Integer.toString(count);
         request.setRequestID(newID);
      }
      Request[] newRequestArray = new Request[requestArray.length + 1];
      for(int i = 0; i < requestArray.length; i++) {            // copy origin to new
         newRequestArray[i] = this.requestArray[i];
      }
      newRequestArray[requestArray.length] = request;
      this.requestArray = newRequestArray;
      isChanged = true;
   }

   public void addRequest(String requestID, String wantService, String location, String payCashunut, int timeOut, boolean isRent, int rentTime, String userID) {
      if(requestID == null) {
         int count = 0;
         try {
            count = getMyRequest(userID).length;
         } catch (RemoteException e) {
            e.printStackTrace();
         }
         String newID = userID + Integer.toString(count);
         requestID = newID;
      }
      
      Request request = new Request(requestID, wantService, location, payCashunut, timeOut, isRent, rentTime,userID);
      Request[] newRequestArray = new Request[requestArray.length + 1];
      for(int i = 0; i < requestArray.length; i++) {            // copy origin to new
         newRequestArray[i] = this.requestArray[i];
      }
      newRequestArray[requestArray.length] = request;
      this.requestArray = newRequestArray;
      isChanged = true;
   }
   
   public String[] showRequest() throws RemoteException{
      Request[] requestArray = getRequestArray();
      String[] str = new String[requestArray.length];
      requestArray[0].getSummaryRequestString();
      for(int i = 0; i < requestArray.length; i++) {
         requestArray[i].getSummaryRequestString();
         str[i] = requestArray[i].getSummaryRequestString();
      }
      
      return str;
      
   }
   
   public String[] showOtherRequest(String ID) throws RemoteException{
      Request[] otherRequest = getOtherRequest(ID);
      String[] str = new String[otherRequest.length];
      System.out.println("length: " + otherRequest.length);
      for(int i = 0; i < otherRequest.length; i++) {
         str[i] = otherRequest[i].getSummaryRequestString();
         System.out.println(str[i]);
      }
      
      return str;
   }

   public String[] showOtherRequest(Request[] otherRequest) throws RemoteException{
      String[] str = new String[otherRequest.length];
      for(int i = 0; i < otherRequest.length; i++) {
         str[i] = otherRequest[i].getSummaryRequestString();
      }
      
      return str;
   }
   
   public boolean getIsChanged() throws RemoteException{
	   return this.isChanged;
   }
   
   public void setIsChanged(boolean isChanged) throws RemoteException{
	   this.isChanged = isChanged;
   }
}