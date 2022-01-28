import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManage {

	String IDPWFilePath = "C:\\Users\\msec\\eclipse-workspace\\SSUpply\\data\\IDPW.txt";
	String userFilePath = "C:\\Users\\msec\\eclipse-workspace\\SSUpply\\data\\user.txt";
	String requestTableFilePath = "C:\\Users\\msec\\eclipse-workspace\\SSUpply\\data\\requestTable.txt";
/*
	String IDPWFilePath = "C:\\Users\\Liberty\\Documents\\eclipse-workspace\\RealSSUpply\\SSUpply\\data\\IDPW.txt";
	String userFilePath = "C:\\Users\\Liberty\\Documents\\eclipse-workspace\\RealSSUpply\\SSUpply\\data\\user.txt";
	String requestTableFilePath = "C:\\Users\\Liberty\\Documents\\eclipse-workspace\\RealSSUpply\\SSUpply\\data\\requestTable.txt";
*/	
	private String[] IDTable;
	private String[] PWTable;
	private String[][] userTable;
	// private String[][] userRequestIDTable;	unuse
	private String requestTableStr = null;
	private int countID = 0;
	private int countPW = 0;
	// private int countRIDArray = 0;		unuse
	
	public FileManage() {
		loginFileOpen();
		userFileOpen();
		requestTableFileOpen();
	}
	
	private void close() {
		loginFileSave();
		userFileSave();
	}
	
	public void loginFileOpen() {
		String str;
		BufferedReader bi = null;
		try {
			bi = new BufferedReader(new FileReader(IDPWFilePath));
			str = bi.readLine();
			bi.close();
			
			IDPWTable(str);
		} catch(FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	public void loginFileSave() {
		BufferedWriter bw = null;
		String str = new String();
		String[] IDTable = getIDTable();
		String[] PWTable = getPWTable();
		for(int i = 0; i < IDTable.length; i++) {
			if(IDTable[i] != null && PWTable[i] != null) {
				str += "%ID?" + IDTable[i] + "%PW?" + PWTable[i];
			}
			else {
				break;
			}
		}
		try {
			bw = new BufferedWriter(new FileWriter(IDPWFilePath));
			bw.write(str);
			bw.close();
		} catch(FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	public void userFileOpen() {
		String str;
		BufferedReader bi = null;
		try {
			bi = new BufferedReader(new FileReader(userFilePath));
			str = bi.readLine();
			bi.close();
			
			userIDCashunutTable(str);
			//userRequestIDTable(str);			unuse
		} catch(FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	public void userFileSave() {
		BufferedWriter bw = null;
		String str = new String();
		String[] userIDTable = getUserIDTable();
		String[] userCashunutTable = getUserCashunutTable();
		for(int i = 0; i < userIDTable.length; i++) {
			if(userIDTable[i] != null) {
				str += "%ID?" + userIDTable[i] + "%cashunut?" + userCashunutTable[i];
				/*		unuse
				for(int j = 0; j < userRequestIDTable[i].length; j++) {
					str += "%rID?" + userRequestIDTable[i][j];
				}
				*/
			}
			else {
				break;
			}
		}
		try {
			bw = new BufferedWriter(new FileWriter(userFilePath));
			bw.write(str);
			bw.close();
		} catch(FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	public void userFileSave(String str) {
		BufferedWriter bw = null;
		userIDCashunutTable(str);
		try {
			bw = new BufferedWriter(new FileWriter(userFilePath));
			bw.write(str);
			bw.close();
		} catch(FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	public void requestTableFileOpen() {
		String str;
		BufferedReader bi = null;
		try {
			bi = new BufferedReader(new FileReader(requestTableFilePath));
			str = bi.readLine();
			bi.close();

			this.requestTableStr = str;
		} catch(FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	public void requestTableFileSave(Request[] request) {
		BufferedWriter bw = null;
		String str = new String();
		
		for(int i = 0; i < request.length; i++) {
			if(request[i] != null) {
				str += "%rID?" + request[i].getRequestID() + "%service?" + request[i].getWantService() + "%location?" + request[i].getLocation() + "%pay?" + request[i].getPayCashunut() + "%timeOut?" + request[i].getTimeOut() + "%userID?" + request[i].getUserID();
			}
			else {
				break;
			}
		}
		try {
			bw = new BufferedWriter(new FileWriter(requestTableFilePath));
			bw.write(str);
			bw.close();
		} catch(FileNotFoundException fnfe) {
			System.out.println(fnfe);
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	
	public boolean IDPWTable(String str) {
		StringSlice ss = new StringSlice(str);
		String [] raw = ss.getSlice();
		IDTable = new String[raw.length];
		PWTable = new String[raw.length];
		
		
		for(int i = 0; i < raw.length; i++) {
			if(raw[i].equals("ID")) {
				IDTable[countID] = raw[i+1];
				countID++;
			}
			if(raw[i].equals("PW")) {
				PWTable[countPW] = raw[i+1];
				countPW++;
			}
		}
		if(countID != countPW) {
			System.out.println("loginFileOpen error: ID - PW counts are not same.");
			return false;
		}
		
		return true;
	}
	
	public void userIDCashunutTable(String str) {
		StringSlice ss = new StringSlice(str);
		String [] raw = ss.getSlice();
		int count = 0;

		for(int i = 0; i < raw.length; i++) {
			if("ID".equals(raw[i])) {
				count++;
			}
		}
		
		userTable = new String[count][2];
		int countID = 0;
		int countCashunut = 0;
		for(int i = 0; i < raw.length; i++) {
			if("ID".equals(raw[i])) {
				userTable[countID][0] = raw[i+1];
				countID++;
			}
			if("cashunut".equals(raw[i])) {
				userTable[countCashunut][1] = raw[i+1];
				countCashunut++;
			}
		}
		printUserCashunutTable();
	}

	
	/*		unuse
	public void userRequestIDTable(String str) {
		StringSlice ss = new StringSlice(str);
		String [] raw = ss.getSlice();
		userRequestIDTable = new String[userIDTable.length][];
		int countRID;
		
		int j = 4, k = 0;
		boolean isNeedNew = true;
		for(int i = 4; i < raw.length; i++) {	// %ID ?examples %cashunut ?2500 %rID ?100
			countRID = 0;
			
			while((j+1) < raw.length && raw[j].equals("ID") != true ) {
				isNeedNew = true;
				if(raw[j].equals("rID")) {
					countRID++;
				}
				if(countRID > 50) {		// for server safe
					break;
				}
				j++;
			}
			
			if(j >= raw.length) {
				break;
			}
			
			if(isNeedNew) {
				userRequestIDTable[countRIDArray] = new String[countRID];
				countRIDArray++;
				k = 0;
				isNeedNew = false;
			}
			
			if(raw[i].equals("rID")) {
				userRequestIDTable[countRIDArray - 1][k] = raw[i+1];
				k++;
			}
			else if(raw[i].equals("ID")){
				j++;
			}
			
		}	
		printUserRequestIDTable();
	}
	*/
	
	public String[] getIDTable() {
		return this.IDTable;
	}

	public String[] getPWTable() {
		return this.PWTable;
	}
	
	public String[][] getUserTable() {
		return this.userTable;
	}
	
	/*		unuse
	public String[][] getUserRequestIDTable() {
		return this.userRequestIDTable;
	}
	*/
	
	public String[] getID2User(String ID) {
		if(ID == null){
			return null;
		}
		String[] user = null;
		int i = 0;
		for(int i = 0; i < userTable.length; i++) {
			if(ID.equals(userTable[i])) {
				user = new String[2];
				user = userTable[i];
				return user;
			}
		}
		
		return null;
	}
	

	private String getRequestTableStr() {
		return this.requestTableStr;
	}
	
	public Request[] getRequestTable() {
		String str = getRequestTableStr();
		if(str == null) {
			return null;
		}
		
		StringSlice ss = new StringSlice(getRequestTableStr());
		String [] raw = ss.getSlice();
		Request[] requestTable = null;
		Request request = new Request();
		
		int tableCount = 0;
		for(int i = 0; i < raw.length; i++) {
			if(raw[i].equals("rID")) {
				tableCount++;
			}
		}
		requestTable = new Request[tableCount];
		tableCount = 0;
		
		for(int i = 0; i + 1 < raw.length; i++) {
			if(raw[i] != null) {
				if(raw[i+1] != null) {
					switch(raw[i]) {
						case "rID":
							request.setRequestID(raw[i+1]);
							break;
						case "service":
							request.setWantService(raw[i+1]);
							break;
						case "location":
							request.setLocation(raw[i+1]);
							break;
						case "pay":
							request.setPayCashunut(raw[i+1]);
							break;
						case "timeOut":
							request.setTimeOut(Integer.parseInt(raw[i+1]));
							break;
							/*				unuse
						case "isRent":
							if(raw[i+1].equals("true")) {
								request.setIsRent(true);
							}
							else if(raw[i+1].equals("false")) {
								request.setIsRent(false);
							}
							break;
						case "rentTime":
							if(raw[i+1].equals(""))
								break;
							
							request.setRentTime(Integer.parseInt(raw[i+1]));
							break;
							*/
						case "userID":
							request.setUserID(raw[i+1]);
							requestTable[tableCount] = request;
							request = new Request();
							tableCount++;
							break;		
					}
				}
				else {
					requestTable[tableCount] = request;
					request = new Request();
					tableCount++;
				}
			}
		}
		
		return requestTable;
	}
	
	public void addIDPWTable(String ID, String PW) {
		if(countID == countPW) {
			this.IDTable[countID] = ID;
			this.PWTable[countPW] = PW;
			countID++;
			countPW++;
		}
	}
	
	public void printIDPWTable() {
		for(int i = 0; i < getIDTable().length; i++) {
			if(getIDTable()[i] != null)
				System.out.println(getIDTable()[i] + ": " + getPWTable()[i]);
		}
	}
	
	public void printUserCashunutTable() {
		for(int i = 0; i < userTable.length; i++) {
			System.out.println("ID: " + userTable[i][0] + "\ncashunut: " + userTable[i][1]);
		}
	}
	
	/* 		unuse
	public void printUserRequestIDTable() {
		for(int i = 0; i < userRequestIDTable.length; i++) {
			System.out.println("ID: " + userIDTable[i]);
			for(int j = 0; j < userRequestIDTable[i].length; j++) {
				System.out.print(userRequestIDTable[i][j] + " ");
			}
			System.out.println();
		}
	}
	*/
	
	public static void main(String[] args) {
		FileManage fm = new FileManage();
		/*
		String[] strID = fm.getIDTable();
		String[] strPW = fm.getPWTable();
		for(int i = 0; i < strID.length; i++) {
			if(strID[i] != null)
				System.out.println(strID[i] + ": " + strPW[i]);
		}*/
//		fm.userFileSave();
		Request[] requestTable = fm.getRequestTable();
		if(requestTable != null) {
			for(int i = 0; i < requestTable.length; i++) {
				System.out.println(requestTable[i].getRequestString());
			}
		}
	}
}
