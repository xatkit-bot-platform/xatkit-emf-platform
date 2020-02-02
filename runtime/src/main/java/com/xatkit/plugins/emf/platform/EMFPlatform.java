package com.xatkit.plugins.emf.platform;

import com.xatkit.core.XatkitCore;
import com.xatkit.core.XatkitException;
import com.xatkit.core.platform.RuntimePlatform;
import com.xatkit.plugins.emf.EMFPlatformUtils;
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

/**
 * A {@link RuntimePlatform} class that loads and manipulates EMF (meta) models.
 * <p>
 * This platform is initialized with a metamodel, and provides utility methods to load instances of this metamodel as
 * EMF {@link Resource}s.
 */
public class EMFPlatform extends RuntimePlatform {

    /**
     * The {@link ResourceSet} used to load the metamodel as well as its concrete instances.
     * <p>
     * The {@link ResourceSet} is configured to laod {@code .ecore} and {@code .xmi} files.
     */
    private ResourceSet rSet;

    /**
     * The EMF {@link Resource} containing the metamodel associated to the {@link EMFPlatform}.
     */
    private Resource metamodelResource;

    /**
     * Constructs an {@link EMFPlatform} with the provided {@code xatkitCore} and {@code configuration}.
     * <p>
     * The provided {@code configuration} must contain a valid metamodel file location associated to the
     * {@link EMFPlatformUtils#METAMODEL_LOCATION_KEY} key.
     *
     * @param xatkitCore    the {@link XatkitCore} instance associated to the platform
     * @param configuration the {@link Configuration} used to initialize the platform
     * @throws IllegalArgumentException if the provided {@code configuration} does not contain a valid metamodel file
     *                                  location, or if the provided file does not exist.
     * @see EMFPlatformUtils#METAMODEL_LOCATION_KEY
     */
    public EMFPlatform(XatkitCore xatkitCore, Configuration configuration) {
        super(xatkitCore, configuration);
        String metamodelLocation = configuration.getString(EMFPlatformUtils.METAMODEL_LOCATION_KEY);
        checkArgument(nonNull(metamodelLocation) && !metamodelLocation.isEmpty(), "Cannot construct the %s: cannot " +
                        "find a valid metamodel location in the provided configuration (configuration key: %s)",
                this.getClass().getSimpleName(), EMFPlatformUtils.METAMODEL_LOCATION_KEY);
        File metamodelFile = FileUtils.getFile(metamodelLocation, configuration);
        checkArgument(metamodelFile.exists(), "Cannot construct the %s: the provided metamodel file does not exist " +
                "(path=%s)", this.getClass().getSimpleName(), metamodelFile.getAbsolutePath());
        this.rSet = initializeResourceSet();
        this.metamodelResource = loadMetamodelResource(metamodelFile);
    }

    /**
     * Returns the EMF {@link Resource} containing the metamodel associated to the platform.
     *
     * @return the EMF {@link Resource} containing the metamodel associated to the platform
     */
    public Resource getMetamodelResource() {
        return this.metamodelResource;
    }

    /**
     * Initializes the underlying {@link ResourceSet} and registers its {@link Resource} factories.
     * <p>
     * The created {@link ResourceSet} handles {@code .ecore} and {@code .xmi} files.
     *
     * @return the initialized {@link ResourceSet}
     */
    private ResourceSet initializeResourceSet() {
        ResourceSet rSet = new ResourceSetImpl();
        rSet.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
        rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
        rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        return rSet;
    }

    /**
     * Loads the metamodel {@link Resource} associated to the provided {@code metamodelFile}.
     *
     * @param metamodelFile the {@link File} containing the metamodel to load
     * @return the EMF {@link Resource} containing the loaded metamodel
     * @throws NullPointerException     if the provided {@code metamodelFile} is {@code null}
     * @throws IllegalArgumentException if the provided {@code metamodelFile} does not exist
     */
    private Resource loadMetamodelResource(File metamodelFile) {
        checkNotNull(metamodelFile, "Cannot load the metamodel from the provided file: %s", metamodelFile);
        checkArgument(metamodelFile.exists(), "Cannot load the metamodel from the provided file: %s, the file does " +
                "not exist", metamodelFile.getAbsolutePath());
        String absolutePath = metamodelFile.getAbsolutePath();
        URI metamodelURI = URI.createFileURI(absolutePath);
        return rSet.getResource(metamodelURI, true);
    }

    /**
     * Loads the {@link Resource} associated to the provided {@code modelPath}.
     * <p>
     * The provided path should point to a file containing an instance of the metamodel associated to this platform.
     *
     * @param modelPath the path of the model to load
     * @return the EMF {@link Resource} containing the loaded model
     * @throws NullPointerException if the provided {@code modelPath} is {@code null}
     * @throws XatkitException      if the provided {@code modelPath} does not correspond to a valid file location
     */
    public Resource getModelResource(String modelPath) {
        checkNotNull(modelPath, "Cannot load the model from the provided path: %s", modelPath);
        File modelFile = FileUtils.getFile(modelPath, configuration);
        if (!modelFile.exists()) {
            Log.warn("Cannot locate the file {0}, trying to resolve it in the classpath", modelPath);
            URL modelURL = this.getClass().getClassLoader().getResource(modelPath);
            if (nonNull(modelURL)) {
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
