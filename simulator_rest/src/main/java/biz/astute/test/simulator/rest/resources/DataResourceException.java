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
     * Construct using cause.
     * @param pCause cause
     */
    public DataResourceException(final Throwable pCause) {
        super(pCause);
    }

    /**
     * Construct using message and cause.
     * @param pMessage message
     * @param pCause cause
     */
    public DataResourceException(final String pMessage,
            final Throwable pCause) {
        super(pMessage, pCause);
    }


}
