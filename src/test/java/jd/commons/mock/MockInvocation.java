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


import java.util.Arrays;
import java.util.Objects;
import jd.commons.util.Utils;


class MockInvocation
{
	public final String methodName;
	public final Object[] args;
	private final int hashCode;
	
	
	public MockInvocation(String methodName, Object[] args)
	{
		if (Utils.isEmpty(args))
			args = null;
		this.methodName = methodName;
		this.args = args;
		this.hashCode = methodName.hashCode() ^ Objects.hash(this.args); 
	}
	
	
	@Override
	public int hashCode()
	{
		return hashCode;
	}
	
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof MockInvocation)
		{
			MockInvocation inv = (MockInvocation)other;
			return inv.methodName.equals(methodName) && Arrays.equals(inv.args, args);
		}
		return false;
	}
}
