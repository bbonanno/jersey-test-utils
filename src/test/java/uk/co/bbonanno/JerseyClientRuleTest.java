package uk.co.bbonanno;

import org.junit.Rule;
import org.junit.Test;
import uk.co.bbonanno.TestService.BusinessException;

import javax.ws.rs.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static uk.co.bbonanno.ExceptionTesting.expect;

public class JerseyClientRuleTest {

    private final MockDependency mockDependency = mock(MockDependency.class);

    @Rule
    public final JerseyClientRule jerseyClientRule = new JerseyClientRule(mockDependency);

    private final TestService testObj = new TestService(jerseyClientRule.webTarget());

    @Test
    public void loadSomeData_shouldReturnTheRightDataFromTheDependency() throws Exception {
        //given
        String param1 = "meh";
        String param2 = "blah";
        String expectedResult = "some data";

        when(mockDependency.someResource(anyString(), anyString())).thenReturn(expectedResult);

        //when
        String result = testObj.loadSomeData(param1, param2);

        //then
        assertThat(result).isEqualTo(expectedResult);
        verify(mockDependency).someResource(param1, param2);
    }

    @Test
    public void loadSomeData_shouldThrowABusinessExceptionIfDependencyFails() throws Exception {
        //given
        String param1 = "meh";
        String param2 = "blah";
        when(mockDependency.someResource(anyString(), anyString())).thenThrow(new InternalServerErrorException());

        //when
        BusinessException exception = expect(BusinessException.class, () -> testObj.loadSomeData(param1, param2));

        //then
        assertThat(exception).hasMessage("Boom: 500");
        verify(mockDependency).someResource(param1, param2);
    }

    @Path("path/to")
    public interface MockDependency {

        @GET
        @Path("what/i/want/{pathParam}")
        String someResource(@PathParam("pathParam") String pathParam, @QueryParam("queryParam") String queryParam);
    }
}