package mainpackage;
import java.awt.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Vector;

import org.apache.commons.net.ftp.*;


public class FtpFileSystem implements FileSystem{
	
	//Path activeFilePath;
	FTPClient ftp;
	
	InputStream inputStream;
	OutputStream outputStream;
	
	public boolean connect(String url, String login, String password)
	{
		this.ftp = new FTPClient();
		
		try {
			ftp.connect(url);
			ftp.login(login, password);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int reply = this.ftp.getReplyCode();
		if (this.ftp.isConnected() && !FTPReply.isPositiveCompletion(reply)) {
			try {
				this.ftp.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		boolean connected = this.ftp.isConnected() && FTPReply.isPositiveCompletion(reply);
		
		if (connected) {
			try {
				this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return connected;
	}
	
	public void disconnect()
	{
		try {
			this.ftp.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (files != null && files.length > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Iterable<File> files(Path path) {		
		FTPFile[] files = null;
		path = this.absolutePathFromRelativePath(path);
		
		try {
			files = this.ftp.listFiles(path.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Vector<File> vector = new Vector<File>();
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
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void finishWriting() {
		try {
			this.outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.ftp.completePendingCommand();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}

	@Override
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

	@Override
	public void finishReading() {
		try {
			this.inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.ftp.completePendingCommand();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		try {
			this.ftp.changeWorkingDirectory(path.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void createFolder(Path path) {
		path = this.absolutePathFromRelativePath(path);
		
		try {
			this.ftp.makeDirectory(path.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
