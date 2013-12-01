import java.io.FileNotFoundException;
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
}
