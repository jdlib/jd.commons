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
import java.io.BufferedReader;
import java.io.Reader;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import jd.commons.io.fluent.handler.IOHandler;


public class CharReadFromTest
{
	private static final IOHandler<CharSource,Reader,String,Exception> HANDLER = new IOHandler<>() {
		@Override
		public String runSupplier(CharSource arg) throws Exception
		{
			try (Reader reader = arg.getReader())
			{
				return runDirect(reader);
			}
		}

		@Override
		public String runDirect(Reader arg) throws Exception
		{
			return "x";
		}
	};
	private static final CharReadFrom<String,Exception> FROM = new CharReadFrom<>(HANDLER); 

	
	@Test
	public void testCoverage() throws Exception
	{
		FROM.from(Chars.fromString("a"));
		FROM.from(Reader.nullReader());
		FROM.silent();
		FROM.throwing(SQLException::new);
		FROM.unchecked();
		FROM.wrap(BufferedReader::new);
	}
}
