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

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpHeader;

/**
 * @author Lloyd.Fernandes
 *
 */
public final class DataResourceUtility {

    /**
     * Separator used for name.
     */
    private static final char NAME_SEPARATOR = '_';

    /**
     *
     */
    private DataResourceUtility() {
        // Do Nothing
    }

    /**
     * Construct part of name from parameters.
     * @param pRequest request
     * @param propertyValue comma separated list of parameters
     * @return name part - cannot be null
     */
    private static String namePartParameter(final HttpServletRequest pRequest,
            final String propertyValue) {
        if (StringUtils.isEmpty(propertyValue)) {
            return "";
        }
        final int bufSize = 128;
        StringBuilder resourceName = new StringBuilder(bufSize);

        if (!StringUtils.isEmpty(propertyValue)) {
            String[] parameters = propertyValue.split("\\s*,\\s*");
            for (String parameter : parameters) {
                final String headerValue = pRequest.getParameter(parameter);
                if (!StringUtils.isEmpty(headerValue)) {
                    resourceName.append(NAME_SEPARATOR).append(headerValue);
                }
            }
        }

        return resourceName.toString();
    }

    /**
     * Construct part of name from headers.
     * @param pRequest request
     * @param propertyValue comma separated list of headers
     * @return name part - cannot be null
     */
    private static String namePartHeader(final HttpServletRequest pRequest,
            final String propertyValue) {
        if (StringUtils.isEmpty(propertyValue)) {
            return "";
        }
        final int bufSize = 128;
        StringBuilder resourceName = new StringBuilder(bufSize);

        if (!StringUtils.isEmpty(propertyValue)) {
            String[] headers = propertyValue.split("\\s*,\\s*");
            for (String header : headers) {
                final String headerValue = pRequest.getHeader(header);
                if (!StringUtils.isEmpty(headerValue)) {
                    resourceName.append(NAME_SEPARATOR).append(headerValue);
                }
            }
        }

        return resourceName.toString();
    }

    /**
     * Construct name for lookup of test resource.
     * @param pRequest request
     * @param pProperties properties
     * @return name constructed for resource
     */
    public static String constructName(final HttpServletRequest pRequest,
            final Properties pProperties) {

        final int bufSize = 255;
        StringBuilder resourceName = new StringBuilder(bufSize);

        resourceName.append(pRequest.getMethod());
        resourceName.append(namePartHeader(pRequest,
                pProperties.getProperty("request.headers")));
        resourceName.append(namePartHeader(pRequest,
                pProperties.getProperty("request.headers.additional")));
        resourceName.append(namePartParameter(pRequest,
                pProperties.getProperty("request.parameters")));
        resourceName.append(namePartParameter(pRequest,
                pProperties.getProperty("request.parameters.additional")));
        resourceName.append('.');
        String headerValue;
        headerValue = pRequest.getContentType();
        if (headerValue == null) {
            headerValue = "text/plain";
        }
        resourceName.append(headerValue);
        headerValue = pRequest.getHeader(HttpHeader.ACCEPT.toString());
        if (!StringUtils.isEmpty(headerValue)) {
            resourceName.append(NAME_SEPARATOR).append(headerValue);
        }

        return resourceName.toString().replaceAll("[^A-Za-z0-9_.-]", "-");
    }

}
