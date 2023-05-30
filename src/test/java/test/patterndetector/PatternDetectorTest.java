package test.patterndetector;

import bin.trade.records.Candle;
import bin.trade.tools.PatternDetector;
import org.junit.*;

public class PatternDetectorTest {
    PatternDetector patternDetector = new PatternDetector();
    @Before
    public void setUp() {
        System.out.println("setUp called");
    }
    @Test
    public void testGraveStoneDoji() {
        Candle candle = new Candle(0, 0, 0, 0);
        boolean result = patternDetector.isGraveStoneDoji(candle);
        Assert.assertEquals(false, result);
    }
    @Test
    public void testLong() {
        Candle candle = new Candle(1, 0, 0, 2);
        boolean result = patternDetector.isLong(candle);
        Assert.assertTrue(result);
    }
    @AfterClass
    public static void afterClass() {
        System.out.println("After class");
    }
}
