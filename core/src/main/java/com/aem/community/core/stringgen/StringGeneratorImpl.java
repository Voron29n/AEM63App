package com.aem.community.core.stringgen;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import java.util.Random;

@Component(immediate = true, service = StringGenerator.class)
@Designate(ocd = StringGeneratorConfig.class)
public class StringGeneratorImpl implements StringGenerator {

    private String[] parts;
    private int length;
    private boolean truncateToFit;

    @Activate
    @Modified
    public void activate(StringGeneratorConfig config){
        this.parts = config.parts();
        this.length = config.length();
        this.truncateToFit = config.truncateToFit();
    }

    @Override
    public String generateString() {
        Random r = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < length){
            stringBuilder.append(parts[r.nextInt(parts.length)]);
        }
        if (truncateToFit && stringBuilder.length() > length){
            stringBuilder.delete((int) length, stringBuilder.length());
        }
        return stringBuilder.toString();
    }
}
