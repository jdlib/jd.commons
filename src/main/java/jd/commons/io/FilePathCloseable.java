package jd.commons.io;


import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;


/**
 * A FilePath class that implements Closeable.
 * When closed, it deletes the underlying file or recursively deletes the underlying directory.
 * @see FilePath#toCloseable()
 */
public class FilePathCloseable extends FilePath implements Closeable
{
	public FilePathCloseable(Path path)
	{
		super(path);
	}
	

	@Override
	public void close() throws IOException
	{
		if (isDirectory())
			deleteRecursively();
		else
			deleteIfExists();
	}
}
