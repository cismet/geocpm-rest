/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import com.sun.jersey.api.client.ClientResponse;

import org.apache.log4j.Logger;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMException;

import de.cismet.tools.Converter;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeoCPMServiceExceptionMapper {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GeoCPMExceptionMapper.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMServiceExceptionMapper object.
     */
    private GeoCPMServiceExceptionMapper() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   t        DOCUMENT ME!
     * @param   builder  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Response toResponse(final Throwable t, final Response.ResponseBuilder builder) {
        final Response.ResponseBuilder response;
        if (builder == null) {
            response = Response.serverError();
        } else {
            response = builder;
        }

        if (t != null) {
            try {
                response.entity(Converter.serialiseToString(t));
            } catch (final IOException ex) {
                LOG.error("could not serialise throwable", ex); // NOI18N
            }
        }

        return response.build();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>       DOCUMENT ME!
     * @param   response  DOCUMENT ME!
     * @param   type      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T extends Throwable> T fromResponse(final ClientResponse response, final Class<T> type) {
        if (response != null) {
            try {
                return Converter.deserialiseFromString(response.getEntity(String.class), type);
            } catch (final Exception e) {
                LOG.warn("could not deserialise throwable", e); // NOI18N
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   errorResponse  DOCUMENT ME!
     *
     * @throws  GeoCPMException           DOCUMENT ME!
     * @throws  GeoCPMClientException     DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    @SuppressWarnings("fallthrough")
    public static void throwException(final ClientResponse errorResponse) throws GeoCPMException,
        GeoCPMClientException,
        IllegalArgumentException,
        IllegalStateException {
        switch (errorResponse.getStatus()) {
            case 550: {
                final GeoCPMException e = GeoCPMServiceExceptionMapper.fromResponse(
                        errorResponse,
                        GeoCPMException.class);
                if (e != null) {
                    throw e;
                }
                // fall-through
            }
            case 450: {
                final IllegalArgumentException e1 = GeoCPMServiceExceptionMapper.fromResponse(
                        errorResponse,
                        IllegalArgumentException.class);
                if (e1 != null) {
                    throw e1;
                }
                // fall-through
            }
            case 451: {
                final IllegalStateException e2 = GeoCPMServiceExceptionMapper.fromResponse(
                        errorResponse,
                        IllegalStateException.class);
                if (e2 != null) {
                    throw e2;
                }
                // fall-through
            }
            default: {
                throw new GeoCPMClientException("cannot remap exception for error status: "
                            + errorResponse.getStatus()); // NOI18N
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Provider
    public static final class GeoCPMExceptionMapper implements ExceptionMapper<GeoCPMException> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Response toResponse(final GeoCPMException e) {
            final Response.ResponseBuilder builder = Response.status(550);

            return GeoCPMServiceExceptionMapper.toResponse(e, builder);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Provider
    public static final class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Response toResponse(final IllegalArgumentException e) {
            final Response.ResponseBuilder builder = Response.status(450);

            return GeoCPMServiceExceptionMapper.toResponse(e, builder);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Provider
    public static final class IllegalStateExceptionMapper implements ExceptionMapper<IllegalStateException> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Response toResponse(final IllegalStateException e) {
            final Response.ResponseBuilder builder = Response.status(451);

            return GeoCPMServiceExceptionMapper.toResponse(e, builder);
        }
    }
}
