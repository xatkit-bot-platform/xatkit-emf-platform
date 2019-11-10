package com.xatkit.plugins.emf.platform.action;

import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.emf.EMFUtils;
import com.xatkit.plugins.emf.platform.EMFPlatformTest;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoadModelTest extends AbstractEMFActionTest {

    private static String VALID_MODEL_PATH = EMFPlatformTest.getModelPath();

    private LoadModel action;

    @Test(expected = NullPointerException.class)
    public void constructNullEMFPlatform() {
        action = new LoadModel(null, createValidXatkitSession(), VALID_MODEL_PATH);
    }

    @Test(expected = NullPointerException.class)
    public void constructNullXatkitSession() {
        action = new LoadModel(emfPlatform, null, VALID_MODEL_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructNullModelPath() {
        action = new LoadModel(emfPlatform, createValidXatkitSession(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructEmptyModelPath() {
        action = new LoadModel(emfPlatform, createValidXatkitSession(), "");
    }

    @Test
    public void constructValidModelPath() {
        action = new LoadModel(emfPlatform, createValidXatkitSession(), VALID_MODEL_PATH);
        /*
         * Nothing to check here, we just want to make sure the constructor didn't throw an exception.
         */
        assertThat(action).as("Action correctly created").isNotNull();
    }

    @Test
    public void computeValidModelPath() {
        XatkitSession session = createValidXatkitSession();
        action = new LoadModel(emfPlatform, session, VALID_MODEL_PATH);
        Object result = action.compute();
        assertThat(session.get(EMFUtils.MODEL_SESSION_KEY)).as("Loaded model stored in session").isNotNull();
        Resource modelResource = (Resource) session.get(EMFUtils.MODEL_SESSION_KEY);
        assertThat(modelResource.getContents()).as("Loaded model content is not empty").isNotEmpty();
        assertThat(modelResource).as("Model in the session is the same as the returned one").isEqualTo(result);
    }
}
