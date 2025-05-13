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
package jd.commons.demo;


import static java.nio.charset.StandardCharsets.*;
import static jd.commons.io.fluent.IO.*;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import jd.commons.io.Resource;


/**
 * How many lines of code do you need to implement the following tasks, using only the JDK or a library of your choice?
 * Rules of the game: 
 * <ul>
 * <li>Less lines is better
 * <li>standard formatting rules are applied to count lines, and 
 * <li>you need to close any opened resources
 * </ul>
 */
public class OneLineIOChallenge
{
	/**
	 * Given a java.io.File, read the content of the file into a byte array.
	 */
	public byte[] challenge1(java.io.File file) throws IOException
	{
		return Bytes.from(file).read().all();
	}
	
	
	/**
	 * Given a java.nio.file.Path, read the first n bytes of it into a byte array.
	 */
	public byte[] challenge2(java.nio.file.Path path, int n) throws IOException
	{
		return Bytes.from(path).read().first(n);
	}


	/**
	 * Given a java.io.InputStream, decode the stream content as UTF-8 and append to a StringBuilder.
	 */
	public void challenge3(java.io.InputStream in, StringBuilder sb) throws IOException
	{
		Bytes.from(in).asUtf8().write().to(sb);
	}


	/**
	 * Given a java.net.URL, read the content, decode as UTF-8, and return as a list of lines.
	 */
	public List<String> challenge4(java.net.URL url) throws IOException
	{
		return Bytes.from(url).asUtf8().read().lines().toList();
	}


	/**
	 * Given a java.net.Socket, decode as ISO-8891-1 and return as a list of trimmed lines.
	 */
	public List<String> challenge5(java.net.Socket socket) throws IOException
	{
		return Bytes.from(socket).as(ISO_8859_1).read().lines().trim().toList();
	}


	/**
	 * Given a java.io.File source and java.io.File target, read the content, decode as UTF-8, encode as ISO-8891-1 and write to the target.
	 */
	public void challenge6(java.io.File inFile, java.io.File outFile) throws IOException
	{
		Bytes.from(inFile).asUtf8().write().as(ISO_8859_1).to(outFile);
	}


	/**
	 * Given a java.io.Reader and a java.io.File, write the Reader content as UTF-8 bytes to the file.
	 */
	public void challenge7(java.io.Reader reader, java.io.File file) throws IOException
	{
		Chars.from(reader).write().asUtf8().to(file);
	}


	/**
	 * Given a java.io.File read all bytes and only throw RuntimeExceptions.
	 */
	public byte[] challenge8(java.io.File file) 
	{
		return Bytes.from(file).read().unchecked().all();
	}


	/**
	 * Given a java.io.Reader and a java.io.File, write the Reader content as UTF-8 bytes to the file, catch and log any exception thrown to a SLF4J logger.
	 */
	public void challenge9(java.io.Reader reader, java.io.File file, Logger log)
	{
		Chars.from(reader).write().asUtf8().silent(e -> log.error("error", e)).to(file);
	}


	/**
	 * Given a classpath resource name, decode the content as UTF-8 and read into a string
	 */
	public String challenge10(String resourceName) throws IOException
	{
		return Resource.of(resourceName).asUtf8().read().all();
	}
}
