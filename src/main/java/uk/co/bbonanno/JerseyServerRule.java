package uk.co.bbonanno;

import lombok.Getter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import java.util.List;

import static java.util.Arrays.asList;

public class JerseyServerRule implements TestRule {

    private final List<Object> resources;
    @Getter
    private WebTarget target;
    @Getter
    private Client client;

    public JerseyServerRule(Object... resources) {
        this.resources = asList(resources);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                JerseyTest server = new Server();
                try {
                    server.setUp();

                    client = server.client();
                    target = server.target("/");

                    base.evaluate();
                } finally {
                    server.tearDown();
                }
            }
        };
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
