import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


public class XcodeBuildCommand implements Command {
	public ConsoleTool xcodeBuildTool;
	public Path outputPath;
	public String namePrefix;
	public String nameSuffix;
	
	int resultCode = 0;
	
	public XcodeBuildCommand(String commandLine) 
	{
		xcodeBuildTool = new ConsoleTool(commandLine);
	}

	@Override
	public void run() {
		Date startDate = new Date();
		xcodeBuildTool.run();

		XcodeBuldResultParser resultParser = new XcodeBuldResultParser();
		resultParser.parse(xcodeBuildTool.result);
		
		if (resultParser.appPath == null) {
			System.out.println("CODESIGNING_FOLDER_PATH wasn't found. Add variables logging in your build step.");
			this.resultCode = 1;
			return;
		}
		
		System.out.printf("parse result: %s %s\n", resultParser.appPath, resultParser.dsymPath);
		
		java.io.File file = new java.io.File(resultParser.appPath.toString());
		Date fileDate = new Date(file.lastModified());
		
		if (startDate.compareTo(fileDate) >= 0) {
			System.out.printf("Building was cancelled, an app file wasn't modified");
			this.resultCode = 2;
			return;
		}
		
		XcodePackage pack = new XcodePackage();
		pack.appPath = Paths.get(resultParser.appPath);
		pack.dsymPath = Paths.get(resultParser.dsymPath);
		
		LocalFileSystem fileSystem = new LocalFileSystem(Paths.get("."));
		fileSystem.createFolder(this.outputPath);
		fileSystem.setPath(this.outputPath);
		XcodePackageManager packageManager = new XcodePackageManager(fileSystem);
		packageManager.namePrefix = this.namePrefix;
		packageManager.nameSuffix = this.nameSuffix;
		packageManager.storePackage(pack);
	}

	@Override
	public int resultCode() {
		return this.resultCode;
	}
}
