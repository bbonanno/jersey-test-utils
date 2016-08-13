package uk.co.bbonanno;

import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class JerseyServerRuleTest {

    private final TestResource resource = new TestResource();

    @Rule
    public final JerseyServerRule jerseyServerRule = new JerseyServerRule(resource);

    @Test
    public void greet_shouldReturnTheExpectedValue() throws Exception {
        //when
        String result = jerseyServerRule.webTarget()
            .path("/myapp/greet")
            .request()
            .get(String.class);

        //then
        assertThat(result).isEqualTo("Hello world");
    }

    @Test
    public void fail_shouldReturnA500Error() throws Exception {
        //when
        Response result = jerseyServerRule.webTarget()
            .path("/myapp/fail500")
            .request()
            .get();

        //then
        assertThat(result.getStatus()).isEqualTo(500);
    }
}