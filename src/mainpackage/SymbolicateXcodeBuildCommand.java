package mainpackage;
import java.util.Iterator;
import java.util.Vector;

import consoleTool.atos.AtosTool;
import consoleTool.dwarfDumpTool.DwarfDumpTool;
import consoleTool.otool.Otool;
import filesystem.Path;

public class SymbolicateXcodeBuildCommand implements Command {

	public Path appPath;
	public String architecture;
	public Path atosPath;
	public String crashLog;
	public String symblicatedCrasLog;

	@Override
	public void run() {
		String fileName = this.appPath.fileName().toString();
		String appEnding = ".app";
		
		if (fileName.endsWith(appEnding)) {
			fileName = fileName.substring(0, fileName.length()-appEnding.length());
		}
		
		Path executablePath = this.appPath.pathByAppendingFileName(fileName);
		
		//loading build UUID
		DwarfDumpTool dwarfDump = new DwarfDumpTool();
		Vector<String> appUUIDs = dwarfDump.loadBuildUUID(executablePath);
		
		XcodeCrashlogParser crashLogParser = new XcodeCrashlogParser();
		crashLogParser.parse(crashLog);
		
		if (crashLogParser.loadAddress == null) {
			System.out.printf("Can't find load address in crashlog");
		}
		
		if (!appUUIDs.contains(crashLogParser.buildUUID)) {
			return;
		}
		
		System.out.printf("crashlog build UUID = %s\n",crashLogParser.buildUUID);
		System.out.printf("crashlog load address = %s\n",crashLogParser.loadAddress);
		System.out.printf("crashlog architecture = %s\n",crashLogParser.architecure);
		
		String arch = crashLogParser.architecure;
		if (this.architecture != null) {
			arch = this.architecture;
		}
		
		//loading vmaddr
		Otool otool = new Otool();
		Integer vmaddrValue = otool.loadAddress(arch, executablePath);
		otool.run();

		if(vmaddrValue == 0) {
			System.out.printf("Can't find vmaddr in executable: %s",executablePath.toString()); 
			return;
		} else {
			System.out.printf("build vmaddr = %s\n",vmaddrValue);
		}
		
		Integer loadAddressValue = Integer.parseInt(crashLogParser.loadAddress.substring(2), 16);
		StringBuilder outCrashLogString = new StringBuilder(crashLog);
		AtosTool atosTool = new AtosTool(this.atosPath.toString());
		
		Iterator<String> stackStringIterator = crashLogParser.stackStrings.iterator();
		for (String address : crashLogParser.stackAdresses) {
			Integer addressValue = Integer.parseInt(address.substring(2),16);
			Integer resultAddress = AtosTool.calcAddress(addressValue, vmaddrValue, loadAddressValue);
			
			atosTool.run(resultAddress, arch, executablePath);
			
			String stackString = stackStringIterator.next();
			Integer startIndex = outCrashLogString.indexOf(stackString);
			
			String atosResultString = atosTool.result;
			if (atosResultString != null && atosResultString.endsWith("\n")) {
				atosResultString = atosResultString.substring(0, atosResultString.length()-1);
			}
			
			if (atosResultString != null) {
				outCrashLogString.replace(startIndex, startIndex+stackString.length(), atosResultString);
			}
		}
		
		this.symblicatedCrasLog = outCrashLogString.toString();
	}

	@Override
	public int resultCode() {
		// TODO Auto-generated method stub
		return 0;
	}
}
