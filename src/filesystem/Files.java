package filesystem;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Paths;


public class Files 
{
	public static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = java.nio.file.Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	public static void writeFile(String path, String string, Charset encoding) throws IOException
	{
		java.io.File file = new java.io.File(path);
		FileOutputStream outputStream = new FileOutputStream(file);
		outputStream.write(string.getBytes(encoding));
		outputStream.close();
	}
	
	public static boolean filesEqualCheck(File f1, File f2) {
		if (f1 == f2) {
			return true;
		}
		
		if (f1.isDirectory() || f1.isDirectory()) { 
			return false;
		}
		
		if (!f1.getClass().isInstance(File.class) || !f2.getClass().isInstance(File.class)) {
			return false;
		}
		
		return f1.path().equals(f2.path());
	}
	
	public static int hashOfFile(File f)
	{
		return f.path().hashCode();
	}
}
