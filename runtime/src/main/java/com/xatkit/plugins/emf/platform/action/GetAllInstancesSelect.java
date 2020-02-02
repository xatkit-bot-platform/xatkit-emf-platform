package com.xatkit.plugins.emf.platform.action;

import com.xatkit.core.XatkitException;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.emf.platform.EMFPlatform;
import com.xatkit.plugins.emf.util.EMFResourceUtils;
import fr.inria.atlanmod.commons.log.Log;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;
import static java.util.Objects.isNull;

public class GetAllInstancesSelect extends GetAllInstances {

    private Map<String, Object> query;

    public GetAllInstancesSelect(EMFPlatform emfPlatform, XatkitSession session, String clazzName, Map<String,
            Object> query) {
        super(emfPlatform, session, clazzName);
        checkNotNull(query, "Cannot construct %s: the provided query Map is not valid (query=%s)",
                this.getClass().getSimpleName(), query);
        this.query = query;
    }

    @Override
    protected Object compute() {
        System.out.println("test0");
        List<EObject> allInstances = (List<EObject>) super.compute();
        Predicate<EObject> p1 = null;
        Predicate<EObject> p2 = null;
        p1 = getPredicate(query, "condition1");
        p2 = getPredicate(query, "condition2");
        Predicate<EObject> pGlobal = applyConditionComposition(query, p1, p2);
        return allInstances.stream().filter(pGlobal).collect(Collectors.toList());
    }

    private @Nullable Predicate<EObject> getPredicate(Map<String, Object> query, String conditionField) {
        checkArgument(conditionField.equals("condition1") || conditionField.equals("condition2"), "Cannot create a " +
                "predicate for the condition field %s, expected 'condition1' or 'condition2'", conditionField);
        Object condition = query.get(conditionField);
        if (condition instanceof Map) {
            Map<String, Object> mapCondition1 = (Map<String, Object>) query.get(conditionField);
            if (mapCondition1.containsKey("StringAttribute")) {
                String stringAttribute = (String) mapCondition1.get("StringAttribute");
                EAttribute eAttribute = EMFResourceUtils.getEAttribute(this.runtimePlatform.getMetamodelResource(),
                        clazzName, stringAttribute);
                String stringComparator = (String) mapCondition1.get("StringComparator");
                String stringValueField = conditionField.equals("condition1") ? "stringValue1" : "stringValue2";
                String stringValue = (String) query.get(stringValueField);
                return getStringComparisonPredicate(eAttribute, stringComparator, stringValue);
            } else if(mapCondition1.containsKey("NumericalAttribute")) {
                String numericalAttribute = (String) mapCondition1.get("NumericalAttribute");
                EAttribute eAttribute = EMFResourceUtils.getEAttribute(this.runtimePlatform.getMetamodelResource(),
                        clazzName, numericalAttribute);
                String numericalComparator = (String) mapCondition1.get("NumericalComparator");
                String stringNumber = (String) mapCondition1.get("number");
                Double number = new Double(stringNumber);
                return getNumericalComparisonPredicate(eAttribute, numericalComparator, number);
            } else {
                throw new XatkitException("Unsupported condition type, expecting StringComparator or " +
                        "NumericalComparator");
            }
        } else {
            return null;
        }
    }

    private Predicate<EObject> getStringComparisonPredicate(EAttribute eAttribute, String stringComparator,
                                                            String value) {
        switch (stringComparator) {
            case "starts with":
                return eObject -> {
                    String eObjectValue = (String) eObject.eGet(eAttribute);
                    return eObjectValue.startsWith(value);
                };
            case "ends with":
                return eObject -> {
                    String eObjectValue = (String) eObject.eGet(eAttribute);
                    return eObjectValue.endsWith(value);
                };
            case "contains":
                return eObject -> {
                    String eObjectValue = (String) eObject.eGet(eAttribute);
                    return eObjectValue.contains(value);
                };
            case "equals":
                return eObject -> {
                    String eObjectValue = (String) eObject.eGet(eAttribute);
                    return eObjectValue.equals(value);
                };
            default:
                throw new XatkitException(MessageFormat.format("Cannot construct a String comparison predicate from " +
                        "the provided comparator {0}", stringComparator));
        }
    }

    private Predicate<EObject> getNumericalComparisonPredicate(EAttribute eAttribute, String numericalComparator,
                                                               Double value) {
        switch (numericalComparator) {
            case "greater than":
                return eObject -> {
                    Number eObjectValue = (Number) eObject.eGet(eAttribute);
                    return eObjectValue.doubleValue() > value;
                };
            case "lower than":
                return eObject -> {
                    Number eObjectValue = (Number) eObject.eGet(eAttribute);
                    return eObjectValue.doubleValue() < value;
                };
            case "equals":
                return eObject -> {
                    Number eObjectValue = (Number) eObject.eGet(eAttribute);
                    return eObjectValue.doubleValue() == value;
                };
            default:
                throw new XatkitException(MessageFormat.format("Cannot construct a Numerical comparison predicate " +
                        "from the provided comparator {0}", numericalComparator));
        }
    }

    private Predicate<EObject> applyConditionComposition(Map<String, Object> query, Predicate<EObject> p1,
                                                         Predicate<EObject> p2) {
        if(isNull(p2)) {
            return p1;
        }
        Object conditionComposition = query.get("conditionComposition");
        if(isNull(conditionComposition)) {
            Log.warn("No condition composition found, returning p1");
            return p1;
        }
        String stringConditionComposition = (String) conditionComposition;
        switch (stringConditionComposition) {
            case "and":
                return p1.and(p2);
            case "or":
                return p1.or(p2);
            default:
                throw new XatkitException(MessageFormat.format("Cannot apply the condition composition {0}, expecting" +
                        " 'and' or 'or'", stringConditionComposition));
        }
    }
}
