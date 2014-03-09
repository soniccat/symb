package consoleTool.xcrun;

import consoleTool.ConsoleTool;
import filesystem.Path;
import filesystem.local.LocalFileSystem;

public class XcrunTool extends ConsoleTool {
	
	public XcrunTool() {
		super();
	}
	
	public void createPackage(Path appPath, Path outPath, String sign, Path provisionProfilePath)
	{
		Path codesignAllocatePath = this.codesignAllocatePath();
		
		String codesignAllocateVariable = String.format("CODESIGN_ALLOCATE=%s", codesignAllocatePath.toString());
		String[] envp = {codesignAllocateVariable};
		
		this.setEnvp(envp);
		this.setStrings(strings);
		
		LocalFileSystem lfs = new LocalFileSystem(outPath);
		Path absoluteOutPath = lfs.path();
		
		String[] command = {"xcrun","-sdk","iphoneos","PackageApplication","-v",appPath.toString(),"-o",absoluteOutPath.toString(),"--sign",sign,"--embed",provisionProfilePath.toString()};
		this.setStrings(command);
		
		super.run();
	}
	
	public Path codesignAllocatePath()
	{
		String[] command = {"xcrun", "-find", "codesign_allocate"};
		this.setStrings(command);
		super.run();
		
		if (this.result != null) {
			if (this.result.endsWith("\n")) {
				this.result = this.result.substring(0,this.result.length()-1);
			}
			
			return new Path(this.result);
		}
		
		return null;
	}
}
