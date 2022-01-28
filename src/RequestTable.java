public interface RequestTable extends java.rmi.Remote {
	public Request getRequestID2Request(String ID) throws java.rmi.RemoteException;
	public Request[] getRequestArray() throws java.rmi.RemoteException;
	public Request[] getOtherRequest(String ID) throws java.rmi.RemoteException;
	public Request[] getMyRequest(String ID) throws java.rmi.RemoteException;
	public String[] showRequest() throws java.rmi.RemoteException;
	public String[] showOtherRequest(String ID) throws java.rmi.RemoteException;
	public String[] showOtherRequest(Request[] otherRequest) throws java.rmi.RemoteException;
	public void addRequest(Request request) throws java.rmi.RemoteException;
	public void addRequest(String requestID, String wantService, String location, String payCashunut, int timeOut, boolean isRent, int rentTime, String userID) throws java.rmi.RemoteException;
	public boolean getIsChanged() throws java.rmi.RemoteException;
	public void setIsChanged(boolean isChanged) throws java.rmi.RemoteException;
}
