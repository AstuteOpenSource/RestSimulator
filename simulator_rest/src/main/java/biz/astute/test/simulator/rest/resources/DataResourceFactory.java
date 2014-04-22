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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import biz.astute.test.simulator.rest.RequestContext;

/**
 * @author Lloyd.Fernandes
 *
 */
public final class DataResourceFactory {

    /**
     * Should never be called.
     *
     */
    private DataResourceFactory() {
        // Do nothing. Maybe throw runtime exception
    }

    /**
     * Get Data resource for request.
     * @param pRequest request context
     * @return data resource
     * @throws DataResourceException exception
     * @throws NoSuchAlgorithmException hash algorithm exception
     * @throws UnsupportedEncodingException encoding exception
     */
    public static DataResourceInterface getDataResource(
            final RequestContext pRequest) throws DataResourceException,
            UnsupportedEncodingException, NoSuchAlgorithmException {

        return new DataResourceFileImpl(pRequest);
    }
}
