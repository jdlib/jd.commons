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
package jd.commons.check;


import java.io.File;


/**
 * CheckFile allows to check a File.
 */
public class CheckFile
{
	private final File file_;
	private final String what_;
	
	
	/**
	 * Creates a new CheckFile.
	 * @param file a file
	 * @param what describes the file
	 */
	protected CheckFile(File file, String what)
	{
		file_ = Check.notNull(file, "file");
		what_ = what;
	}
	
	
	/**
	 * Checks if this file exists.
	 * @param expected the expected value
	 * @exception IllegalArgumentException if the file does not exist
	 * @return this
	 */
	public CheckFile exists(boolean expected)
	{
		if (file_.exists() != expected)
			throw error(expected ? "does not exist" : "exists");
		return this;
	}
	

	/**
	 * Checks that this file is a directory.
	 * @exception IllegalArgumentException if the file is not a directory
	 * @return this
	 */
	public CheckFile isDir()
	{
		if (!file_.isDirectory())
			throw error("is not a directory");
		return this;
	}

	
	/**
	 * Checks that this file is a regular file.
	 * @exception IllegalArgumentException if the file is not a file
	 * @return this
	 */
	public CheckFile isFile()
	{
		if (!file_.isFile())
			throw error("is not a file");
		return this;
	}

	
	/**
	 * @return a CheckSize object to check the file length.
	 */
	public CheckSize length()
	{
		return Check.length(file_, what_);
	}
	
	
	private IllegalArgumentException error(String op)
	{
		StringBuilder msg = new StringBuilder();
		if (what_ != null)
			msg.append(what_).append(' ');
		msg.append(file_.getAbsolutePath()).append(' ').append(op);
		return new IllegalArgumentException(msg.toString());
	}
}
