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


import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import jd.commons.check.Check;


class MockImpl<T> implements Mock<T>, Mock.Then<T>
{
	private final Class<T> type_;
	private final Map<MockInvocation,MockAction> map_ = new HashMap<>();
	private MockInvocation lastInvocation_;

	
	public MockImpl(Class<T> type)
	{
		type_ = type;
	}


	@Override
	public Then<T> when(String methodName, Object... args)
	{
		lastInvocation_ = new MockInvocation(methodName, args);
		return this;
	}


	@Override
	public Mock<T> thenReturn(Object value)
	{
		return thenAction(() -> value);
	}
	

	@Override
	public Mock<T> thenThrow(Exception e)
	{
		return thenAction(() -> { throw e; });
	}
	
	
	private Mock<T> thenAction(MockAction action)
	{
		Check.notNull(lastInvocation_, "lastInvocation");
		map_.put(lastInvocation_, action);
		lastInvocation_ = null;
		return this;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public T create()
	{
		MockHandler handler = new MockHandler(map_);
		return (T)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { type_ }, handler);
	}
}
	
	
