package mainpackage;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XcodeCrashlogParser implements StringParser {

	String loadAddress;
	String buildUUID;
	String architecure;
	Collection<String> stackAdresses;
	Collection<String> stackStrings;

	@Override
	public void parse(String string) {
		parseLoadAddress(string);
		parseStackAddresses(string);
		parseBuildUUID(string);
		parseArchitecture(string);
	}

	private void parseStackAddresses(String string) {
		Pattern p = Pattern
				.compile("\\d+ +[^ ]+ +\t(0x[^ ]+) ([^\\+]+ \\+ \\d+)\n");
		Matcher matcher = p.matcher(string);

		stackAdresses = new Vector<String>();
		stackStrings = new Vector<String>();

		while (matcher.find()) {
			stackAdresses.add(matcher.group(1));
			stackStrings.add(matcher.group(2));
			// System.out.println(matcher.group(0));
		}
	}

	private void parseLoadAddress(String string) {
		Pattern p = Pattern.compile("Binary Images:\n {0,3}([^ ]+) ");
		Matcher matcher = p.matcher(string);

		while (matcher.find()) {
			this.loadAddress = matcher.group(1);
			if (this.loadAddress != null) {
				break;
			}
		}

		if (this.loadAddress == null) {
			System.out.print("Can't find load address in crashlog");
		}
	}

	private void parseBuildUUID(String string) {
		Pattern p = Pattern.compile("Binary Images:\n {0,3}[^<]+<([^>]+)>");
		Matcher matcher = p.matcher(string);

		while (matcher.find()) {
			this.buildUUID = matcher.group(1);
			if (this.buildUUID != null) {
				break;
			}
		}

		if (this.buildUUID == null) {
			System.out.print("Can't find load buildUUID in crashlog");
		}
	}

	private void parseArchitecture(String string) {
		Pattern p = Pattern
				.compile("Binary Images:\n {0,3}[^\\+]+[^ ]+ ([^ ]+)");
		Matcher matcher = p.matcher(string);

		while (matcher.find()) {
			this.architecure = matcher.group(1);
			if (this.architecure != null) {
				break;
			}
		}

		if (this.architecure == null) {
			System.out.print("Can't find architecure in crashlog");
		}
	}
}
