package org.bladerunnerjs.model;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.YamlBladerunnerConf;

public class BladerunnerConf extends ConfFile<YamlBladerunnerConf> {
	
	public static final String OUTPUT_ENCODING = "UTF-8";
	
	public BladerunnerConf(BRJS brjs) throws ConfigException {
		super(brjs, YamlBladerunnerConf.class, getConfigFilePath(brjs), "UTF-8");
	}
	
	public int getJettyPort() throws ConfigException {
		return getConf().jettyPort;
	}
	
	public void setJettyPort(int jettyPort) throws ConfigException {
		getConf().jettyPort = jettyPort;
		verifyAndAutoWrite();
	}
	
	public String getDefaultFileCharacterEncoding() throws ConfigException {
		return getConf().defaultFileCharacterEncoding;
	}
	
	public void setDefaultFileCharacterEncoding(String defaultFileCharacterEncoding) throws ConfigException {
		getConf().defaultFileCharacterEncoding = defaultFileCharacterEncoding;
		verifyAndAutoWrite();
	}

	public String getLoginRealm() throws ConfigException
	{
		return getConf().loginRealm;
	}

	public String getLoginModuleName() throws ConfigException
	{
		return getConf().LOGIN_MODULE_NAME;
	}
	
	public String[] getIgnoredPaths() throws ConfigException {
		String[] ignoredFileStrings = getConf().ignoredPaths.split("\\s*,\\s*");
		return ignoredFileStrings;
	}
	
	public void setIgnoredPaths(String... ignoredFiles) throws ConfigException {
		getConf().ignoredPaths = StringUtils.join(ignoredFiles,",");
		verifyAndAutoWrite();
	}
	
	public boolean useNodeCommands() throws ConfigException
	{
		return getConf().useNodeCommands;
	}
	
	public static MemoizedFile getConfigFilePath(BRJS brjs) {
		return brjs.conf().file("brjs.conf");
	}
	
	public Boolean getAllowAnonymousStats() throws ConfigException {
		return getConf().allowAnonymousStats;
	}
	
	public void setAllowAnonymousStats(boolean allow) throws ConfigException {
		getConf().allowAnonymousStats = allow;
		verify();
	}
	
	public String getFileObserverValue() throws ConfigException {
		return getConf().fileObserver;
	}
	
	public void setFileObserverValue(String value) throws ConfigException {
		getConf().fileObserver = value;
		verify();
	}
	
}
