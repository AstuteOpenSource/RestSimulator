package biz.astute.test.simulator.rest.resources;

import org.testng.annotations.Test;

/**
 * Bare bones - Make sure no exception and code coverage.
 * @author Lloyd.Fernandes
 *
 */
public class DataResourceExceptionTest {

    /**
     * Make sure no exceptions.
     */
    @Test
    public void DataResourceException() {
        new DataResourceException("Test This One");

        new DataResourceException(new IllegalArgumentException());

        new DataResourceException("Test This Two",
                new IllegalArgumentException());

        new DataResourceException((String) null);

        new DataResourceException((Throwable) null);

        new DataResourceException(null, null);
    }
}
