/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public final class ExecutionStatus implements Serializable {

    //~ Static fields/initializers ---------------------------------------------

    public static final String STARTED = "STARTED";   // NOI18N
    public static final String FAILED = "FAILED";     // NOI18N
    public static final String RUNNING = "RUNNING";   // NOI18N
    public static final String FINISHED = "FINISHED"; // NOI18N
    public static final String BROKEN = "BROKEN";     // NOI18N

    //~ Instance fields --------------------------------------------------------

    private String status;
    private String taskId;
    private String statusDesc;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ExecutionStatus object.
     */
    public ExecutionStatus() {
    }

    /**
     * Creates a new ExecutionStatus object.
     *
     * @param  status  DOCUMENT ME!
     * @param  taskId  DOCUMENT ME!
     */
    public ExecutionStatus(final String status, final String taskId) {
        this.status = status;
        this.taskId = taskId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getStatus() {
        return status;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  status  DOCUMENT ME!
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  taskId  DOCUMENT ME!
     */
    public void setTaskId(final String taskId) {
        this.taskId = taskId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getStatusDesc() {
        return statusDesc;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  statusDesc  DOCUMENT ME!
     */
    public void setStatusDesc(final String statusDesc) {
        this.statusDesc = statusDesc;
    }
}
