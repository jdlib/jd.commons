package jd.commons.io.fluent.handler;


import java.io.IOException;
import java.io.Writer;
import jd.commons.check.Check;
import jd.commons.io.fluent.CharTarget;
import jd.commons.util.function.XConsumer;


public class ConsumeCharsHandler extends IOHandler<CharTarget,Writer,Void,IOException>
{
	protected final XConsumer<Writer,? extends IOException> consumer_;


	public ConsumeCharsHandler(XConsumer<Writer,? extends IOException> consumer)
	{
		consumer_ = Check.notNull(consumer, "consumer");
	}


	@Override
	public Void runSupplier(CharTarget target) throws IOException
	{
		try (Writer out = target.getWriter())
		{
			consumer_.accept(out);
		}
		return null;
	}


	@Override
	public Void runDirect(Writer writer) throws IOException
	{
		consumer_.accept(writer);
		writer.flush();
		return null;
	}
}
