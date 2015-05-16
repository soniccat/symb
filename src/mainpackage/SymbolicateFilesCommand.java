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
	
	@Override
	public void run() {
		LocalFileSystem lf = new LocalFileSystem(archiveFolderPath);
		
		symbolicateFolder(lf.path(), lf);
	}
	
	private void symbolicateFolder(Path path, LocalFileSystem lf) {
		for (File f : lf.files(path)) {
			if (f.name().endsWith("xcarchive")) {
				symbolicateFile(f, lf);
			} else if (f.isDirectory()) {
				symbolicateFolder(path.pathByAppendingFileName(f.name()), lf);
			}
		}
	}
	
	private void symbolicateFile(File f, LocalFileSystem lf) {		
		System.out.printf("Looking at %s\n", f.path().toString());
		String crashLog = null;
		try {
			crashLog = Files.readFile(this.crashLogPath.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Can't read the crashlog %s\n", this.crashLogPath.toString());
			return;
		}
		
		SymbolicateXcodeBuildCommand symbolicateCommand = new SymbolicateXcodeBuildCommand();
		symbolicateCommand.archivePath = f.path();
		symbolicateCommand.architecture = this.architecture;
		symbolicateCommand.atosPath = this.atosPath;
		symbolicateCommand.crashLog = crashLog;
		symbolicateCommand.run();
		
		if (symbolicateCommand.symblicatedCrasLog != null) {
			this.crashLogManger.addCrashLog(f.name() + ".crash", symbolicateCommand.symblicatedCrasLog);
		}
	}

	@Override
	public int resultCode() {
		// TODO Auto-generated method stub
		return 0;
	}
}
