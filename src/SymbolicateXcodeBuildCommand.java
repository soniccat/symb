import java.io.IOException;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

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
		String fileName = this.appPath.getFileName().toString();
		String appEnding = ".app";
		
		if (fileName.endsWith(appEnding)) {
			fileName = fileName.substring(0, fileName.length()-appEnding.length());
		}
		
		String executableString = this.appPath.toString() + java.io.File.separator + fileName;
		
		String[] otoolStrings = {"otool", "-arch", this.architecture, "-l", executableString};
		ConsoleTool otool = new ConsoleTool(otoolStrings);
		otool.run();
		
		OtoolResultParser otoolParser = new OtoolResultParser();
		otoolParser.parse(otool.result);
		
		if(otoolParser.vmaddr == null) {
			System.out.printf("Can't find vmaddr in executable: %s",executableString);
			return;
		}
		
		System.out.printf("vmaddr = %s\n",otoolParser.vmaddr);
		
		/*
		try {
			crashLog = Files.readFile(this.crashLogPath.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Can't read the crashlog %s\n", this.crashLogPath.toString());
			return;
		}
		*/
		
		XcodeCrashlogParser crashLogParser = new XcodeCrashlogParser();
		crashLogParser.parse(crashLog);
		
		if (crashLogParser.loadAddress == null) {
			System.out.printf("Can't find load address in crashlog");
		}
		
		System.out.printf("load address = %s\n",crashLogParser.loadAddress);
		
		Integer vmaddrValue = Integer.parseInt(otoolParser.vmaddr.substring(2), 16);
		Integer loadAddressValue = Integer.parseInt(crashLogParser.loadAddress.substring(2), 16);
		
		StringBuilder outCrashLogString = new StringBuilder(crashLog);
		
		Iterator<String> stackStringIterator = crashLogParser.stackStrings.iterator();
		for (String address : crashLogParser.stackAdresses) {
			Integer addressValue = Integer.parseInt(address.substring(2),16);
			Integer resultAddress = vmaddrValue + addressValue - loadAddressValue;
			
			String resultHex = "0x" + Integer.toHexString(resultAddress);
			//System.out.printf("%s + %s - %s = %s\n", otoolParser.vmaddr, address, crashLogParser.loadAddress, resultHex);
			
			String[] atosString = {this.atosPath.toString(), "-arch", this.architecture, "-o", executableString, resultHex};
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
		
		//System.out.println(outCrashLogString.toString());
		/*
		try {
			Files.writeFile(this.outputCrashLogPath.toString(), outCrashLogString.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Can't save crashlog file");
		}
		*/
	}
}
