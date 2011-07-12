/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import org.openide.util.lookup.ServiceProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.cismet.commons.simplerestserver.ServerParamProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = ServerParamProvider.class)
public final class GeoCPMServerParamProvider implements ServerParamProvider {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Map<String, String> getServerParams() {
        final Map<String, String> params = new HashMap<String, String>(1);
        params.put(PARAM_JERSEY_PROPERTY_PACKAGES, "de.cismet.cids.custom.sudplan.geocpmrest"); // NOI18N

        return Collections.unmodifiableMap(params);
    }
}
