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
package biz.astute.test.simulator.rest.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import biz.astute.test.simulator.rest.RequestContext;

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
    private static File DATA_ROOT;

    /**
     * Used for initializing.
     */
    private static boolean notFirstTime = false;

    /**
     * Request specific properties.
     */
    private final Properties properties = new Properties();

    /**
     *Request URI translated
     */
    private File requestResourceLocation;

    /**
     * Construct resource.
     * @param pRequestContext request context
     * @throws DataResourceException exception
     * @throws NoSuchAlgorithmException  hash algorithm exception
     * @throws UnsupportedEncodingException  encoding exception
     */
    public DataResourceFileImpl(final RequestContext pRequestContext)
            throws DataResourceException, UnsupportedEncodingException,
            NoSuchAlgorithmException {

        bootstrap(pRequestContext);

        properties.putAll(GLOBAL_PROPERTIES);

        requestResourceLocation =
                new File(DATA_ROOT, pRequestContext.getResourcePath(
                        GLOBAL_PROPERTIES).substring(1));
        if (!requestResourceLocation.exists()) {
            throw new DataResourceException(" Resource does not exist "
                    + requestResourceLocation.getAbsolutePath());
        }

        File propertyFile =
                new File(requestResourceLocation, PROPERTY_FILE_NAME);
        if (propertyFile.exists()) {
            try (InputStream inStream = new FileInputStream(propertyFile)) {
                properties.load(inStream);
            } catch (IOException exc) {
                throw new DataResourceException("Failed to load "
                        + propertyFile.getAbsolutePath(), exc);
            }
        }

        propertyFile =
                new File(requestResourceLocation,
                        pRequestContext.getResourceName(properties));
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
     * Initialize stuff.
     * @param pRequestContext request context.
     * @throws DataResourceException exception
     */
    private void bootstrap(final RequestContext pRequestContext)
            throws DataResourceException {
        if (notFirstTime) {
            return;
        }

        synchronized (LOGGER) {
            if (notFirstTime) {
                return;
            }
            File dataRoot =
                    new File(System.getProperty(
                            DataResourceInterface.dataRoot, "./data"));
            if (!dataRoot.exists()) {
                throw new DataResourceException(DataResourceInterface.dataRoot
                        + " - Does not exist " + dataRoot.getAbsolutePath());
            }
            File propertyFile = new File(dataRoot, PROPERTY_FILE_NAME);
            if (propertyFile.exists()) {
                try (InputStream inStream = new FileInputStream(propertyFile)) {
                    GLOBAL_PROPERTIES.load(inStream);
                } catch (IOException exc) {
                    throw new DataResourceException("Failed to load "
                            + propertyFile.getAbsolutePath(), exc);
                }
            }

            DATA_ROOT = dataRoot;
            notFirstTime = true;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> getProperties(final String pKey) {

        final String beginKey = pKey + ".";
        List<String> propertiesList = new ArrayList<>();

        for (Object keyObject : properties.keySet()) {
            String keyProperty = (String) keyObject;
            if (keyProperty.startsWith(beginKey)) {
                propertiesList.add(keyProperty);
            }

        }

        return propertiesList;
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
    public final InputStream getResourceData(final String pResource)
            throws DataResourceException {

        final String fileName = properties.getProperty(DATA_RESPONSE_RESOURCE);
        if (StringUtils.isEmpty(fileName)) {
            // Not printing as possible to send nothing back other than status.
            return null;
        }
        File file;
        if (fileName.startsWith("/")) {
            file = new File(DATA_ROOT, fileName.substring(1));
        } else {
            file = new File(requestResourceLocation, fileName);
        }

        InputStream inpStream;
        try {
            inpStream = new FileInputStream(file);
        } catch (FileNotFoundException execp) {
            throw new DataResourceException(file.getAbsolutePath(), execp);
        }
        return inpStream;
    }

}
