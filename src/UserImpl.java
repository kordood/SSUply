import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserImpl extends UnicastRemoteObject implements User{
	private static final long serialVersionUID = 1L;
	
	private String ID;
	private int cashunut;
	
	public UserImpl(String ID) throws RemoteException{
		super();
		this.ID = ID;
		this.cashunut = 3000;
	}

	public void loadUser(String[] user) throws RemoteException{
		this.ID = user[0];
		this.cashunut = Integer.parseInt(user[1]);
	}
	
	public String getID() throws RemoteException{
		return this.ID;
	}
	
	public int getCashunut() throws RemoteException{
		return this.cashunut;
	}
	
	public void setCashunut(int cashunut) throws RemoteException{
		this.cashunut = cashunut;
	}
	
	public void addCashunut(int cashunut, int add) throws RemoteException{
		this.cashunut += cashunut;
	}
	
	public void subCashunut(int cashunut, int sub) throws RemoteException{
		this.cashunut -= cashunut;
	}
	
	public String sayHello(String name) {
		return "Hello World" + name + "!!";
	}
}