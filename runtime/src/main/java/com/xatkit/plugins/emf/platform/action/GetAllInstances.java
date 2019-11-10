package com.xatkit.plugins.emf.platform.action;

import com.xatkit.core.XatkitException;
import com.xatkit.core.platform.action.RuntimeAction;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.emf.EMFUtils;
import com.xatkit.plugins.emf.platform.EMFPlatform;
import com.xatkit.plugins.emf.util.EMFResourceUtils;
import fr.inria.atlanmod.commons.log.Log;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.text.MessageFormat;
import java.util.List;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * A {@link RuntimeAction} that returns all the instances of the provided {@code clazzName} in the session
 * {@link Resource}.
 * <p>
 * This action manipulates the {@link Resource} stored in the {@link XatkitSession} using the
 * {@link EMFUtils#MODEL_SESSION_KEY} key, and will throw an exception if no model has been loaded. See
 * {@link LoadModel} action to load a model with a given path.
 *
 * @see LoadModel
 */
public class GetAllInstances extends RuntimeAction<EMFPlatform> {

    /**
     * The name of the {@link EClass} to retrieve the instances of.
     */
    private String clazzName;

    /**
     * Constructs a new {@link GetAllInstances} action from the provided {@code emfPlatform}, {@code session}, and
     * {@code clazzName}.
     * <p>
     * The provided {@code clazzName} should refer to an existing {@link EClass}'s name in the metamodel associated
     * to the platform. If this is not the case an exception will be thrown when calling the {@link #compute()} method.
     *
     * @param emfPlatform the {@link EMFPlatform} containing this action
     * @param session     the {@link XatkitSession} associated to this action
     * @param clazzName   the name of the {@link EClass} to retrieve the instances of
     * @throws IllegalArgumentException if the provided {@code clazzName} is {@code null} or {@code empty}
     */
    public GetAllInstances(EMFPlatform emfPlatform, XatkitSession session, String clazzName) {
        super(emfPlatform, session);
        checkArgument(nonNull(clazzName) && !clazzName.isEmpty(), "Cannot construct %s: the provided class name is " +
                "not valid (name=%s)", this.getClass().getSimpleName(), clazzName);
        this.clazzName = clazzName;
        /*
         * Do not check here that the session contains the model resource, it may not be the case if the LoadModel
         * action hasn't been computed yet.
         */
    }

    /**
     * Returns all the instances of the provided {@code clazzName} in the session {@link Resource} model.
     * <p>
     * This method accesses the {@link Resource} stored in the session with the {@link EMFUtils#MODEL_SESSION_KEY}
     * key. If there is no {@link Resource} in the session a {@link NullPointerException} is thrown. See
     * {@link LoadModel} to load a model from a given path.
     * <p>
     * This method relies on the {@link EMFResourceUtils} utility class to retrieve the {@link EClass} corresponding
     * to the provided {@code clazzName} in the platform's metamodel, and to retrieve the instances of this
     * {@link EClass}.
     *
     * @return a {@link List} of {@link EObject} that are instances of the {@link EClass} associated to the
     * provided {@code clazzName}
     * @throws NullPointerException if the {@link XatkitSession} does not contain a model {@link Resource}
     * @throws XatkitException      if the metamodel associated to the platform does not contain an {@link EClass}
     *                              with a name matching the provided {@code clazzName}
     * @see LoadModel
     * @see EMFResourceUtils
     */
    @Override
    protected Object compute() {
        Resource modelResource = (Resource) this.session.get(EMFUtils.MODEL_SESSION_KEY);
        checkNotNull(modelResource, "Cannot compute %s, cannot find the model from the %s (session key=%s)",
                this.getClass().getSimpleName(), XatkitSession.class.getSimpleName(), EMFUtils.MODEL_SESSION_KEY);
        Resource metamodelResource = this.runtimePlatform.getMetamodelResource();
        EClass eClass = EMFResourceUtils.getEClassWithName(metamodelResource, clazzName);
        if (isNull(eClass)) {
            throw new XatkitException(MessageFormat.format("Cannot find the EClass with the provided name {0} in the" +
                    " resource {1}", clazzName, metamodelResource.getURI().toString()));
        }
        List<EObject> allInstances = EMFResourceUtils.getAllInstancesOfType(modelResource, eClass);
        Log.info("Found {0} instances of {1}", allInstances.size(), eClass.getName());
        return allInstances;
    }
}
