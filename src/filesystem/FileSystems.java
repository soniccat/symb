package filesystem;
import java.nio.charset.Charset;
import java.util.Vector;


public class FileSystems {
	
	public static String readFileToString(Path fromPath, FileSystem fs, Charset charset) {
		StringBuilder sbuilder = new StringBuilder();
		
		byte[] buffer = new byte[1024*50];
		int wasReadedLen;
		boolean dataIsAvailable = true;
		
		fs.beginReading(fromPath);
		
		while (dataIsAvailable) {
			wasReadedLen = fs.readBytes(buffer, 0);
			
			if (wasReadedLen == -1) {
				dataIsAvailable = false;
				break;
			} else {
				sbuilder.append( new String(buffer,0, wasReadedLen, charset) );
			}
		}
		
		fs.finishReading();
		
		return sbuilder.toString();
	}
	
	public static void writeStringToFile(String str, Path toPath, FileSystem fs, Charset charset) {
		fs.beginWriting(toPath);
		fs.writeString(str, charset, false);
		fs.finishWriting();
	}
	
	//TODO: add error return
	public static void copyFile(Path fromPath, Path toPath, FileSystem fromFileSystem, FileSystem toFileSystem) {
		//System.out.printf("copyFile %s\n",fromPath.toString());
		//System.out.printf("copyFile from %s to %s\n",fromFileSystem.absolutePathFromRelativePath(fromPath).toString(), toFileSystem.absolutePathFromRelativePath(toPath).toString() );
		
		byte[] buffer = new byte[1024*50];
		
		boolean dataIsAvailable = true;
		boolean append = false;
		int wasReadedLen;
		
		fromFileSystem.beginReading(fromPath);
		toFileSystem.beginWriting(toPath);
		
		while (dataIsAvailable) {
			wasReadedLen = fromFileSystem.readBytes(buffer, 0);
			if (wasReadedLen == -1) {
				dataIsAvailable = false;
				break;
			}

			toFileSystem.writeBytes(buffer, wasReadedLen, append);
			
			append = true;
		}
		
		fromFileSystem.finishReading();
		toFileSystem.finishWriting();
	}
	
	public static void copyDirectory(Path fromPath, Path toPath, FileSystem fromFileSystem, FileSystem toFileSystem) {
		Path oldFromFsPath = fromFileSystem.path();
		Path oldToFsPath = toFileSystem.path();
		
		fromFileSystem.setPath(fromPath);
		
		//create a folder in toPath and move in it
		//System.out.printf("%s\n",fromPath.getFileName());
		String folderPath = toPath.toString() + java.io.File.separator + fromPath.fileName();
		if (fromPath.fileName().toString().equals(".")) {
			folderPath = toPath.toString();
		} else {
			folderPath = toPath.toString() + java.io.File.separator + fromPath.fileName();
		}
		
		toFileSystem.createFolder(new Path(folderPath));
		toFileSystem.setPath(new Path(folderPath));
		toPath = new Path(folderPath);
		
		System.out.printf("copy directory from %s to %s\n",fromFileSystem.absolutePathFromRelativePath(fromPath).toString(), toFileSystem.absolutePathFromRelativePath(toPath).toString() );
		
		
		Iterable<File> files = fromFileSystem.files();
		for (File file : files) {
			Path newFromFilePath = new Path("." + java.io.File.separator + file.name());
			Path newToFilePath = new Path("." + java.io.File.separator + file.name());
			
			//System.out.println(newFromFilePath.toString());
			//System.out.println(newToFilePath.toString());
			if (file.isDirectory()) {
				FileSystems.copyDirectory(newFromFilePath, new Path("."), fromFileSystem, toFileSystem);
				
			} else {
				FileSystems.copyFile(newFromFilePath, newToFilePath, fromFileSystem, toFileSystem);
			}
		}
		
		fromFileSystem.setPath(oldFromFsPath);
		toFileSystem.setPath(oldToFsPath);
	}
	
	public static void copyFileObject(File f, Path toPath, FileSystem fromFileSystem, FileSystem toFileSystem) {
		if(f.isDirectory()) {
			FileSystems.copyDirectory(f.path(), toPath, fromFileSystem, toFileSystem);
		} else {
			FileSystems.copyFile(f.path(), toPath, fromFileSystem, toFileSystem);
		}
	}
	
	public static Vector<File> vectorOfFilesFomIterable(Iterable<File> files) 
	{
		Vector<File> vectorOfFiles = new Vector<File>();
		for(File f : files) {
			vectorOfFiles.add(f);
		}
		
		return vectorOfFiles;
	}
	
	public static void syncDirectories(FileSystem fs1, FileSystem fs2, long lastSyncDate, int syncInnerDirectoriesCount) {
		
		Iterable<File> files1 = fs1.files();
		Iterable<File> files2 = fs2.files();
		
		Vector<File> files1Vec = FileSystems.vectorOfFilesFomIterable(files1);
		Vector<File> files2Vec = FileSystems.vectorOfFilesFomIterable(files2);
		
		Vector<File> filteredFiles1 = FileSystems.vectorOfFilesFomIterable(files1);
		Vector<File> filteredFiles2 = FileSystems.vectorOfFilesFomIterable(files2);

		//remove old items
		for (int i=filteredFiles1.size(); --i>=0; ) {
			File f = filteredFiles1.elementAt(i);
			if (f.isDirectory()) {
				if (syncInnerDirectoriesCount == 0 && f.lastModified() <= lastSyncDate) {
					filteredFiles1.remove(i);
				}
				
			} else {
				if (f.lastModified() <= lastSyncDate) {
					filteredFiles1.remove(i);
				}
			}
		}

		for (int i=filteredFiles2.size(); --i>=0; ) {
			File f = filteredFiles2.elementAt(i);
			if (f.isDirectory()) {
				if (syncInnerDirectoriesCount == 0 && f.lastModified() <= lastSyncDate) {
					filteredFiles2.remove(i);
				}
				
			} else {
				if (f.lastModified() <= lastSyncDate) {
					filteredFiles2.remove(i);
				}
			}
		}

		//move files
		if (filteredFiles1.size() !=0 || filteredFiles2.size() !=0) {
			if (syncInnerDirectoriesCount > 0) {
				syncInnerDirectoriesCount--;
			}
			
			moveFiles(fs1, fs2, filteredFiles1, files2Vec, lastSyncDate, syncInnerDirectoriesCount);
			moveFiles(fs2, fs1, filteredFiles2, files1Vec, lastSyncDate, syncInnerDirectoriesCount);
		}
	}

	private static void moveFiles(FileSystem fromFs, FileSystem toFs,
			Vector<File> files, Vector<File> destFiles, long lastSyncDate, int syncInnerDirectoriesCount) {
		for (File f : files) {
			if (!f.isDirectory()) {
				String toFilePath = toFs.path().toString() + java.io.File.separator + f.name();
				if (!FileSystems.haveFileWithName(f.name(), destFiles)) {
					FileSystems.copyFileObject(f, new Path(toFilePath), fromFs, toFs);
				} else {
					//System.out.printf("File already exists %s\n", toFilePath);
				}
			} else {
				if (!FileSystems.haveFileWithName(f.name(), destFiles)) {
					FileSystems.copyDirectory(f.path(), toFs.path(), fromFs, toFs);
				} else {
					Path oldPath1 = fromFs.path();
					Path oldPath2 = toFs.path();
					
					Path newPath1 = new Path("." + java.io.File.separator + f.name());
					Path newPath2 = new Path("." + java.io.File.separator + f.name());
					fromFs.setPath(newPath1);
					toFs.setPath(newPath2);
					
					FileSystems.syncDirectories(fromFs, toFs, lastSyncDate, syncInnerDirectoriesCount);
					
					fromFs.setPath(oldPath1);
					toFs.setPath(oldPath2);
				}
			}
		}
	}
	
	private static boolean haveFileWithName(String fileName, Iterable<File> list)
	{
		for (File f : list) {
			if (f.name().equals(fileName)) {
				return true;
			}
		}
		
		return false;
	}
}
