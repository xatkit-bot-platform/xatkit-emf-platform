package com.xatkit.plugins.emf.platform.action;

import com.xatkit.core.platform.action.RuntimeAction;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.emf.EMFUtils;
import com.xatkit.plugins.emf.platform.EMFPlatform;
import org.eclipse.emf.ecore.resource.Resource;

import java.text.MessageFormat;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

public class LoadModel extends RuntimeAction<EMFPlatform> {

    private String modelPath;

    public LoadModel(EMFPlatform emfPlatform, XatkitSession session, String modelPath) {
        super(emfPlatform, session);
        checkArgument(nonNull(modelPath) && !modelPath.isEmpty(), "Cannot construct %s: the provided model path is " +
                "not valid (path=%s)", this.getClass().getSimpleName(), modelPath);
        this.modelPath = modelPath;
    }

    @Override
    protected Object compute() {
        Resource resource = this.runtimePlatform.getModelResource(modelPath);
        if (nonNull(resource)) {
            this.session.store(EMFUtils.MODEL_SESSION_KEY, resource);
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot load the model at the given path: {0}", modelPath));
        }
        return resource;
    }
}
