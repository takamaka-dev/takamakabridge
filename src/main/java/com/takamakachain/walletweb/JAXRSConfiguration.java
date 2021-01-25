package com.takamakachain.walletweb;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Configures JAX-RS for the application.
 *
 * @author Juneau
 */
@ApplicationPath("resources")
public class JAXRSConfiguration extends ResourceConfig {

    public JAXRSConfiguration() {
        packages("de.rieckpil.blog").register(MultiPartFeature.class);
    }
}
