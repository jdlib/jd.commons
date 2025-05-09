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


public interface SetString<T>
{
	public T to(String s);


	public default T to(boolean b)
	{
		return to(String.valueOf(b));
	}


	public default T to(byte b)
	{
		return to(String.valueOf(b));
	}


	public default T to(char c)
	{
		return to(String.valueOf(c));
	}


	public default T to(double n)
	{
		return to(String.valueOf(n));
	}


	public default T to(float n)
	{
		return to(String.valueOf(n));
	}


	public default T to(int n)
	{
		return to(String.valueOf(n));
	}


	public default T to(long n)
	{
		return to(String.valueOf(n));
	}


	public default T to(short n)
	{
		return to(String.valueOf(n));
	}


	public default T toClass(Class<?> c)
	{
		return to(c != null ? c.getName() : null);
	}


	public default T toObject(Object object)
	{
		return to(object != null ? object.toString() : null);
	}
}
