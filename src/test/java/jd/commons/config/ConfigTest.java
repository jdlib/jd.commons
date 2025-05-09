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


import static jd.commons.io.fluent.IO.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.jupiter.api.Test;
import jd.commons.mock.Mock;


public class ConfigTest
{
	@Test
	public void testImmutableConfig()
	{
		Config map = new MapConfig();
		Config ro  = map.immutable();
		assertNotSame(ro, map);
		assertSame(ro, ro.immutable());
		
		ConfigAssert.of(ro)
			.immutable(true)
			.contains("a", false)
			.get("a", null)
			.keys()
			.toString("Config[readonly->map]");
	}


	@Test
	public void testJndiConfig()
	{
		Context context = Mock.mock(Context.class)
			.when("lookup", "a").thenReturn("1")
			.when("lookup", "b").thenReturn(null)
			.when("lookup", "c").thenThrow(new NamingException("invalid key"))
			.create();
		
		JndiConfig jc = new JndiConfig(context);
		assertSame(context, jc.getContext());
		
		ConfigAssert.of(jc)
			.immutable(true)
			.contains("a", true)
			.contains("b", false)
			.get("a", "1")
			.get("b", null)
			.get("c", null)
			.keys()
			.toString("Config[jndi]");
	}

	
	@Test
	public void testJndiConfigCreate() throws Exception
	{
		InitialContext ic = new InitialContext(); 

		assertNull(JndiConfig.createOrNull(null));
		assertSame(ic, JndiConfig.createOrNull(ic).getContext());

		assertSame(ImmutableConfig.EMPTY, JndiConfig.createOrEmpty(null));
		assertSame(ic, assertInstanceOf(JndiConfig.class, JndiConfig.createOrEmpty(ic)).getContext());
	}
	
	
	@Test
	public void testConcat()
	{
		Config c1 = new MapConfig();
		c1.setValue("a", "a1");
		Config c2 = new MapConfig();
		c2.setValue("a", "a2");
		c2.setValue("b", "b2");
		Config c3 = new MapConfig();
		c3.setValue("c", "c3");
		
		assertNull(Config.concat(null, null));
		assertSame(c1, Config.concat(c1, null));
		assertSame(c2, Config.concat(null, c2));
		
		ConfigAssert.of(Config.concat(c1, c2))
			.immutable(true)
			.contains("a", true)
			.contains("b", true)
			.contains("c", false)
			.get("a", "a1")
			.get("b", "b2")
			.toString("Config[map | map]")
			.keys("a", "b");

		ConfigAssert.of(Config.concat(c2, c1))
			.get("a", "a2")
			.get("b", "b2");
		
		assertNull(Config.concat((Config[])null));
		assertNull(Config.concat());
		assertNull(Config.concat((Config)null));
		assertSame(c1, Config.concat(c1));
		
		ConfigAssert.of(Config.concat(c1, c2, c3))
			.immutable(true)
			.contains("a", true)
			.contains("b", true)
			.contains("c", true)
			.get("a", "a1")
			.get("b", "b2")
			.get("c", "c3")
			.toString("Config[map | map | map]")
			.keys("a", "b", "c");
	}

	
	@Test
	public void testMapConfig()
	{
		MapConfig c = MapConfig.env();
		assertNotNull(c.getMap()); 		// covers .getMap()
		assertTrue(c.isImmutable());
		
		ConfigAssert.of(new MapConfig().set("a").to(1))
			.get("a", "1")
			.keys("a")
			.immutable(false)
			.clear()
			.get("a", null);
		
		ConfigAssert.of(new MapConfig(Map.of()))
			.immutable(true);

		ConfigAssert.of(new MapConfig(Map.of(), true))
			.immutable(true);
	}


	@Test
	public void testPrefixedConfig()
	{
		Config mc = new MapConfig();
		mc.setValue("1.1.1", "111").setValue("1.1.2", "112").setValue("1.2.1", "121");
		Config p1 = mc.prefix("1.");
		Config p2 = p1.prefix("1.");
		ConfigAssert.of(p1)
			.immutable(false)
			.get("1.1", "111")
			.keys("1.1", "1.2", "2.1")
			.toString("Config[\"1.\"->map]");

		ConfigAssert.of(p2)
			.get("1", "111")
			.set("1", "aaa")
			.get("1", "aaa")
			.keys("1", "2")
			.remove("1")
			.get("1", null)
			.toString("Config[\"1.1.\"->map]");
	}


	@Test
	public void testPropsConfig() throws Exception
	{
		assertEquals(System.getProperty("user.dir"), PropsConfig.system().getValue("user.dir"));
		
		PropsConfig pc = new PropsConfig();
		assertNotNull(pc.getProperties());
		pc.setValue("a", "1");
		ConfigAssert.of(pc)
			.contains("a", true)
			.set("b", "2")
			.get("b", "2")
			.remove("b")
			.get("b", null)
			.immutable(false)
			.keys("a")
			.toString("Config[props]")
			.clear()
			.get("a", null);
		
		ByteArrayOutputStream propsBAOS = new ByteArrayOutputStream();
		pc.write().comment("hello").to(Bytes.to(propsBAOS));
		byte[] propsBytes  = pc.write().comment("hello").toByteArray();
		assertArrayEquals(propsBAOS.toByteArray(), propsBytes);
		byte[] xmlBytes    = pc.write().xml().toByteArray();
		
		PropsConfig pcread1a = PropsConfig.readProps().from(propsBytes); 
		PropsConfig pcread1b = PropsConfig.readProps().from(Bytes.from(propsBytes)); 
		PropsConfig pcread2  = PropsConfig.readProps().xml().from(xmlBytes);
		
		assertEquals(pc.getProperties(), pcread1a.getProperties());
		assertEquals(pc.getProperties(), pcread1b.getProperties());
		assertEquals(pc.getProperties(), pcread2.getProperties());
	}
	
	
	@Test
	public void testProxyConfig()
	{
		// increase coverage of ProxyConfig methods:
		// using TranslateConfig since it passes the tested methods
		ConfigAssert.of(TranslateConfig.norm(new MapConfig()))
			.immutable(false)
			.clear()
			.get("a", null)
			.set("a", "1")
			.get("a", "1");
	}
	
	
	
	@Test
	public void testTranslateConfig()
	{
		ConfigAssert.of(TranslateConfig.norm(new MapConfig(Map.of("a", "1 "), true)))
			.get("a", "1")
			.get("b", null)
			.get("c", null)
			.immutable(true)
			.contains("a", true)
			.contains("b", false)
			.toString("Config[translate->map]");
	}
}
