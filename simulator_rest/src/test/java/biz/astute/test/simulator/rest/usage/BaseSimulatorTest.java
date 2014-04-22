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
package biz.astute.test.simulator.rest.usage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.http.HttpMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import biz.astute.test.simulator.rest.SimulatorRestServer;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * Setup and tear down the server.
 * @author Lloyd.Fernandes
 *
 */
public class BaseSimulatorTest {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger
            .getLogger(BaseSimulatorTest.class.getName());

    public static final String HEADER_TEST_ID = "TEST-ID";
    /**
     * init state.
     */
    static boolean initFinished = false;

    /**
     * close response and release resources.
     * @param response response
     */
    protected void closeResponse(final HttpResponse response) {
        if (response == null) {
            return;
        }
        HttpClientUtils.closeQuietly(response);
    }

    /**
     * Generate a request specification with default headers.
     * @return default request Specification.
     */
    protected RequestSpecification defaultRequestSpec() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.addHeader(HttpHeaders.ACCEPT,
                ContentType.TEXT.toString());
        requestSpecBuilder.addHeader(HttpHeaders.CONTENT_ENCODING, "utf-8");
        return requestSpecBuilder.build().contentType(ContentType.TEXT);

    }

    protected String getResponseText(final HttpResponse response)
            throws IOException {

        byte[] data = IOUtils.toByteArray(response.getEntity().getContent());
        Header header = response.getEntity().getContentEncoding();
        String encoding = "utf-8";
        if (header != null) {
            encoding = header.getValue();
        }

        return new String(data, encoding);
    }

    /**
     * @return the initFinished
     */
    public synchronized boolean isInitFinished() {
        return initFinished;
    }

    /**
     * Response created from request. 
     * The caller is responsible for closing the response to 
     * ensure proper resource cleanup.
     *  
     * @param uriPath uri path of request
     * @param method the method 
     * @param headers headers to add - can be null
     * @param queryParameters query parameters - can be null
     * @return response or runtime exception
     */
    protected HttpResponse makeRequest(final String uriPath,
            final HttpMethod method, final Header[] headers,
            final NameValuePair[] queryParameters) {
        try {

            final URIBuilder uriBuilder =
                    new URIBuilder().setScheme("http").setHost("localhost")
                            .setPath(uriPath).setPort(9090);

            if (queryParameters != null) {
                uriBuilder.setParameters(queryParameters);
            }
            final URI uri = uriBuilder.build();

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpUriRequest httprequest;
            switch (method) {
                case GET:
                    httprequest = new HttpGet(uri);
                    break;
                case DELETE:
                    httprequest = new HttpDelete(uri);
                    break;
                case HEAD:
                    httprequest = new HttpHead(uri);
                    break;
                case OPTIONS:
                    httprequest = new HttpOptions(uri);
                    break;
                case POST:
                    httprequest = new HttpPost(uri);
                    break;
                case PUT:
                    httprequest = new HttpPut(uri);
                    break;
                case TRACE:
                    httprequest = new HttpTrace(uri);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported method - "
                            + method.toString());
            }
            httprequest.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
            httprequest.setHeader(HttpHeaders.ACCEPT, "text/plain");
            httprequest.setHeader(HttpHeaders.CONTENT_ENCODING, "utf-8");
            if (headers != null) {
                for (Header header : headers) {
                    httprequest.addHeader(header);
                }
            }

            CloseableHttpResponse response = null;
            LOGGER.log(Level.FINE, "Requesting %s", uriPath);
            response = httpclient.execute(httprequest);
            return response;
        } catch (IOException | URISyntaxException execp) {
            throw new RuntimeException("Failed connectivity", execp);
        }

    }

    /**
     * @param pInitFinished the initFinished to set
     */
    public synchronized void setInitFinished(final boolean pInitFinished) {
        initFinished = pInitFinished;
    }

    /**
     * Start the server.
     * @throws Exception exception 
     * 
     */
    @BeforeSuite
    public void StartServer() throws Exception {
        RestAssured.port = 9090;
        SimulatorRestServer.startDetached();
    }

    /**
     * Stop the server.
     * @throws Exception exception 
     * 
     */
    @AfterSuite
    public void StopServer() throws Exception {
        SimulatorRestServer.stop();
    }
}
