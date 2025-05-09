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
package jd.commons.io.lib;


import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import jd.commons.check.Check;


/**
 * StringWriter2 is an improved version of {@link StringWriter}
 * based on a StringBuilder.
 */
public class StringWriter2 extends Writer
{
    private final StringBuilder builder_;
    
    
    public static String readAll(Reader reader) throws IOException
    {
    	Check.notNull(reader, "reader");
    	StringWriter2 sw = new StringWriter2();
    	reader.transferTo(sw);
    	return sw.toString();
    }

    
    public static StringBuilder readAll(Reader reader, StringBuilder sb) throws IOException
    {
    	Check.notNull(reader, "reader");
    	StringWriter2 sw = new StringWriter2(sb);
    	reader.transferTo(sw);
    	return sw.getBuilder(); // sb could be null
    }

    
    /**
     * Constructs a new StringWriter2.
     */
    public StringWriter2() 
    {
        this(null);
    }

    /**
     * Constructs a new StringWriter2.
     * @param capacity initial capacity of StringBuilder
     */
    public StringWriter2(int capacity) 
    {
        this(new StringBuilder(capacity));
    }

    
    /**
     * Constructs a new StringWriter2.
     * @param builder the StringBuilder, if null a new instance will be created
     */
    public StringWriter2(StringBuilder builder) 
    {
        builder_ = builder != null ? builder : new StringBuilder();
    }
    
    /**
     * @return the internal builder
     */
    public StringBuilder getBuilder() 
    {
        return builder_;
    }


    
    /**
     * {@inheritDoc}
     */
    @Override
	public void write(int c)
    {
    	builder_.append((char)c);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void write(char cbuf[], int off, int len)
    {
    	builder_.append(cbuf, off, len);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void write(String str)
    {
    	builder_.append(str);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
	public void write(String str, int off, int len)
    {
    	builder_.append(str, off, off + len);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public Writer append(char c) 
    {
        builder_.append(c);
        return this;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Writer append(CharSequence csq) 
    {
        builder_.append(csq);
        return this;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public Writer append(CharSequence csq, int start, int end) 
    {
        builder_.append(csq, start, end);
        return this;
    }


    /**
     * Does nothing.
     */
    @Override
    public void flush() 
    {
        // does nothing
    }
    
    
    /**
     * Does nothing.
     */
    @Override
    public void close() 
    {
        // does nothing
    }
    

    /**
     * @return the StringBuilder turned into a String.
     */
    @Override
    public String toString() 
    {
        return builder_.toString();
    }
}
