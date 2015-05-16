package consoleTool.otool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import consoleTool.ConsoleTool;
import filesystem.Path;

public class Otool extends ConsoleTool {
	public Long loadAddress(String arch, Path path)
	{
		String[] otoolStrings = {"otool", "-arch", arch, "-l", path.toString()};
		this.setStrings(otoolStrings);
		
		super.run();
		
		Long vmaddrValue = this.parseVMaddr(this.result);
		return vmaddrValue;
	}
	
	Long parseVMaddr(String string)
	{
		Pattern p = Pattern.compile("segname __TEXT\n   vmaddr ([^\n]+)\n");
		Matcher matcher = p.matcher(string);
		String addrString = null;
		
		while (matcher.find()) {
			addrString = matcher.group(1);
		    if (addrString != null) {
		    	break;
		    }
		}
		
		return Long.parseLong(addrString.substring(2), 16);
	}
}
