package consoleTool.xcodeBuildTool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import consoleTool.ConsoleTool;

public class XcodeBuildTool extends ConsoleTool {
	
	public String appPath;
	public String dsymPath;
	
	public XcodeBuildTool(String command) {
		super(command);
	}
	
	public void run()
	{
		super.run();
		
		this.parseResult(this.result);
	}
	
	void parseResult(String result){
		Pattern p = Pattern.compile("CODESIGNING_FOLDER_PATH[ =]([^\n]+)\n");
		Matcher matcher = p.matcher(result);
		
		while (matcher.find()) {
			//System.out.print("Start index: " + matcher.start());
		    //System.out.print(" End index: " + matcher.end() + " ");
		    String value = matcher.group(1);
		    //System.out.println(value);
		    
		    String extension = ".app";
		    if (value.endsWith(extension)) {
		    	this.appPath = value;  	
		    	this.dsymPath = this.appPath + ".dSYM";
		    } else {
		    }
		}
	}
}
