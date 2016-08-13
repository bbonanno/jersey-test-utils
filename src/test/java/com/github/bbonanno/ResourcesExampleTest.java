package com.github.bbonanno;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Set;

public class ResourcesExampleTest extends ResourcesTest {

    public ResourcesExampleTest() {
        super("uk.co.bbonanno");
    }

    @Test
    public void enforceSomethingInAllHttpMethods() throws Exception {
        Set<Method> httpMethods = findAllHttpMethods();

        httpMethods.forEach(method -> {/* do something*/} );
    }
}