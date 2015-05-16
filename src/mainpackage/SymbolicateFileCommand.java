package mainpackage;
import java.util.Iterator;
import java.util.Vector;

import consoleTool.atos.AtosTool;
import consoleTool.dwarfDumpTool.DwarfDumpTool;
import consoleTool.otool.Otool;
import filesystem.File;
import filesystem.Path;
import filesystem.local.LocalFileSystem;

public class SymbolicateFileCommand implements Command {

	public final String appEnding = ".app";
	
	public Path archivePath;
	public String architecture;
	public Path atosPath;
	public String crashLog;
	public String symblicatedCrasLog;
	public boolean isDebugMode = false;

	@Override
	public void run() {
		Path appPath = getAppPath();
		
		String fileName = appPath.fileName().toString();
		
		if (fileName.endsWith(appEnding)) {
			fileName = fileName.substring(0, fileName.length()-appEnding.length());
		}
		
		Path executablePath = appPath.pathByAppendingFileName(fileName);
		
		//loading build UUID
		DwarfDumpTool dwarfDump = new DwarfDumpTool();
		dwarfDump.setDebugMode(isDebugMode);
		Vector<String> appUUIDs = dwarfDump.loadBuildUUID(executablePath);
		
		XcodeCrashlogParser crashLogParser = new XcodeCrashlogParser();
		crashLogParser.parse(crashLog);
		
		System.out.printf("crashlog build UUID = %s\n",crashLogParser.buildUUID);
		
		if (crashLogParser.loadAddress == null) {
			System.out.printf("Can't find load address in crashlog");
		}
		
		if (!appUUIDs.contains(crashLogParser.buildUUID)) {
			System.out.printf("crashlog UUID is different = %s\n", appUUIDs.toString());
			return;
		}
		
		System.out.printf("crashlog load address = %s\n",crashLogParser.loadAddress);
		System.out.printf("crashlog architecture = %s\n",crashLogParser.architecure);
		
		String arch = crashLogParser.architecure;
		if (this.architecture != null) {
			if (arch != null && this.architecture != null && this.architecture.compareTo(arch) != 0){
				System.out.printf("Warning!: Architecture of crashlog is different = %s\n",arch);
			}
			
			arch = this.architecture;	
		}
		
		//loading vmaddr
		Otool otool = new Otool();
		otool.setDebugMode(isDebugMode);
		Long vmaddrValue = otool.loadAddress(arch, executablePath);
		otool.run();

		if(vmaddrValue == 0) {
			System.out.printf("Can't find vmaddr in executable: %s",executablePath.toString()); 
			return;
		} else {
			System.out.printf("build vmaddr = 0x%x\n",vmaddrValue);
		}
		
		Long loadAddressValue = Long.parseLong(crashLogParser.loadAddress.substring(2), 16);
		StringBuilder outCrashLogString = new StringBuilder(crashLog);
		AtosTool atosTool = new AtosTool(this.atosPath.toString());
		atosTool.setDebugMode(isDebugMode);
		
		Iterator<String> stackStringIterator = crashLogParser.stackStrings.iterator();
		for (String address : crashLogParser.stackAdresses) {
			Long addressValue = Long.parseLong(address.substring(2),16);
			Long resultAddress = AtosTool.calcAddress(addressValue, vmaddrValue, loadAddressValue);
			
			atosTool.run(resultAddress, arch, executablePath);
			
			String stackString = stackStringIterator.next();
			Integer startIndex = outCrashLogString.indexOf(stackString);
			
			String atosResultString = atosTool.result;
			if (atosResultString != null && atosResultString.endsWith("\n")) {
				atosResultString = atosResultString.substring(0, atosResultString.length()-1);
			}
			
			if (atosResultString != null && !isAddressString(atosResultString)) {
				outCrashLogString.replace(startIndex, startIndex+stackString.length(), atosResultString);
			}
		}
		
		this.symblicatedCrasLog = outCrashLogString.toString();
	}
	
	public boolean isAddressString(String str) {
		return str.length() > 1 && str.charAt(1) == 'x';
	}

	public Path getAppPath() {
		Path applicationFolderPath = this.archivePath.pathByAppendingFileName("Products").pathByAppendingFileName("Applications");
		LocalFileSystem lf = new LocalFileSystem(applicationFolderPath);
		
		Path resultPath = null;
		for (File f : lf.files()) {
			if (f.name().endsWith(appEnding)) {
				resultPath = f.path();
			}
		}
		
		return resultPath;
	}
	
	@Override
	public int resultCode() {
		// TODO Auto-generated method stub
		return 0;
	}
}
