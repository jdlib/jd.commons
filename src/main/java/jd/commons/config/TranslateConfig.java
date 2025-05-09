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


import java.util.function.Function;
import jd.commons.check.Check;
import jd.commons.util.Utils;


/**
 * A ProxyConfig which applies a function to its values.
 */
public class TranslateConfig extends ProxyConfig
{
	private final Function<String,String> norm_;

	
	public static Config norm(Config wrapped)
	{
		return new TranslateConfig(wrapped, Utils::norm);
	}
	
	
	public TranslateConfig(Config wrapped, Function<String,String> fn)
	{
		super(wrapped);
		norm_ = Check.notNull(fn, "fn");
	}


	@Override
	protected boolean containsInternal(String key)
	{
		return getInternal(key) != null;
	}

	
	@Override
	protected String getInternal(String key)
	{
		String s = wrapped_.getInternal(key);
		if (s != null)
			s = norm_.apply(s);
		return s;
	}


	@Override
	protected void describeSelf(StringBuilder s)
	{
		s.append("translate");
	}
}
