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

/**
 * Exception specific to data resources.
 * @author Lloyd.Fernandes
 *
 */
public class DataResourceException extends Exception {

    /**
     * Serial id.
     */
    private static final long serialVersionUID = 7822748214150327216L;

    /**
     * Construct exception using message.
     * @param pMessage message
     */
    public DataResourceException(final String pMessage) {
        super(pMessage);
    }

    /**
     * Construct using message and cause.
     * @param pMessage message
     * @param pCause cause
     */
    public DataResourceException(final String pMessage, final Throwable pCause) {
        super(pMessage, pCause);
    }

    /**
     * Construct using cause.
     * @param pCause cause
     */
    public DataResourceException(final Throwable pCause) {
        super(pCause);
    }

}
