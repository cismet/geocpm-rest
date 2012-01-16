/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeoCPMException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>GeoCPMException</code> without detail message.
     */
    public GeoCPMException() {
    }

    /**
     * Constructs an instance of <code>GeoCPMException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public GeoCPMException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>GeoCPMException</code> with the specified detail message and the specified cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public GeoCPMException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
