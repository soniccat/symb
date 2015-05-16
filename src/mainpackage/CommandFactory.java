package mainpackage;
import java.util.Iterator;
import java.util.Vector;

import consoleTool.ConsoleTool;
import consoleTool.xcrun.XcrunTool;
import filesystem.Path;
import filesystem.local.LocalFileSystem;

public class CommandFactory {
	public Command createCommand(String name, Vector<String> parameters)
	{
		Command resultCommand = null;
		if (name.equals("-build")) {
			resultCommand = parseXcodeBuildCommand(parameters);
		
		} else if (name.equals("-symbolicate")) {
			resultCommand = parseSymbolicateCommand(parameters);
		
		} else if (name.equals("-ftpsync")) {
			resultCommand = parseFtpSyncCommand(parameters);
		
		} else if (name.equals("-archive")) {
			resultCommand = parseArchiveCommand(parameters);
		}
		
		return resultCommand;
	}

	private Command parseArchiveCommand(Vector<String> parameters) {
		Command resultCommand;
		Iterator<String> iterator = parameters.iterator();
		String parameterName = null;
		
		String sign = null;
		Path provisionProfilePath = null;
		Path appPath = null;
		Path outPath = null;
		
		while(iterator.hasNext()) {
			parameterName = iterator.next();
			
			if (parameterName.equals("-p")) {
				provisionProfilePath = new Path(iterator.next());
			
			} else if (parameterName.equals("-a")) {
				appPath = new Path(iterator.next());
				
			} else if (parameterName.equals("-o")) {
				outPath = new Path(iterator.next());
			
			} else if (parameterName.equals("-s")) {
				sign = iterator.next();
			}
		}
		
		if (provisionProfilePath == null) {
			System.out.println("-p (provisionProfile path) not found");
			return null;
		}
		
		if (appPath == null) {
			System.out.println("-a (application folder path) not found");
			return null;
		}
		
		if (sign == null) {
			System.out.println("-s (sign) not found");
			System.out.println("Pick one and write its hash or the begining of its name:");
			
			ConsoleTool tool = new ConsoleTool("security find-identity -v -p codesigning");
			tool.run();
			return null;
		}
		
		if (outPath == null) {
			System.out.println("-o (output file path) not found");
			return null;
		}
		
		CreateIpaCommand createIpaCommand = new CreateIpaCommand();
		createIpaCommand.sign = sign;
		createIpaCommand.appPath = appPath;
		createIpaCommand.outPath = outPath;
		createIpaCommand.provisionProfilePath = provisionProfilePath;
		
		resultCommand = createIpaCommand;
		return resultCommand;
	}

	private Command parseFtpSyncCommand(Vector<String> parameters) {
		Command resultCommand;
		Iterator<String> iterator = parameters.iterator();
		String parameterName = null;
		
		Path localPath = null;
		Path ftpPath = null;
		String ftpUserName = "";
		String ftpUserPassword = "";
		Path syncLogPath = null;
		int subDirectorySyncCount = Integer.MAX_VALUE;
		
		while(iterator.hasNext()) {
			parameterName = iterator.next();
			
			if (parameterName.equals("-l")) {
				localPath = new Path(iterator.next());
			
			} else if (parameterName.equals("-f")) {
				ftpPath = new Path(iterator.next());
				
			} else if (parameterName.equals("-n")) {
				ftpUserName = iterator.next();
				
			} else if (parameterName.equals("-p")) {
				ftpUserPassword = iterator.next();
			
			} else if (parameterName.equals("-s")) {
				syncLogPath = new Path(iterator.next());
				
			} else if (parameterName.equals("-d")) {
				subDirectorySyncCount = Integer.parseInt(iterator.next());
			}
		}
		
		if (localPath == null) {
			System.out.println("-l (local path) not found");
			return null;
		}
		
		if (ftpPath == null) {
			System.out.println("-f (ftp path) not found");
			return null;
		}
		
		SyncFilesCommand syncCommand = new SyncFilesCommand();
		syncCommand.localPath = localPath;
		syncCommand.ftpPath = ftpPath;
		syncCommand.ftpUserName = ftpUserName;
		syncCommand.ftpUserPassword = ftpUserPassword;
		syncCommand.syncLogPath = syncLogPath;
		syncCommand.subDirectorySyncCount = subDirectorySyncCount;
		
		resultCommand = syncCommand;
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
		
		while(iterator.hasNext()) {
			parameterName = iterator.next();
			
			if (parameterName.equals("-c")) {
				crashLogPath = new Path(iterator.next());
			
			} else if (parameterName.equals("-o")) {
				outputPath = new Path(iterator.next());
				
			} else if (parameterName.equals("-arch")) {
				architecture = iterator.next();
				
			} else if (parameterName.equals("-atos")) {
				atosPath = new Path(iterator.next());
				
			} else if (parameterName.equals("-s")) {
				searchPath = new Path(iterator.next());
			} 
		}
		
		if (crashLogPath == null) {
			System.out.println("-c (crashlog path) not found");
			return null;
		}
		
		if (outputPath == null) {
			System.out.println("-o (output path) not found");
			return null;
		}
		
		SymbolicateFilesCommand symbolicateAll = new SymbolicateFilesCommand();
		symbolicateAll.crashLogPath = crashLogPath;
		symbolicateAll.outputPath = outputPath;

		LocalFileSystem fs2 = new LocalFileSystem(new Path("."));
		XcodeCrashlogManager cm = new XcodeCrashlogManager(fs2);
		fs2.createFolder(symbolicateAll.outputPath);
		fs2.setPath(symbolicateAll.outputPath);

		symbolicateAll.archiveFolderPath = searchPath;
		symbolicateAll.crashLogManger = cm;
		symbolicateAll.architecture = architecture;
		symbolicateAll.atosPath = atosPath;
		
		resultCommand = symbolicateAll;
		return resultCommand;
	}

	private Command parseXcodeBuildCommand(Vector<String> parameters) {
		Command resultCommand;
		Iterator<String> iterator = parameters.iterator();
		String parameterName = null;
		String buildString = null;
		String namePrefix = "";
		String nameSuffix = "";
		Path outputPath = new Path(".");
		String identifierToCheck = null;
		
		while(iterator.hasNext()) {
			parameterName = iterator.next();
			
			if (parameterName.equals("-c")) {
				buildString = iterator.next();
			
			} else if (parameterName.equals("-o")) {
				outputPath = new Path(iterator.next());
				
			} else if (parameterName.equals("-p")) {
				namePrefix = iterator.next();
				
			} else if (parameterName.equals("-s")) {
				nameSuffix = iterator.next();
				
			} else if (parameterName.equals("-i")) {
				identifierToCheck = iterator.next();
			}
		}
		
		if (buildString == null) {
			System.out.println("-c (command) not found");
			return null;
		}
		
		XcodeBuildCommand consoleTool = new XcodeBuildCommand(buildString);
		consoleTool.outputPath = outputPath;
		consoleTool.namePrefix = namePrefix;
		consoleTool.nameSuffix = nameSuffix;
		consoleTool.identifierToCheck = identifierToCheck;
		resultCommand = consoleTool;
		return resultCommand;
	}
}
