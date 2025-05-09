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


public class UnboxTest
{
	private static final Number ONE = Integer.valueOf(1);
	
	
	@Test
	public void testBoolean()
	{
		assertTrue(Unbox.isTrue(Boolean.TRUE));
		assertFalse(Unbox.isTrue(Boolean.FALSE));
		assertFalse(Unbox.isTrue(null));

		assertTrue(Unbox.isFalse(Boolean.FALSE));
		assertFalse(Unbox.isFalse(Boolean.TRUE));
		assertFalse(Unbox.isFalse(null));
		
		assertFalse(Unbox.toBoolean(null, false));
		assertTrue(Unbox.toBoolean(null, true));
		assertFalse(Unbox.toBoolean(Boolean.FALSE, true));
		assertTrue(Unbox.toBoolean(Boolean.TRUE, false));
	}


	@Test
	public void testByte()
	{
		assertEquals((byte)0, Unbox.toByte(null));
		assertEquals((byte)1, Unbox.toByte(ONE));
	}


	@Test
	public void testChar()
	{
		assertEquals((char)0, Unbox.toChar(null));
		assertEquals('a', Unbox.toChar(Character.valueOf('a')));
	}


	@Test
	public void testDouble()
	{
		assertEquals(0.0, Unbox.toDouble(null));
		assertEquals(1.0, Unbox.toDouble(ONE));
		assertEquals(2.0, Unbox.addDouble(ONE, ONE, null));
	}


	@Test
	public void testFloat()
	{
		assertEquals(0f, Unbox.toFloat(null));
		assertEquals(1f, Unbox.toFloat(ONE));
		assertEquals(2f, Unbox.addFloat(ONE, ONE, null));
	}


	@Test
	public void testInt()
	{
		assertEquals(0, Unbox.toInt(null));
		assertEquals(1, Unbox.toInt(ONE));
		assertEquals(2, Unbox.addInt(ONE, ONE, null));
	}


	@Test
	public void testLong()
	{
		assertEquals(0L, Unbox.toLong(null));
		assertEquals(1L, Unbox.toLong(ONE));
		assertEquals(2L, Unbox.addLong(ONE, ONE, null));
	}


	@Test
	public void testShort()
	{
		assertEquals((short)0, Unbox.toShort(null));
		assertEquals((short)1, Unbox.toShort(ONE));
	}
}
