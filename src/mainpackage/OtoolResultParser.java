package mainpackage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OtoolResultParser implements StringParser {

	public String vmaddr;
	
	@Override
	public void parse(String string) {
		Pattern p = Pattern.compile("segname __TEXT\n   vmaddr ([^\n]+)\n");
		Matcher matcher = p.matcher(string);
		
		while (matcher.find()) {
		    this.vmaddr = matcher.group(1);
		    if (this.vmaddr != null) {
		    	break;
		    }
		}
	}
}
