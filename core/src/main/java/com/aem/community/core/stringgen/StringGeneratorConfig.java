package com.aem.community.core.stringgen;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface StringGeneratorConfig {

    @AttributeDefinition(name = "Parts", description = "Element the string is generated form",
            type = AttributeType.STRING)
    String[] parts() default {"Lorem" , "ipsum", "now", "what" , "you" , "mean"};

    @AttributeDefinition(name = "Length", type = AttributeType.INTEGER)
     int length() default 15;

    @AttributeDefinition(name = "Truncate to Fit", type = AttributeType.BOOLEAN)
    boolean truncateToFit() default false;
}
