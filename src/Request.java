import java.io.Serializable;

public class Request implements Serializable{
	private String requestID;
	private String wantService;
	private String location;
	private String payCashunut;
	private int timeOut;
	/*		unuse
	private boolean isRent;
	private int rentTime;
	*/
	private String userID;

	public Request(){
		
	}
	
	public Request(String requestID, String wantService, String location, String payCashunut, int timeOut, boolean isRent, int rentTime, String userID) {
		this.requestID = requestID;
		this.wantService = wantService;
		this.location = location;
		this.payCashunut = payCashunut;
		this.timeOut = timeOut;
		/*		unuse
		this.isRent = isRent;
		this.rentTime = rentTime;
		*/
		this.userID = userID;
	}
	public String getRequestID() {
		return this.requestID;
	}
	
	public String getWantService() {
		return this.wantService;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public String getPayCashunut() {
		return this.payCashunut;
	}
	
	public int getTimeOut() {
		return this.timeOut;
	}
	
	/*		unuse
	public boolean getIsRent() {
		return this.isRent;
	}
	
	public int getRentTime() {
		return this.rentTime;
	}
	*/
	
	public String getUserID() {
		return this.userID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public void setWantService(String wantService) {
		this.wantService = wantService;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setPayCashunut(String payCashunut) {
		this.payCashunut = payCashunut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	/*		unuse
	public void setIsRent(boolean isRent) {
		this.isRent = isRent;
	}

	public void setRentTime(int rentTime) {
		this.rentTime = rentTime;
	}
	*/
	
	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getRequestString() {
		String str = getRequestID() + " " + getWantService()  + " " + getLocation()  + " " + getPayCashunut()  + " " + Integer.toString(getTimeOut());
		/*		unuse
		if(getIsRent()) {
			str += " " + Integer.toString(getRentTime());
		}
		*/
		
		return str;
	}
	
	public String getSummaryRequestString() {
		String str = String.format("%-18s%-18s%-8s",getWantService(), getLocation(), getPayCashunut());
		
		return str;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
