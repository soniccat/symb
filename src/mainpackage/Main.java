package mainpackage;

public class Main 
{	
    public static void main ( String [] arguments )
    {    	
    	if (arguments.length == 0) {
    		System.out.print(helpString);
    		return;
    	}

    	ArgsParser argsParser = new ArgsParser();
    	Command command = argsParser.parse(arguments);

    	if (command != null) {
    		command.run();
    		System.exit(command.resultCode());
    	} else {
    		System.out.printf("Parse error: %s", argsParser.errorDescription);
    	}

    	System.exit(1);
    }

    static String helpString = "NAME\n" +
"\tsymb — symbolicate crash logs using xarchives\n" +
"\n" +
"SYNOPSIS\n" +
"\tsymb crash-path [-arch <architecture>] [-s <path>] [-atos <path>] [-d]\n"+
"\n"+
"OPTIONS\n" +
"\t-arch <architecture>\n"+
"\t    Force to set arch parameter of atos. When it is skipped, it is got from a crash log.\n" +
"\n"+
"\t-s <path>\n"+
"\t    Path to xarchives folder. The default value is ~/Library/Developer/Xcode/Archives/.\n" +
"\n"+
"\t-atos <path>\n"+
"\t    Path for atos command. The default value is /Applications/Xcode.app/Contents/Developer/usr/bin/atos.\n" +
"\n"+
"\t-d\n"+
"\t    Log all command outputs.\n" +
"\n"+
"EXAMPLES\n" +
"\tTo symbolicate a crashlog:\n" +
"\tsymb ./crash.crash -o ./symbolicated\n";    
}
