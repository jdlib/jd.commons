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
import jd.commons.check.Check;


/**
 * A Config base class for configs which wrap another config.
 */
public abstract class ProxyConfig extends Config
{
	protected final Config wrapped_;

	
	public ProxyConfig(Config wrapped)
	{
		wrapped_ = Check.notNull(wrapped, "wrapped");
	}


	@Override
	protected boolean containsInternal(String key)
	{
		return wrapped_.containsInternal(key);
	}

	
	@Override
	protected String getInternal(String key)
	{
		return wrapped_.getInternal(key);
	}


	@Override
	protected void setInternal(String key, String value)
	{
		wrapped_.setInternal(key, value);
	}


	@Override
	public Stream<String> keys()
	{
		return wrapped_.keys();
	}


	@Override
	public boolean isImmutable()
	{
		return wrapped_.isImmutable();
	}
	
	
	@Override
	public Config clear()
	{
		wrapped_.clear();
		return this;
	}


	@Override
	protected void describe(StringBuilder s)
	{
		describeSelf(s);
		s.append("->");
		wrapped_.describe(s);
	}


	protected abstract void describeSelf(StringBuilder s);
}
