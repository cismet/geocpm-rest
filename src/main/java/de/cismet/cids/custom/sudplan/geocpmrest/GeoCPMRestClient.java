/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;

import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMInput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMOutput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.Status;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeoCPMRestClient implements GeoCPMService {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GeoCPMRestClient.class);

    //~ Instance fields --------------------------------------------------------

    private final transient String rootResource;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMRestClient object.
     *
     * @param  rootResource  DOCUMENT ME!
     */
    public GeoCPMRestClient(final String rootResource) {
        // remove training '/' if present
        if ('/' == rootResource.charAt(rootResource.length() - 1)) {
            this.rootResource = rootResource.substring(0, rootResource.length() - 1);
        } else {
            this.rootResource = rootResource;
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void deleteRun(final String runId) {
        try {
            final Client c = getClient();
            final WebResource webResource = c.resource(rootResource + GeoCPMRestServiceImpl.PATH_DEL_RUN);

            // we send the runid and expect nothing
            final ClientResponse response = webResource.type(MediaType.TEXT_PLAIN).post(ClientResponse.class, runId);

            if (LOG.isInfoEnabled()) {
                LOG.info("GeoCPM Wrapper Service response status: " + response.getStatus()); // NOI18N
            }

            final int status = response.getStatus() - 200;

            if ((status < 0) || (status > 10)) {
                final String message = "status code did not indicate success: " + response.getStatus(); // NOI18N
                LOG.error(message);
                throw new GeoCPMClientException(message);
            }
        } catch (final Exception ex) {
            final String message = "could not delete run: " + runId;                                    // NOI18N
            LOG.error(message, ex);
            throw new GeoCPMClientException(message, ex);
        }
    }

    @Override
    public GeoCPMOutput getResults(final String runId) {
        try {
            final Client c = getClient();
            final WebResource webResource = c.resource(rootResource + GeoCPMRestServiceImpl.PATH_GET_RESULTS);

            // we expect json
            final WebResource.Builder builder = webResource.queryParam(GeoCPMRestServiceImpl.PARAM_RUN_ID, runId)
                        .accept(MediaType.APPLICATION_JSON);

            final ClientResponse response = builder.get(ClientResponse.class);

            if (LOG.isInfoEnabled()) {
                LOG.info("GeoCPM Wrapper Service response status: " + response.getStatus()); // NOI18N
            }

            final int status = response.getStatus() - 200;

            if ((status < 0) || (status > 10)) {
                final String message = "status code did not indicate success: " + response.getStatus(); // NOI18N
                LOG.error(message);
                throw new GeoCPMClientException(message);
            }

            return response.getEntity(GeoCPMOutput.class);
        } catch (final Exception ex) {
            final String message = "could not get results for run: " + runId; // NOI18N
            LOG.error(message, ex);
            throw new GeoCPMClientException(message, ex);
        }
    }

    @Override
    public Status getStatus(final String runId) {
        try {
            final Client c = getClient();
            final WebResource webResource = c.resource(rootResource + GeoCPMRestServiceImpl.PATH_GET_STATUS);

            // we expect json
            final WebResource.Builder builder = webResource.queryParam(GeoCPMRestServiceImpl.PARAM_RUN_ID, runId)
                        .accept(MediaType.APPLICATION_JSON);

            final ClientResponse response = builder.get(ClientResponse.class);

            if (LOG.isInfoEnabled()) {
                LOG.info("GeoCPM Wrapper Service response status: " + response.getStatus()); // NOI18N
            }

            final int status = response.getStatus() - 200;

            if ((status < 0) || (status > 10)) {
                final String message = "status code did not indicate success: " + response.getStatus(); // NOI18N
                LOG.error(message);
                throw new GeoCPMClientException(message);
            }

            return response.getEntity(Status.class);
        } catch (final Exception ex) {
            final String message = "could not get status for run: " + runId; // NOI18N
            LOG.error(message, ex);
            throw new GeoCPMClientException(message, ex);
        }
    }

    @Override
    public String runGeoCPM(final GeoCPMInput input) {
        try {
            final Client c = getClient();
            final WebResource webResource = c.resource(rootResource + GeoCPMRestServiceImpl.PATH_RUN_GEOCPM);

            // we send json and expect the runid as string
            final WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN);

            final ClientResponse response = builder.put(ClientResponse.class, input);

            if (LOG.isInfoEnabled()) {
                LOG.info("GeoCPM Wrapper Service response status: " + response.getStatus()); // NOI18N
            }

            final int status = response.getStatus() - 200;

            if ((status < 0) || (status > 10)) {
                final String message = "status code did not indicate success: " + response.getStatus(); // NOI18N
                LOG.error(message);
                throw new GeoCPMClientException(message);
            }

            return response.getEntity(String.class);
        } catch (final Exception ex) {
            final String message = "could not run geocpm: " + input; // NOI18N
            LOG.error(message, ex);
            throw new GeoCPMClientException(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Client getClient() {
        final ClientConfig config = new DefaultClientConfig();
        final Client client = Client.create(config);

        return client;
    }
}
