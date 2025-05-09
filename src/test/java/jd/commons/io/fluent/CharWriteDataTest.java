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
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.util.Holder;
import jd.commons.util.UncheckedException;


public class CharWriteDataTest
{
	private static final IOException IOE = new IOException("x");
	
	
	@Test
	public void testLines() throws Exception
	{
		StringBuilder sb = new StringBuilder();
		write(Chars.to(sb)).lines("a", "b");
		assertEquals("a" + System.lineSeparator() + "b" + System.lineSeparator(), sb.toString());
	}
	
	
	@Test
	public void testSilent() throws Exception
	{
		Holder<Exception> holder = new Holder<>();
		Exception e = write(Chars.toError(IOE)).silent(holder).string("abc");
		assertSame(IOE, e);
		assertSame(IOE, holder.get());
	}
	
	
	@Test
	public void testThrowing() throws Exception
	{
		assertThatThrownBy(() -> write(Chars.toError(IOE)).throwing(SQLException::new).string("abc"))
			.isInstanceOf(SQLException.class)
			.cause().isSameAs(IOE);
	}
	
	
	@Test
	public void testUnchecked() throws Exception
	{
		assertThatThrownBy(() -> write(Chars.toError(IOE)).unchecked().string("abc"))
			.isInstanceOf(UncheckedException.class)
			.cause().isSameAs(IOE);
	}


	private CharWriteData<Void,IOException> write(CharTarget target)
	{
		return new CharWriteData<>(target, ErrorFunction.throwUncheckedOrIOE());
	}
}
