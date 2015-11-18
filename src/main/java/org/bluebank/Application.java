package org.bluebank;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.google.common.util.concurrent.ServiceManager;
import org.bluebank.banking.Bank;
import org.bluebank.resource.AtmResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.server.ServerEndpointConfig;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.UUID;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.ImmutableList.of;
import static java.util.UUID.randomUUID;
import static javax.websocket.server.ServerEndpointConfig.Builder.create;
import static javax.websocket.server.ServerEndpointConfig.Configurator;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;
import static org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer.configureContext;

public class Application extends ResourceConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private final ApplicationComponent applicationComponent;
    private ServiceManager serviceManager;
    private static Server server;

    public Application() {
        this(DaggerApplicationComponent.create());
    }

    public Application(ApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
        LOGGER.info("Starting Blue Bank ATM");

        packages(this.getClass().getPackage().getName());

        register(applicationComponent.getTransactionResource());
        register(applicationComponent.getAccountResource());
        register(CORSResponseFilter.class);

        serviceManager = applicationComponent.getServiceManager();
        serviceManager.startAsync().awaitHealthy();
        serviceManager.startupTimes().forEach((service, startTime) ->
                LOGGER.info("Service {} start in {} ms", service, startTime));

        applicationComponent.getCardValidationRequestedHandler();
        applicationComponent.getPinValidationResponseHandler();
        applicationComponent.getDepositRequestedHandler();
        applicationComponent.getInquiryRequestedHandler();
        applicationComponent.getPinValidationRequestHandler();
        applicationComponent.getDepositPerformedHandler();
        applicationComponent.getInquiryPerformedHandler();
        applicationComponent.getPinValidationHandler();

        applicationComponent.getThreadDeadlockHealthCheck();

        UUID id = randomUUID();
        Bank bank = applicationComponent.getBank();
        bank.createAccount(id, "5555444433331111", "1234");
    }

    public void start(int port) {
        server = new Server(port);
        ServletHolder servletHolder = new ServletHolder(new ServletContainer(this));
        ServletContextHandler context = new ServletContextHandler(NO_SESSIONS);
        context.setContextPath("/");
        context.addServlet(servletHolder, "/*");
        context.addServlet(new ServletHolder(new AdminServlet()), "/admin/*");
        context.addEventListener(new MetricsServletContextListener(applicationComponent.getMetricRegistry()));
        context.addEventListener(new HealthCheckServletContextListener(applicationComponent.getHealthCheckRegistry()));
        server.setHandler(context);

        ServerEndpointConfig endpointConfig = create(AtmResource.class, "/events")
                .configurator(new ServerSocketEndpointConfig(applicationComponent.getAtmResource()))
                .subprotocols(of("arraybuffer"))
                .build();

        try {
            ServerContainer container = configureContext(context);
            container.addEndpoint(endpointConfig);
            server.start();
        } catch (Exception e) {
            propagate(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Application().start(8080);
        server.join();
    }


    public void stop() {
        serviceManager.stopAsync().awaitStopped();
    }

    private static class ServerSocketEndpointConfig extends Configurator {

        private final AtmResource atmResource;

        private ServerSocketEndpointConfig(AtmResource atmResource) {
            this.atmResource = atmResource;
        }

        @Override
        public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
            return (T) atmResource;
        }
    }

    private static class CORSResponseFilter implements ContainerResponseFilter {
        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
                throws IOException {
            MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
            headers.add("Access-Control-Allow-Headers", "X-Requested-With, Authorization, Content-Type");
        }
    }

    private static class MetricsServletContextListener extends MetricsServlet.ContextListener {

        private final MetricRegistry metricRegistry;

        private MetricsServletContextListener(MetricRegistry metricRegistry) {
            this.metricRegistry = metricRegistry;
        }

        @Override
        protected MetricRegistry getMetricRegistry() {
            return metricRegistry;
        }
    }

    private static class HealthCheckServletContextListener extends HealthCheckServlet.ContextListener {

        private final HealthCheckRegistry healthCheckRegistry;

        public HealthCheckServletContextListener(HealthCheckRegistry healthCheckRegistry) {
            this.healthCheckRegistry = healthCheckRegistry;
        }

        @Override
        protected HealthCheckRegistry getHealthCheckRegistry() {
            return healthCheckRegistry;
        }

    }
}
