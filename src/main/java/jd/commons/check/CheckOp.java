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
package jd.commons.check;


interface CheckOp
{
	public static final CheckOp GREATER = new CheckOpGreater();
	public static final CheckOp GREATER_EQ = new CheckOpGreaterEq();
	public static final CheckOp LESS = new CheckOpLess();
	public static final CheckOp LESS_EQ = new CheckOpLessEq();
	public static final CheckOp EQ = new CheckOpEq();
	public static final CheckOp NOT_EQ = new CheckOpNotEq();

	
	public String symbol();
		

	public boolean compare(double v1, double v2);

	
	public boolean compare(long v1, long v2);
}


class CheckOpGreater implements CheckOp
{
	@Override
	public String symbol()
	{
		return ">";
	}
	
	
	@Override
	public boolean compare(double v1, double v2)
	{
		return v1 > v2;
	}


	@Override
	public boolean compare(long v1, long v2)
	{
		return v1 > v2;
	}
}
  

class CheckOpGreaterEq implements CheckOp
{
	@Override
	public String symbol()
	{
		return ">=";
	}

	
	@Override
	public boolean compare(double v1, double v2)
	{
		return v1 >= v2;
	}
	

	@Override
	public boolean compare(long v1, long v2)
	{
		return v1 >= v2;
	}
}


class CheckOpLess implements CheckOp
{
	@Override
	public String symbol()
	{
		return "<";
	}

	
	@Override
	public boolean compare(double v1, double v2)
	{
		return v1 < v2;
	}
	

	@Override
	public boolean compare(long v1, long v2)
	{
		return v1 < v2;
	}
}


class CheckOpLessEq implements CheckOp
{
	@Override
	public String symbol()
	{
		return "<=";
	}

	
	@Override
	public boolean compare(double v1, double v2)
	{
		return v1 <= v2;
	}
	

	@Override
	public boolean compare(long v1, long v2)
	{
		return v1 <= v2;
	}
}


class CheckOpEq implements CheckOp
{
	@Override
	public String symbol()
	{
		return "==";
	}

	
	@Override
	public boolean compare(double v1, double v2)
	{
		return v1 == v2;
	}
	

	@Override
	public boolean compare(long v1, long v2)
	{
		return v1 == v2;
	}
}


class CheckOpNotEq implements CheckOp
{
	@Override
	public String symbol()
	{
		return "!=";
	}

	
	@Override
	public boolean compare(double v1, double v2)
	{
		return v1 != v2;
	}
	

	@Override
	public boolean compare(long v1, long v2)
	{
		return v1 != v2;
	}
}
