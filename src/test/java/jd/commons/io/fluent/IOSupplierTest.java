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


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.sql.SQLException;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import jd.commons.util.function.XSupplier;


public class IOSupplierTest
{
	@Test
	public void testThrowsException()
	{
		IllegalArgumentException iae = new IllegalArgumentException();
		IOException ioe = new IOException();
		SQLException sqle = new SQLException(); 
		
		assertThrown(iae).isSameAs(iae);
		assertThrown(ioe).isSameAs(ioe);
		assertThrown(sqle).isInstanceOf(IOException.class).hasCause(sqle);
	}
	
	
	@Test
	public void testToString()
	{
		XSupplier<String,?> ts = () -> "x"; 
		assertEquals(ts.toString(), new IOSupplier<>(ts).toString());  
	}
	
	
	private AbstractThrowableAssert<?,? extends Throwable> assertThrown(Exception e)
	{
		IOSupplier<?> supplier = new IOSupplier<>(() -> { throw e; });
		ThrowingCallable callable = () -> supplier.get(); 
		return assertThatThrownBy(callable);
	}
}
