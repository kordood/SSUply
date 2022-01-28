public class Login{
	private String isRegister = "false";
	private String ID = null;
	private String PW = null;
	
	public Login() {
	}
	
	public Login(String packet) {
		StringSlice ss = new StringSlice(packet);
		String[] str = ss.getSlice();
		
		for(int i = 0; i < str.length; i++) {
			if(str[i].equals("isRegister")) {
				setID(str[i+1]);
			}
			if(str[i].equals("ID")) {
				setID(str[i+1]);
			}
			if(str[i].equals("PW")) {
				setPW(str[i+1]);
			}
		}
	}

	public Login(String isRegister, String ID, String PW) {
		setIsRegister(isRegister);
		setID(ID);
		setPW(PW);
	}
	private void setIsRegister(String isRegister) {
		this.isRegister = isRegister;
	}

	private void setID(String ID) {

		if (validID(ID))
			this.ID = ID;
	}

	private void setPW(String PW) {

		if (validPW(PW))
			this.PW = PW;
	}

	private boolean validID(String ID) {
		if (ID.length() < 1 || ID.length() > 12) {
			System.out.println("setID error: " + ID.length() + "is Invalid ID length");
			return false;
		}

		char cmp1 = '%';
		char cmp2 = '?';
		for (int i = 0; i < ID.length(); i++) {
			if (cmp1 == ID.charAt(i) || cmp2 == ID.charAt(i)) {
				System.out.println("setID error: ID included invalid character");
				return false;
			}
		}
		return true;
	}

	private boolean validPW(String PW) {
		if (PW.length() != 50) {
			System.out.println("setPW error: " + PW.length() + "is Invalid PW length");
			return false;
		}

		char cmp1 = '%';
		char cmp2 = '?';
		for (int i = 0; i < PW.length(); i++) {
			if (cmp1 == PW.charAt(i) || cmp2 == PW.charAt(i)) {
				System.out.println("setPW error: PW included invalid character");
				return false;
			}
		}
		return true;
	}

	public String getIsRegister() {
		return this.isRegister;
	}

	public String getID() {
		return this.ID;
	}

	public String getPW() {
		return this.PW;
	}

	public String getLogin() {
		String thisObj = "%isRegister?" + isRegister + "%ID?" + getID() + "%PW?" + getPW();
		return thisObj;
	}

	public int loginRegister(FileManage fm) {
		String[] IDTable = fm.getIDTable();
		String[] PWTable = fm.getPWTable();
		
		fm.printIDPWTable();
		try {
			if(getIsRegister().equals("false")) {					// when login
				for(int i = 0; i < IDTable.length; i++) {
					if((IDTable[i] != null && PWTable[i] != null)) {		// before End of Table
						/* <trace>
						 * System.out.println(getID() + " : " + IDTable[i] + " ->	" + getID().equals(IDTable[i]));
						 * System.out.println(getPW() + " : " + PWTable[i] + " ->	" + getPW().equals(PWTable[i]));
						 */
						if(IDTable[i].equals(getID()) && PWTable[i].equals(getPW())){				// same ID && same PW -> login success
								return 1;
						}
					}
				}
				return 3;											// login failed
			}
			else {													// when register
				for(int i = 0; i < IDTable.length; i++) {
					if((IDTable[i] != null && PWTable[i] != null)) {		// before End of Table
						if(IDTable[i].equals(getID())) {				// register failed: same ID exists!
							return 2;
						}
					}
				}
				fm.addIDPWTable(ID, PW);
				return 0;											// register success
		}
		} catch(Exception e) {
			System.out.println("[Method] loginRegister error: " +e);
			return -1;		// login or register failed by unknown problem
		}
	}
	
	public void printLoginInfo() {
		String msg = getIsRegister() + getID() + getPW();
		System.out.println(msg);
	}

	public static void main(String[] args) {
		String isRegister = "false";
		String ID = "publica";
		String PW = "eqbvusb219";
		String obj;
		PasswordAuthentication PA = new PasswordAuthentication();

		PW = PA.hash(PW.toCharArray());

		Login login = new Login(isRegister, ID, PW);

		obj = login.getLogin();
		System.out.println(obj);

		System.out.println("\n\n");
		
		
	}

}