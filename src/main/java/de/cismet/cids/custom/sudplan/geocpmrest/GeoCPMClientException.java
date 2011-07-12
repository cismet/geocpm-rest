/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeoCPMClientException extends RuntimeException {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>GeoCPMClientException</code> without detail message.
     */
    public GeoCPMClientException() {
    }

    /**
     * Constructs an instance of <code>GeoCPMClientException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public GeoCPMClientException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>GeoCPMClientException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public GeoCPMClientException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
