/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aem.community.core.models;

import junitx.util.PrivateAccessor;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Simple JUnit test verifying the HelloWorldModel
 */
public class TestHelloWorldModel {

    //@Inject
    private HelloWorldModel hello;
    
    private String slingId;
    
    @Before
    public void setup() throws Exception {
//        SlingSettingsService settings = mock(SlingSettingsService.class);
//        slingId = UUID.randomUUID().toString();
//        when(settings.getSlingId()).thenReturn(slingId);
//
//        hello = new HelloWorldModel();
//        PrivateAccessor.setField(hello, "settings", settings);
//        hello.init();
    }
    

    public void testGetMessage() throws Exception {
        // some very basic junit tests
        String msg = hello.getMessage();
        assertNotNull(msg);
        assertTrue(msg.length() > 0);
    }


    public void testStringFormat() throws Exception {
        String text = "asset";
        String path = "/content";
        String sqlStatement= "SELECT * FROM [nt:base] AS base WHERE ISDESCENDANTNODE(base,'" + path + "') and base.[cq:name] like '%" + text + "%'";
        System.out.println(sqlStatement);
    }


    @Test
    public void testNodePathSplit() {
        String nodePath = "/content/AEM63App/en/jcr:content/par" ;
        String destnodePath = "/content/AEM63App/en/test_1/jcr:content/par";
        String[] parts = nodePath.split("/");
        String destrelativepath = parts[(parts.length - 1)];
        String destpath = destnodePath + "/" + parts[(parts.length - 1)];
        System.out.println(destpath);
    }

    @Test
    public void testSplitString() {
        String nodePath = "/content/AEM63App/en/jcr:content/par/text" ;
        String[] parts = nodePath.split("/");
        String path = nodePath.substring(0 , nodePath.lastIndexOf("jcr:content"));
        String node = "/title";
        String nameCopyableNode = nodePath.substring(nodePath.lastIndexOf("/") , nodePath.length());
        String nodeName = node.substring(1);
        String destrelativepath = parts[(parts.length - 2)];
        System.out.println(nodeName);

        String removedNodeName = nodePath.substring((nodePath.lastIndexOf("/") + 1));
        System.out.println(removedNodeName);

    }

    @Test
    public void testRegexp() {
        String pathToMove = "/content";
        Pattern pattern = Pattern.compile("^(\\/content)");
        Matcher matcher = pattern.matcher(pathToMove);
        System.out.println(matcher.find());

        String pagePath = "/content/AEM63App/test";
        System.out.println(pagePath.substring(0 , pagePath.lastIndexOf("/") ));
        System.out.println(pathToMove.equals(pagePath.substring(0 , pagePath.lastIndexOf("/") - 1 )));

    }
}
