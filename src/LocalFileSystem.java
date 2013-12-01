
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;

public class LocalFileSystem implements FileSystem {
	java.io.File file;
	
	public LocalFileSystem(Path path) 
	{
		this.setPath(path);
	}
	
	public Path path() {
		return this.file.toPath();
	}
	
	public void setPath(Path path) {
		String stringPath = path.toString();
		this.file = new java.io.File(stringPath);
	}

	@Override
	public Iterable<File> files() {
		return this.files(this.path());
	}

	@Override
	public Iterable<File> files(Path path) {
		java.io.File pathFile = new java.io.File(path.toString());
		java.io.File[] files = pathFile.listFiles();
		List<java.io.File> list = Arrays.asList(files);
		
		Vector<File> filesVector = new Vector<File>();
		for (java.io.File file : list) {
			filesVector.add(new LocalFile(file));
		}
		
		return filesVector;
	}
	
	public void writeBytes(Path path, byte[] byteArray, int countToWrite, boolean append) 
	{
		java.io.File file = new java.io.File(path.toString());
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file, append);
			outputStream.write(byteArray,0,countToWrite);
			outputStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeString(Path path, String string, Charset encoding, boolean append)
	{
		byte[] bytes = string.getBytes(encoding);
		this.writeBytes(path, bytes, bytes.length, append);
	}
	
	public int readBytes(Path path, byte[] buffer, int start) {
		java.io.File file = new java.io.File(path.toString());
		FileInputStream inputStream;
		
		int nextByte = 0;
		try {
			inputStream = new FileInputStream(file);

			inputStream.skip(start);
			nextByte = inputStream.read(buffer, 0, buffer.length);
			inputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			nextByte = -1;
		}
		
		return nextByte;
	}
	
	public void createFolder(Path path) {
		java.io.File file = new java.io.File(path.toString());
		file.mkdir();
	}
}

class LocalFile implements File {

	java.io.File file;
	
	public LocalFile(java.io.File file) {
		this.file = file;
	}
	
	public LocalFile(Path path) {
		this.file = new java.io.File(path.toString());
	}
	
	@Override
	public Path path() {
		return file.toPath();
	}

	@Override
	public String name() {
		return file.getName();
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}
}