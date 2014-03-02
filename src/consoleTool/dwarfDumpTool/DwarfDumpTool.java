package consoleTool.dwarfDumpTool;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import consoleTool.ConsoleTool;
import filesystem.Path;

public class DwarfDumpTool extends ConsoleTool {

	public DwarfDumpTool() {
		super();
	}

	public Vector<String> loadBuildUUID(Path buildPath)
	{
		String[] dwarfdumpStrings = {"dwarfdump", "--uuid", buildPath.toString()};
		this.setStrings(dwarfdumpStrings);
		
		super.run();
		
		System.out.printf("%s\n", this.result);
		Vector<String> result = parseUUID(this.result);
		return result;
	}
	
	Vector<String> parseUUID(String string)
	{
		Pattern p = Pattern.compile("UUID: ([^ ]+)");
		Matcher matcher = p.matcher(string);
		String uuid = "";
		Vector<String> resultVector = new Vector<String>();
		
		while (matcher.find()) {
			uuid = matcher.group(1);
		    if (uuid != null) {
		    	uuid = uuid.replaceAll("-", "");
		    	resultVector.add(uuid.toLowerCase());
		    }
		}

		return resultVector;
	}
}
