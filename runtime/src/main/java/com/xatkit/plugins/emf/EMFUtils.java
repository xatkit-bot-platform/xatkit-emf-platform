package com.xatkit.plugins.emf;

/**
 * An utility interface that holds {@link com.xatkit.plugins.emf.platform.EMFPlatform}-related constants.
 */
public interface EMFUtils {

    /**
     * The Xatkit {@link org.apache.commons.configuration2.Configuration} key used to set the location of the
     * metamodel file to import when starting the platform.
     */
    String METAMODEL_LOCATION_KEY = "xatkit.emf.metamodel.location";

    /**
     * The {@link com.xatkit.core.session.XatkitSession} key used to store the loaded model.
     *
     * @see com.xatkit.plugins.emf.platform.action.LoadModel
     */
    String MODEL_SESSION_KEY = "com.xatkit.emf.runtime.model";

}