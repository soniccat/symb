package mainpackage;

import filesystem.FileSystem;

public abstract class FileManager {
	FileSystem fileSystem;
	
	public FileManager(FileSystem fileSystem) {
		this.fileSystem = fileSystem;
	}
	
	
}
