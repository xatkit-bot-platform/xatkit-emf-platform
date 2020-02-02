package com.xatkit.plugins.emf.platform;

import com.xatkit.AbstractXatkitTest;
import com.xatkit.core.XatkitCore;
import com.xatkit.core.XatkitException;
import com.xatkit.plugins.emf.EMFPlatformUtils;
import com.xatkit.stubs.StubXatkitCore;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class EMFPlatformTest extends AbstractXatkitTest {

    private static XatkitCore xatkitCore;

    @BeforeClass
    public static void setUpBeforeClass() {
        xatkitCore = new StubXatkitCore();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        if (nonNull(xatkitCore) && !xatkitCore.isShutdown()) {
            xatkitCore.shutdown();
        }
    }

    private EMFPlatform emfPlatform;

    @After
    public void tearDown() {
        if (nonNull(emfPlatform)) {
            emfPlatform.shutdown();
        }
    }

    @Test(expected = NullPointerException.class)
    public void constructNullXatkitCore() {
        this.emfPlatform = new EMFPlatform(null, new BaseConfiguration());
    }

    @Test(expected = NullPointerException.class)
    public void constructNullConfiguration() {
        this.emfPlatform = new EMFPlatform(xatkitCore, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructConfigurationDoesNotContainMetamodelLocation() {
        this.emfPlatform = new EMFPlatform(xatkitCore, new BaseConfiguration());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructMetamodelFileDoesNotExist() {
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty(EMFPlatformUtils.METAMODEL_LOCATION_KEY, "invalid");
        this.emfPlatform = new EMFPlatform(xatkitCore, configuration);
    }

    @Test
    public void constructExistingMetamodelFile() {
        String metamodelPath = getMetamodelPath();
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty(EMFPlatformUtils.METAMODEL_LOCATION_KEY, metamodelPath);
        this.emfPlatform = new EMFPlatform(xatkitCore, configuration);
        Resource metamodelResource = this.emfPlatform.getMetamodelResource();
        assertThat(metamodelResource.getContents()).as("Metamodel resource content is not empty").isNotEmpty();
        assertThat(metamodelResource.getContents().get(0)).as("Metamodel resource contains a top-level EPackage")
                .isInstanceOf(EPackage.class);
        EPackage ePackage = (EPackage) metamodelResource.getContents().get(0);
        assertThat(ePackage.getNsURI()).as("Valid EPackage nsURI").isEqualTo("projectManager");
    }

    @Test(expected = NullPointerException.class)
    public void getModelResourceNullResource() {
        this.emfPlatform = getValidEMFPlatform();
        emfPlatform.getModelResource(null);
    }

    @Test(expected = XatkitException.class)
    public void getModelResourceFileDoesNotExist() {
        this.emfPlatform = getValidEMFPlatform();
        emfPlatform.getModelResource("invalid");
    }

    @Test
    public void getModelResourceExistingModelFile() {
        this.emfPlatform = getValidEMFPlatform();
        Resource modelResource = emfPlatform.getModelResource(getModelPath());
        assertThat(modelResource).as("Model resource is not null").isNotNull();
        assertThat(modelResource.getContents()).as("Model resource contents is not empty").isNotEmpty();
        EObject topLevelElement = modelResource.getContents().get(0);
        assertThat(topLevelElement.eClass().getName()).as("Top-level element is an instance of Project").isEqualTo(
                "Project");
    }

    private EMFPlatform getValidEMFPlatform() {
        this.emfPlatform = new EMFPlatform(xatkitCore, buildConfiguration());
        return emfPlatform;
    }

    public static Configuration buildConfiguration() {
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty(EMFPlatformUtils.METAMODEL_LOCATION_KEY, getMetamodelPath());
        return configuration;
    }

    public static String getMetamodelPath() {
        return getAbsolutePath("Project.ecore");
    }

    public static String getModelPath() {
        return getAbsolutePath("Project.xmi");
    }

    private static String getAbsolutePath(String path) {
        URL url = EMFPlatformTest.class.getClassLoader().getResource(path);
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        return file.getAbsolutePath();
    }

}
