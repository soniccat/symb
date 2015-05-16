package mainpackage;
import java.util.Iterator;
import java.util.Vector;

import consoleTool.ConsoleTool;
import consoleTool.xcrun.XcrunTool;
import filesystem.Path;
import filesystem.local.LocalFileSystem;

public class CommandFactory {
	public Command createCommand(String name, Vector<String> parameters) {
		Command resultCommand = parseSymbolicateCommand(parameters);		
		return resultCommand;
	}

	private Command parseSymbolicateCommand(Vector<String> parameters) {
		Command resultCommand;
		Iterator<String> iterator = parameters.iterator();
		String parameterName = null;
		Path crashLogPath = null;
		Path outputPath = null;
		Path searchPath = new Path(".");
		String architecture = null;
		Path atosPath = new Path("/Applications/Xcode.app/Contents/Developer/usr/bin/atos");
		boolean isDebug = false;
		
		while(iterator.hasNext()) {
			parameterName = iterator.next();
			
			if (parameterName.charAt(0) != '-') {
				crashLogPath = new Path(parameterName);
			
			} else if (parameterName.equals("-o")) {
				outputPath = new Path(iterator.next());
				
			} else if (parameterName.equals("-arch")) {
				architecture = iterator.next();
				
			} else if (parameterName.equals("-atos")) {
				atosPath = new Path(iterator.next());
				
			} else if (parameterName.equals("-s")) {
				searchPath = new Path(iterator.next());
				
			} else if (parameterName.equals("-d")) {
				isDebug = true;
			} 
		}
		
		if (crashLogPath == null) {
			System.out.println("crashlog path is missing");
			return null;
		}
		
		if (outputPath == null) {
			System.out.println("-o (output path) is missing");
			return null;
		}
		
		SymbolicateFilesCommand symbolicateAll = new SymbolicateFilesCommand();
		symbolicateAll.crashLogPath = crashLogPath;
		symbolicateAll.outputPath = outputPath;

		LocalFileSystem fs2 = new LocalFileSystem(new Path("."));
		XcodeCrashlogManager cm = new XcodeCrashlogManager(fs2);
		fs2.createFolder(symbolicateAll.outputPath);
		fs2.setPath(symbolicateAll.outputPath);

		symbolicateAll.isDebugMode = isDebug;
		symbolicateAll.archiveFolderPath = searchPath;
		symbolicateAll.crashLogManger = cm;
		symbolicateAll.architecture = architecture;
		symbolicateAll.atosPath = atosPath;
		
		resultCommand = symbolicateAll;
		return resultCommand;
	}
}
