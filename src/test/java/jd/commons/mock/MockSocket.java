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
package jd.commons.mock;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class MockSocket extends Socket
{
    public final ByteArrayInputStream in;
    public final ByteArrayOutputStream out;

    
    public MockSocket(byte... bytes) 
    {
        in  = new ByteArrayInputStream(bytes);
        out = new ByteArrayOutputStream();
    }

    @Override
    public InputStream getInputStream() 
    {
        return in;
    }
    

    @Override
    public OutputStream getOutputStream() 
    {
        return out;
    }
}
