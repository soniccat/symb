import java.util.Vector;


public class ArgsParser {
	public String errorDescription;
	public CommandFactory commandFactory = new CommandFactory();
	
	public Command parse(String [] arguments) 
	{
		String commandName = arguments[0];
		Vector<String> parameters = new Vector<>();
		for (int i=1; i<arguments.length; ++i) {
			parameters.add(arguments[i]);
		}
		
		Command command = commandFactory.createCommand(commandName, parameters);
		
		return command;
	}
}
