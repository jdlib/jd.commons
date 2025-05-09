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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.ByteReadFrom;
import jd.commons.io.fluent.ByteSource;
import jd.commons.io.fluent.ByteTarget;
import jd.commons.io.fluent.ByteWriteTo;
import jd.commons.io.fluent.handler.IOHandler;


/**
 * A Config implementation based on a java.util.Properties object.
 */
public class PropsConfig extends Config
{
	public static PropsConfig system()
	{
		return new PropsConfig(System.getProperties());
	}

	
	public PropsConfig()
	{
		this(null);
	}


	/**
	 * Creates an Config object based on the given properties.
	 * @param properties a properties object. If null a new properties object is created.
	 */
	public PropsConfig(Properties properties)
	{
		props_ = properties != null ? properties : new Properties();
	}


	@Override
	protected boolean containsInternal(String key)
	{
		return props_.containsKey(key);
	}

	
	/**
	 * @return the underlying Properties.
	 */
	public Properties getProperties()
	{
		return props_;
	}
	
	
	/**
	 * Returns an Input builder object to specify the source
	 * which should be read in order to fill this PropConfig.
	 * @return the builder
	 */
	@CheckReturnValue
	public static PropsRead readProps()
	{
		return new PropsConfig().read();
	}


	@CheckReturnValue
	public PropsRead read()
	{
		return new PropsRead();
	}

	
	public class PropsRead extends ByteReadFrom<PropsConfig,IOException>
	{
		private PropsRead()
		{
			super(new PropReadHandler());
		}

		
		public PropsRead xml()
		{
			((PropReadHandler)handler_).xml_ = true;
			return this;
		}
	}


	private class PropReadHandler extends IOHandler<ByteSource,InputStream,PropsConfig,IOException>
	{
		private boolean xml_;

		
		@Override
		public PropsConfig runSupplier(ByteSource source) throws IOException
		{
			try (InputStream in = source.getInputStream()) 
			{
				return runDirect(in);
			}
		}
		
	
		@Override
		public PropsConfig runDirect(InputStream in) throws IOException
		{
			if (xml_)
				props_.loadFromXML(in);
			else
				props_.load(in);
			return PropsConfig.this;
		}
	}

	
	public PropsWrite write()
	{
		return new PropsWrite();
	}
	
	
	public class PropsWrite extends ByteWriteTo<Void,IOException>
	{
		private PropsWrite()
		{
			super(new PropWriteHandler());
		}
		
		
		private PropWriteHandler handler()
		{
			return (PropWriteHandler)handler_; 
		}
		
		
		public PropsWrite comment(String comment)
		{
			handler().comment_ = comment;
			return this;
		}
		
		
		public PropsWrite xml()
		{
			return xml(StandardCharsets.UTF_8);
		}

		
		public PropsWrite xml(Charset charset)
		{
			handler().xmlCharset_ = Check.notNull(charset, "charset");
			return this;
		}
	}
	

	private class PropWriteHandler extends IOHandler<ByteTarget,OutputStream,Void,IOException>
	{
		private String comment_;
		private Charset xmlCharset_;

		
		@Override
		public Void runSupplier(ByteTarget target) throws IOException
		{
			Check.notNull(target, "target");
			try (OutputStream out = target.getOutputStream())
			{
				return runDirect(out);
			}
		}
		
	
		@Override
		public Void runDirect(OutputStream out) throws IOException
		{
			if (xmlCharset_ != null)
				props_.storeToXML(out, comment_, xmlCharset_);
			else
				props_.store(out, comment_);
			return null;
		}
	}

	
	@Override protected String getInternal(String key)
	{
		return props_.getProperty(key);
	}


	@Override protected void setInternal(String key, String value)
	{
		if (value == null)
			props_.remove(key);
		else
			props_.setProperty(key, value);
	}


	@Override public Stream<String> keys()
	{
		return props_.keySet().stream().map(Object::toString);
	}


	@Override
	public boolean isImmutable()
	{
		return false;
	}
	
	
	@Override
	public Config clear()
	{
		props_.clear();
		return this;
	}
	
	
	@Override
	protected void describe(StringBuilder s)
	{
		s.append("props");
	}


	private final Properties props_;
}
