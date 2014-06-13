package org.bladerunnerjs.model;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.*;
import org.bladerunnerjs.aliasing.*;
import org.bladerunnerjs.aliasing.aliasdefinitions.*;
import org.bladerunnerjs.memoization.*;
import org.bladerunnerjs.model.engine.*;
import org.bladerunnerjs.model.exception.*;
import org.bladerunnerjs.model.exception.modelupdate.*;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.utility.*;
import org.bladerunnerjs.utility.*;

// TODO Java 8 (1.8.0-b123) compiler throws errors when this class is named 'AbstractAssetLocation'
public abstract class TheAbstractAssetLocation extends InstantiatedBRJSNode implements AssetLocation {
	private final AssetContainer assetContainer;
	private final FileInfo dirInfo;
	
	private final AssetLocator assetLocator;
	private List<AssetLocation> dependentAssetLocations = new ArrayList<>();
	private AliasDefinitionsFile aliasDefinitionsFile;
	private final Assets emptyAssets;
	private final MemoizedValue<String> jsStyle = new MemoizedValue<>("AssetLocation.jsStyle", root(), dir());
	private String relativeRequirePath;
	
	public TheAbstractAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations) {
		super(rootNode, parent, dir);
		
		dirInfo = root().getFileInfo(dir);
		assetLocator = new AssetLocator(this);
		emptyAssets = new Assets(root());
		this.assetContainer = (AssetContainer) parent;
		this.dependentAssetLocations.addAll( Arrays.asList(dependentAssetLocations) );
		relativeRequirePath = RelativePathUtility.get(assetContainer.dir(), dir());
	}
	
	protected abstract List<File> getCandidateFiles();
	
	@Override
	public String requirePrefix() {
		return assetContainer.requirePrefix() + "/" + relativeRequirePath;
	}
	
	@Override
	public AssetContainer assetContainer() {
		return assetContainer;
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations() {
		return dependentAssetLocations;
	}
	
	@Override
	public AliasDefinitionsFile aliasDefinitionsFile() {		
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(this, dir(), "aliasDefinitions.xml");
		}
		
		return aliasDefinitionsFile;
	}
	
	@Override
	public List<LinkedAsset> linkedAssets() {
		return assets().linkedAssets;
	}
	
	@Override
	public List<Asset> bundlableAssets(AssetPlugin assetPlugin) {
		return assets().pluginAssets.get(assetPlugin);
	}
	
	@Override
	public List<SourceModule> sourceModules() {
		return assets().sourceModules;
	}
	
	@Override
	public String jsStyle() {
		return jsStyle.value(() -> {
			return JsStyleUtility.getJsStyle(dir());
		});
	}
	
	@Override
	public void assertIdentifierCorrectlyNamespaced(String identifier) throws NamespaceException, RequirePathException {
		String namespace = NamespaceUtility.convertToNamespace(requirePrefix());
		
		if(assetContainer.isNamespaceEnforced() && !identifier.startsWith(namespace)) {
			throw new NamespaceException( "The identifier '" + identifier + "' is not correctly namespaced.\nNamespace '" + namespace + ".*' was expected.");
		}
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		// do nothing
	}
	
	private Assets assets() {
		return (!dirInfo.exists()) ? emptyAssets : assetLocator.assets(getCandidateFiles());
	}
	
	@Override
	public String canonicaliseRequirePath(String requirePath) throws RequirePathException
	{
		String requirePrefix = requirePrefix();
		
		List<String> requirePrefixParts = new LinkedList<String>( Arrays.asList(requirePrefix.split("/")) );
		List<String> requirePathParts = new LinkedList<String>( Arrays.asList(requirePath.split("/")) );
		
		if(!requirePath.contains("../") && !requirePath.contains("./")) {
			return requirePath;
		}
		
		Iterator<String> requirePathPartsIterator = requirePathParts.iterator();
		while(requirePathPartsIterator.hasNext()) {
			String pathPart = requirePathPartsIterator.next();
			
			switch (pathPart) {
				case ".":
					requirePathPartsIterator.remove();
					break;
				
				case "..":
					requirePathPartsIterator.remove();
					if (requirePrefixParts.size() > 0)
					{
						requirePrefixParts.remove( requirePrefixParts.size()-1 );						
					}
					else
					{
						String msg = String.format("Unable to continue up to parent require path, no more parents remaining. Require path of container was '%s', relative require path was '%s'", requirePrefix, requirePath);
						throw new UnresolvableRelativeRequirePathException(msg);
					}
					break;
				
				default:
					break;
			}
		}
		
		return StringUtils.join(requirePrefixParts, "/") + "/" + StringUtils.join(requirePathParts, "/");
	}
	
	protected FileInfo getDirInfo() {
		return dirInfo;
	}
	
}