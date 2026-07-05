package junitcases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JUnitShowcase {
    @Test
    @Tag("basic")
    @DisplayName("basic test for List size")
    void test(){
        assertEquals(3, List.of(10,20,30).size());
    }

    @Test
    @Tag("advanced")
    @DisplayName("advanced test for List reverse")
    void test2(){
        var l = List.of(30,20,10);
        assertThrows(UnsupportedOperationException.class, () -> Collections.reverse(l));
    }
    
}
