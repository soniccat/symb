package mainpackage;



public class Path {
	final public String separator = "/";
	public String string;
	
	public Path(String value)
	{
		this.string = value;
	}
	
	public String fileName()
	{
		int lastSlashIndex = this.string.lastIndexOf(this.separator);
		if (lastSlashIndex == -1) {
			return this.string;
		}
		
		return this.string.substring(lastSlashIndex+1);
	}
	
	public String toString()
	{
		return this.string;
	}
	
	public Path parentPath()
	{
		int lastSlashIndex = this.string.lastIndexOf(this.separator);
		if (lastSlashIndex == -1) {
			return new Path(this.separator);
		}
		
		return new Path(this.string.substring(0, lastSlashIndex));
	}
	
	public Path firstPart()
	{
		int splashIndex = this.string.indexOf(this.separator);
		if (splashIndex == -1) {
			return this;
		}
		
		return new Path(this.string.substring(0, splashIndex));
	}
	
	public Path tailPart()
	{
		if (this.string == null) {
			return null;
		}
		
		int splashIndex = this.string.indexOf(this.separator);
		if (splashIndex == -1) {
			return null;
		}
		
		return new Path(this.string.substring(splashIndex));
	}
	
	public Path pathByAppendingFileName(String name) {
		return new Path(this.string + this.separator + name);
	}
}
