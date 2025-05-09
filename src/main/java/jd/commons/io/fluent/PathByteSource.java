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
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import jd.commons.io.fluent.handler.ErrorFunction;


class PathByteSource implements ByteSource
{
	private final Path path_;
	private final OpenOption[] options_;
	
	
	public PathByteSource(Path path, OpenOption[] options)
	{
		path_	 = path;
		options_ = options;
	}


	@Override
	public InputStream getInputStream() throws IOException
	{
		return Files.newInputStream(path_, options_);
	}
	
	
	@Override 
	public ByteReadData<IOException> read()
	{
		return new PathByteReadData<>(ErrorFunction.throwUncheckedOrIOE());
	}
	
	
	// a ByteReadData implementation which optimizes #all
	class PathByteReadData<E extends Exception> extends ByteReadData<E>
	{
		public PathByteReadData(ErrorFunction<Void,Void,E> rethrow)
		{
			super(PathByteSource.this, rethrow);
		}

		
		@Override
		public byte[] all() throws E
		{
			if (options_.length > 0)
				return super.all();
			
			try
			{
				// call the optimized implementation
				return Files.readAllBytes(path_);
			}
			catch (Exception e)
			{
				error_.handleException(e);
				return null;
			}
		}	
	}
	

	@Override 
	public PathCharSource as(Charset charset)
	{
		// returning a specialized CharSource/CharRead which has a optimized implementations
		return new PathCharSource(path_, options_, charset);
	}
}
