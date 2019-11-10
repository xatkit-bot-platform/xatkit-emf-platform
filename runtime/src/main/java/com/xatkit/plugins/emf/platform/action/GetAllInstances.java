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

public class GetAllInstances extends RuntimeAction<EMFPlatform> {

    private String clazzName;

    public GetAllInstances(EMFPlatform emfPlatform, XatkitSession session, String clazzName) {
        super(emfPlatform, session);
        checkArgument(nonNull(clazzName) && !clazzName.isEmpty(), "Cannot construct %s: the provided class name is " +
                "not valid (name=%s)", this.getClass().getSimpleName(), clazzName);
        this.clazzName = clazzName;
    }

    @Override
    protected Object compute() {
        Resource modelResource = (Resource) this.session.get(EMFUtils.MODEL_SESSION_KEY);
        checkNotNull(modelResource, "Cannot compute %s, cannot find the model from the %s (session key=%s)",
                this.getClass().getSimpleName(), XatkitSession.class.getSimpleName(), EMFUtils.MODEL_SESSION_KEY);
        Resource metamodelResource = this.runtimePlatform.getMetamodelResource();
        EClass eClass = EMFResourceUtils.getEClassWithName(metamodelResource, clazzName);
        if(isNull(eClass)) {
            throw new XatkitException(MessageFormat.format("Cannot find the EClass with the provided name {0} in the" +
                    " resource {1}", clazzName, metamodelResource.getURI().toString()));
        }
        List<EObject> allInstances = EMFResourceUtils.getAllInstancesOfType(modelResource, eClass);
        Log.info("Found {0} instances of {1}", allInstances.size(), eClass.getName());
        return allInstances;
    }
}
