import java.nio.charset.Charset;
import java.nio.file.Path;


public interface FileSystem {
	Path path();
	Iterable<File> files();
	Iterable<File> files(Path path);
	
	void setPath(Path path);
	void writeString(Path path, String string, Charset encoding, boolean append);
	void writeBytes(Path path, byte[] byteArray, int countToWrite, boolean append);
	int readBytes(Path path, byte[] buffer, int start);
	void createFolder(Path path);
}
