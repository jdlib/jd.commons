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
package jd.commons.demo;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jd.commons.io.FilePath;


public class FilePathDemo
{
	public static void main(String[] args) throws Exception
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java " + FilePathDemo.class.getName() + " <filepath> [attrPattern]");
			return;
		}
		
		FilePath path = FilePath.of(args[0]);
		String attrPattern = args.length > 1 ? args[1] : "*";
		//FilePath path = FilePath.of(arg);

		System.out.println("absolute path: " + path.toAbsolutePath());
		System.out.print("filesystem: ");
		System.out.println(path.getFileSystem());

		System.out.print("filestore: ");
		System.out.println(path.getFileStore());
		
		System.out.println("attributes:");
		Map<String,Object> attrs = path.attrsNoFollowLinks().map(attrPattern);
		List<String> names = new ArrayList<>(attrs.keySet());
		names.sort(null); 
		for (String name : names)
		{
			System.out.print(" - ");
			System.out.print(name);
			System.out.print(" = ");
			System.out.println(attrs.get(name));
		}
	}
}
