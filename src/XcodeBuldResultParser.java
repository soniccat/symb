import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class XcodeBuldResultParser implements StringParser {

	public String appPath;
	public String dsymPath;
	
	public void parse(String string) {
		Pattern p = Pattern.compile("CODESIGNING_FOLDER_PATH ([^\n]+)\n");
		Matcher matcher = p.matcher(string);
		
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
