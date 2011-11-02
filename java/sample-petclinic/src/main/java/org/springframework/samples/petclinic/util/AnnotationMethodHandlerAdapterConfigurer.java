package org.springframework.samples.petclinic.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * Configures the {@link AnnotationMethodHandlerAdapter} instance created by
 * {@code <mvc:annotation-driven/>}.
 */
public class AnnotationMethodHandlerAdapterConfigurer
    implements InitializingBean
{
    @Autowired
    private AnnotationMethodHandlerAdapter adapter;

    private WebBindingInitializer webBindingInitializer;

    public void setWebBindingInitializer(
            WebBindingInitializer webBindingInitializer)
    {
        this.webBindingInitializer = webBindingInitializer;
    }

    public void afterPropertiesSet() throws Exception {
        if (webBindingInitializer != null) {
            adapter.setWebBindingInitializer(webBindingInitializer);
        }
    }
}