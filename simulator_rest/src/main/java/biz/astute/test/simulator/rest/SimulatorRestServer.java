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
     * @param args arguments
     * @throws Exception exception
     */
    public static void main(final String[] args) throws Exception {
        start();
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
        server.join();
        LOGGER.info("Server Started");
    }

    /**
     * Start the server detached.
     * @throws Exception exception
     */
    public static void startDetached() throws Exception {

        Runnable runnable = new Runnable() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                try {
                    start();
                } catch (Exception execp) {
                    throw new RuntimeException(execp);
                }
            }

        };
        new Thread(runnable).start();
        int counter = 1000;
        while ((--counter > 0) && ((server == null) || (!server.isRunning()))) {
            Thread.sleep(5);
        }
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

}
