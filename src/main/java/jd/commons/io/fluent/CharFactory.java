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


import java.io.BufferedWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import jd.commons.check.Check;
import jd.commons.io.lib.OpenReader;
import jd.commons.io.lib.OpenWriter;


/**
 * CharFactory allows to to create {@link CharSource} objects.
 */
public class CharFactory implements 
	CharFrom<CharSource,RuntimeException>,
	CharTo<CharTarget,RuntimeException>
{
	@Override 
	public CharSource from(CharSource source)
	{
		return Check.notNull(source, "source");
	}
	
	
	/**
	 * Creates a CharSource for the Reader and forwards to {@link CharFrom#from(CharSource)}.
	 * The Reader will not be closed by CharSource operations.
	 * @param reader a Reader, not null
	 * @return the computed result
	 */
	@Override 
	public CharSource from(Reader reader)
	{
		return from(reader, true);
	}
	
	
	/**
	 * Creates a CharSource for the Reader and forwards to {@link CharFrom#from(CharSource)}.
	 * The Reader can be used in subsequent CharSource operations.
	 * The closeable flag controls if the Reader can be closed
	 * in these operations (true) or if should stay open (false). 
	 * @param reader a Reader, not null
	 * @param keepOpen if true the Reader will be wrapped in a {@link OpenReader}
	 * 		to prevent that it is closed. 
	 * @return the computed result
	 */
	public CharSource from(Reader reader, boolean keepOpen)
	{
		Check.notNull(reader, "reader");
		Reader finalReader = keepOpen ? new OpenReader(reader) : reader;
		return from(() -> finalReader);
	}
	
	
	/**
	 * @return a CharContent which can write the lines + line separator to a target.
	 * @param lines the lines, not null
	 */
	public CharWritable fromLines(Iterable<? extends CharSequence> lines)
	{
		Check.notNull(lines, "lines");
		return CharWritable.of(w -> {
			BufferedWriter bw = new BufferedWriter(w);
	        for (CharSequence line: lines) 
	        {
	            bw.append(line);
	            bw.newLine();
	        }
	        bw.flush();
	    });
	}
	
	
	public CharWritable fromLines(CharSequence... lines)
	{
		return fromLines(Arrays.asList(lines));
	}
	
	
	@Override 
	public CharTarget to(CharTarget target)
	{
		return Check.notNull(target, "target");
	}
	
	
	/**
	 * @return a CharTarget for the given Writer.
	 * The Writer will not be closed in any operations of the CharTarget.
	 * @param writer a Writer, not null
	 */
	@Override
	public CharTarget to(Writer writer)
	{
		return to(writer, true);
	}	
	
	
	/**
	 * Creates a CharTarget which returns the given Writer.
	 * @param writer a Writer, not null
	 * @param keepOpen if true the Writer will be wrapped in a {@link OpenWriter}
	 * 		to prevent that it is closed 
	 * @return the CharTarget
	 */
	public CharTarget to(Writer writer, boolean keepOpen)
	{
		Check.notNull(writer, "writer");
		Writer finalWriter = !keepOpen || (writer instanceof OpenWriter) ? writer : new OpenWriter(writer);
		return to(() -> finalWriter);
	}	
}
