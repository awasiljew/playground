package multiplier;

import adder.Adder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Adam Wasiljew
 */
public class MultiplierTest {

    private Multiplier multiplier;

    @BeforeMethod
    public void setUp() {
        multiplier = new Multiplier(new Adder());
    }

    @Test
    public void shouldPassStandardLongs() {
        testCase(2, 2);
        testCase(123, 342);
        testCase(123, 983298123);
        testCase(12332112, 983298123);
        testCase(91212332112L, 98);
        testCaseOverflow(123454334543598743L, 3242348983492384723L);
    }

    private void testCase(long a, long b) {
        // When
        String c = multiplier.multiply(a + "", b + "");
        // Then
        assertNotNull(c);
        assertEquals(((a * b) + ""), c);
    }

    private void testCaseOverflow(long a, long b) {
        // When
        String c = multiplier.multiply(a + "", b + "");
        // Then
        assertNotNull(c);
        assertFalse(((a * b) + "").equals(c));
    }


}
