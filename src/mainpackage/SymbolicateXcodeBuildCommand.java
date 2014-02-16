package mainpackage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Vector;

public class SymbolicateXcodeBuildCommand implements Command {

	public Path appPath;
	public String architecture;
	//public Path crashLogPath;
	public Path atosPath;
	public String crashLog;
	public String symblicatedCrasLog;
	//public Path outputCrashLogPath;

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
		String[] otoolStrings = {"otool", "-arch", arch, "-l", executablePath.toString()};
		ConsoleTool otool = new ConsoleTool(otoolStrings);
		otool.run();
				
		OtoolResultParser otoolParser = new OtoolResultParser();
		otoolParser.parse(otool.result);
				
		if(otoolParser.vmaddr == null) {
			System.out.printf("Can't find vmaddr in executable: %s",executablePath.toString());
			return;
		}
				
		System.out.printf("build vmaddr = %s\n",otoolParser.vmaddr);
		
		Integer vmaddrValue = Integer.parseInt(otoolParser.vmaddr.substring(2), 16);
		Integer loadAddressValue = Integer.parseInt(crashLogParser.loadAddress.substring(2), 16);
		
		StringBuilder outCrashLogString = new StringBuilder(crashLog);
		
		Iterator<String> stackStringIterator = crashLogParser.stackStrings.iterator();
		for (String address : crashLogParser.stackAdresses) {
			Integer addressValue = Integer.parseInt(address.substring(2),16);
			Integer resultAddress = vmaddrValue + addressValue - loadAddressValue;
			
			String resultHex = "0x" + Integer.toHexString(resultAddress);
			//System.out.printf("%s + %s - %s = %s\n", otoolParser.vmaddr, address, crashLogParser.loadAddress, resultHex);
			
			String[] atosString = {this.atosPath.toString(), "-arch", arch, "-o", executablePath.toString(), resultHex};
			ConsoleTool atos = new ConsoleTool(atosString);
			atos.run();
			
			String stackString = stackStringIterator.next();
			Integer startIndex = outCrashLogString.indexOf(stackString);
			
			String atosResultString = atos.result;
			if (atosResultString.endsWith("\n")) {
				atosResultString = atosResultString.substring(0, atosResultString.length()-1);
			}
			
			if (atosResultString.equals(resultHex) == false) {
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
