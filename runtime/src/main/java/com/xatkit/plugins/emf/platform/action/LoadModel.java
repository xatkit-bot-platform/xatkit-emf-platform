package com.xatkit.plugins.emf.platform.action;

import com.xatkit.core.XatkitException;
import com.xatkit.core.platform.action.RuntimeAction;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.emf.EMFPlatformUtils;
import com.xatkit.plugins.emf.platform.EMFPlatform;
import org.eclipse.emf.ecore.resource.Resource;

import java.text.MessageFormat;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

/**
 * A {@link RuntimeAction} that loads a given model and registers it in the current {@link XatkitSession}.
 * <p>
 * The loaded EMF {@link Resource} is stored in the {@link XatkitSession} using the
 * {@link EMFPlatformUtils#MODEL_SESSION_KEY} key, easing its access by other EMF-related actions.
 */
public class LoadModel extends RuntimeAction<EMFPlatform> {

    /**
     * The path of the model to load.
     */
    private String modelPath;

    /**
     * Constructs a new {@link LoadModel} action from the provided {@code emfPlatform}, {@code session}, and {@code
     * modelPath}.
     * <p>
     * The provided {@code modelPath} should point to a file containing an instance of the metamodel associated to
     * the {@link EMFPlatform}.
     *
     * @param emfPlatform the {@link EMFPlatform} containing this action
     * @param session     the {@link XatkitSession} associated to this action
     * @param modelPath   the path of the model to load
     * @throws IllegalArgumentException if the provided {@code modelPath} is {@code null} or {@code empty}
     */
    public LoadModel(EMFPlatform emfPlatform, XatkitSession session, String modelPath) {
        super(emfPlatform, session);
        checkArgument(nonNull(modelPath) && !modelPath.isEmpty(), "Cannot construct %s: the provided model path is " +
                "not valid (path=%s)", this.getClass().getSimpleName(), modelPath);
        this.modelPath = modelPath;
    }

    /**
     * Loads the EMF {@link Resource} at the given {@code modelPath} and returns it.
     * <p>
     * This method stores the loaded {@link Resource} in the {@link XatkitSession} using the
     * {@link EMFPlatformUtils#MODEL_SESSION_KEY} key, easing its access by other EMF-related actions.
     *
     * @return the loaded {@link Resource}
     * @throws XatkitException if the provided {@code modelPath} does not correspond to a valid file location
     */
    @Override
    protected Object compute() {
        Resource resource = this.runtimePlatform.getModelResource(modelPath);
        if (nonNull(resource)) {
            this.session.store(EMFPlatformUtils.MODEL_SESSION_KEY, resource);
        } else {
            throw new XatkitException(MessageFormat.format("Cannot load the model at the given path: {0}", modelPath));
        }
        return resource;
    }
}
