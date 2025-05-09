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
package jd.commons.io.fluent.handler;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;


public class ErrorFunctionTest
{
	@Test
	public void throwThrowUncheckedOrIOE()
	{
		IllegalArgumentException iae = new IllegalArgumentException();
		IOException ioe = new IOException();
		SQLException sqe = new SQLException();
		
		ErrorFunction<Void,Void,IOException> ef = ErrorFunction.throwUncheckedOrIOE();
		assertThatThrownBy(() -> ef.handleException(iae)).isSameAs(iae);
		assertThatThrownBy(() -> ef.handleException(ioe)).isSameAs(ioe);
		assertThatThrownBy(() -> ef.handleException(sqe)).isInstanceOf(IOException.class).cause().isSameAs(sqe);
		assertEquals("Throwing", ef.toString());
	}
	
	
	@Test
	public void testSwallow() throws Exception
	{
		ErrorFunction<String,String,RuntimeException> sw = ErrorFunction.swallow();
		assertNull(sw.handleResult("a"));
		assertNull(sw.handleException(new IOException()));
		assertEquals("Swallow", sw.toString());
	}
	
	
	@Test
	public void testThrowing() throws Exception
	{
		// even if the throwing factory returns null, an exception is thrown
		NullPointerException npe = new NullPointerException();
		ErrorFunction<?,?,?> ef = ErrorFunction.throwing(e -> null);
		assertThatThrownBy(() -> ef.handleException(npe))
			.isInstanceOf(IllegalStateException.class)
			.hasCause(npe);
	}
}
