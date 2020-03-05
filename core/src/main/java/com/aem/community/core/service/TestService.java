package com.aem.community.core.service;

import org.osgi.service.component.annotations.Component;

@Component(
        immediate = true,
        service = TestService.class
)
public class TestService {

    public String VAL = "Hello World";

    public String getVersion(){
        return "Version 1/0";
    }
}
