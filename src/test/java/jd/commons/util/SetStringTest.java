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


public class SetStringTest
{
	private static final SetString<String> set_ = new SetString<>()
	{
		@Override
		public String to(String s)
		{
			return s;
		}
	};

	@Test
	public void test()
	{
		expectEqual("s", 		set_.to("s"));
		expectEqual("1", 		set_.to(1));
		expectEqual("2", 		set_.to((byte)2));
		expectEqual("3", 		set_.to((short)3));
		expectEqual("4", 		set_.to(4L));
		expectEqual("5.0", 	set_.to(5.0));
		expectEqual("6.0", 	set_.to(6.0f));
		expectEqual("c", 		set_.to('c'));
		expectEqual("true", 	set_.to(true));
		expectEqual("42", 		set_.toObject(Integer.valueOf(42)));
		expectEqual(String.class.getName(), set_.toClass(String.class));
		expectNull(set_.to(null));
		expectNull(set_.toClass(null));
		expectNull(set_.toObject(null));
	}
}
