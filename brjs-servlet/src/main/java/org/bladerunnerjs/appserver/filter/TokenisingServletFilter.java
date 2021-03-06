package org.bladerunnerjs.appserver.filter;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.appserver.util.CommitedResponseCharResponseWrapper;
import org.bladerunnerjs.appserver.util.JndiTokenFinder;
import org.bladerunnerjs.appserver.util.StreamTokeniser;

public class TokenisingServletFilter implements Filter
{
	private StreamTokeniser streamTokeniser = new StreamTokeniser();
	private JndiTokenFinder tokenFinder;
	private final List<String> validExtensions = Arrays.asList(".xml", ".json", ".html", ".htm", ".jsp", "/");
	private String contextPath;

	public TokenisingServletFilter() throws ServletException
	{
		try
		{ 
			this.tokenFinder = new JndiTokenFinder();
		}
		catch (NamingException e)
		{
			throw new ServletException("Error getting context for JNDI lookups. (" + e + ")", e);
		}
	}
	
	/* this should only be used for testing */
	public TokenisingServletFilter(JndiTokenFinder tokenFinder) throws ServletException
	{
		this.tokenFinder = tokenFinder;
	}

	@Override
	public void init(FilterConfig filterConfig)
	{
		contextPath = filterConfig.getServletContext().getContextPath();
	}
	
	@Override
	public void destroy()
	{
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if (shouldProcessResponse(request))
		{
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String parentRequestPath = httpRequest.getRequestURI().replaceAll("/[^/]*$", "");
			String hostIdentifier = httpRequest.getRequestURL().toString().replaceAll(contextPath + ".*$", "");
			String requestUri = hostIdentifier + parentRequestPath;
			ServletOutputStream out = response.getOutputStream();
			CommitedResponseCharResponseWrapper responseWrapper = new CommitedResponseCharResponseWrapper((HttpServletResponse) response);
			chain.doFilter(request, responseWrapper);
			
			try
			{
				StringBuffer filteredResponse = streamTokeniser.replaceTokens(responseWrapper.getReader(), tokenFinder, requestUri);
				byte[] filteredData = filteredResponse.toString().getBytes(response.getCharacterEncoding());
				if (!response.isCommitted()) { // only write the content if the headers havent been commited (an error code hasnt been sent)
					response.setContentLength(filteredData.length);
					out.write(filteredData);
					response.flushBuffer();
				}
			}
			catch(Exception e)
			{
				throw new ServletException(e);
			}
		}
		else
		{
			chain.doFilter(request, response);
		}
	}

	private boolean shouldProcessResponse(ServletRequest request)
	{
		HttpServletRequest theRequest = (HttpServletRequest) request;
		String requestUrl = theRequest.getRequestURL().toString();
		for (String extension : validExtensions)
		{
			if (requestUrl.endsWith(extension))
			{
				return true;
			}
		}
		return false;
	}
}
