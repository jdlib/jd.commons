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
import java.io.Writer;
import jd.commons.check.Check;


/**
 * A Writer implementation that writes to an {@link Appendable}.
 */
public class AppendableWriter extends Writer 
{
    private final Appendable appendable_;
    

    /**
     * Creates a new AppendableWriter.
     * @param appendable the appendable, not null
     */
    public AppendableWriter(Appendable appendable) 
    {
        appendable_ = Check.notNull(appendable, "appendable");
    }

    
    /**
     * @return the appendable.
     */
    public Appendable getAppendable() 
    {
        return appendable_;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Writer append(char c) throws IOException 
    {
        appendable_.append(c);
        return this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Writer append(CharSequence csq) throws IOException 
    {
        appendable_.append(csq);
        return this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException 
    {
        appendable_.append(csq, start, end);
        return this;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException 
    {
    	Check.notNull(cbuf, "cbuf");
    	int max = off + len;
        for (; off < max; off++)
            appendable_.append(cbuf[off]);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int c) throws IOException 
    {
        appendable_.append((char)c);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(String str, int off, int len) throws IOException 
    {
        appendable_.append(Check.notNull(str, "str"), off, off + len);
    }


    /**
     * Does nothing.
     */
    @Override
    public void flush() throws IOException 
    {
    }

    
    /**
     * Does nothing.
     */
    @Override
    public void close() throws IOException 
    {
    }
    

    /**
     * @return the Appendable turned into a String.
     */
    @Override
    public String toString() 
    {
        return appendable_.toString();
    }
}
