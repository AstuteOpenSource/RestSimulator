/**
 * (c) 2014 Astute.BIZ, Inc.
 *               A New Jersey Corporation, USA.
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    String dataRoot = "REST_DATA_ROOT";

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
     * Prefix for replacement to set.
     */
    String REQUEST_REQUEST_PREFIX = "request.replace";
    /**
     * Header name.
     */
    String NAME_HEADER = "header";
    /**
     * Parameter name.
     */
    String NAME_PARAMETER = "parameter";

    /**
     * Return a list of properties given a prefix.
     * For example if provided with a.b the all keys starting
     * with a.b. will be returned.
     * @param pKey prefix
     * @return list of properties
     */
    List<String> getProperties(final String pKey);

    /**
     * Return value of given property.
     * @param pProperty property key
     * @return property value
     */
    String getPropertyValue(final String pProperty);

    /**
     * Breaks comma separated values of a property as a list.
     * @param pProperty property
     * @return value list
     */
    List<String> getPropertyValues(final String pProperty);

    /**
     * Return a stream pointing to data to return in response.
     * Caller is responsible for closing the stream.
     * <p/>
     * This will be called only if the property {@link #DATA_RESPONSE_VALUE}
     * is not specified.
     * @param resource the resource
     * @exception DataResourceException exception
     * @return data stream
     */
    InputStream getResourceData(final String resource)
            throws DataResourceException;

}
