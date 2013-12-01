import java.nio.file.Paths;


public class XcodeBuildCommand implements Command {
	ConsoleTool xcodeBuildTool;
	
	public XcodeBuildCommand(String commandLine) 
	{
		xcodeBuildTool = new ConsoleTool(commandLine);
	}

	@Override
	public void run() {
		xcodeBuildTool.run();
		
		XcodeBuldResultParser resultParser = new XcodeBuldResultParser();
		resultParser.parse(xcodeBuildTool.result);
		
		System.out.printf("parse result: %s %s\n", resultParser.appPath, resultParser.dsymPath);
		
		XcodePackage pack = new XcodePackage();
		pack.appPath = Paths.get(resultParser.appPath);
		pack.dsymPath = Paths.get(resultParser.dsymPath);
		
		LocalFileSystem fileSystem = new LocalFileSystem(Paths.get("."));
		XcodePackageManager packageManager = new XcodePackageManager(fileSystem);
		packageManager.storePackage(pack);
	}
}
