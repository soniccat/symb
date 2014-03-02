package filesystem.local;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;

import filesystem.File;
import filesystem.FileSystem;
import filesystem.Files;
import filesystem.Path;

public class LocalFileSystem implements FileSystem {
	java.io.File file;
	
	Path activeFilePath;
	FileOutputStream outputStream;
	FileInputStream inputStream;
	
	public LocalFileSystem(Path path) 
	{
		this.setPath(path);
	}

	public Path path() {
		String stringValue = null;
		if (this.file == null) {
			stringValue = java.nio.file.Paths.get(".").toAbsolutePath().toString();
		} else {
			stringValue = this.file.toPath().toAbsolutePath().toString();
		}
		
		Path p = new Path(stringValue);
		return p;
	}
	
	public File file()
	{
		LocalFile lf = new LocalFile(this.file);
		return lf;
	}
	
	public Path absolutePathFromRelativePath(Path relativePath) 
	{
		String resultPath = relativePath.toString();
		if (resultPath.startsWith(".")) {
			String currentPath = this.path().toString();
			/*if (currentPath.endsWith(".")) {
				currentPath = currentPath.substring(0, currentPath.length()-1);
			}*/
			
			//System.out.printf("path %s|%s",currentPath,resultPath.substring(1));
			resultPath = currentPath + resultPath.substring(1);
		}
		return new Path(resultPath);
	}
	
	public void setPath(Path path) {
		path = this.absolutePathFromRelativePath(path);
		
		String stringPath = path.toString();
		this.file = new java.io.File(stringPath);
	}

	@Override
	public Iterable<File> files() {
		return this.files(this.path());
	}

	@Override
	public Iterable<File> files(Path path) {
		path = this.absolutePathFromRelativePath(path);
		
		java.io.File pathFile = new java.io.File(path.toString());
		java.io.File[] files = pathFile.listFiles();
		List<java.io.File> list = Arrays.asList(files);
		
		Vector<File> filesVector = new Vector<File>();
		for (java.io.File file : list) {
			filesVector.add(new LocalFile(file));
		}
		
		return filesVector;
	}
	
	public boolean isFileExists(Path path)
	{
		java.io.File f = new java.io.File(path.toString());
		return f.exists();//t f.isFile();
	}
	
	public void writeBytes(byte[] byteArray, int countToWrite, boolean append) 
	{
		try {
			outputStream.write(byteArray,0,countToWrite);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeString(String string, Charset encoding, boolean append)
	{
		byte[] bytes = string.getBytes(encoding);
		this.writeBytes(bytes, bytes.length, append);
	}
	
	public int readBytes(byte[] buffer, int start) {
		int nextByte = 0;
		try {
			this.inputStream.skip(start);
			nextByte = this.inputStream.read(buffer, 0, buffer.length);
		} catch (Exception e) {
			e.printStackTrace();
			nextByte = -1;
		}
		
		return nextByte;
	}
	
	public void createFolder(Path path) {
		path = this.absolutePathFromRelativePath(path);
		
		java.io.File file = new java.io.File(path.toString());
		file.mkdirs();
	}
	
	public void prepareToWriteOrRead() {
		if(this.inputStream != null) {
			this.finishReading();
		}
		
		if(this.outputStream != null) {
			this.finishWriting();
		}
	}

	@Override
	public void beginWriting(Path path) {
		this.prepareToWriteOrRead();
		
		path = this.absolutePathFromRelativePath(path);
		this.activeFilePath = path;
		java.io.File file = new java.io.File(this.activeFilePath.toString());
		try {
			this.outputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.activeFilePath = null;
			this.outputStream = null;
		}
	}

	@Override
	public void finishWriting() {
		try {
			this.outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.outputStream = null;
		this.activeFilePath = null;
	}

	@Override
	public void beginReading(Path path) {
		this.prepareToWriteOrRead();
		
		path = this.absolutePathFromRelativePath(path);
		this.activeFilePath = path;
		java.io.File file = new java.io.File(this.activeFilePath.toString());
		try {
			this.inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.activeFilePath = null;
			this.inputStream = null;
		}
	}

	@Override
	public void finishReading() {
		try {
			this.inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.inputStream = null;
		this.activeFilePath = null;
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
		Path p = new Path(file.toPath().toString());
		return p;
	}

	@Override
	public String name() {
		return file.getName();
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}
	
	public boolean equals(Object obj) {
		return Files.filesEqualCheck(this, (File)obj);
	}
	
	public int hash()
	{
		return Files.hashOfFile(this);
	}
	
	public long lastModified()
	{
		return file.lastModified();
	}
	
	public String toString()
	{
		return this.file.getPath();
	}
}