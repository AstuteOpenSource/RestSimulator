/**
 * (c) 2014 Astute.BIZ, Inc.
 *               A New Jersey Corporation, USA.
 *
 * THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND
 * COPYRIGHT HOLDERS MAKE NO REPRESENTATIONS OR WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO, WARRANTIES
 * OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR
 * THAT THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE
 * ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 * COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 */
package biz.astute.test.simulator.rest.resources;

import java.io.InputStream;
import java.util.List;

/**
 * @author Lloyd.Fernandes
 *
 */
public interface DataResourceInterface {

    /**
     * System property that defines the root location of the data.
     */
    String DATA_ROOT = "REST_DATA_ROOT";

    /**
     * Data to return in response.
     */
    String RESPONSE_STATUS = "response.status";
    /**
     * Data to return in response.
     */
    String DATA_RESPONSE_VALUE = "response.data.value";

    /**
     * Resource name of data to return.
     */
    String DATA_RESPONSE_RESOURCE = "response.data.resource";

    /**
     * Prefix for headers to set.
     */
    String HEADER_RESPONSE_PREFIX = "response.header";

    /**
     * Return a list of properties given a prefix.
     * For example if provided with a.b the all keys starting
     * with a.b. will be returned.
     * @param pKey prefix
     * @return list of properties
     */
    List<String> getProperties(final String pKey);

    /**
     * Breaks comma separated values of a property as a list.
     * @param pProperty property
     * @return value list
     */
    List<String> getPropertyValues(final String pProperty);

    /**
     * Return value of given property.
     * @param pProperty property key
     * @return property value
     */
    String getPropertyValue(final String pProperty);

    /**
     * Return a stream pointing to data to return in response.
     * Caller is responsible for closing the stream.
     * <p/>
     * This will be called only if the property {@link #DATA_RESPONSE_VALUE}
     * is not specified.
     * @param resource the resource
     * @return data stream
     */
    InputStream getResourceData(final String resource);

}
