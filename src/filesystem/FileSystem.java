package filesystem;
import java.nio.charset.Charset;


public interface FileSystem {
	Path path();
	Path absolutePathFromRelativePath(Path relativePath);
	
	File file();
	
	Iterable<File> files();
	Iterable<File> files(Path path);
	boolean isFileExists(Path path);
	
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
