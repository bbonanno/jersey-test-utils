package com.github.bbonanno;

import com.github.bbonanno.TestService.BusinessException;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.*;

import static com.github.bbonanno.ExceptionTesting.expect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class JerseyClientRuleExampleTest {

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
    interface MockDependency {

        @GET
        @Path("what/i/want/{pathParam}")
        String someResource(@PathParam("pathParam") String pathParam, @QueryParam("queryParam") String queryParam);
    }
}