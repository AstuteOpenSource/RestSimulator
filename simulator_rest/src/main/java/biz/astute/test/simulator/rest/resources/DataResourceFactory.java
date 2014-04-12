/**
 * (c) 2013-2014 Astute.BIZ, Inc.
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

import javax.servlet.http.HttpServletRequest;

/**
 * @author Lloyd.Fernandes
 *
 */
public final class DataResourceFactory {

    
    /**
     *
     */
    private DataResourceFactory() {
        // Nothing to do
    }

    /**
     * Get Data resource for request.
     * @param pRequest request
     * @return data resource
     * @throws DataResourceException exception
     */
    public static DataResourceInterface getDataResource(final HttpServletRequest pRequest) throws DataResourceException {
        
        return new DataResourceFileImpl(pRequest);
    }
}
