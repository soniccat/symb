package filesystem.ftp;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Vector;

import org.apache.commons.net.ftp.*;

import filesystem.File;
import filesystem.FileSystem;
import filesystem.Files;
import filesystem.Path;


public class FtpFileSystem implements FileSystem{
	
	String url;
	String login;
	String password;
	Path cachedPath;
	
	//Path activeFilePath;
	FTPClient ftp;
	
	InputStream inputStream;
	OutputStream outputStream;
	
	public boolean connect(String url, String login, String password)
	{
		this.url = url;
		this.login = login;
		this.password = password;
		
		this.ftp = new FTPClient();
		
		try {
			ftp.connect(url);
			ftp.login(login, password);
		} catch (IOException e) {
			this.logException(e);
		}
		
		int reply = this.ftp.getReplyCode();
		if (this.ftp.isConnected() && !FTPReply.isPositiveCompletion(reply)) {
			try {
				this.ftp.disconnect();
			} catch (IOException e) {
				this.logException(e);
			}
		}
		
		boolean connected = this.ftp.isConnected() && FTPReply.isPositiveCompletion(reply);
		
		if (connected) {
			try {
				this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
				this.ftp.setRemoteVerificationEnabled(false);
				this.ftp.setControlKeepAliveTimeout(300);
				this.ftp.setBufferSize(5*1024*1024);
				this.ftp.enterLocalPassiveMode();
			} catch (IOException e1) {
				this.logException(e1);
			}
		}
		
		return connected;
	}
	
	public void disconnect()
	{
		try {
			this.ftp.disconnect();
		} catch (IOException e) {
			this.logException(e);
		}
	}

	@Override
	public Path path() {
		Path result = null;
		try {
			if (this.ftp == null) {
				return new Path("/");
			} else {
				result = new Path(this.ftp.printWorkingDirectory());
			}
		} catch (IOException e) {
			this.logException(e);
		}
		
		return result;
	}
	
	FTPFileFilter filterByName(String name){
		final String n = name;
		FTPFileFilter filter = new FTPFileFilter() {
			
			@Override
			public boolean accept(FTPFile arg0) {
				String name1 = arg0.getName();
				return name1.equals(n);
			}
		};
		
		return filter;
	}
	
	Path parentPath()
	{
		Path p = null;
		try {
			p = new Path(this.ftp.printWorkingDirectory()).parentPath();
		} catch (IOException e) {
			this.logException(e);
			
			try {
				p = new Path(this.ftp.printWorkingDirectory()).parentPath();
			} catch (IOException e1) {
				this.logException(e1);
			}
		}
		
		return p;
	}
	
	public File file()
	{
		FTPFileFilter filter = filterByName(this.path().fileName());
		FTPFile[] files;
		FtpFile result = null;
		try {
			String path = parentPath().toString();
			files = this.ftp.listFiles(path, filter);
			result = new FtpFile(files[0], this.path());
		} catch (IOException e) {
			this.logException(e);
			
			try {
				String path = parentPath().toString();
				files = this.ftp.listFiles(path, filter);
				result = new FtpFile(files[0], this.path());
			} catch (IOException e1) {
				this.logException(e1);
			}
			
		}
		
		return result;
	}

	@Override
	public Iterable<File> files() {
		return files(this.path());
	}
	
	public boolean isFileExists(Path path)
	{
		FTPFile[] files = null;
		try {
			files = this.ftp.listFiles(path.toString());
		} catch (IOException e) {
			this.logException(e);
			
			try {
				files = this.ftp.listFiles(path.toString());
			} catch (IOException e1) {
				this.logException(e1);
			}
		}
		if (files != null && files.length > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	void logException(IOException e)
	{
		int reply = this.ftp.getReplyCode();
		System.out.printf("message=%s\nlocalized message=%s\nreply code=%d\nstring=%s\n",e.getMessage(), e.getLocalizedMessage(), reply,this.ftp.getReplyString());
		for(String s : this.ftp.getReplyStrings()) {
			System.out.print(s);
		}
		
		e.printStackTrace();
		this.reconnect();
	}
	
	void reconnect()
	{
		System.out.print("Start reconnecting...");
		Path currentPath = this.cachedPath;
		
		this.disconnect();
		this.connect(url, login, password);
		this.setPath(currentPath);
		System.out.println("reconnected");
	}

	@Override
	public Iterable<File> files(Path path) {		
		FTPFile[] files = null;
		path = this.absolutePathFromRelativePath(path);
		
		try {
			files = this.ftp.listFiles(path.toString());
		} catch (IOException e) {
			this.logException(e);
			try {
				files = this.ftp.listFiles(path.toString());
			} catch (IOException e1) {
				this.logException(e1);
			}
		}
		
		Vector<File> vector = new Vector<File>();
		if (files == null) {
			return vector;
		}
		
		for (FTPFile file : Arrays.asList(files)) {
			String name = file.getName();
			if (name.equals(".") || name.equals("..")) {
				continue;
			}
			
			FtpFile ftpFile = new FtpFile(file, new Path(path.toString() + "/" + name));
			vector.add(ftpFile);
		}
		
		return vector;
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
	//TODO: return status code
	public void beginWriting(Path path) {
		this.prepareToWriteOrRead();
		path = this.absolutePathFromRelativePath(path);
		
		try {
			this.outputStream = this.ftp.storeFileStream(path.toString());
		} catch (IOException e) {
			this.logException(e);
			
			try {
				this.outputStream = this.ftp.storeFileStream(path.toString());
			} catch (IOException e1) {
				this.logException(e1);
			}
		}
	}

	@Override
	public void writeString(String string, Charset encoding, boolean append) {
		byte[] bytes = string.getBytes(encoding);
		this.writeBytes(bytes, bytes.length, append);
	}

	@Override
	public void writeBytes(byte[] byteArray, int countToWrite, boolean append) {
		try {
			this.outputStream.write(byteArray,0,countToWrite);
		} catch (IOException e) {
			this.logException(e);
			
			try {
				this.outputStream.write(byteArray,0,countToWrite);
			} catch (IOException e1) {
				this.logException(e1);
			}
		}
	}

	@Override
	public void finishWriting() {
		try {
			this.outputStream.close();
		} catch (IOException e) {
			this.logException(e);
		}
		
		try {
			this.ftp.completePendingCommand();
		} catch (IOException e) {
			this.logException(e);
		}
		this.outputStream = null;
	}

	@Override
	public void beginReading(Path path) {
		this.prepareToWriteOrRead();
		path = this.absolutePathFromRelativePath(path);
		
		try {
			this.inputStream = this.ftp.retrieveFileStream(path.toString());
		} catch (IOException e) {
			this.logException(e);
			
			try {
				this.inputStream = this.ftp.retrieveFileStream(path.toString());
			} catch (IOException e1) {
				this.logException(e1);
			}
		}
	}

	@Override
	public int readBytes(byte[] buffer, int start) {
		int nextByte = 0;
		try {
			if (start > 0) {
				this.inputStream.skip(start);
			}
			nextByte = this.inputStream.read(buffer, 0, buffer.length);
		} catch (Exception e) {
			e.printStackTrace();
			nextByte = -1;
		}
		
		return nextByte;
	}

	@Override
	public void finishReading() {
		try {
			this.inputStream.close();
		} catch (IOException e) {
			this.logException(e);
		}
		
		try {
			this.ftp.completePendingCommand();
		} catch (IOException e) {
			this.logException(e);
		}
		this.inputStream = null;
	}
	
	public Path absolutePathFromRelativePath(Path relativePath) 
	{
		String resultPath = relativePath.toString();
		if (resultPath.startsWith(".")) {
			resultPath = this.path().toString() + resultPath.substring(1);
		}
		return new Path(resultPath);
	}

	@Override
	public void setPath(Path path) {
		path = this.absolutePathFromRelativePath(path);
		this.cachedPath = path;
		
		try {
			this.ftp.changeWorkingDirectory(path.toString());
		} catch (IOException e) {
			this.logException(e);
			
			try {
				this.ftp.changeWorkingDirectory(path.toString());
			} catch (IOException e1) {
				this.logException(e1);
			}
		}
	}

	@Override
	public void createFolder(Path path) {
		path = this.absolutePathFromRelativePath(path);
		
		try {
			this.ftp.makeDirectory(path.toString());
		} catch (IOException e) {
			this.logException(e);
			
			try {
				this.ftp.makeDirectory(path.toString());
			} catch (IOException e1) {
				this.logException(e1);
			}
		}
	}

	public String toString()
	{
		return this.path().toString();
	}
}

class FtpFile implements File
{
	FTPFile file;
	Path path;
	
	public FtpFile(FTPFile inFile, Path inPath) 
	{
		this.file = inFile;
		this.path = inPath;
	}
	
	@Override
	public Path path() {
		return this.path;
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
		return file.getTimestamp().getTimeInMillis();
	}
	
	public String toString()
	{
		return this.path.toString();
	}
}
