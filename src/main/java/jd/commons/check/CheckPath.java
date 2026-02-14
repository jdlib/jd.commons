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


import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;


/**
 * Checks {@link Path} values.
 */
public class CheckPath
{
	private final Path path_;
	private final String what_;
	private final LinkOption[] options_;


	/**
	 * Creates a new CheckPath.
	 * @param path a path, not nulk
	 * @param what describes the path
	 * @param options options to be used when retrieving attributes of the path
	 */
	protected CheckPath(Path path, String what, LinkOption... options)
	{
		path_ 		= Check.notNull(path, "path");
		what_ 		= what;
		options_	= options;
	}


	/**
	 * Checks if the path exists.
	 * @param expected the expected value
	 * @exception IllegalArgumentException if the expectation is not met
	 * @return this
	 */
	public CheckPath exists(boolean expected)
	{
		if (Files.exists(path_, options_) != expected)
			throw error(expected ? "does not exist" : "exists");
		return this;
	}


	/**
	 * Checks that the path exists. Calls #exists(true).
	 * @exception IllegalArgumentException if the path does not exist
	 * @return this
	 */
	public CheckPath exists()
	{
		return exists(true);
	}


	/**
	 * Checks if this path is a directory.
	 * @return this
	 */
	public CheckPath isDir()
	{
		if (!Files.isDirectory(path_, options_))
			throw error("is not a directory");
		return this;
	}


	/**
	 * Checks if this path is a file.
	 * @return this
	 */
	public CheckPath isFile()
	{
		if (!Files.isRegularFile(path_, options_))
			throw error("is not a file");
		return this;
	}


	/**
	 * Checks if this path is a symbolic link.
	 * @return this
	 */
	public CheckPath isSymbolicLink()
	{
		if (!Files.isSymbolicLink(path_))
			throw error("is not a symbolic link");
		return this;
	}


	/**
	 * @return a CheckSize object to check the path size.
	 */
	public CheckSize size()
	{
		return Check.size(path_, what_);
	}


	private IllegalArgumentException error(String op)
	{
		StringBuilder msg = new StringBuilder();
		if (what_ != null)
			msg.append(what_).append(' ');
		msg.append(path_.toAbsolutePath().toString()).append(' ').append(op);
		return new IllegalArgumentException(msg.toString());
	}
}
