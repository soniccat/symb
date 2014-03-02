package mainpackage;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import filesystem.FileSystems;
import filesystem.Path;
import filesystem.ftp.FtpFileSystem;
import filesystem.local.LocalFileSystem;


public class SyncFilesCommand implements Command {

	public Path localPath;
	public Path ftpPath;
	public String ftpUserName;
	public String ftpUserPassword;
	public Path syncLogPath;
	public int subDirectorySyncCount;

	long lastSyncDateFromFile() {
		LocalFileSystem lfs = new LocalFileSystem(new Path("./"));

		if (lfs.isFileExists(this.syncLogPath)) {
			String str = FileSystems.readFileToString(this.syncLogPath, lfs, StandardCharsets.UTF_8);
			return Long.parseLong(str);
		}
		
		return 0;
	}

	@Override
	public void run() {
		FtpFileSystem ftpFs = new FtpFileSystem();
		DateFormat dataFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		LocalFileSystem syncLocalFileSystem = new LocalFileSystem(new Path("./"));
		Date lastSyncDate = null;
		if (syncLocalFileSystem.isFileExists(this.syncLogPath)) {
			String syncDateString = FileSystems.readFileToString(this.syncLogPath, syncLocalFileSystem, StandardCharsets.UTF_8);
			try {
				lastSyncDate = dataFormat.parse(syncDateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String ftpHost = ftpPath.firstPart().toString();
		String ftpTail = ftpPath.tailPart().toString();

		ftpFs.connect(ftpHost, this.ftpUserName, this.ftpUserPassword);
		ftpFs.setPath(new Path(ftpTail));

		LocalFileSystem lfs = new LocalFileSystem(new Path("./"));
		lfs.createFolder(this.localPath);
		lfs.setPath(this.localPath);

		
		long syncTime = 0;
		if (lastSyncDate != null) {
			syncTime = lastSyncDate.getTime();
		}
		
		FileSystems.syncDirectories(ftpFs, lfs, syncTime, this.subDirectorySyncCount);
		ftpFs.disconnect();
		
		Date date = new Date();
		FileSystems.writeStringToFile(dataFormat.format(date), this.syncLogPath, syncLocalFileSystem, StandardCharsets.UTF_8);
	}

	@Override
	public int resultCode() {
		// TODO Auto-generated method stub
		return 0;
	}

}
