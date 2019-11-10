package com.xatkit.plugins.emf.platform.action;

import com.xatkit.core.XatkitException;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.emf.platform.EMFPlatformTest;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetAllInstancesTest extends AbstractEMFActionTest {

    private static String VALID_ECLASS_NAME = "Project";

    private GetAllInstances action;

    @Test(expected = NullPointerException.class)
    public void constructNullEMFPlatform() {
        action = new GetAllInstances(null, createValidXatkitSession(), VALID_ECLASS_NAME);
    }

    @Test(expected = NullPointerException.class)
    public void constructNullXatkitSession() {
        action = new GetAllInstances(emfPlatform, null, VALID_ECLASS_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructNullClassName() {
        action = new GetAllInstances(emfPlatform, createValidXatkitSession(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructEmptyClassName() {
        action = new GetAllInstances(emfPlatform, createValidXatkitSession(), "");
    }

    @Test
    public void constructValidClassName() {
        action = new GetAllInstances(emfPlatform, createValidXatkitSession(), VALID_ECLASS_NAME);
        assertThat(action).as("Action correctly created").isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void computeValidClassNameNotLoadedModel() {
        action = new GetAllInstances(emfPlatform, createValidXatkitSession(), VALID_ECLASS_NAME);
        action.compute();
    }

    @Test
    public void computeValidClassNameLoadedModel() {
        XatkitSession session = this.loadModel();
        action = new GetAllInstances(emfPlatform, session, VALID_ECLASS_NAME);
        Object result = action.compute();
        assertThat(result).as("Result is not null").isNotNull();
        assertThat(result).as("Result is a List").isInstanceOf(List.class);
        List<EObject> allInstances = (List<EObject>) result;
        for (EObject e : allInstances) {
            assertThat(e.eClass().getName()).as("Returned element has the correct type").isEqualTo(VALID_ECLASS_NAME);
        }
    }

    @Test(expected = XatkitException.class)
    public void computeInvalidClassNameLoadedModel() {
        XatkitSession session = loadModel();
        action = new GetAllInstances(emfPlatform, session, "INVALID");
        action.compute();
    }

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
