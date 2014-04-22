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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import biz.astute.test.simulator.rest.RequestContext;
import biz.astute.test.simulator.rest.resources.DataResourceInterface;

import com.jayway.restassured.RestAssured;

/**
 * Only tests that are set to fail because of bad server 
 * configuration will be run here. 
 * @author Lloyd.Fernandes
 *
 */
@Test(groups = "SimulatorFailedInit")
public class SimulatorFailedInitTest extends BaseSimulatorTest {

    /**
     * Setup data directory for failure.
     */
    @BeforeClass
    private void addSetup() {
        System.setProperty(DataResourceInterface.dataRoot, "SetupToFail");

    }

    /**
     * reset initialization. 
     */
    @AfterClass
    private void removeSetup() {
        setInitFinished(true);
    }

    /**
     * Test non existent data directory.
     */
    public void Test() {
        RestAssured
                .given()
                .when()
                .get("/testNoWhere")
                .then()
                .statusCode(HttpStatus.SC_NOT_IMPLEMENTED)
                .content(
                        Matchers.containsString("- REST_DATA_ROOT - Does not exist "));
    }

    /**
     * Test to cover null parameters.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestNullContext() {
        new RequestContext(null, null);
    }

    /**
     * Test to cover null response parameter.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestNullContextReq() {
        HttpServletRequest req = zzzRequest();
        new RequestContext(req, null);
    }

    /**
     * Test to cover null request parameter.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestNullContextResp() {
        HttpServletResponse resp = zzResponse();
        new RequestContext(null, resp);
    }

    /**
     * Create dummy response object.
     * @return response
     */
    private HttpServletResponse zzResponse() {
        return new Response(null, null);

    }

    /**
     * Create dummy request object.
     * @return request
     */
    private HttpServletRequest zzzRequest() {

        return new Request(null, null);
    }
}
