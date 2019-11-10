package com.xatkit.plugins.emf.platform.action;

import com.xatkit.AbstractXatkitTest;
import com.xatkit.core.XatkitCore;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.emf.platform.EMFPlatform;
import com.xatkit.plugins.emf.platform.EMFPlatformTest;
import com.xatkit.stubs.StubXatkitCore;
import org.apache.commons.configuration2.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import static java.util.Objects.nonNull;

public class AbstractEMFActionTest extends AbstractXatkitTest {

    protected static XatkitCore xatkitCore;

    protected static Configuration configuration;

    protected EMFPlatform emfPlatform;

    @BeforeClass
    public static void setUpBeforeClass() {
        xatkitCore = new StubXatkitCore();
        configuration = EMFPlatformTest.buildConfiguration();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        if(nonNull(xatkitCore) && !xatkitCore.isShutdown()) {
            xatkitCore.shutdown();
        }
    }

    @Before
    public void setUp() {
        this.emfPlatform = new EMFPlatform(xatkitCore, configuration);
    }

    @After
    public void tearDown() {
        this.emfPlatform.shutdown();
    }

    protected XatkitSession createValidXatkitSession() {
        return new XatkitSession("test");
    }
}
