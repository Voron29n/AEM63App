package com.aem.community.core.listeners.removeNodeExercise;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface CustomListenerConfig {

    @AttributeDefinition(name = "RemoveNodeListener", type = AttributeType.BOOLEAN)
    boolean isRemoveNodeListenerActive() default false;

    @AttributeDefinition(name = "EditPageListener", type = AttributeType.BOOLEAN)
    boolean isPageEditListenerActive() default false;

}
