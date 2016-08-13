package uk.co.bbonanno;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class ResourcesTest {

    public static final List<Class<? extends Annotation>> HTTP_METHOD_ANNOTATIONS
        = asList(GET.class, POST.class, PUT.class, DELETE.class);

    private final Reflections reflections;

    public ResourcesTest(String packageToScan) {
        reflections = new Reflections(
            packageToScan,
            new MethodAnnotationsScanner(),
            new TypeAnnotationsScanner(),
            new SubTypesScanner()
        );
    }

    @Test
    public void allPublicMethodsShouldHaveAnHttpMethodAnnotation() {
        Set<Method> resourceMethodsWithNoHttpAnnotation = reflections.getTypesAnnotatedWith(Path.class).stream()
            .flatMap(type -> asList(type.getDeclaredMethods()).stream())
            .filter(method -> !annotationClasses(method).stream().anyMatch(HTTP_METHOD_ANNOTATIONS::contains))
            .collect(toSet());

        assertThat(resourceMethodsWithNoHttpAnnotation)
            .withFailMessage("There are public resource methods with no HTTP Method Annotations, %s", resourceMethodsWithNoHttpAnnotation)
            .isEmpty();
    }

    @Test
    public void allClassesWithMethodsDeclaringHttpMethodAnnotationShouldHaveThePathAnnotation() {
        Set<? extends Class<?>> resourceClassesWithNoPathAnnotation = HTTP_METHOD_ANNOTATIONS.stream()
            .flatMap(annotation -> reflections.getMethodsAnnotatedWith(annotation).stream())
            .map(Method::getDeclaringClass)
            .filter(resource -> !resource.isAnnotationPresent(Path.class))
            .collect(toSet());

        assertThat(resourceClassesWithNoPathAnnotation)
            .withFailMessage("There are resource classes with no Path Annotations, %s", resourceClassesWithNoPathAnnotation)
            .isEmpty();
    }


    private static Set<Class<? extends Annotation>> annotationClasses(Method method) {
        return asList(method.getAnnotations()).stream()
            .map(Annotation::annotationType)
            .collect(toSet());
    }
}
