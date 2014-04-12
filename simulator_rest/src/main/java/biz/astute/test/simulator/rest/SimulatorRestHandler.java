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
package biz.astute.test.simulator.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import biz.astute.test.simulator.rest.resources.DataResourceException;
import biz.astute.test.simulator.rest.resources.DataResourceFactory;
import biz.astute.test.simulator.rest.resources.DataResourceInterface;

/**
 * Processes request, Sends a signed XML with client info.
 *
 * @author Lloyd.Fernandes
 *
 */
public class SimulatorRestHandler extends AbstractHandler implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3227615591126407328L;
    /**
     *
     */
    private static final Logger LOGGER = Logger
            .getLogger(SimulatorRestHandler.class.getName());

    /**
     * {@inheritDoc}
     */
    public void handle(final String pTarget, final Request pBaseRequest,
            final HttpServletRequest pRequest,
            final HttpServletResponse pResponse) throws IOException,
            ServletException {

        // Lets set the default
        pResponse.setContentType("text/plain");

        try {

            DataResourceInterface dataResourceInterface =
                    DataResourceFactory.getDataResource(pRequest);

            for (String header : dataResourceInterface
                    .getProperties(DataResourceInterface.HEADER_RESPONSE_PREFIX)) {
                pResponse.setHeader(header,
                        dataResourceInterface.getPropertyValue(header));
            }

            String status =
                    dataResourceInterface
                            .getPropertyValue(DataResourceInterface.RESPONSE_STATUS);
            if (StringUtils.isEmpty(status)) {
                status = "200";
            }
            pResponse.setStatus(Integer.parseInt(status));
            String val =
                    dataResourceInterface
                            .getPropertyValue(DataResourceInterface.DATA_RESPONSE_VALUE);
            if (val == null) {

                try (InputStream inStream =
                        dataResourceInterface
                                .getResourceData(DataResourceInterface.DATA_RESPONSE_RESOURCE);) {
                    OutputStream outStream = pResponse.getOutputStream(); 
                    IOUtils.copy(inStream, outStream);
                    outStream.flush();
                }
            } else {
                pResponse.getWriter().print(val);
            }

        } catch (DataResourceException execp) {
            LOGGER.log(Level.SEVERE,
                    "Error Processing " + pRequest.getRequestURL(), execp);
            pResponse.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            pResponse.getWriter().print(
                    "Error Processing " + pRequest.getRequestURL() + " - "
                            + execp.getMessage());
        }
    }
}
