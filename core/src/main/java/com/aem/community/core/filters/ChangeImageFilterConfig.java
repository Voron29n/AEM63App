package com.aem.community.core.filters;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface ChangeImageFilterConfig {

    @AttributeDefinition(name = "Model 1", description = "Grayscale the images" ,
            type = AttributeType.BOOLEAN)
    boolean model1() default false;

    @AttributeDefinition(name = "Model 2", description = "Turn the images" ,
            type = AttributeType.BOOLEAN)
    boolean model2() default false;

    @AttributeDefinition(name = "Degree to turn the images", description = "Turn the images by set degrees" ,
            type = AttributeType.INTEGER)
    int degrees() default 0;
}
