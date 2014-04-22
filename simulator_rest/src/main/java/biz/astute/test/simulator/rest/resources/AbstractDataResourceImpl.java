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

import biz.astute.test.simulator.rest.RequestContext;

/**
 * Abstract Data resource that needs to be extended by providers.
 * @author Lloyd.Fernandes
 *
 */
public abstract class AbstractDataResourceImpl implements
        DataResourceInterface {

    /**
     * Data resource initialization. 
     * It is expected that the one time initialization will be done 
     * first time this is called. Note that the factory will call it 
     * each time an object is created. 
     * @param pRequestContext request context
     * @throws DataResourceException exception
     */
    abstract public void bootstrap(final RequestContext pRequestContext)
            throws DataResourceException;

}
