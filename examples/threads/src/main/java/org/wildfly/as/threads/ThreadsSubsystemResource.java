package org.wildfly.as.threads;

import java.util.Map;
import org.wildfly.core.management.Node;
import org.wildfly.core.management.annotation.Provides;
import org.wildfly.core.management.annotation.RootResource;
import org.wildfly.core.management.annotation.SubResource;
import org.wildfly.core.management.annotation.XmlName;

/**
 * @author <a href="mailto:tomaz.cerar@redhat.com">Tomaz Cerar</a> (c) 2012 Red Hat Inc.
 */
@Threads_1_0
@Provides("threads")
@XmlName("subsystem")
@RootResource(
        type = "subsystem",
        name = "threads"
)
public interface ThreadsSubsystemResource extends Node {

    @SubResource
    Map<String, ThreadFactoryResource> getThreadFactories();
}
