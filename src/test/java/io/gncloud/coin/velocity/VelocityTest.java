package io.gncloud.coin.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */

public class VelocityTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(VelocityTest.class);
    private String TEMPLATE_NAME = "runAlgo.vm";

    @Test
    public void convertString(){

        //parameter live simulation exchange capital coin

        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        org.apache.velocity.Template template = engine.getTemplate(TEMPLATE_NAME, "utf-8");
        StringWriter stringWriter = new StringWriter();


        VelocityContext context = new VelocityContext();
        context.put("frontends", "aaa");



        template.merge(context, stringWriter);
        String runAlgoPy = stringWriter.toString();


        logger.info("결과 : {}", runAlgoPy);
    }
}