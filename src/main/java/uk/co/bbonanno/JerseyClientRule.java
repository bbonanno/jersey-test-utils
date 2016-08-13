package uk.co.bbonanno;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.client.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JerseyClientRule extends JerseyRule {

    private final WebTargetWrapper webTarget = new WebTargetWrapper();
    private final ClientWrapper clientWrapper = new ClientWrapper();
    private final CheckResponseFilter responseFilter = new CheckResponseFilter();

    public JerseyClientRule(Object... resources) {
        super(resources);
    }

    @Override
    public WebTarget webTarget() {
        return webTarget;
    }

    @Override
    public Client client() {
        return clientWrapper;
    }

    @Override
    protected void beforeTest(JerseyTest server) {
        super.beforeTest(server);
        responseFilter.clear();

        Client client = server.client().register(responseFilter);

        clientWrapper.setDelegate(client);
        webTarget.setDelegate(client.target("/"));
    }

    @Override
    protected void afterTest(JerseyTest server) {
        super.afterTest(server);
        responseFilter.checkAllResponsesAreClosed();
    }

    /**
     * This filter will store references to all responses involved in each test with the purpose
     * of checking if they are closed
     */
    private static class CheckResponseFilter implements ClientResponseFilter {

        @Getter
        private final List<ClientResponseContext> responses = new ArrayList<>();

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            responses.add(responseContext);
        }

        void clear() {
            responses.clear();
        }

        void checkAllResponsesAreClosed() {
            responses.forEach(this::assertClosedResponse);
        }

        private void assertClosedResponse(ClientResponseContext responseContext) {
            try {
                responseContext.getEntityStream().read();
                throw new AssertionError("Response hasn't been closed");
            } catch (Exception exception) {
                assertThat(exception)
                    .withFailMessage("Response hasn't been closed")
                    .hasMessage("Entity input stream has already been closed.");
            }
        }
    }

    /**
     * Wrapper so the Object being tested can be injected with a WebTarget in construction time, which happens
     * before the rule initilises them
     */
    private static class WebTargetWrapper implements WebTarget {

        @Setter
        @Delegate
        private WebTarget delegate;

    }

    /**
     * Wrapper so the Object being tested can be injected with a Client in construction time, which happens
     * before the rule initilises them
     */
    private static class ClientWrapper implements Client {

        @Setter
        @Delegate
        private Client delegate;

    }
}