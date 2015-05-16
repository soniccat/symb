package consoleTool.atos;

import consoleTool.ConsoleTool;
import filesystem.Path;

public class AtosTool extends ConsoleTool {

	String path; //atos path
	
	public AtosTool(String path) {
		super();
		this.path = path;
	}
	
	public static Long calcAddress(Long address, Long vmaddress, Long loadAddress)
	{
		Long resultAddress = vmaddress + address - loadAddress;
		return resultAddress;
	}
	
	public void run(Long address, String arch, Path filePath)
	{		
		String resultHex = "0x" + Long.toHexString(address);	
		String[] atosString = {this.path.toString(), "-arch", arch, "-o", filePath.toString(), resultHex};
		this.setStrings(atosString);
		super.run();
		
		if (this.result.equals(resultHex)) {
			this.result = null;
		}
	}
}
