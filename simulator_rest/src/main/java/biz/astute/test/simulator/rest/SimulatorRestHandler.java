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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
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
    @Override
    public final void handle(final String pTarget, final Request pBaseRequest,
            final HttpServletRequest pRequest,
            final HttpServletResponse pResponse) throws IOException,
            ServletException {

        // Lets set the default
        pResponse.setContentType("text/plain");

        try {

            DataResourceInterface dataResourceInterface =
                    DataResourceFactory.getDataResource(new RequestContext(
                            pRequest, pResponse));

            for (String header : dataResourceInterface
                    .getProperties(DataResourceInterface.HEADER_RESPONSE_PREFIX)) {
                pResponse
                        .setHeader(
                                header.substring(DataResourceInterface.HEADER_RESPONSE_PREFIX
                                        .length() + 1), dataResourceInterface
                                        .getPropertyValue(header));
            }

            String status =
                    dataResourceInterface
                            .getPropertyValue(DataResourceInterface.RESPONSE_STATUS);
            if (StringUtils.isEmpty(status)) {
                status = Integer.toString(HttpStatus.OK_200);
            }
            pResponse.setStatus(Integer.parseInt(status));
            String val =
                    dataResourceInterface
                            .getPropertyValue(DataResourceInterface.DATA_RESPONSE_VALUE);
            if (val == null) {

                try (InputStream inStream =
                        dataResourceInterface
                                .getResourceData(DataResourceInterface.DATA_RESPONSE_RESOURCE);) {
                    if (inStream != null) {
                        OutputStream outStream = pResponse.getOutputStream();
                        pResponse.setContentLength(IOUtils.copy(inStream,
                                outStream));
                        outStream.flush();
                    }
                }
            } else {
                pResponse.getWriter().print(val);
            }

        } catch (DataResourceException | NoSuchAlgorithmException execp) {
            LOGGER.log(Level.SEVERE,
                    "Error Processing " + pRequest.getRequestURL(), execp);
            pResponse.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            pResponse.getWriter().print(
                    "Error Processing " + pRequest.getRequestURL() + " - "
                            + execp.getMessage());
        }
        pResponse.flushBuffer();
        pBaseRequest.setHandled(true);
    }
}
