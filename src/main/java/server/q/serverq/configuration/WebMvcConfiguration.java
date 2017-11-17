//package server.q.serverq.configuration;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//@Configuration
//public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
//
//    @Value("${resources}")
//    private String resources;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//        if (resources != null) {
//            registry.addResourceHandler("/myresources/**").addResourceLocations("file:" + resources);
//        }
//        super.addResourceHandlers(registry);
//    }
//
//}