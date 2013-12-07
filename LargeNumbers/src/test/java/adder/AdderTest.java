package adder;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Adam Wasiljew
 */
public class AdderTest {

    private Adder adder;

    @BeforeMethod
    public void setUp() {
        adder = new Adder();
    }

    @Test
    public void adderTestShouldNotFail() {
        testCase(1234, 98);
        testCase(1234, 9889);
        testCase(1234, 988992);
        testCase(1234, 0);
        testCase(0, 0);
        testCase(0, 12);
    }

    private void testCase(int a, int b) {
        // Given
        String aa = a+"";
        String bb = b+"";
        // When
        String v = adder.add(aa, bb);
        // Then
        assertEquals(v, (a+b)+"");
    }

}
