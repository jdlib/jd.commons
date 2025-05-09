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
package jd.commons.io;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import jd.commons.check.Check;


/**
 * A builder class to specify the target of a file operation. 
 */
public interface FileTarget
{
	/**
	 * Specifies the target as sibling of the current source.
	 * @param name the name of the sibling
	 * @return the target file path
	 * @throws IOException if an I/O error occurs
	 */
	public FilePath toSibling(String name) throws IOException;

	
	/**
	 * Specifies the target as a Path.
	 * @param target the target Path
	 * @throws IOException if an I/O error occurs
	 * @return the target converted to a FilePath
	 */
	public default FilePath to(Path target) throws IOException
	{
		return to(FilePath.of(target));
	}


	/**
	 * Specifies the target as a File.
	 * @param file the target File
	 * @throws IOException if an I/O error occurs
	 * @return the target converted to a FilePath
	 */
	public default FilePath to(File file) throws IOException
	{
		Check.notNull(file, "file");
		return to(file.toPath());
	}
	
	
	/**
	 * Specifies the target.
	 * @param path the target FilePath
	 * @return the target FilePath
	 * @throws IOException if an I/O error occurs
	 */
	public FilePath to(FilePath path) throws IOException;
}
