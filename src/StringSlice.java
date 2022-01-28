
public class StringSlice {
	private String[] arguments = null;

	public StringSlice() {

	}

	public StringSlice(String raw) {
		this.arguments = slicing(raw);
	}

	public void setRaw(String raw) {
		this.arguments = slicing(raw);
	}

	public String[] getSlice() {
		return this.arguments;
	}

	public String[] slicing(String raw) {
		int argc = 0;
		String[] save = null;
		String buf = "";
		char cmp1 = '%';
		char cmp2 = '?';
		boolean isKey = false;
		boolean isValue = false;

		for (int j = 0; j < raw.length(); j++) { // how many arguments?
			if (cmp1 == raw.charAt(j)) {
				argc++;
			}
		}

		save = new String[argc * 2];

		try{												// save <- raw
			int i = 0;
			boolean valid = false;
			
			for (int j = 0; j < raw.length(); j++) {
				if (cmp1 == raw.charAt(j)) { 				// %[key]
					isValue = false;
					isKey = true;
					
					if (j != 0) { 							// first % ignore
						save[i + 1] = buf;
						i += 2;
						buf = "";
					}
					valid = false;
				} else if (cmp2 == raw.charAt(j)) { 		// ?[value]
					isKey = false;
					isValue = true;
					save[i] = buf;
					buf = "";
					valid = false;
				}

				if ((isKey || isValue) && valid) {
					buf += raw.charAt(j);
				}
				valid = true;
			}
			save[i + 1] = buf;
		} catch(ArrayIndexOutOfBoundsException aioe) {
			System.out.println("Is correct message? check % count again");
		}
		return save;
	}

	public void printSlice() {
		String[] str = getSlice();
		
		for(int i = 0; i < str.length; i++) {
			System.out.println(str[i]);
		}
	}
	public static void main(String[] args) {
		String str = "%isRegister?false%ID?publica%PW?$31$16$MTJhM2EzODAxMmEzYTM4MEd60unN8bPU_s_m5xEF8iA";

		StringSlice SS = new StringSlice(str);
		SS.printSlice();
	}

}
