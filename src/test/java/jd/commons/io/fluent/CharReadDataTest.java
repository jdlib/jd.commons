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
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import jd.commons.io.fluent.handler.ErrorFunction;


public class CharReadDataTest
{
	@Test
	public void testAllStringBuilder() throws Exception
	{
		StringBuilder sb = new StringBuilder();
		Chars.fromString("abc").read().all(sb);
		assertEquals("abc", sb.toString());
	}
	
	
	@Test
	public void testThrowing() throws Exception
	{
		Chars.fromString("abc").read().throwing(SQLException::new).all();
		
		// coverage for apply catch clause
		assertNull(new CharReadData<>(Chars.fromError("err"), ErrorFunction.swallow()).all());
	}
}
