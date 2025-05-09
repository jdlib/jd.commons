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


import static jd.commons.io.fluent.IO.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import jd.commons.io.fluent.handler.IOHandler;


public class ByteReadFromTest
{
	private static final IOHandler<ByteSource,InputStream,String,Exception> HANDLER = new IOHandler<>() {
		@Override
		public String runSupplier(ByteSource arg) throws Exception
		{
			return runDirect(null);
		}

		@Override
		public String runDirect(InputStream arg) throws Exception
		{
			return "x";
		}
	};
	
	
	@Test
	public void test() throws Exception
	{
		new ByteReadFrom<>(HANDLER).from(Bytes.from(new byte[0]));
		new ByteReadFrom<>(HANDLER).from(InputStream.nullInputStream());
		new ByteReadFrom<>(HANDLER).silent();
		new ByteReadFrom<>(HANDLER).throwing(SQLException::new);
		new ByteReadFrom<>(HANDLER).unchecked();
		new ByteReadFrom<>(HANDLER).wrap(BufferedInputStream::new).from(InputStream.nullInputStream());
	}
}
