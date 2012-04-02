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
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;

import de.cismet.cids.custom.sudplan.geocpmrest.io.ExecutionStatus;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMException;
import de.cismet.cids.custom.sudplan.geocpmrest.io.ImportConfig;
import de.cismet.cids.custom.sudplan.geocpmrest.io.ImportStatus;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationConfig;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationResult;

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

    /**
     * DOCUMENT ME!
     *
     * @param   <T>         DOCUMENT ME!
     * @param   response    DOCUMENT ME!
     * @param   entityType  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  GeoCPMException           DOCUMENT ME!
     * @throws  GeoCPMClientException     DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    private <T> T handleResponse(final ClientResponse response, final Class<T> entityType) throws GeoCPMException,
        GeoCPMClientException,
        IllegalArgumentException,
        IllegalStateException {
        this.checkResponseForError(response);

        return response.getEntity(entityType);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   response  DOCUMENT ME!
     *
     * @throws  GeoCPMException           DOCUMENT ME!
     * @throws  GeoCPMClientException     DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    private void checkResponseForError(final ClientResponse response) throws GeoCPMException,
        GeoCPMClientException,
        IllegalArgumentException,
        IllegalStateException {
        if (response.getStatus() >= 300) {
            GeoCPMServiceExceptionMapper.throwException(response);
        }
    }

    @Override
    public ImportStatus importConfiguration(final ImportConfig cfg) throws GeoCPMException, IllegalArgumentException {
        if (cfg == null) {
            throw new IllegalArgumentException("cfg must not be null"); // NOI18N
        }

        final Client c = getClient();

        final WebResource webResource = c.resource(rootResource + GeoCPMRestServiceImpl.PATH_IMPORT_CFG);

        // we send json and expect json
        final WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

        final ClientResponse response = builder.put(ClientResponse.class, cfg);

        if (LOG.isInfoEnabled()) {
            LOG.info("GeoCPM Wrapper Service response status for importConfiguration('" + cfg + "'): " // NOI18N
                        + response.getStatus()); // NOI18N
        }

        return this.handleResponse(response, ImportStatus.class);
    }

    @Override
    public ExecutionStatus startSimulation(final SimulationConfig cfg) throws GeoCPMException,
        IllegalArgumentException {
        if (cfg == null) {
            throw new IllegalArgumentException("cfg must not be null"); // NOI18N
        }

        final Client c = getClient();
        final WebResource webResource = c.resource(rootResource + GeoCPMRestServiceImpl.PATH_START_SIM);

        // we send json and expect json
        final WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

        final ClientResponse response = builder.post(ClientResponse.class, cfg);

        if (LOG.isInfoEnabled()) {
            LOG.info("GeoCPM Wrapper Service response status for startSimulation('" + cfg + "'): " // NOI18N
                        + response.getStatus()); // NOI18N
        }

        return handleResponse(response, ExecutionStatus.class);
    }

    @Override
    public ExecutionStatus getStatus(final String runId) throws GeoCPMException, IllegalArgumentException {
        if ((runId == null) || runId.isEmpty()) {
            throw new IllegalArgumentException("runId must not be null"); // NOI18N
        }

        final Client c = getClient();
        final WebResource webResource = c.resource(rootResource + GeoCPMRestServiceImpl.PATH_GET_STATUS);

        // we expect json
        final WebResource.Builder builder = webResource.queryParam(GeoCPMRestServiceImpl.PARAM_RUN_ID, runId)
                    .accept(MediaType.APPLICATION_JSON);

        final ClientResponse response = builder.get(ClientResponse.class);

        if (LOG.isInfoEnabled()) {
            LOG.info("GeoCPM Wrapper Service response status for getStatus('" + runId + "'): " // NOI18N
                        + response.getStatus());
        }

        return handleResponse(response, ExecutionStatus.class);
    }

    @Override
    public SimulationResult getResults(final String runId) throws GeoCPMException,
        IllegalArgumentException,
        IllegalStateException {
        if ((runId == null) || runId.isEmpty()) {
            throw new IllegalArgumentException("runId must not be null"); // NOI18N
        }

        final Client c = getClient();
        final WebResource webResource = c.resource(rootResource + GeoCPMRestServiceImpl.PATH_GET_RESULTS);

        // we expect json
        final WebResource.Builder builder = webResource.queryParam(GeoCPMRestServiceImpl.PARAM_RUN_ID, runId)
                    .accept(MediaType.APPLICATION_JSON);

        final ClientResponse response = builder.get(ClientResponse.class);

        if (LOG.isInfoEnabled()) {
            LOG.info("GeoCPM Wrapper Service response status for getResults('" + runId + "'): " // NOI18N
                        + response.getStatus());
        }

        return this.handleResponse(response, SimulationResult.class);
    }

    @Override
    public void cleanup(final String runId) throws GeoCPMException, IllegalArgumentException, IllegalStateException {
        if ((runId == null) || runId.isEmpty()) {
            throw new IllegalArgumentException("runId must not be null"); // NOI18N
        }

        final Client c = getClient();
        final WebResource webResource = c.resource(rootResource + GeoCPMRestServiceImpl.PATH_CLEANUP);

        // we send the runid and expect nothing
        final ClientResponse response = webResource.type(MediaType.TEXT_PLAIN).post(ClientResponse.class, runId);

        if (LOG.isInfoEnabled()) {
            LOG.info("GeoCPM Wrapper Service response status for cleanup('" + runId + "'): " // NOI18N
                        + response.getStatus());
        }

        this.checkResponseForError(response);
    }
}
