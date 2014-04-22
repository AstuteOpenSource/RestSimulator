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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import biz.astute.test.simulator.rest.resources.DataResourceInterface;

/**
 * @author Lloyd.Fernandes
 *
 */
public class RequestContext implements Serializable {

    /**
     * serialization id.
     */
    private static final long serialVersionUID = 6073790167740701508L;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RequestContext.class
            .getName());
    /**
     * Separator used for name.
     */
    private static final char NAME_SEPARATOR = '_';

    /**
     * Used to convert/remove special characters.
     */
    private static final String NAME_SPECIAL_PATTERN = "[^A-Za-z0-9_.\\-]";
    /**
     * Used to convert uri pattern before use.
     */
    private static final String NAME_SPECIAL_PATTERN_URI =
            "[^A-Za-z0-9_\\-\\{\\}\\/]";
    /**
     * Special character replacement.
     */

    /**
     * Patterns.
     */
    private static Pattern[] patterns;

    /**
     * Replacement for any special characters.
     */
    private static final String NAME_REPLACE_SPECIAL = "-";
    /**
     * Character identifying if has needed.
     */
    private static final char HASH_IDENTIFIER = ':';
    /**
     * Request.
     */
    private final HttpServletRequest request;
    /**
     * Response.
     */
    private final HttpServletResponse response;

    /**
     * URI Properties.
     */
    private final List<String> uriProperties = new ArrayList<>();

    /**
     * Message Digest.
     */
    private MessageDigest messageDigest;

    /**
     * Construct request context.
     * @param pRequest request
     * @param pResponse response
     */
    public RequestContext(final HttpServletRequest pRequest,
            final HttpServletResponse pResponse) {
        request = pRequest;
        response = pResponse;
        if ((request == null) || (response == null)) {
            throw new IllegalArgumentException(
                    "Request and reesponse cannot be null");
        }
    }

    /**
     * Get the digest engine.
     * @return digest
     * @throws NoSuchAlgorithmException algorithm exception
     */
    private MessageDigest getDigest() throws NoSuchAlgorithmException {
        if (messageDigest == null) {
            messageDigest = MessageDigest.getInstance("SHA-1");
        }
        messageDigest.reset();
        return messageDigest;
    }

    /**
     * Build list of patterns from property file.
     * @param globalProperties global properties
     * @return patterns
     */
    private Pattern[] getPatterns(final Properties globalProperties) {
        if (patterns == null) {
            synchronized (NAME_SPECIAL_PATTERN) {
                if (patterns != null) {
                    return patterns;
                }
                String patternsPropertyValue =
                        globalProperties.getProperty("request.uri.patterns");
                if (StringUtils.isEmpty(patternsPropertyValue)) {
                    patterns = new Pattern[0];
                    return patterns;
                }
                List<Pattern> patternsList = new ArrayList<>();
                for (String pattern : patternsPropertyValue.split(",")) {
                    pattern = pattern.trim();
                    if (StringUtils.isEmpty(pattern)) {
                        continue;
                    }
                    pattern = "request.uri.pattern." + pattern;
                    String patternValue =
                            globalProperties.getProperty(pattern);
                    if (StringUtils.isEmpty(patternValue)
                            || StringUtils.isEmpty(patternValue.trim())) {
                        LOGGER.warning("Missing Pattern value " + pattern);
                        continue;
                    }
                    patternValue =
                            patternValue.replaceAll(NAME_SPECIAL_PATTERN_URI,
                                    "-");
                    patternValue = patternValue.replaceAll("-", "\\-");
                    patternValue = patternValue.replaceAll("\\.", "\\.");
                    patternValue =
                            patternValue.replaceAll("\\{\\}", "(.*?)") + "/.*";
                    try {
                        patternsList.add(Pattern.compile(patternValue));
                    } catch (PatternSyntaxException excep) {
                        LOGGER.log(Level.WARNING, "Failed to compile Pattern "
                                + pattern, excep);
                    }

                }
                patterns = patternsList.toArray(new Pattern[0]);
            }
        }

        return patterns;
    }

    /**
     * Compute the resource name from configuration properties and request.
     * @param pProperties properties
     * @return resource name
     * @throws NoSuchAlgorithmException has algorithm exception
     * @throws UnsupportedEncodingException  encoding exception
     */
    public final String getResourceName(final Properties pProperties)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final int bufSize = 255;
        StringBuilder resourceName = new StringBuilder(bufSize);

        resourceName.append(request.getMethod());
        resourceName.append(namePartVariables(pProperties));
        resourceName.append(namePartHeader(pProperties, "request.headers"));
        resourceName.append(namePartHeader(pProperties,
                "request.headers.additional"));
        resourceName.append(namePartParameter(pProperties,
                "request.parameters"));
        resourceName.append(namePartParameter(pProperties,
                "request.parameters.additional"));

        return resourceName.toString().replaceAll(NAME_SPECIAL_PATTERN,
                NAME_REPLACE_SPECIAL);

    }

    /**
     * Return path portion of URL. The url may be modified to extract variables.
     *
     * @param globalProperties global properties
     * @return path portion of url
     * @throws UnsupportedEncodingException exception
     */
    public final String getResourcePath(final Properties globalProperties)
            throws UnsupportedEncodingException {

        uriProperties.clear();
        String requestURI =
                URLDecoder.decode(request.getRequestURI(), "utf-8");
        Pattern[] currentPatterns = getPatterns(globalProperties);
        if (currentPatterns.length < 1) {
            return requestURI;
        }

        StringBuilder resourceName = new StringBuilder(requestURI);
        resourceName.append('/'); // Remove this later - need for matcher

        for (Pattern pattern : currentPatterns) {
            Matcher matcher = pattern.matcher(resourceName);
            if (matcher.matches() && (matcher.groupCount() > 0)) {
                for (int index = 1; index <= matcher.groupCount(); index++) {
                    String matched = matcher.group(index);
                    uriProperties.add(matched);
                }
                // Do so in reverse order so as to not affect offset
                for (int index = matcher.groupCount(); index > 0; index--) {
                    resourceName.replace(matcher.start(index),
                            matcher.end(index), StringUtils.EMPTY);
                }
                break;
            }
        }

        // remove '/' appended earlier
        resourceName.setLength(resourceName.length() - 1);
        // Remove any // that result from pattern replacement
        return resourceName.toString().replaceAll("//", "/");
    }

    /**
     * Compute hash if first character is {@link #HASH_IDENTIFIER}.
     * @param value value
     * @return non-null and hashed if needed
     * @throws UnsupportedEncodingException encoding exception
     * @throws NoSuchAlgorithmException algorithm exception
     */
    private String hashIt(final String value)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final MessageDigest md = getDigest();
        md.update(value.substring(1).getBytes(CharEncoding.UTF_8));
        return DatatypeConverter.printHexBinary(md.digest());
    }

    /**
     * Construct part of name from headers.
     * @param pProperties properties
     * @param pKey key of parameter elements
     * @return name part - cannot be null
     * @throws NoSuchAlgorithmException  alg exception
     * @throws UnsupportedEncodingException  encoding exception
     */
    private String namePartHeader(final Properties pProperties,
            final String pKey) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        final String propertyValue = pProperties.getProperty(pKey);
        if (StringUtils.isEmpty(propertyValue)) {
            return StringUtils.EMPTY;
        }
        final int bufSize = 128;
        StringBuilder resourceName = new StringBuilder(bufSize);

        if (!StringUtils.isEmpty(propertyValue)) {
            String[] headers = propertyValue.split("\\s*,\\s*");
            for (String header : headers) {
                if (StringUtils.isEmpty(header)) {
                    return (StringUtils.EMPTY);
                }
                boolean doHash = false;
                if (header.charAt(0) == HASH_IDENTIFIER) {
                    header = header.substring(1);
                    doHash = true;
                }

                String headerValue = request.getHeader(header);
                if (!StringUtils.isEmpty(headerValue)) {
                    if (doHash) {
                        headerValue = hashIt(headerValue);
                    }
                    headerValue =
                            replaceRequestElementIfNeeded(pProperties,
                                    DataResourceInterface.NAME_HEADER, header,
                                    headerValue);
                    resourceName.append(NAME_SEPARATOR).append(headerValue);
                }
            }
        }

        return resourceName.toString();
    }

    /**
     * Construct part of name from parameters.
     * @param pProperties properties
     * @param pKey key of parameter elements
     * @return name part - cannot be null
     * @throws NoSuchAlgorithmException  alg exception
     * @throws UnsupportedEncodingException  encoding exception
     */
    private String namePartParameter(final Properties pProperties,
            final String pKey) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        final String propertyValue = pProperties.getProperty(pKey);
        if (StringUtils.isEmpty(propertyValue)) {
            return StringUtils.EMPTY;
        }
        final int bufSize = 128;
        StringBuilder resourceName = new StringBuilder(bufSize);

        if (!StringUtils.isEmpty(propertyValue)) {
            String[] parameters = propertyValue.split("\\s*,\\s*");
            for (String parameter : parameters) {
                if (StringUtils.isEmpty(parameter)) {
                    return (StringUtils.EMPTY);
                }
                boolean doHash = false;
                if (parameter.charAt(0) == HASH_IDENTIFIER) {
                    parameter = parameter.substring(1);
                    doHash = true;
                }

                String parameterValue = request.getParameter(parameter);
                if (!StringUtils.isEmpty(parameterValue)) {
                    if (doHash) {
                        parameterValue = hashIt(parameterValue);
                    }
                    parameterValue =
                            replaceRequestElementIfNeeded(pProperties,
                                    DataResourceInterface.NAME_PARAMETER,
                                    parameter, parameterValue);
                    resourceName.append(NAME_SEPARATOR).append(parameterValue);
                }
            }
        }

        return resourceName.toString();
    }

    /**
     * Construct part of name from headers.
     * @param pProperties properties
     * @param pKey key of parameter elements
     * @return name part - cannot be null
     * @throws NoSuchAlgorithmException  alg exception
     * @throws UnsupportedEncodingException  encoding exception
     */
    private String namePartVariables(final Properties pProperties)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String propertyValue =
                pProperties.getProperty("request.uri.variables");
        if (StringUtils.isEmpty(propertyValue)) {
            return StringUtils.EMPTY;
        }
        propertyValue = propertyValue.trim();
        final int bufSize = 128;
        StringBuilder resourceName = new StringBuilder(bufSize);

        Set<String> indices = null;
        if (!StringUtils.isEmpty(propertyValue)) {
            boolean doAll = false;
            if (propertyValue.equals("*")) {
                doAll = true;
            } else {
                indices =
                        new HashSet<>(Arrays.asList(propertyValue
                                .split("\\s*,\\s*")));
            }
            for (int index = 0; index < uriProperties.size(); index++) {
                if (doAll || indices.contains(Integer.toString(index))) {
                    String headerValue = uriProperties.get(index);
                    if (!StringUtils.isEmpty(headerValue)) {
                        resourceName.append(NAME_SEPARATOR)
                                .append(headerValue);
                    }
                }
            }
        }

        return resourceName.toString();
    }

    /**
     * Get replacement value if specified.
     * @param pProperties properties
     * @param pType type like header or parameter
     * @param pKey key
     * @param pValue value
     * @return final value
     */
    private String replaceRequestElementIfNeeded(final Properties pProperties,
            final String pType, final String pKey, final String pValue) {
        StringBuilder propertyKey =
                new StringBuilder(
                        pType.length()
                                + pKey.length()
                                + pValue.length()
                                + DataResourceInterface.REQUEST_REQUEST_PREFIX
                                        .length() + 2);
        final char period = '.';
        propertyKey.append(DataResourceInterface.REQUEST_REQUEST_PREFIX)
                .append(period).append(pType).append(period).append(pKey)
                .append(period).append(pValue);
        String retVal = pProperties.getProperty(propertyKey.toString());
        if (StringUtils.isEmpty(retVal)) {
            retVal = pValue;
        }
        return retVal;
    }

    // public static void main(final String[] args) {
    // Pattern pat = Pattern.compile("/aa/bb/(.*?)/(.*?)/.*");
    // StringBuilder sample = new StringBuilder("/aa/bb/132/78/");
    // Matcher matcher = pat.matcher(sample);
    // if (matcher.matches()) {
    // System.out.println("Matched ");
    // for (int i = 0; i <= matcher.groupCount(); i++) {
    // System.out.println("" + i + ":" + matcher.group(i));
    // }
    // for (int i = matcher.groupCount(); i > 0; i--) {
    // sample.replace(matcher.start(i) - 1, matcher.end(i), "");
    // }
    // System.out.println("REPL:" + sample.toString());
    // }
    // System.out.println("End");
    // }

}
