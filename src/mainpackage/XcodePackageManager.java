package mainpackage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class XcodePackageManager extends FileManager {
	
	public String namePrefix;
	public String nameSuffix;

	public XcodePackageManager(FileSystem fileSystem) 
	{
		super(fileSystem);
		this.namePrefix = "";
		this.nameSuffix = "";
	}

	public void storePackage(XcodePackage pack) 
	{
		DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy HH-mm-ss");
		Date date = new Date();
		
		String folderName = this.namePrefix + dateFormat.format(date) + this.nameSuffix;
		Path folderPath = new Path(this.fileSystem.path()
				+ java.io.File.separator + folderName);

		System.out.println(folderPath.toString());
		this.fileSystem.createFolder(folderPath);
		this.fileSystem.setPath(folderPath);

		LocalFileSystem fromFileSystem = new LocalFileSystem(new Path(""));

		System.out.printf("%s coping\n", pack.appPath.toString());
		FileSystems.copyDirectory(pack.appPath, folderPath, fromFileSystem,
				this.fileSystem);

		System.out.printf("%s coping\n", pack.dsymPath.toString());
		FileSystems.copyDirectory(pack.dsymPath, folderPath, fromFileSystem,
				this.fileSystem);

		System.out.printf("The package was stored\n");
	}
	
	public Iterable<XcodePackage> packages()
	{
		Vector<XcodePackage> packs = new Vector<XcodePackage>();
		Iterable<File> files = this.fileSystem.files();
		
		for( File file : files ) {
			if (file.isDirectory()) {
				XcodePackage pack = this.packageFromPath(file.path());
				if(pack != null) {
					packs.add(pack);
				}
			}
		}
		
		return packs;
	}

	private XcodePackage packageFromPath(Path path)
	{
		XcodePackage pack = new XcodePackage();
		pack.name = path.fileName().toString();

		Iterable<File> files = this.fileSystem.files(path);
		for( File file : files ) {
			if(file.name().endsWith(".app")) {
				pack.appPath = file.path();
				
			} else if(file.name().endsWith(".dSYM")) {
				pack.dsymPath = file.path();
			}
		}
		
		if (pack.appPath == null) {
			return null;
		}
		
		return pack;
	}
}
