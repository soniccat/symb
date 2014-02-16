package mainpackage;
import java.nio.charset.StandardCharsets;

public class XcodeCrashlogManager extends FileManager {
	
	public XcodeCrashlogManager(FileSystem fileSystem) {
		super(fileSystem);
	}
	
	public void addCrashLog(String name, String content)
	{
		String pathToFile = this.fileSystem.path() + java.io.File.separator + name;
		this.fileSystem.beginWriting(new Path(pathToFile));
		fileSystem.writeString(content, StandardCharsets.UTF_8, false);
		this.fileSystem.finishWriting();
	}
}
