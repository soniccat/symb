package mainpackage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

import org.apache.commons.net.ftp.*;

import consoleTool.xcrun.XcrunTool;
import filesystem.Path;

/*
 * configurations
 * -build "xcodebuild -workspace /Users/username/foldername/superapp.xcworkspace -scheme TargetName -configuration Debug build"
 * -symbolicate -c "./crash.crash" -o "./symbolicated" -arch armv7
 * -ftpsync -l ./uploaded -f ftp/path/folder -n name -p pass -s ./synclog -d 2
 * -archive -a ./folder/appFile -o ./folder/ipaFile -s signString -p ./folder/provisionProfile
 */

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
"\tXcodeBuilder — build and store and symbolicate your builds\n" +
"\n" +
"SYNOPSIS\n" +
"\tXcodeBuilder -build [-c buildString] [-o outputFolder] [-p packageFolderPrefix] [-s packageFolderSuffix] [-i appIdentifierToCheck]\n" +
"\tXcodeBuilder -symbolicate [-c crashLogPath] [-arch architecture] [-s packagesFolderPath] [-atos atospath]\n"+
"\tXcodeBuilder -ftpsync [-l localPath] [-f ftpPath] [-n ftpName] [-p ftpPass] [-s syncLogFile] [-d deepOfSyncing]\n"+
"\tXcodeBuilder -archive [-a appFolder] [-o ipaOutputFolder] [-s sign] [-p profisionProfile]\n"+
"\n"+
"EXAMPLES\n" +
"\tTo store a build result in a package:\n"+
"\t-build \"xcodebuild -workspace /Users/username/foldername/superapp.xcworkspace -scheme TargetName -configuration Debug build\" -o ./builds\n" +
"\t\n" +
"\tTo symbolicate a crahslog using saved packages:\n" +
"\t-symbolicate -c ./crash.crash -o ./symbolicated -arch armv7 -s ./builds\n" +
"\t\n" +
"\tTo sync folder with ftp:\n" +
"\t-ftpsync -l ./uploaded -f ftp/path/folder -n name -p pass -s ./synclog -d 2\n" +
"\t\n" +
"\tTo create ipa file:\n" +
"\t-archive -a ./lastbuild.app -o ./out/app.ipa -s \"my ceritficate\" -p \"profisionProfile\"\n";
    
}
