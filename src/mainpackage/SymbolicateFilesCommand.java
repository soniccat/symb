package mainpackage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import filesystem.File;
import filesystem.Files;
import filesystem.Path;
import filesystem.local.LocalFileSystem;


public class SymbolicateFilesCommand implements Command {

	public Path archiveFolderPath;
	public Path crashLogPath;
	public Path outputPath;
	public String architecture;
	public Path atosPath;
	public XcodeCrashlogManager crashLogManger;
	public boolean isDebugMode = false;
	
	@Override
	public void run() {
		LocalFileSystem lf = new LocalFileSystem(archiveFolderPath);
		
		symbolicateFile(lf.file(), lf);
	}
	
	private void symbolicateFile(File f, LocalFileSystem lf) {
		if (f.name().endsWith("xcarchive")) {
			symbolicate(f, lf);

		} else if (f.isDirectory()) {
			symbolicateFolder(f.path(), lf);
		}
	}
	
	private void symbolicateFolder(Path path, LocalFileSystem lf) {
		for (File f : lf.files(path)) {
			symbolicateFile(f, lf);
		}
	}
	
	private void symbolicate(File f, LocalFileSystem lf) {
		System.out.printf("Looking at %s\n", f.path().toString());
		String crashLog = null;
		try {
			crashLog = Files.readFile(this.crashLogPath.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Can't read the crashlog %s\n", this.crashLogPath.toString());
			return;
		}
		
		SymbolicateFileCommand symbolicateCommand = new SymbolicateFileCommand();
		symbolicateCommand.archivePath = f.path();
		symbolicateCommand.architecture = this.architecture;
		symbolicateCommand.atosPath = this.atosPath;
		symbolicateCommand.crashLog = crashLog;
		symbolicateCommand.isDebugMode = isDebugMode;
		symbolicateCommand.run();
		
		if (symbolicateCommand.symblicatedCrasLog != null) {
			String resultName = f.name() + ".crash";
			this.crashLogManger.addCrashLog(resultName, symbolicateCommand.symblicatedCrasLog);
			System.out.printf("Symbolicated to  = %s\n", this.crashLogManger.getStorePath(resultName).toString());
		}
	}

	@Override
	public int resultCode() {
		// TODO Auto-generated method stub
		return 0;
	}
}
