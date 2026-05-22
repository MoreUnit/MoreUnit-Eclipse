package org.moreunit.core.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.moreunit.core.log.Logger;
import org.osgi.framework.BundleContext;

public class ModuleTest
{
    private Logger logger;
    private BundleContext context;
    private TestModule module;

    @Before
    public void setUp()
    {
        logger = mock(Logger.class);
        context = mock(BundleContext.class);
        TestModule.instance = null;
        module = new TestModule(false, logger);
    }

    @Test
    public void start_should_prepare_and_start_registered_services()
    {
        Service service1 = mock(Service.class);
        Service service2 = mock(Service.class);

        module.addService(service1);
        module.addService(service2);

        module.start(context);

        assertThat(module.prepareCalled).isTrue();
        assertThat(module.getContext()).isEqualTo(context);

        InOrder inOrder = inOrder(service1, service2);
        inOrder.verify(service1).start();
        inOrder.verify(service2).start();
    }

    @Test
    public void stop_should_stop_services_in_reverse_order_and_clean()
    {
        Service service1 = mock(Service.class);
        Service service2 = mock(Service.class);

        module.addService(service1);
        module.addService(service2);

        module.start(context);
        module.stop();

        assertThat(module.cleanCalled).isTrue();
        assertThat(module.getContext()).isNull();

        InOrder inOrder = inOrder(service1, service2);
        inOrder.verify(service2).stop();
        inOrder.verify(service1).stop();
    }

    @Test
    public void start_should_log_exceptions_and_continue_when_service_fails_to_start()
    {
        Service service1 = mock(Service.class);
        Service service2 = mock(Service.class);

        RuntimeException exception = new RuntimeException("start failed");
        doThrow(exception).when(service1).start();

        module.addService(service1);
        module.addService(service2);

        module.start(context);

        verify(service2).start();
        verify(logger).error(startsWith("Could not start service"), eq(exception));
    }

    @Test
    public void stop_should_log_exceptions_and_continue_when_service_fails_to_stop()
    {
        Service service1 = mock(Service.class);
        Service service2 = mock(Service.class);

        RuntimeException exception = new RuntimeException("stop failed");
        doThrow(exception).when(service2).stop();

        module.addService(service1);
        module.addService(service2);

        module.start(context);
        module.stop();

        verify(service1).stop();
        verify(logger).error(startsWith("Could not stop service"), eq(exception));
    }

    @Test
    public void constructor_should_replace_existing_instance_and_transfer_context_when_override_is_true()
    {
        Service service = mock(Service.class);
        module.addService(service);
        module.start(context);

        TestModule newModule = new TestModule(true, logger);

        assertThat(module.cleanCalled).isTrue();
        verify(service).stop();

        assertThat(newModule.getContext()).isEqualTo(context);
        assertThat(newModule.prepareCalled).isTrue();

        assertThat(TestModule.instance).isEqualTo(newModule);
    }

    @Test
    public void constructor_should_not_replace_existing_instance_when_override_is_false()
    {
        TestModule newModule = new TestModule(false, logger);

        assertThat(module.cleanCalled).isFalse();
        assertThat(TestModule.instance).isEqualTo(module);
    }

    private static class TestModule extends Module<TestModule>
    {
        static TestModule instance;
        boolean prepareCalled = false;
        boolean cleanCalled = false;
        private final Logger logger;

        TestModule(boolean override, Logger logger)
        {
            super(override);
            this.logger = logger;
            if (override || instance == null) {
                instance = this;
            }
        }

        @Override
        protected TestModule getInstance()
        {
            return instance;
        }

        @Override
        protected void setInstance(TestModule inst)
        {
            if (inst != this) {
                 instance = inst;
            }
        }

        @Override
        protected void prepare()
        {
            prepareCalled = true;
        }

        @Override
        protected void clean()
        {
            cleanCalled = true;
        }

        @Override
        public Logger getLogger()
        {
            return logger;
        }

        public void addService(Service s)
        {
            registerService(s);
        }
    }
}
