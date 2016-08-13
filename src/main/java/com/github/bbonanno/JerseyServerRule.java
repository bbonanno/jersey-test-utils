package com.github.bbonanno;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

@Accessors(fluent = true)
public class JerseyServerRule extends JerseyRule {

    @Getter
    private WebTarget webTarget;
    @Getter
    private Client client;

    public JerseyServerRule(Object... resources) {
        super(resources);
    }

    @Override
    protected void beforeTest(JerseyTest server) {
        super.beforeTest(server);
        client = server.client();
        webTarget = server.target("/");
    }

}
