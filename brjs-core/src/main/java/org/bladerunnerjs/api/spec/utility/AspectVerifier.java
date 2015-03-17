package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.VerifierChainer;

import com.google.common.base.Joiner;


public class AspectVerifier extends BundlableNodeVerifier<Aspect> {
	private Aspect aspect;
	private AssetContainerVerifier assetContainerVerifier;
	
	public AspectVerifier(SpecTest modelTest, Aspect aspect) {
		super(modelTest, aspect);
		this.aspect = aspect;
		assetContainerVerifier = new AssetContainerVerifier(aspect);
	}
	
	public VerifierChainer hasSourceModules(String... sourceModules) throws Exception {
		assetContainerVerifier.hasSourceModules(sourceModules);
		
		return verifierChainer;
	}
	
	public VerifierChainer classHasPreExportDependencies(String requirePath, String... expectedRequirePaths) throws Exception {
		SourceModule sourceModule = (SourceModule) aspect.asset(requirePath);
		List<String> actualRequirePaths = requirePaths(sourceModule.getPreExportDefineTimeDependentAssets(aspect));
		
		assertEquals(Joiner.on(", ").join(expectedRequirePaths), Joiner.on(", ").join(actualRequirePaths));
		
		return verifierChainer;
	}
	
	public VerifierChainer classHasPostExportDependencies(String requirePath, String... expectedRequirePaths) throws Exception {
		SourceModule sourceModule = (SourceModule) aspect.asset(requirePath);
		List<String> actualRequirePaths = requirePaths(sourceModule.getPostExportDefineTimeDependentAssets(aspect));
		
		assertEquals(Joiner.on(", ").join(expectedRequirePaths), Joiner.on(", ").join(actualRequirePaths));
		
		return verifierChainer;
	}
	
	public VerifierChainer classHasUseTimeDependencies(String requirePath, String... expectedRequirePaths) throws Exception {
		SourceModule sourceModule = (SourceModule) aspect.asset(requirePath);
		List<String> actualRequirePaths = requirePaths(sourceModule.getUseTimeDependentAssets(aspect));
		
		assertEquals(Joiner.on(", ").join(expectedRequirePaths), Joiner.on(", ").join(actualRequirePaths));
		
		return verifierChainer;
	}

	private List<String> requirePaths(List<Asset> assets) {
		List<String> requirePaths = new ArrayList<>();
		
		for(Asset asset : assets) {
			SourceModule sourceModule = (SourceModule) asset;
			requirePaths.add(sourceModule.getRequirePaths().get(0));
		}
		
		return requirePaths;
	}
}