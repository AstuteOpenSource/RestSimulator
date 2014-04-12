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
package biz.astute.test.simulator.rest.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * Data resource that uses files and paths to get test data.
 * @author Lloyd.Fernandes
 *
 */
public class DataResourceFileImpl implements DataResourceInterface {

    /**
    * Logger.
    */
    private static final Logger LOGGER = Logger
            .getLogger(DataResourceFileImpl.class.getName());

    /**
     *property file name.
     */
    private static final String PROPERTY_FILE_NAME = "config.properties";

    /**
     * Global properties.
     */
    private static final Properties GLOBAL_PROPERTIES = new Properties();

    /**
     *Data root location.
     */
    private static final File DATA_ROOT = new File(System.getProperty(
            DataResourceInterface.DATA_ROOT, "./data"));

    /**
     * Used for initializing.
     */
    private static boolean notFirstTime = false;

    /**
     * Request specific properties.
     */
    private final Properties properties = new Properties();

    /**
     * Bootstrap/initialization.
     * @throws DataResourceException exception
     */
    private void bootstrap() throws DataResourceException {

        if (notFirstTime) {
            return;
        }

        synchronized (LOGGER) {
            if (notFirstTime) {
                return;
            }
            notFirstTime = true;
            if (!DATA_ROOT.exists()) {
                throw new DataResourceException(
                        DataResourceInterface.DATA_ROOT + " - Does not exist "
                                + DATA_ROOT.getAbsolutePath());
            }
            File propertyFile = new File(DATA_ROOT, PROPERTY_FILE_NAME);
            if (propertyFile.exists()) {
                try (InputStream inStream = new FileInputStream(propertyFile)) {
                    GLOBAL_PROPERTIES.load(inStream);
                } catch (IOException exc) {
                    throw new DataResourceException("Failed to load "
                            + propertyFile.getAbsolutePath(), exc);
                }
            }

        }

    }

    /**
     * Construct resource.
     * @param pRequest request
     * @throws DataResourceException exception
     *
     * TODO change the request argument to a service or callback.
     * Essentially hide the request object from data source implementor.
     */
    public DataResourceFileImpl(final HttpServletRequest pRequest)
            throws DataResourceException {
        bootstrap();

        properties.putAll(GLOBAL_PROPERTIES);

        File reqLoc =
                new File(DATA_ROOT + pRequest.getRequestURI().substring(1));
        if (!reqLoc.exists()) {
            throw new DataResourceException(" Resource does not exist "
                    + reqLoc.getAbsolutePath());
        }

        File propertyFile = new File(reqLoc, PROPERTY_FILE_NAME);
        if (propertyFile.exists()) {
            try (InputStream inStream = new FileInputStream(propertyFile)) {
                properties.load(inStream);
            } catch (IOException exc) {
                throw new DataResourceException("Failed to load "
                        + propertyFile.getAbsolutePath(), exc);
            }
        }

        propertyFile =
                new File(reqLoc, DataResourceUtility.constructName(pRequest,
                        properties));
        if (propertyFile.exists()) {
            try (InputStream inStream = new FileInputStream(propertyFile)) {
                properties.load(inStream);
            } catch (IOException exc) {
                throw new DataResourceException("Failed to load "
                        + propertyFile.getAbsolutePath(), exc);
            }
        } else {
            throw new DataResourceException(
                    " Resource response definition does not exist "
                            + propertyFile.getAbsolutePath());

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> getProperties(final String pKey) {

        final String beginKey = pKey + ".";
        List<String> propertiesList = new ArrayList<>();
        
        for(Object keyObject : properties.keySet()) {
            String keyProperty = (String)keyObject;
            if(keyProperty.startsWith(beginKey)) {
                propertiesList.add(keyProperty);
            }
            
        }

        return propertiesList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> getPropertyValues(final String pProperty) {
        String propertyValue = properties.getProperty(pProperty);
        List<String> listOfValues = new ArrayList<>();
        if (!StringUtils.isEmpty(propertyValue)) {
            String[] valueElements = propertyValue.split("\\s*,\\s*");
            listOfValues = Arrays.asList(valueElements);
        }
        return listOfValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getPropertyValue(final String pProperty) {
        return properties.getProperty(pProperty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final InputStream getResourceData(final String pResource) {
        // TODO Auto-generated method stub
        return null;
    }


}
