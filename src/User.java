public interface User extends java.rmi.Remote{
	public String sayHello(String name) throws java.rmi.RemoteException;
	public void loadUser(String[] user) throws java.rmi.RemoteException;
	public String getID() throws java.rmi.RemoteException;
	public int getCashunut() throws java.rmi.RemoteException;
	void setCashunut(int cashunut) throws java.rmi.RemoteException;
	void addCashunut(int cashunut, int add) throws java.rmi.RemoteException;
	void subCashunut(int cashunut, int sub) throws java.rmi.RemoteException;
}