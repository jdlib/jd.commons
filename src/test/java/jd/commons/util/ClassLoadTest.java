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
package jd.commons.util;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class ClassLoadTest
{
	@Test
	public void testGet() throws Exception
	{
		Class<?> c = getClass();
		assertSame(c, ClassLoad.forName(c.getName()).get());

		assertThatThrownBy(() -> ClassLoad.forName("x").get())
			.isInstanceOfAny(ClassNotFoundException.class);
	}
	

	@Test
	public void testOrNull() throws Exception
	{
		assertSame(String.class, ClassLoad.forName(String.class.getName()).orNull());
		assertNull(ClassLoad.forName("x").orNull());
	}
	
	
	@Test
	public void testOrThrow() throws Exception
	{
		assertSame(String.class, ClassLoad.forName(String.class.getName()).orThrow(IllegalStateException::new));
		assertThatThrownBy(() -> ClassLoad.forName("x").orThrow(IllegalStateException::new))
			.isInstanceOf(IllegalStateException.class)
			.cause().isInstanceOf(ClassNotFoundException.class);
	}
	
	
	@Test
	public void testLoadBy() throws Exception
	{
		Class<?> c = getClass();
		assertSame(c, ClassLoad.forName(c.getName()).using(c.getClassLoader()).get());
	}
	
	
	@Test
	public void testDerivedFrom() throws Exception
	{
		Class<? extends CharSequence> csc = ClassLoad.forName(String.class.getName())
			.derivedFrom(CharSequence.class).get(); 
		assertSame(String.class, csc);
		
		assertThatThrownBy(() -> ClassLoad.forName(String.class.getName())
			.derivedFrom(getClass()).get())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("java.lang.String is not derived from " + getClass().getName());
	}
}
