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
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.apache.http.HttpStatus;
import org.eclipse.jetty.http.HttpHeader;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import biz.astute.test.simulator.rest.resources.DataResourceInterface;

import com.jayway.restassured.RestAssured;

/**
 * Test simulator that has been setup correctly. 
 * Must be run after the test of simulator that has been setup to fail.
 * See the before/after class annotated methods.
 * 
 * @author Lloyd.Fernandes
 *
 */
// You may need to comment out dependsOnGroups as well as @BeforeClass
// on setupCorrectstuff() while running single in IDE
@Test(dependsOnGroups = "SimulatorFailedInit")
public class SimulatorTest extends BaseSimulatorTest {

    /**
    * Logger.
    */
    private static final Logger LOGGER = Logger.getLogger(SimulatorTest.class
            .getName());

    @BeforeClass
    protected void setupCorrectstuff() throws URISyntaxException {

        if (!isInitFinished()) {

            throw new IllegalStateException(
                    "Something is not right with the test setup."
                            + " This should run after the failed test class");
        }
    }

    @BeforeClass
    protected void setupDirectory() throws URISyntaxException {
        final String path =
                Paths.get(getClass().getResource("/data").toURI()).toString();
        System.setProperty(DataResourceInterface.dataRoot, path);
        LOGGER.fine("Exiting");
    }

    public void testDummyGET() throws IOException {

        RestAssured.given(defaultRequestSpec()).header(HEADER_TEST_ID, "DGET")
                .when().get("/dummy").then().statusCode(HttpStatus.SC_OK)
                .content(Matchers.containsString("Test DGET"));

    }

    public void testProfileAlt() throws IOException {

        RestAssured.given().header(HEADER_TEST_ID, "DGET")
                .header(HttpHeader.ACCEPT.asString(), "image/png").when()
                .get("/profile-alternate/users//avatar").then()
                .statusCode(HttpStatus.SC_NOT_FOUND).contentType("image/png")
                .header(HttpHeader.CONTENT_LENGTH.asString(), "757");

    }

    public void testProfileAlt1() throws IOException {

        RestAssured.given().header(HEADER_TEST_ID, "DGET")
                .header(HttpHeader.ACCEPT.asString(), "image/png")
                .param("p1", "pv1").param("p2", "pv2").when()
                .get("/profile-alternate/users/1/avatar").then()
                .statusCode(HttpStatus.SC_OK).contentType("image/png")
                .header(HttpHeader.CONTENT_LENGTH.asString(), "757");
    }

    public void testProfileAlt2() throws IOException {

        RestAssured.given().header(HEADER_TEST_ID, "DGET")
                .header(HttpHeader.ACCEPT.asString(), "image/png").when()
                .get("/profile-alternate/users/2/avatar").then()
                .statusCode(HttpStatus.SC_OK).contentType("image/png")
                .header(HttpHeader.CONTENT_LENGTH.asString(), "836");

    }

    public void testResourcePathNonExistent() throws IOException {
        RestAssured.given().when().get("/testNowhere").then()
                .statusCode(HttpStatus.SC_NOT_IMPLEMENTED)
                .content(Matchers.containsString("Resource does not exist "));
    }

    public void testResponseFileNonExistent() throws IOException {

        RestAssured
                .given()
                .when()
                .get("/profile")
                .then()
                .statusCode(HttpStatus.SC_NOT_IMPLEMENTED)
                .content(
                        Matchers.containsString("Resource response definition does not exist "));

    }

}
