package com.xatkit.plugins.emf.platform.action;

import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.emf.platform.EMFPlatformTest;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GetAllInstancesSelectTest extends AbstractEMFActionTest {

    private static String VALID_ECLASS_NAME = "Project";

    private static Map<String, Object> DEFAULT_QUERY_MAP = new HashMap<>();

    private GetAllInstancesSelect action;

    @Test(expected = NullPointerException.class)
    public void constructNullEMFPlatform() {
        action = new GetAllInstancesSelect(null, createValidXatkitSession(), VALID_ECLASS_NAME, DEFAULT_QUERY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void constructNullXatkitSession() {
        action = new GetAllInstancesSelect(emfPlatform, null, VALID_ECLASS_NAME, DEFAULT_QUERY_MAP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructNullClassName() {
        action = new GetAllInstancesSelect(emfPlatform, createValidXatkitSession(), null, DEFAULT_QUERY_MAP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructEmptyClassName() {
        action = new GetAllInstancesSelect(emfPlatform, createValidXatkitSession(), "", DEFAULT_QUERY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void constructNullQueryMap() {
        action = new GetAllInstancesSelect(emfPlatform, createValidXatkitSession(), VALID_ECLASS_NAME, null);
    }

    @Test
    public void constructValidClassNameValidQueryMap() {
        action = new GetAllInstancesSelect(emfPlatform, createValidXatkitSession(), VALID_ECLASS_NAME,
                DEFAULT_QUERY_MAP);
        assertThat(action).as("Action correctly created").isNotNull();
    }

    @Test
    public void computeStringConditionStartsWith1Result() {
        XatkitSession session = this.loadModel();
        Map<String, Object> queryMap = new HashMap<>();
        setStringComparison(queryMap, true, "name", "starts with", "Project");
        String eClassName = "Project";
        action = new GetAllInstancesSelect(emfPlatform, session, eClassName, queryMap);
        Object result = action.compute();
        assertThat(result).as("Result is a list").isInstanceOf(List.class);
        List<EObject> listResult = (List<EObject>) result;
        assertThat(listResult).as("Result contains a single element").hasSize(1);
        EObject eObjectResult = listResult.get(0);
        assertThat(eObjectResult.eClass().getName()).as("Valid result EObject").isEqualTo("Project");
    }

    @Test
    public void computeStringConditionStartsWith0Result() {
        XatkitSession session = this.loadModel();
        Map<String, Object> queryMap = new HashMap<>();
        setStringComparison(queryMap, true, "name", "starts with", "ERROR");
        String eClassName = "Project";
        action = new GetAllInstancesSelect(emfPlatform, session, eClassName, queryMap);
        Object result = action.compute();
        assertThat(result).as("Result is a list").isInstanceOf(List.class);
        List<EObject> listResult = (List<EObject>) result;
        assertThat(listResult).as("Result contains 0 element").isEmpty();
    }

    @Test
    public void computeStringConditionEndsWith1Result() {
        XatkitSession session = this.loadModel();
        Map<String, Object> queryMap = new HashMap<>();
        setStringComparison(queryMap, true, "name", "ends with", "Test");
        String eClassName = "Project";
        action = new GetAllInstancesSelect(emfPlatform, session, eClassName, queryMap);
        Object result = action.compute();
        assertThat(result).as("Result is a list").isInstanceOf(List.class);
        List<EObject> listResult = (List<EObject>) result;
        assertThat(listResult).as("Result contains a single element").hasSize(1);
        EObject eObjectResult = listResult.get(0);
        assertThat(eObjectResult.eClass().getName()).as("Valid result EObject").isEqualTo("Project");
    }

    @Test
    public void computeStringConditionEndsWith0Result() {
        XatkitSession session = this.loadModel();
        Map<String, Object> queryMap = new HashMap<>();
        setStringComparison(queryMap, true, "name", "ends with", "ERROR");
        String eClassName = "Project";
        action = new GetAllInstancesSelect(emfPlatform, session, eClassName, queryMap);
        Object result = action.compute();
        assertThat(result).as("Result is a list").isInstanceOf(List.class);
        List<EObject> listResult = (List<EObject>) result;
        assertThat(listResult).as("Result contains 0 element").isEmpty();
    }

    /*
     * TODO add test for equals and contains comparators
     */

    @Test
    public void computeNumericalConditionGreaterThan2Result() {
        XatkitSession session = this.loadModel();
        Map<String, Object> queryMap = new HashMap<>();
        setNumericalComparison(queryMap, true, "days", "greater than", "-1");
        String eClassName = "Task";
        action = new GetAllInstancesSelect(emfPlatform, session, eClassName, queryMap);
        Object result = action.compute();
        assertThat(result).as("Result is a list").isInstanceOf(List.class);
        List<EObject> listResult = (List<EObject>) result;
        assertThat(listResult).as("Result contains 2 element").hasSize(2);
    }

    @Test
    public void computeNumericalConditionGreaterThan1Result() {
        XatkitSession session = this.loadModel();
        Map<String, Object> queryMap = new HashMap<>();
        setNumericalComparison(queryMap, true, "days", "greater than", "2");
        String eClassName = "Task";
        action = new GetAllInstancesSelect(emfPlatform, session, eClassName, queryMap);
        Object result = action.compute();
        assertThat(result).as("Result is a list").isInstanceOf(List.class);
        List<EObject> listResult = (List<EObject>) result;
        assertThat(listResult).as("Result contains 1 element").hasSize(1);
    }

    @Test
    public void computeStringStartsWithAndNumericalGreaterThan1Result() {
        XatkitSession session = this.loadModel();
        Map<String, Object> queryMap = new HashMap<>();
        /*
         * Returns the 2 tasks
         */
        setStringComparison(queryMap, true, "description", "starts with", "this is the");
        /*
         * Returns only 1 of the 2 tasks
         */
        setNumericalComparison(queryMap, false, "days", "greater than", "3");
        queryMap.put("conditionComposition", "and");
        String eClassName = "Task";
        action = new GetAllInstancesSelect(emfPlatform, session, eClassName, queryMap);
        Object result = action.compute();
        assertThat(result).as("Result is a list").isInstanceOf(List.class);
        List<EObject> listResult = (List<EObject>) result;
        assertThat(listResult).as("Result contains 1 element").hasSize(1);
    }

    @Test
    public void computeStringStartsWithOrNumericalGreaterThan1Result() {
        XatkitSession session = this.loadModel();
        Map<String, Object> queryMap = new HashMap<>();
        /*
         * Returns the 2 tasks
         */
        setStringComparison(queryMap, true, "description", "starts with", "this is the");
        /*
         * This shouldn't change the result, the first predicate matches all the tasks
         */
        setNumericalComparison(queryMap, false, "days", "greater than", "3");
        queryMap.put("conditionComposition", "or");
        String eClassName = "Task";
        action = new GetAllInstancesSelect(emfPlatform, session, eClassName, queryMap);
        Object result = action.compute();
        assertThat(result).as("Result is a list").isInstanceOf(List.class);
        List<EObject> listResult = (List<EObject>) result;
        assertThat(listResult).as("Result contains 2 elements").hasSize(2);
    }

    private void setStringComparison(Map<String, Object> from, boolean isCondition1,
                                                       String stringAttribute, String stringComparator,
                                                       String stringValue) {
        String fromConditionKey = isCondition1 ? "condition1" : "condition2";
        String fromStringValueKey = isCondition1 ? "stringValue1" : "stringValue2";
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("StringAttribute", stringAttribute);
        conditionMap.put("StringComparator", stringComparator);
        from.put(fromConditionKey, conditionMap);
        from.put(fromStringValueKey, stringValue);
    }

    private void setNumericalComparison(Map<String, Object> from, boolean isCondition1, String numericalAttribute,
                                        String numericalComparator, String numericalValue) {
        String fromConditionKey = isCondition1 ? "condition1" : "condition2";
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("NumericalAttribute", numericalAttribute);
        conditionMap.put("NumericalComparator", numericalComparator);
        conditionMap.put("number", numericalValue);
        from.put(fromConditionKey, conditionMap);
    }

//    @Test(expected = NullPointerException.class)
//    public void computeValidClassNameNotLoadedModel() {
//        action = new GetAllInstances(emfPlatform, createValidXatkitSession(), VALID_ECLASS_NAME);
//        action.compute();
//    }
//
//    @Test
//    public void computeValidClassNameLoadedModel() {
//        XatkitSession session = this.loadModel();
//        action = new GetAllInstances(emfPlatform, session, VALID_ECLASS_NAME);
//        Object result = action.compute();
//        assertThat(result).as("Result is not null").isNotNull();
//        assertThat(result).as("Result is a List").isInstanceOf(List.class);
//        List<EObject> allInstances = (List<EObject>) result;
//        for (EObject e : allInstances) {
//            assertThat(e.eClass().getName()).as("Returned element has the correct type").isEqualTo(VALID_ECLASS_NAME);
//        }
//    }
//
//    @Test(expected = XatkitException.class)
//    public void computeInvalidClassNameLoadedModel() {
//        XatkitSession session = loadModel();
//        action = new GetAllInstances(emfPlatform, session, "INVALID");
//        action.compute();
//    }

    /**
     * Loads the test metamodel using {@link LoadModel} action and returns the updated {@link XatkitSession}.
     *
     * @return the updated {@link XatkitSession}
     */
    private XatkitSession loadModel() {
        XatkitSession session = createValidXatkitSession();
        LoadModel loadModelAction = new LoadModel(emfPlatform, session, EMFPlatformTest.getModelPath());
        loadModelAction.compute();
        return session;
    }
}
