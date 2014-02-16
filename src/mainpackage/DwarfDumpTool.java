package mainpackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DwarfDumpTool extends ConsoleTool {

	DwarfDumpTool() {
		super();
	}

	public String loadBuildUUID(Path buildPath)
	{
		String[] dwarfdumpStrings = {"dwarfdump", "--uuid", buildPath.toString()};
		this.setStrings(dwarfdumpStrings);
		
		super.run();
		
		System.out.printf("%s\n", this.result);
		String result = parseUUID(this.result);
		return result;
	}
	
	String parseUUID(String string)
	{
		Pattern p = Pattern.compile("UUID: ([^ ]+)");
		Matcher matcher = p.matcher(string);
		String result = "";
		
		while (matcher.find()) {
			result = matcher.group(1);
		    if (result != null) {
		    	break;
		    }
		}
		
		result = result.replaceAll("-", "");
		return result.toLowerCase();
	}
}
