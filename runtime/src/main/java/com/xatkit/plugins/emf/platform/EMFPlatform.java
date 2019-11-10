package com.xatkit.plugins.emf.platform;

import com.xatkit.core.XatkitCore;
import com.xatkit.core.XatkitException;
import com.xatkit.core.platform.RuntimePlatform;
import com.xatkit.plugins.emf.EMFUtils;
import com.xatkit.util.FileUtils;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;
import static java.util.Objects.nonNull;

public class EMFPlatform extends RuntimePlatform {

    private ResourceSet rSet;

    private Resource metamodelResource;

    public EMFPlatform(XatkitCore xatkitCore, Configuration configuration) {
        super(xatkitCore, configuration);
        String metamodelLocation = configuration.getString(EMFUtils.METAMODEL_LOCATION_KEY);
        checkArgument(nonNull(metamodelLocation) && !metamodelLocation.isEmpty(), "Cannot construct the %s: cannot " +
                "find a valid metamodel location in the provided configuration (configuration key: %s)",
                this.getClass().getSimpleName(), EMFUtils.METAMODEL_LOCATION_KEY);
        File metamodelFile = FileUtils.getFile(metamodelLocation, configuration);
        checkArgument(metamodelFile.exists(), "Cannot construct the %s: the provided metamodel file does not exist " +
                "(path=%s)", this.getClass().getSimpleName(), metamodelFile.getAbsolutePath());
        this.rSet = initializeResourceSet();
        this.metamodelResource = loadMetamodelResource(metamodelFile);
    }

    public Resource getMetamodelResource() {
        return this.metamodelResource;
    }

    private ResourceSet initializeResourceSet() {
        ResourceSet rSet = new ResourceSetImpl();
        rSet.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
        rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
        rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        return rSet;
    }

    private Resource loadMetamodelResource(File metamodelFile) {
        checkNotNull(metamodelFile, "Cannot load the metamodel from the provided file: %s", metamodelFile);
        checkArgument(metamodelFile.exists(), "Cannot load the metamodel from the provided file: %s, the file does " +
                "not exist", metamodelFile.getAbsolutePath());
        String absolutePath = metamodelFile.getAbsolutePath();
        URI metamodelURI = URI.createFileURI(absolutePath);
        return rSet.getResource(metamodelURI, true);
    }

    public Resource getModelResource(String modelPath) {
        checkNotNull(modelPath, "Cannot load the model from the provided path: %s", modelPath);
        File modelFile = FileUtils.getFile(modelPath, configuration);
        if(!modelFile.exists()) {
            Log.warn("Cannot locate the file {0}, trying to resolve it in the classpath", modelPath);
            URL modelURL = this.getClass().getClassLoader().getResource(modelPath);
            if(nonNull(modelURL)) {
                try {
                    modelFile = new File(modelURL.toURI());
                } catch (URISyntaxException e) {
                    modelFile = new File(modelURL.getPath());
                }
            } else {
                throw new XatkitException(MessageFormat.format("Cannot load the model from the provided path: ",
                        modelPath));
            }
        }
        URI modelURI = URI.createFileURI(modelFile.getAbsolutePath());
        return this.rSet.getResource(modelURI, true);
    }
}
