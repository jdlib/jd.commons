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


import static deepdive.ExpectStatic.*;
import static deepdive.ExpectThat.*;
import org.junit.jupiter.api.Test;


public class ClassLoadTest
{
	@Test
	public void testGet() throws Exception
	{
		Class<?> c = getClass();
		expectSame(c, ClassLoad.forName(c.getName()).get());

		expectError(() -> ClassLoad.forName("x").get())
			.isA(ClassNotFoundException.class);
	}


	@Test
	public void testOrNull() throws Exception
	{
		expectSame(String.class, ClassLoad.forName(String.class.getName()).orNull());
		expectNull(ClassLoad.forName("x").orNull());
	}


	@Test
	public void testOrThrow() throws Exception
	{
		expectSame(String.class, ClassLoad.forName(String.class.getName()).orThrow(IllegalStateException::new));
		expectError(() -> ClassLoad.forName("x").orThrow(IllegalStateException::new))
			.isA(IllegalStateException.class)
			.cause().isA(ClassNotFoundException.class);
	}


	@Test
	public void testLoadBy() throws Exception
	{
		Class<?> c = getClass();
		expectSame(c, ClassLoad.forName(c.getName()).using(c.getClassLoader()).get());
	}


	@Test
	public void testDerivedFrom() throws Exception
	{
		Class<? extends CharSequence> csc = ClassLoad.forName(String.class.getName())
			.derivedFrom(CharSequence.class).get();
		expectSame(String.class, csc);

		expectError(() -> ClassLoad.forName(String.class.getName())
			.derivedFrom(getClass()).get())
			.isA(IllegalArgumentException.class)
			.message("java.lang.String is not derived from " + getClass().getName());
	}
}
