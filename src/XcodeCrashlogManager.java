import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class XcodeCrashlogManager extends FileManager {
	
	public XcodeCrashlogManager(FileSystem fileSystem) {
		super(fileSystem);
	}
	
	public void addCrashLog(String name, String content)
	{
		String pathToFile = this.fileSystem.path() + java.io.File.separator + name;
		this.fileSystem.beginWriting(Paths.get(pathToFile));
		fileSystem.writeString(content, StandardCharsets.UTF_8, false);
		this.fileSystem.finishWriting();
	}
}
