package ch.heiafr.isc.datacockpit.general_libraries.clazzes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassUtilsTest {

    @Test
    void testHeritingFrom() {
        // Given

        class B implements B1 {}
        class A extends B implements A1 {}
        class X extends A implements X1, X2 {}

        class NOT {}
        assertTrue(ClassUtils.isHeritingFrom(X.class, X1.class));
        assertTrue(ClassUtils.isHeritingFrom(X.class, X2.class));
        assertTrue(ClassUtils.isHeritingFrom(X.class, A.class));
        assertTrue(ClassUtils.isHeritingFrom(X.class, A1.class));
        assertTrue(ClassUtils.isHeritingFrom(X.class, B.class));
        assertTrue(ClassUtils.isHeritingFrom(X.class, B1.class));
        assertTrue(ClassUtils.isHeritingFrom(X.class, Object.class));
        assertTrue(ClassUtils.isHeritingFrom(B.class, B.class));
        assertTrue(ClassUtils.isHeritingFrom(Object.class, Object.class));

        assertFalse(ClassUtils.isHeritingFrom(X.class, NOT.class));
        assertFalse(ClassUtils.isHeritingFrom(A.class, X2.class));

    }


}

interface A1 {}
interface B1 {}
interface X1 {}
interface X2 {}