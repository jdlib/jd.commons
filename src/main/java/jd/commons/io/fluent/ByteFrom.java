/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2025 the original author or authors.
 */
package jd.commons.io.fluent;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.sql.Blob;
import jd.commons.check.Check;
import jd.commons.io.FilePath;
import jd.commons.util.function.XSupplier;

/**
 * ByteFrom allows to specify a {@link ByteSource} for byte input.
 * <ul>
 * <li>Directly specify a {@link #from(ByteSource) ByteSource} 
 * <li>Directly provide a {@link #from(InputStream) InputStream} to be used as ByteSource
 * <li>Specify IO related objects which
 *     can be turned into a InputStream or ByteSource. The default implementations
 *     of these methods forwards them to {@link #from(ByteSource)}
 * </ul>
 * The return value computed from the InputStream or ByteSource depends on the implementation.
 * @param<R> the result type
 * @param<E> the exception thrown by the ByteFrom methods 
 */
public interface ByteFrom<R,E extends Exception>
{
	/**
	 * Accepts a ByteSource.
	 * @param source a ByteSource, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public R from(ByteSource source) throws E;

	
	/**
	 * Accepts a InputStream.
	 * @param in a InputStream, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public R from(InputStream in) throws E;

	
	/**
	 * Creates a ByteSource for the InputStream provided by the Path.
	 * @param path a path, not null
	 * @param options specifying how the path is opened
	 * @return the computed result
	 * @throws E if an error occurs
	 * @see Files#newInputStream(Path, OpenOption...)
	 */
	public default R from(Path path, OpenOption... options) throws E
	{
		return from(new PathByteSource(Check.notNull(path, "path"), options));
	}

	
	/**
	 * Creates a ByteSource for the InputStream provided by the FilePath.
	 * Forwards the call to {@link #from(Path, OpenOption...)} using the Path contained in the FilePath.
	 * @param path a path, not null
	 * @param options specifying how the path is opened
	 * @return the computed result
	 * @throws E if an error occurs
	 * @see Files#newInputStream(Path, OpenOption...)
	 */
	public default R from(FilePath path, OpenOption... options) throws E
	{
		return from(Check.notNull(path, "path").toNioPath(), options);
	}

	
	/**
	 * Creates a ByteSource which returns a FileInputStream for the File.
	 * @param file a file, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 * @see FileInputStream
	 */
	public default R from(File file) throws E
	{
		Check.notNull(file, "file");
		return from(() -> new FileInputStream(file));
	}

	
	/**
	 * Creates a ByteSource to read from the file with the given name.
	 * @param fileName a file name, not null or empty
	 * @return the computed result
	 * @throws E if an error occurs
	 * @see #fromFile(String)
	 */
	public default R fromFile(String fileName) throws E
	{
		Check.notEmpty(fileName, "fileName");
		return from(new File(fileName));
	}

	
	/**
	 * Creates a ByteSource to return the given bytes
	 * @param bytes the bytes, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R from(byte[] bytes) throws E
	{
		Check.notNull(bytes, "bytes");
		// don't call "from(new ByteArrayInputStream(byte))", but create a ByteSource which
		// can be called repeatedly
		return from(() -> new ByteArrayInputStream(bytes));
	}

	
	/**
	 * Creates a ByteSource to read from a URL. 
	 * @param url the URL, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R from(URL url) throws E
	{
		return from(Check.notNull(url, "url")::openStream);
	}

	
	/**
	 * Creates a ByteSource to read from a URLConnection. 
	 * @param con the connection, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R from(URLConnection con) throws E
	{
		return from(Check.notNull(con, "con")::getInputStream);
	}

	
	/**
	 * Creates a ByteSource to read from the URI.
	 * @param uri a URI, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R from(URI uri) throws E
	{
		return from(Path.of(Check.notNull(uri, "uri")));
	}

	
	/**
	 * Creates a ByteSource to read from the channel.
	 * @param channel a channel, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R from(ReadableByteChannel channel) throws E
	{
		Check.notNull(channel, "channel");
		return from(() -> Channels.newInputStream(channel));
	}

	
	/**
	 * Creates a ByteSource to read the socket.
	 * @param socket a socket, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R from(Socket socket) throws E
	{
		Check.notNull(socket, "socket");
		return from(socket::getInputStream);
	}

	
	/**
	 * Creates a ByteSource to read the Blob.
	 * @param blob a blob, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R from(Blob blob) throws E
	{
		Check.notNull(blob, "blob");
		return fromSupplier(blob::getBinaryStream);
	}

	
	/**
	 * Creates a ByteSource to read Blob.
	 * @param blob a blob, not null
	 * @param pos the offset to the first byte of the partial value to be
     *        retrieved. The first byte in the {@code Blob} is at position 1.
     * @param length the number of bytes to retrieve
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R from(Blob blob, long pos, long length) throws E
	{
		Check.notNull(blob, "blob");
		Check.value(pos, "pos").greaterEq(1);
		return fromSupplier(() -> blob.getBinaryStream(pos, length));
	}

	
	/**
	 * Creates a ByteSource which throws the exception when a InputStream is requested.
	 * @param e either an exception (which is turned into a RuntimeException or IOException),
	 * 		or any other objects which is used as message for a IOException.
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R fromError(Object e) throws E
	{
		return fromSupplier(IOHelper.getThrowsIOorRTException(e));
	}
	
	
	/**
	 * Creates a ByteSource which invokes the given supplier to return an InputStream.
	 * @param supplier a supplier, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default R fromSupplier(XSupplier<? extends InputStream,?> supplier) throws E
	{
		Check.notNull(supplier, "supplier");
		return from(new GenericByteSource<>(supplier));
	}
}
