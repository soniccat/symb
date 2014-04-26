package consoleTool.atos;

import consoleTool.ConsoleTool;
import filesystem.Path;

public class AtosTool extends ConsoleTool {

	String path; //atos path
	
	public AtosTool(String path) {
		super();
		this.path = path;
	}
	
	public static Integer calcAddress(Integer address, Integer vmaddress, Integer loadAddress)
	{
		Integer resultAddress = vmaddress + address - loadAddress;
		return resultAddress;
	}
	
	public void run(Integer address, String arch, Path filePath)
	{		
		String resultHex = "0x" + Integer.toHexString(address);	
		String[] atosString = {this.path.toString(), "-arch", arch, "-o", filePath.toString(), resultHex};
		this.setStrings(atosString);
		super.run();
		
		if (this.result.equals(resultHex)) {
			this.result = null;
		}
	}
}
