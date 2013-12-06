import java.nio.charset.Charset;
import java.nio.file.Path;


public interface FileSystem {
	Path path();
	Iterable<File> files();
	Iterable<File> files(Path path);
	
	void beginWriting(Path path);
	void writeString(String string, Charset encoding, boolean append);
	void writeBytes(byte[] byteArray, int countToWrite, boolean append);
	void finishWriting();
	
	void beginReading(Path path);
	int readBytes(byte[] buffer, int start);
	void finishReading();
	
	void setPath(Path path);
	void createFolder(Path path);
}
