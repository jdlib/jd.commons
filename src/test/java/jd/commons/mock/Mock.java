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
package jd.commons.mock;


/**
 * Poor mans mockito.
 */
public interface Mock<T>
{
	public static <T> Mock<T> mock(Class<T> type)
	{
		return new MockImpl<>(type);
	}
	
	
	public T create();
	
	
	public Then<T> when(String methodName, Object... args);
	
	
	public interface Then<T>
	{
		public Mock<T> thenReturn(Object value);

	
		public Mock<T> thenThrow(Exception e);
	}
}


