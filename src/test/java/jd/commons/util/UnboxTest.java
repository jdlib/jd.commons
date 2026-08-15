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
import org.junit.jupiter.api.Test;


public class UnboxTest
{
	private static final Number ONE = Integer.valueOf(1);


	@Test
	public void testBoolean()
	{
		expectTrue(Unbox.isTrue(Boolean.TRUE));
		expectFalse(Unbox.isTrue(Boolean.FALSE));
		expectFalse(Unbox.isTrue(null));

		expectTrue(Unbox.isFalse(Boolean.FALSE));
		expectFalse(Unbox.isFalse(Boolean.TRUE));
		expectFalse(Unbox.isFalse(null));

		expectFalse(Unbox.toBoolean(null, false));
		expectTrue(Unbox.toBoolean(null, true));
		expectFalse(Unbox.toBoolean(Boolean.FALSE, true));
		expectTrue(Unbox.toBoolean(Boolean.TRUE, false));
	}


	@Test
	public void testByte()
	{
		expectEqual((byte)0, Unbox.toByte(null));
		expectEqual((byte)1, Unbox.toByte(ONE));
	}


	@Test
	public void testChar()
	{
		expectEqual((char)0, Unbox.toChar(null));
		expectEqual('a', Unbox.toChar(Character.valueOf('a')));
	}


	@Test
	public void testDouble()
	{
		expectEqual(0.0, Unbox.toDouble(null));
		expectEqual(1.0, Unbox.toDouble(ONE));
		expectEqual(2.0, Unbox.addDouble(ONE, ONE, null));
	}


	@Test
	public void testFloat()
	{
		expectEqual(0f, Unbox.toFloat(null));
		expectEqual(1f, Unbox.toFloat(ONE));
		expectEqual(2f, Unbox.addFloat(ONE, ONE, null));
	}


	@Test
	public void testInt()
	{
		expectEqual(0, Unbox.toInt(null));
		expectEqual(1, Unbox.toInt(ONE));
		expectEqual(2, Unbox.addInt(ONE, ONE, null));
	}


	@Test
	public void testLong()
	{
		expectEqual(0L, Unbox.toLong(null));
		expectEqual(1L, Unbox.toLong(ONE));
		expectEqual(2L, Unbox.addLong(ONE, ONE, null));
	}


	@Test
	public void testShort()
	{
		expectEqual((short)0, Unbox.toShort(null));
		expectEqual((short)1, Unbox.toShort(ONE));
	}
}
