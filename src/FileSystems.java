import java.nio.file.Path;
import java.nio.file.Paths;


public class FileSystems {
	public static void copyFile(Path fromPath, Path toPath, FileSystem fromFileSystem, FileSystem toFileSystem) {
		byte[] buffer = new byte[1024];
		
		boolean dataIsAvailable = true;
		boolean append = false;
		int readedCount = 0;
		int wasReadedLen;
		
		while (dataIsAvailable) {
			wasReadedLen = fromFileSystem.readBytes(fromPath, buffer, readedCount);
			if (wasReadedLen == -1) {
				dataIsAvailable = false;
				break;
			}

			toFileSystem.writeBytes(toPath, buffer, wasReadedLen, append);
			
			readedCount += wasReadedLen;
			append = true;
		}
	}
	
	public static void copyDirectory(Path fromPath, Path toPath, FileSystem fromFileSystem, FileSystem toFileSystem) {
		Path oldFromFsPath = fromFileSystem.path();
		Path oldToFsPath = toFileSystem.path();
		
		fromFileSystem.setPath(fromPath);
		
		//create a folder in toPath and move in it
		String folderPath = toPath.toString() + java.io.File.separator + fromPath.getFileName();
		toFileSystem.createFolder(Paths.get(folderPath));
		toFileSystem.setPath(Paths.get(folderPath));
		toPath = Paths.get(folderPath);
		
		Iterable<File> files = fromFileSystem.files(fromPath);
		for (File file : files) {
			Path newFromFilePath = Paths.get(fromPath.toString() + java.io.File.separator + file.name());
			Path newToFilePath = Paths.get(toPath.toString() + java.io.File.separator + file.name());
			
			//System.out.println(newFromFilePath.toString());
			//System.out.println(newToFilePath.toString());
			if (file.isDirectory()) {
				FileSystems.copyDirectory(newFromFilePath, toPath, fromFileSystem, toFileSystem);
				
			} else {
				FileSystems.copyFile(newFromFilePath, newToFilePath, fromFileSystem, toFileSystem);
			}
		}
		
		fromFileSystem.setPath(oldFromFsPath);
		toFileSystem.setPath(oldToFsPath);
	}
}
