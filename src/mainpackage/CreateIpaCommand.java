package mainpackage;

import consoleTool.xcrun.XcrunTool;
import filesystem.Path;

public class CreateIpaCommand extends XcrunTool {
	public String sign;
	public Path provisionProfilePath;
	public Path appPath;
	public Path outPath;
	
	public void run()
	{
		super.createPackage(this.appPath, this.outPath, this.sign, this.provisionProfilePath);
	}
}
