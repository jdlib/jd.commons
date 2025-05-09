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
package jd.commons.config;


import java.util.stream.Stream;
import javax.naming.Context;
import javax.naming.NamingException;
import jd.commons.check.Check;


/**
 * JndiConfig makes the values bound in a JNDI context available as Config.<br>
 * By design it is immutable i.e. does not allow to modify the JNDI context.<br>
 * It never {@link Context#close() closes} its Context, so in case this is needed
 * the owner of the JndiConfig must control the lifecycle of the Context and close
 * by own means. 
 */
// about prefixing:
// when JndiConfig#prefixed is called, JndiConfig could switch to Context bound to the prefix
// and return a new JndiConfig instead of a PrefixedConfig
// We don't do this since we don't know if the Context.lookup(prefix) results in a valid Context
public class JndiConfig extends Config
{
	private final Context context_;
	
	
	/**
	 * Creates a new JndiConfig.
	 * @param context a Context
	 * @return the JndiConfig or null if the context is null
	 */
	public static JndiConfig createOrNull(Context context)
	{
		return context != null ? new JndiConfig(context) : null;
	}

	
	/**
	 * Creates a new Config.
	 * @param context a Context
	 * @return the JndiConfig or an empty Config if the context is null
	 */
	public static Config createOrEmpty(Context context)
	{
		return context != null ? new JndiConfig(context) : ImmutableConfig.EMPTY;
	}

	
	/**
	 * Creates a new JndiConfig.
	 * @param context a Context, not null
	 */
	public JndiConfig(Context context)
	{
		context_ = Check.notNull(context, "context");
	}
	
	
	/**
	 * @return the context used by this JndiConfig.
	 */
	public Context getContext()
	{
		return context_;
	}
		

	@Override 
	protected boolean containsInternal(String key)
	{
		return getInternal(key) != null;
	}
	
	
	@Override 
	protected String getInternal(String key)
	{
		try
		{
			Object value = context_.lookup(key);
			return value != null ? value.toString() : null;
		}
		catch (NamingException e)
		{
			return null;
		}
	}


	@Override
	public boolean isImmutable()
	{
		return true;
	}

	
	@Override protected void setInternal(String key, String value)
	{
		throw createImmutableEx();
	}
	
	
	@Override
	public Config clear()
	{
		throw createImmutableEx();
	}


	@Override public Stream<String> keys()
	{
		return Stream.of();
	}


	@Override
	protected void describe(StringBuilder s)
	{
		s.append("jndi");
	}
}
