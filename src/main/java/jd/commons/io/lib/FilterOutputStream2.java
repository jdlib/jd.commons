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
import java.io.OutputStream;
import jd.commons.check.Check;


/**
 * FilterOutputStream2 is OutputStream which forwards all method calls 
 * to another OutputStream. Subclasses of this class
 * may override some of the methods to provide additional functionality.
 * This class is similar to java.io.FilterOutputStream with these differences:
 * <ul>
 * <li>In order to modify write calls you need to override both {@link #write(int)} 
 *     and {@link #write(byte[], int, int)} (whereas java.io.FilterOutputStream 
 *     allows to intercept all write calls by only overriding write(int)).
 * <li>FilterOutputStream2 does not track if the stream was already closed. All calls to {@link #close()}
 * 		are forwarded to the wrapped OutputStream.  
 * </ul>
 */
public class FilterOutputStream2 extends OutputStream
{
    /**
     * The underlying output stream to be filtered.
     */
    protected OutputStream out_;


    /**
     * Creates a FilterOutputStream2.
     * @param out the wrapped OutputStream.
     */
    public FilterOutputStream2(OutputStream out) 
    {
        out_ = Check.notNull(out, "out");
    }


    @Override
    public void write(int b) throws IOException 
    {
        out_.write(b);
    }
    

    @Override
    public void write(byte[] b) throws IOException 
    {
        write(b, 0, b.length);
    }
    

    @Override
    public void write(byte[] b, int off, int len) throws IOException 
    {
    	out_.write(b, off, len);
    }
    

    @Override
    public void flush() throws IOException 
    {
        out_.flush();
    }
    

    @Override
    public void close() throws IOException 
    {
    	out_.close();
    }
}
