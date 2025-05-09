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


import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import jd.commons.check.Check;
import com.google.common.base.Preconditions;


/**
 * Compares various check/precondition/validation libraries.
 */
public class CheckDemo
{
	public void jdCommons(String id, List<String> names, double percent) 
	{
	    Check.notNull(id, "id");
	    Check.elems(names, "names").notEmpty().noneNull();
	    Check.value(percent, "percent").greaterEq(0.0).lessEq(100.0);
	}


	public void jdkObjects(String id, List<String> names, double percent) 
	{
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(names, "names");
		if (names.stream().anyMatch(Objects::isNull))
			throw new IllegalArgumentException("names contains null elements");
		if (percent < 0.0 || percent > 100.0)
			throw new IllegalArgumentException("percent must be >= 0.0 and <= 100.0, is " + percent);
	}
	
	
	public void guava(String id, List<String> names, double percent)
	{
		Preconditions.checkNotNull(id, "id");
		Preconditions.checkArgument(names != null && !names.isEmpty(), "names cannot be null or empty");
		Preconditions.checkArgument(names.stream().noneMatch(java.util.Objects::isNull), "names cannot contain null elements");
		Preconditions.checkArgument(percent >= 0.0 && percent <= 100.0, "percent must be between 0.0 and 100.0, but is " + percent);
	}


	public void apacheCommons(String id, List<String> names, double percent)
	{
	    Validate.notNull(id, "id cannot be null");
	    Validate.notEmpty(names, "names cannot be null or empty");
	    Validate.noNullElements(names, "names cannot contain null elements");
	    Validate.inclusiveBetween(0.0, 100.0, percent, "percent must be between 0.0 and 100.0, but is " + percent);
	}
}


