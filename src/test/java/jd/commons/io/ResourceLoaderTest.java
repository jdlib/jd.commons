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
package jd.commons.io;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import org.junit.jupiter.api.Test;


public class ResourceLoaderTest
{
	@Test
	public void testNop()
	{
		ResourceLoader nop = ResourceLoader.nop();
		assertEquals("Loader['nop']", nop.toString());
		assertEquals("nop".hashCode(), nop.hashCode());
		assertEquals(nop, nop);
		assertEquals(nop, ResourceLoader.of("nop", s -> null, s -> null));
		assertNotEquals(nop, "nop");
		
		Resource res = Resource.of().path("dummy.txt").loadBy(nop);
		assertThatThrownBy(() -> res.getInputStream()).hasMessage("resource 'dummy.txt' not found");
	}


	@Test
	public void testOfClass() throws Exception
	{
		Class<?> c 				= getClass();
		ResourceLoader loader 	= ResourceLoader.of(c); 
		assertEquals(c.hashCode(), loader.hashCode());
		assertEquals(loader, loader);
		assertNotEquals(loader, c);
		assertEquals("Loader[jd.commons.io.ResourceLoaderTest]", loader.toString());
	}


	@Test
	public void testOfClassLoader() throws Exception
	{
		ClassLoader cl 			= getClass().getClassLoader();
		ResourceLoader loader  	= ResourceLoader.of(cl);
		assertEquals(cl.hashCode(), loader.hashCode());
		assertEquals(loader, loader);
		assertNotEquals(loader, cl);
		assertEquals("Loader[" + cl + ']', loader.toString());

		assertEquals("Loader[<system>]", ResourceLoader.system().toString());
		assertEquals("Loader[<platform>]", ResourceLoader.platform().toString());
	}


	@Test
	public void testOfContextClassLoader() throws Exception
	{
		ResourceLoader rl = ResourceLoader.context();
		assertEquals("Loader[<context>]", rl.toString());
		
		assertNotNull(rl.getURL("java/lang/String.class"));
		try (InputStream in = rl.getInputStream("java/lang/String.class"))
		{
		}
		
		try
		{
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			assertNotNull(rl.getURL("java/lang/String.class"));
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(null);
		}
		

	}
}
