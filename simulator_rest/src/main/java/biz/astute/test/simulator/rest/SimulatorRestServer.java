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
package biz.astute.test.simulator.rest;

import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;

/**
 * Start the server.
 * <p/>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Lloyd.Fernandes
 *
 */
public final class SimulatorRestServer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger
            .getLogger(SimulatorRestServer.class.getName());

    /**
     * server object.
     */
    private static Server server;

    /**
     * default constructor.
     */
    private SimulatorRestServer() {
        // Do Nothing
    }
    /**
     * Start the server.
     * @throws Exception exception
     */
    public static void start() throws Exception {

        if (server != null) {
            LOGGER.warning("Server is already started");
            return;
        }

        XmlConfiguration config =
                new XmlConfiguration(
                        ClassLoader.getSystemResourceAsStream("jetty.xml"));
        final Server serverTmp = (Server) config.configure();
        LOGGER.info("Server Configuration Completed");

        config = null;
        serverTmp.start();
        server = serverTmp;
        LOGGER.info("Server Started");
    }

    /**
     * Stop the server.
     * @throws Exception exception
     */
    public static void stop() throws Exception {
        if (server == null) {
            LOGGER.warning("Server is NOT started");
            return;
        }
        server.stop();
        LOGGER.info("Server is stopped");

    }

    /**
     * Start the server.
     * @param args arguments
     * @throws Exception exception
     */
    public static void main(final String[] args) throws Exception {

        start();
        server.join();
    }

}
