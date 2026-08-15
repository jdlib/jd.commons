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


import static deepdive.ExpectStatic.*;
import static deepdive.ExpectThat.*;
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
		expectError(() -> ef.handleException(iae)).same(iae);
		expectError(() -> ef.handleException(ioe)).same(ioe);
		expectError(() -> ef.handleException(sqe)).isA(IOException.class).cause().same(sqe);
		expectEqual("Throwing", ef.toString());
	}


	@Test
	public void testSwallow() throws Exception
	{
		ErrorFunction<String,String,RuntimeException> sw = ErrorFunction.swallow();
		expectNull(sw.handleResult("a"));
		expectNull(sw.handleException(new IOException()));
		expectEqual("Swallow", sw.toString());
	}


	@Test
	public void testThrowing() throws Exception
	{
		// even if the throwing factory returns null, an exception is thrown
		NullPointerException npe = new NullPointerException();
		ErrorFunction<?,?,?> ef = ErrorFunction.throwing(e -> null);
		expectError(() -> ef.handleException(npe))
			.isA(IllegalStateException.class)
			.cause().same(npe);
	}
}
