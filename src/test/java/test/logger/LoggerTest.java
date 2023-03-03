package test.logger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(LoggerTest.class);
        for (int i = 0; i < 100; i++)
            logger.error("TEST:" + i);
    }
}
