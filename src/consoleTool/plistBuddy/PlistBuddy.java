package consoleTool.plistBuddy;

import consoleTool.ConsoleTool;

public class PlistBuddy extends ConsoleTool {
	
	final static String appPath = "/usr/libexec/PlistBuddy";
	String filePath;
	
	public PlistBuddy(String filePath) {
		super();
		
		this.filePath = filePath;
	}
	
	public String propertyValue(String propertyName) 
	{
		String[] args = {PlistBuddy.appPath, "-c","Print "+propertyName,this.filePath};
		setStrings(args);
		super.run();
		
		if (this.result.endsWith("\n")) {
			this.result = this.result.substring(0, this.result.length()-1);
		}
		
		return this.result;
	}
}
