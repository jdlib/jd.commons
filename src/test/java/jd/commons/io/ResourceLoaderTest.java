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


import static deepdive.ExpectStatic.*;
import static deepdive.ExpectThat.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import org.junit.jupiter.api.Test;


public class ResourceLoaderTest
{
	@Test
	public void testNop()
	{
		ResourceLoader nop = ResourceLoader.nop();
		expectEqual("Loader['nop']", nop.toString());
		expectEqual("nop".hashCode(), nop.hashCode());
		expectEqual(nop, nop);
		expectEqual(nop, ResourceLoader.of("nop", s -> null, s -> null));
		assertNotEquals(nop, "nop");

		Resource res = Resource.of().path("dummy.txt").loadBy(nop);
		expectError(() -> res.getInputStream()).message("resource 'dummy.txt' not found");
	}


	@Test
	public void testOfClass() throws Exception
	{
		Class<?> c 				= getClass();
		ResourceLoader loader 	= ResourceLoader.of(c);
		expectEqual(c.hashCode(), loader.hashCode());
		expectEqual(loader, loader);
		assertNotEquals(loader, c);
		assertNotEquals(loader, getClass());
		assertNotEquals(loader, ResourceLoader.of(ResourceLoader.class));
		expectEqual("Loader[jd.commons.io.ResourceLoaderTest]", loader.toString());
	}


	@Test
	public void testOfClassLoader() throws Exception
	{
		ClassLoader cl 			= getClass().getClassLoader();
		ResourceLoader loader  	= ResourceLoader.of(cl);
		expectEqual(cl.hashCode(), loader.hashCode());
		expectEqual(loader, loader);
		assertNotEquals(loader, cl);
		expectEqual("Loader[" + cl + ']', loader.toString());

		expectEqual("Loader[<system>]", ResourceLoader.system().toString());
		expectEqual("Loader[<platform>]", ResourceLoader.platform().toString());
	}


	@Test
	public void testOfContextClassLoader() throws Exception
	{
		ResourceLoader rl = ResourceLoader.context();
		expectEqual("Loader[<context>]", rl.toString());

		expectNotNull(rl.getURL("java/lang/String.class"));
		try (InputStream in = rl.getInputStream("java/lang/String.class"))
		{
		}

		try
		{
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			expectNotNull(rl.getURL("java/lang/String.class"));
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(null);
		}


	}
}
