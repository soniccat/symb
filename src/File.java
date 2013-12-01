import java.nio.file.Path;


public interface File {
	Path path();
	String name();
	boolean isDirectory();
}
