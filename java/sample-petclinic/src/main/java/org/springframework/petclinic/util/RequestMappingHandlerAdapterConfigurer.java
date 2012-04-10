package org.springframework.petclinic.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * Configures the {@link RequestMappingHandlerAdapter} instance created by
 * {@code <mvc:annotation-driven/>}.
 */
public class RequestMappingHandlerAdapterConfigurer
    implements InitializingBean
{
    @Autowired
    private RequestMappingHandlerAdapter adapter;

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