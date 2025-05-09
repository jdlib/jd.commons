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


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class HolderTest
{
	@Test
	public void test()
	{
		Holder<String> holder;
		
		holder = new Holder<>("a");
		assertTrue(holder.has("a"));
		assertFalse(holder.has(null));
		assertEquals("Holder:a", holder.toString());
		assertEquals("a", holder.get());
		assertEquals("a", holder.getOr("b"));
		assertEquals("b", holder.apply("b"));
		
		holder.clear();
		assertNull(holder.get());
		assertNull(holder.getOr(null));
	}
}
