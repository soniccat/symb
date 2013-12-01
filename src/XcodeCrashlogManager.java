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
		fileSystem.writeString(Paths.get(pathToFile), content, StandardCharsets.UTF_8, false);
	}
}
