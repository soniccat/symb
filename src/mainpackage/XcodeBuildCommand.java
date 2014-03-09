package mainpackage;

import java.util.Date;

import consoleTool.xcodeBuildTool.XcodeBuildTool;
import filesystem.Path;
import filesystem.local.LocalFileSystem;


public class XcodeBuildCommand implements Command {
	public XcodeBuildTool xcodeBuildTool;
	public Path outputPath;
	public String namePrefix;
	public String nameSuffix;
	
	int resultCode = 0;
	
	public XcodeBuildCommand(String commandLine) 
	{
		this.xcodeBuildTool = new XcodeBuildTool(commandLine);
	}

	@Override
	public void run() {
		Date startDate = new Date();
		this.xcodeBuildTool.run();
		
		if (this.xcodeBuildTool.appPath == null) {
			System.out.println("CODESIGNING_FOLDER_PATH wasn't found. Add variables logging in your build step.");
			this.resultCode = 1;
			return;
		}
		
		System.out.printf("parse result: %s %s\n", this.xcodeBuildTool.appPath, this.xcodeBuildTool.dsymPath);
		
		java.io.File file = new java.io.File(this.xcodeBuildTool.appPath.toString());
		Date fileDate = new Date(file.lastModified());
		
		if (startDate.compareTo(fileDate) >= 0) {
			System.out.printf("Building was cancelled, an app file wasn't modified");
			this.resultCode = 2;
			return;
		}
		
		XcodePackage pack = new XcodePackage();
		pack.appPath = new Path(this.xcodeBuildTool.appPath);
		pack.dsymPath = new Path(this.xcodeBuildTool.dsymPath);
		
		LocalFileSystem fileSystem = new LocalFileSystem(new Path("."));
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
