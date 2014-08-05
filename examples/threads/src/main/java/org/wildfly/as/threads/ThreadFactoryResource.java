package org.wildfly.as.threads;

import org.wildfly.core.management.Node;
import org.wildfly.core.management.annotation.Attribute;
import org.wildfly.core.management.annotation.Required;
import org.wildfly.core.management.annotation.XmlName;
import org.wildfly.core.management.annotation.XmlRender;

/**
 * @author <a href="mailto:tomaz.cerar@redhat.com">Tomaz Cerar</a> (c) 2012 Red Hat Inc.
 */
@XmlName("thread-factory")
public interface ThreadFactoryResource extends Node {
    @XmlRender(as = XmlRender.As.ATTRIBUTE)
    @Attribute()
    @Required(false)
    String getGroupName();

    @Attribute
    @Required(false)
    @XmlRender(as = XmlRender.As.ATTRIBUTE)
    String getThreadNamePattern();

    @Required(false)
    @XmlRender(as = XmlRender.As.ATTRIBUTE)
    @Attribute(/*validators = {IntRangeValidator.class}*/)
    int getPriority();

}
