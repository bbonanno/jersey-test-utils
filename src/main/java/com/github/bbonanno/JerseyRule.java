package com.github.bbonanno;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Arrays.asList;

@Accessors(fluent = true)
public abstract class JerseyRule implements TestRule {

    private final List<Object> resources;

    @Setter
    private BiConsumer<JerseyTest, List<Object>> beforeTest = (s, l) -> {};
    @Setter
    private BiConsumer<JerseyTest, List<Object>> afterTest  = (s, l) -> {};

    public JerseyRule(Object... resources) {
        this.resources = asList(resources);
    }

    public abstract Client client();

    public abstract WebTarget webTarget();

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                JerseyTest server = newTestServer();
                try {
                    server.setUp();

                    beforeTest(server);

                    base.evaluate();

                    afterTest(server);
                } finally {
                    server.tearDown();
                }
            }
        };
    }

    protected void beforeTest(JerseyTest server) {
        beforeTest.accept(server, resources);
    }

    protected void afterTest(JerseyTest server) {
        afterTest.accept(server, resources);
    }

    protected JerseyTest newTestServer() {
        return new Server();
    }

    private class Server extends JerseyTest {

        @Override
        protected Application configure() {
            ResourceConfig config = new ResourceConfig();
            resources.forEach(config::register);
            return config;
        }
    }
}
