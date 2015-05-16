package mainpackage;
import java.nio.charset.StandardCharsets;

import filesystem.FileSystem;
import filesystem.Path;

public class XcodeCrashlogManager extends FileManager {
	
	public XcodeCrashlogManager(FileSystem fileSystem) {
		super(fileSystem);
	}
	
	public void addCrashLog(String name, String content) {
		this.fileSystem.beginWriting(getStorePath(name));
		fileSystem.writeString(content, StandardCharsets.UTF_8, false);
		this.fileSystem.finishWriting();
	}
	
	public Path getStorePath(String name) {
		return this.fileSystem.path().pathByAppendingFileName(name);
	}
}
