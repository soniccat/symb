
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

/*
 * configurations
 * -build "xcodebuild -workspace /Users/username/foldername/superapp.xcworkspace -scheme TargetName -configuration Debug build"
 * -symbolicate -c "./crash.crash" -o "./symbolicated" -arch armv7
 */

public class Main 
{	
    public static void main ( String [] arguments )
    {
    	//testing
    	/*
    	SymbolicateFilesCommand symbolicateAll = new SymbolicateFilesCommand();
    	symbolicateAll.crashLogPath = Paths.get("./crash.crash");
    	symbolicateAll.outputPath = Paths.get("./symbolicated");

    	LocalFileSystem fs = new LocalFileSystem(Paths.get("."));
    	XcodePackageManager pm = new XcodePackageManager(fs);

    	LocalFileSystem fs2 = new LocalFileSystem(Paths.get("."));
    	XcodeCrashlogManager cm = new XcodeCrashlogManager(fs2);
    	fs2.createFolder(symbolicateAll.outputPath);
    	fs2.setPath(symbolicateAll.outputPath);

    	symbolicateAll.packageManager = pm;
    	symbolicateAll.crashLogManger = cm;
    	symbolicateAll.architecture = "armv7";
    	symbolicateAll.atosPath = Paths.get("/Applications/Xcode.app/Contents/Developer/usr/bin/atos");
    	symbolicateAll.run();
    	*/
    	
    	/*
    	SymbolicateXcodeBuildCommand cmd = new SymbolicateXcodeBuildCommand();
    	cmd.appPath = Paths.get("/Users/alexeyglushkov/androidProjects/XcodeBuilder/11_29_2013 21-46-41/News360iPad.app");
    	cmd.architecture = "armv7";
    	cmd.crashLogPath = Paths.get("./crash.crash");
    	cmd.atosPath = Paths.get("/Applications/Xcode.app/Contents/Developer/usr/bin/atos");
    	cmd.outputCrashLogPath = Paths.get("./out.crash");
    	cmd.run();
    	*/
    	
    	/*
    	///Users/alexeyglushkov/Library/Developer/Xcode/DerivedData/News360iPhone-fsfipbxnikwukxglpreqafamnoql/Build/Products/Debug-iphoneos/News360iPad.app.dSYM
    	
    	
    	Path toPath = Paths.get(".");
    	Path fromPath = Paths.get("/Users/alexeyglushkov/Library/Developer/Xcode/DerivedData/News360iPhone-fsfipbxnikwukxglpreqafamnoql/Build/Products/Debug-iphoneos/News360iPad.app.dSYM");
    	
    	LocalFileSystem fromFs = new LocalFileSystem(fromPath);
    	LocalFileSystem toFs = new LocalFileSystem(toPath);
    	
    	FileSystems.copyDirectory(fromPath, toPath, fromFs, toFs);
    	*/
    	
    	/*
    	LocalFileSystem fs = new LocalFileSystem(Paths.get("."));
    	XcodePackageManager manager = new XcodePackageManager(fs);
    	manager.storePackage(null);
    	*/
    	
    	/*
    	LocalFileSystem fsTo = new LocalFileSystem(Paths.get("."));
    	LocalFileSystem fsFrom = new LocalFileSystem(new java.io.File("/Users/alexeyglushkov/tmp").toPath());

    	FileSystems.copyFile(Paths.get("/Users/alexeyglushkov/tmp/IMG_5452.JPG"), Paths.get("./copyTest.jpg"), fsFrom, fsTo);
    	*/
    	
    	//ByteArrayOutputStream buffer = new ByteArrayOutputStream(100);
    	//buffer.write(4);
    	//buffer.write(10);
    	
    	/*
    	Path path = Paths.get(".");
    	LocalFileSystem fs = new LocalFileSystem(path);
    	
    	Iterable<File> files = fs.files();
    	for (File f : files) {
    		System.out.println(f.name());
    		
    		if (f.isDirectory()) {
    			fs.setPath(f.path());
    		}
    	}
    	*/

    	
    	if (arguments.length == 0) {
    		System.out.print(helpString);
    		return;
    	}
    	
    	ArgsParser argsParser = new ArgsParser();
    	Command command = argsParser.parse(arguments);
    	
    	if (command != null) {
    		command.run();
    	} else {
    		System.out.printf("Parse error: %s", argsParser.errorDescription);
    	}
    	
    }
    
    static String helpString = "Help is under construction";
}
