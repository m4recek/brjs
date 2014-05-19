package org.bladerunnerjs.spec.bundling.workbench;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class WorkbenchBundlingOfResources extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private Blade blade2;
	private Theme standardAspectTheme, standardBladesetTheme, standardBladeTheme;
	private StringBuffer response = new StringBuffer();
	private Workbench workbench;
	private NamedDirNode workbenchTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			standardAspectTheme = aspect.theme("standard");
			bladeset = app.bladeset("bs");
			standardBladesetTheme = bladeset.theme("standard");
			blade = bladeset.blade("b1");
			blade2 = bladeset.blade("b2");
			standardBladeTheme = blade.theme("standard");
			workbench = blade.workbench();
			workbenchTemplate = brjs.template("workbench");
			
			// workbench setup
			given(workbenchTemplate).containsFileWithContents("index.html", "<@css.bundle theme='standard'@/>")
				.and(workbenchTemplate).containsFolder("resources")
				.and(workbenchTemplate).containsFolder("src");
	}
	 
	// C S S
	@Test
	public void workbenchesLoadCssFromTheAspectLevel() throws Exception
	{
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(standardAspectTheme).containsFileWithContents("style.css", "ASPECT theme content")
			.and(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(standardBladesetTheme).containsFileWithContents("style.css", "BLADESET theme content")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1", "appns.Class1")
			.and(standardBladeTheme).containsFileWithContents("style.css", "BLADE theme content")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1");	
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/css/standard/bundle.css", response);
		then(response).containsOrderedTextFragments("ASPECT theme content",
													"BLADESET theme content",
													"BLADE theme content");
	}
	
	
	@Test
	public void assetsFromAnotherBladeArentLoadedIfTheAspectResourcesDependsOnThem() throws Exception
	{
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("resources/someFile.xml", "appns.bs.b2.Class1")
			.and(blade2).hasClass("appns/bs/b2/Class1")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1");	
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/js/dev/combined/bundle.js", response);		
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	
	
}
