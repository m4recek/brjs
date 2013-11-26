package org.bladerunnerjs.testing.utility;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.core.plugin.taghandler.AbstractTagHandlerPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;


public class MockTagHandler extends AbstractTagHandlerPlugin implements TagHandlerPlugin
{
	
	String name;
	String devContent;
	String prodContent;
	boolean printLocales;
	
	public MockTagHandler(String name, String devContent, String prodContent)
	{
		this(name, devContent, prodContent, false);
	}
	
	public MockTagHandler(String name, String devContent, String prodContent, boolean printLocales)
	{
		this.name = name;
		this.devContent = devContent;
		this.prodContent = prodContent;
		this.printLocales = printLocales;
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public String getTagName()
	{
		return name;
	}

	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		writeOutContentAndTagAttributes(writer, devContent, tagAttributes, locale);
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		writeOutContentAndTagAttributes(writer, prodContent, tagAttributes, locale);
	}
	
	private void writeOutContentAndTagAttributes(Writer writer, String content, Map<String, String> tagAttributes, String locale) throws IOException
	{
		PrintWriter printWriter = new PrintWriter(writer);
		if (!content.equals(""))
		{
    		printWriter.println(content);
    		for (String attributeKey : tagAttributes.keySet())
    		{
    			String attributeValue = tagAttributes.get(attributeKey);
    			printWriter.println(attributeKey+"="+attributeValue);			
    		}
		}
    		
		if (printLocales && !locale.equals(""))
		{
			printWriter.println("- "+locale);
		}
	}

}