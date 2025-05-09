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


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.sql.Blob;
import jd.commons.check.Check;
import jd.commons.io.FilePath;
import jd.commons.util.function.XSupplier;


/**
 * ByteTo allows to specify a target for byte output.
 * <ul>
 * <li>Directly specify a {@link #to(OutputStream) OutputStream}
 * <li>Directly specify a {@link #to(ByteTarget) ByteTarget} 
 * <li>Specify IO related objects which
 *     can be turned into a OutputStream or ByteTarget. The default implementations
 *     of these methods forward them to {@link #to(OutputStream)} or {@link #to(ByteTarget)}
 * </ul>
 * The return value computed from the Writer or ByteTarget depends on the implementation.
 * @param<T> the result type
 * @param<E> the exception thrown by the ByteTo methods 
 */
public interface ByteTo<T,E extends Exception>
{
	/**
	 * Accepts a ByteTarget.
	 * @param target the target, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public T to(ByteTarget target) throws E;


	/**
	 * Accepts a OutputStream.
	 * The OutputStream will not be closed by subsequent IO operations.
	 * @param out the OutputStream, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public T to(OutputStream out) throws E;
	

	/**
	 * Creates a file from the file name and forwards to {@link #to(File)}.
	 * @param fileName the file name, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T toFile(String fileName) throws E
	{
		Check.notEmpty(fileName, "fileName");
		return to(new File(fileName));
	}

	
	/**
	 * Calls {@link #to(File, boolean) to(file, false)}.
	 * @param file a file, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(File file) throws E
	{
		return to(file, false);
	}

	
	/**
	 * Creates a ByteTarget that returns an FileOutputStream for the file.
	 * @param file a file, not null
	 * @param append true if bytes should be appended, false if not
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(File file, boolean append) throws E
	{
		Check.notNull(file, "file");
		return to(() -> new FileOutputStream(file, append));
	}

	
	/**
	 * Creates a ByteTarget for the path.
	 * @param path a path, not null
	 * @param options optional open options
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(Path path, OpenOption... options) throws E
	{
		Check.notNull(path, "path");
		return to(() -> Files.newOutputStream(path, options));
	}

	
	/**
	 * Creates a ByteTarget for the path.
	 * @param path a path, not null
	 * @param options optional open options
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(FilePath path, OpenOption... options) throws E
	{
		Check.notNull(path, "path");
		return to(path.toNioPath(), options);
	}

	
	/**
	 * Creates a ByteTarget for the channel.
	 * @param channel a channel, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(WritableByteChannel channel) throws E
	{
		Check.notNull(channel, "channel");
		return to(() -> Channels.newOutputStream(channel));
	}

	
	/**
	 * Creates a ByteTarget for the socket.
	 * @param socket a socket, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(Socket socket) throws E
	{
		Check.notNull(socket, "socket");
		return to(socket::getOutputStream);
	}

	
	/**
	 * Creates a ByteTarget for the Blob.
	 * @param blob a blob, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(Blob blob) throws E
	{
		return to(blob, 1);
	}

	
	/**
	 * Creates a ByteTarget for the Blob.
	 * @param blob a blob, not null
	 * @param pos the offset to the first byte of the partial value to be
     *        retrieved. The first byte in the {@code Blob} is at position 1.
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(Blob blob, long pos) throws E
	{
		Check.notNull(blob, "blob");
		Check.value(pos, "pos").greaterEq(1);
		return toSupplier(() -> blob.setBinaryStream(pos));
	}

	
	/**
	 * Creates a ByteTarget which throws the exception when a OutputStream is requested.
	 * @param e either an exception (which is turned into a RuntimeException or IOException),
	 * 		or any other objects which is used as message for a IOException.
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T toError(Object e) throws E
	{
		return toSupplier(IOHelper.getThrowsIOorRTException(e));
	}


	/**
	 * Calls {@link #to(OutputStream)} passing {@link OutputStream#nullOutputStream()}.
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T toNull() throws E
	{
		return to(OutputStream.nullOutputStream());
	}

	
	/**
	 * Creates a ByteTarget which returns the OutputStream provided by the supplier.
	 * @param supplier a supplier, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T toSupplier(XSupplier<? extends OutputStream,?> supplier) throws E
	{
		Check.notNull(supplier, "supplier");
		return to(new GenericByteTarget<>(supplier));
	}
}
