package filesystem;


public interface File {
	Path path();
	String name();
	boolean isDirectory();
	long lastModified();
}
