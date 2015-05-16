package consoleTool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import mainpackage.Command;


public class ConsoleTool implements Command {
	protected String[] strings;
	protected String[] envp;
	protected boolean isDebugMode;
	public String result;
	
	public ConsoleTool(String string) 
	{
		this.strings = new String[] {string};
	}
	
	public ConsoleTool(String[] strings)
	{
		this.strings = strings;
	}
	
	public ConsoleTool()
	{
		
	}
	
	public void setDebugMode(boolean value) {
		isDebugMode = value;
	}
	
	public void setStrings(String[] strings) {
		this.strings = strings;
	}
	
	public void setEnvp(String[] envp) {
		this.envp = envp;
	}
	
	public void run() 
	{
		try {
			Process p;

			if (isDebugMode) {
				System.out.printf("Start: %s\n",Arrays.toString(this.strings));
			}
			
			if (strings.length == 1) {
				p = Runtime.getRuntime().exec(strings[0],this.envp);
			}else {
				p = Runtime.getRuntime().exec(strings,this.envp);
			}
			
			InputStream stream = this.streamFromProcess(p);
			BufferedReader reader = null;
			
			if(stream != null) {
				reader = new BufferedReader(new InputStreamReader(stream));
			}
			
			StringBuilder out = new StringBuilder(); 
			String line = null;
			
			do {
				while(reader != null && reader.ready()){ 
					line = reader.readLine();
					out.append(line);
					out.append('\n');
				
					if (isDebugMode) {
						System.out.println(line);
					}
				}
				
				stream = this.streamFromProcess(p);
				
				if(!this.isRunning(p) && stream == null) {
					break;
				}
				
				reader = new BufferedReader(new InputStreamReader(stream));
			} while(this.isRunning(p) || stream != null);
			
			result = out.toString();
			
			if (isDebugMode) {
				System.out.println("Completed");
			}
			
		} catch (Exception e) {
			System.out.println("Exception:");
			e.printStackTrace();
		}
	}
	
	boolean isRunning(Process process) {
	    try {
	        process.exitValue();
	        return false;
	    } catch (Exception e) {
	        return true;
	    }
	}
	
	InputStream streamFromProcess(Process process) throws IOException, InterruptedException 
	{
		InputStream stream = null;
		while(stream == null)
		{
			Thread.sleep(10);
			if (process.getErrorStream().available() > 0) {
				stream = process.getErrorStream();
			} else if (process.getInputStream().available() > 0){
				stream = process.getInputStream();
			}
			
			if(!this.isRunning(process)) {
				break;
			}
		}
		
		return stream;
	}

	@Override
	public int resultCode() {
		// TODO Auto-generated method stub
		return 0;
	}
}
