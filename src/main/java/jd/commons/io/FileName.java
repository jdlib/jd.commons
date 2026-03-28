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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jd.commons.check.Check;


/**
 * Represents a file name (the last path element) and provides helpers to
 * query its base name and extensions.
 * <p>Examples:
 * <ul>
 *   <li>"archive.tar.gz" - base name: "archive", extensions: ["tar", "gz"],
 *       getExtension(): "gz"</li>
 *   <li>"README" - base name: "README", extensions: [], getExtension(): ""</li>
 * </ul>
 * <p>The class is immutable.</p>
 */
public class FileName
{
	private final String name_;
	private final int firstDotPos_;


	/**
	 * Create a {@code FileName} from a {@link File} instance.
	 * @param file the file to extract the name from; must not be {@code null}
	 * @return a {@code FileName} representing {@code file.getName()}
	 */
	public static FileName of(File file)
	{
		Check.notNull(file, "file");
		return new FileName(file.getName());
	}


	/**
	 * Create a {@code FileName} from a {@link Path} instance.
	 * @param path the path to extract the file name from; must not be {@code null}
	 * @return a {@code FileName} representing the last name element of the path.
	 *         If the path has no file name element an empty name is used.
	 */
	public static FileName of(Path path)
	{
		Check.notNull(path, "path");
		Path fnPath = path.getFileName();
		return new FileName(fnPath != null ? fnPath.toString() : "");
	}


	/**
	 * Create a {@code FileName} from a base name and one or more extensions.
	 * <p>Calling {@code FileName.of("archive", "tar", "gz")} produces the
	 * equivalent of {@code new FileName("archive.tar.gz")}.</p>
	 * @param baseName the main (base) name; must not be {@code null}
	 * @param extensions one or more extensions; must not be {@code null}
	 * @return a {@code FileName} constructed from the base name and extensions
	 */
	public static FileName of(String baseName, String... extensions)
	{
		Check.notNull(baseName, "baseName");
		Check.notNull(extensions, "extensions");
		StringBuilder sb = new StringBuilder(baseName);
		for (String ext : extensions)
			sb.append('.').append(ext);
		return new FileName(sb.toString());
	}


	/**
	 * Construct a {@code FileName} from the raw name string.
	 * @param name the file name (may be empty but not {@code null})
	 */
	public FileName(String name)
	{
		name_ 			= Check.notNull(name, "name");
		firstDotPos_ 	= name_.indexOf('.');
	}


	/**
	 * @return the raw file name as provided to the constructor.
	 */
	public String getName()
	{
		return name_;
	}


	/**
	 * @return the base name, the part of the name before the first dot.
	 * Examples: for "a.b.c" the base name is "a"; for "README" the base name
	 * is the full name.
	 */
	public String getBaseName()
	{
		return firstDotPos_ == -1 ? name_ : name_.substring(0, firstDotPos_);
	}


	/**
	 * Return a list containing all extensions in order (excluding the dots).
	 * For the name "archive.tar.gz" this returns ["tar", "gz"]. If the name
	 * contains no dot an empty list is returned.
	 * @return the list
	 */
	public List<String> getExtensions()
	{
		int p = firstDotPos_;
		if (p == -1)
			return List.of();
		List<String> result = new ArrayList<>();
		int q;
		while ((q = name_.indexOf('.', p + 1)) != -1)
		{
			result.add(name_.substring(p + 1, q));
			p = q;
		}
		if (p < name_.length())
			result.add(name_.substring(p + 1));
		return result;
	}


	/**
	 * @return if the FileName has the given extension.
	 * @see #getExtension()
	 */
	public boolean hasExtension(String extension)
	{
		return Objects.equals(extension, getExtension());
	}


	/**
	 * @return the last extension (the substring after the last dot) or an empty
	 * string if the name contains no dot.
	 */
	public String getExtension()
	{
		return getExtensionOr("");
	}


	/**
	 * @return the last extension (the substring after the last dot) or the
	 * supplied default value if the name contains no dot.
	 * @param defaultValue the value to return when there is no extension
	 */
	public String getExtensionOr(String defaultValue)
	{
		int p = name_.lastIndexOf('.');
		return p >= 0 ? name_.substring(p + 1) : defaultValue;
	}


	@Override
	public int hashCode()
	{
		return name_.hashCode();
	}


	@Override
	public boolean equals(Object other)
	{
		return other instanceof FileName ? name_.equals(((FileName) other).name_) : false;
	}


	@Override
	public String toString()
	{
		return name_;
	}
}