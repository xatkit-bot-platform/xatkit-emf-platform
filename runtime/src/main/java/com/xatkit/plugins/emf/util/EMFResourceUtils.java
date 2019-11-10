package com.xatkit.plugins.emf.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Provides utility methods easing the access and manipulation of EMF resource.
 */
public class EMFResourceUtils {

    /**
     * Retrieves the {@link EClass} matching the provided {@code eClassName} in the given {@code metamodelResource}.
     * <p>
     * This method returns the first {@link EClass} that matches the following equality: {@code eClass.getName()
     * .equals(eClassName)}. Note that multiple {@link EClass}es with the same name are not supported, as the method
     * may return one or the other.
     * <p>
     * <b>Note</b>: this method assumes that the provided {@code metamodelResource} contains an Ecore metamodel.
     *
     * @param metamodelResource the EMF {@link Resource} containing the metamodel to retrieve the {@link EClass} from
     * @param eClassName        the name of the {@link EClass} to retrieve
     * @return the retrieved {@link EClass} if it exists, {@code null} otherwise
     */
    public static EClass getEClassWithName(Resource metamodelResource, String eClassName) {
        Iterable<EObject> iterableContent = metamodelResource::getAllContents;
        Optional<EObject> result = StreamSupport.stream(iterableContent.spliterator(), false).filter(element -> {
            if (element instanceof EClass) {
                EClass eClass = (EClass) element;
                return eClass.getName().equals(eClassName);
            }
            return false;
        }).findAny();
        return (EClass) result.orElse(null);
    }

    /**
     * Retrieves all the instances of the provided {@code eClass} in the given {@code modelResource}.
     * <p>
     * This method checks the contained {@link EObject}s of the provided {@code modelResource} using
     * {@link EClass#isInstance(Object)}.
     *
     * @param modelResource the EMF {@link Resource} containing the model to retrieve the instances from
     * @param eClass        the {@link EClass} to retrieve the instances of
     * @return the {@link List} of instances of the provided {@code eClass}
     */
    public static List<EObject> getAllInstancesOfType(Resource modelResource, EClass eClass) {
        Iterable<EObject> iterableContent = modelResource::getAllContents;
        return StreamSupport.stream(iterableContent.spliterator(), false).filter(eClass::isInstance).collect(Collectors.toList());
    }
}
