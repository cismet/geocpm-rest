/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ext.ContextResolver;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMInfo;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMInput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMMax;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMOutput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMSubInfo;
import de.cismet.cids.custom.sudplan.geocpmrest.io.ResultsElement;
import de.cismet.cids.custom.sudplan.geocpmrest.io.Status;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class JAXBContextProvider implements ContextResolver<JAXBContext> {

    //~ Instance fields --------------------------------------------------------

    private final transient JAXBContext context;

    private final transient Set<Class> types;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JAXBContextProvider object.
     *
     * @throws  JAXBException  DOCUMENT ME!
     */
    public JAXBContextProvider() throws JAXBException {
        types = new HashSet<Class>();

        types.add(GeoCPMInfo.class);
        types.add(GeoCPMInput.class);
        types.add(GeoCPMMax.class);
        types.add(GeoCPMOutput.class);
        types.add(GeoCPMSubInfo.class);
        types.add(ResultsElement.class);
        types.add(Status.class);

        context = new JSONJAXBContext(JSONConfiguration.natural().build(), types.toArray(new Class[types.size()]));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public JAXBContext getContext(final Class<?> type) {
        return types.contains(type) ? context : null;
    }
}
