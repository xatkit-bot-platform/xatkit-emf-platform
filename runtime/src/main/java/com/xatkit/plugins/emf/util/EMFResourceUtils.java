package com.xatkit.plugins.emf.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utility methods easing the access and manipulation of EMF resource.
 */
public class EMFResourceUtils {

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

    public static List<EObject> getAllInstancesOfType(Resource modelResource, EClass eClass) {
        Iterable<EObject> iterableContent = modelResource::getAllContents;
        return StreamSupport.stream(iterableContent.spliterator(), false).filter(eClass::isInstance).collect(Collectors.toList());
    }
}
