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


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import jd.commons.io.fluent.handler.ErrorFunction;


class PathCharSource implements CharSource
{
	private final Path path_;
	private final OpenOption[] options_;
	private final Charset charset_;
	
	
	public PathCharSource(Path path, OpenOption[] options, Charset charset)
	{
		path_	 = path;
		options_ = options;
		charset_ = charset;
	}


	@Override
	public Reader getReader() throws IOException
	{
		return new InputStreamReader(Files.newInputStream(path_, options_), charset_);
	}


	@Override
	public CharReadData<IOException> read()
	{
		return new PathCharReadData<>(ErrorFunction.throwUncheckedOrIOE());
	}


	class PathCharReadData<E extends Exception> extends CharReadData<E>
	{
		public PathCharReadData(ErrorFunction<Void,Void,E> error)
		{
			super(PathCharSource.this, error);
		}
		
		
		@Override
		public String all() throws E
		{
			if (options_.length > 0)
				return super.all();
			
			try
			{
				// call the optimized implementation
				return Files.readString(path_, charset_);
			}
			catch (Exception e)
			{
				error_.handleException(e);
				return null; // will never be executed
			}
		}	
	}
}
