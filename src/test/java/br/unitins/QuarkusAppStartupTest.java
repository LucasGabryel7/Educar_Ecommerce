package br.unitins;

import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class QuarkusAppStartupTest {

    @Test
    public void testAppStartup() {
        assertTrue(true);
    }
}
