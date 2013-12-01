import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;


public class SymbolicateFilesCommand implements Command {

	public Path crashLogPath;
	public Path outputPath;
	public String architecture;
	public Path atosPath;
	public XcodePackageManager packageManager;
	public XcodeCrashlogManager crashLogManger;
	
	@Override
	public void run() {
		Iterable<XcodePackage> packages = packageManager.packages();
		
		for (XcodePackage pack : packages) {
			String crashLog = null;
			try {
				crashLog = Files.readFile(this.crashLogPath.toString(), StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.printf("Can't read the crashlog %s\n", this.crashLogPath.toString());
				return;
			}
			
			SymbolicateXcodeBuildCommand symbolicateCommand = new SymbolicateXcodeBuildCommand();
			symbolicateCommand.appPath = pack.appPath;
			symbolicateCommand.architecture = this.architecture;
			symbolicateCommand.atosPath = this.atosPath;
			symbolicateCommand.crashLog = crashLog;
			symbolicateCommand.run();
			
			if (symbolicateCommand.symblicatedCrasLog != null) {
				this.crashLogManger.addCrashLog(pack.name + ".crash", symbolicateCommand.symblicatedCrasLog);
			}
		}
	}
}
