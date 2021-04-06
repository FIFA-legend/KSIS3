package by.bsuir;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class HttpMethods extends WebMvcConfigurationSupport {

    public static class CopyMethodDispatcher extends DispatcherServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            if (request.getMethod().equals("COPY") || request.getMethod().equals("MOVE")) {
                super.doPost(request, response);
            } else {
                super.service(request, response);
            }
        }
    }

    @Override
    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        final RequestMappingHandlerAdapter requestMappingHandlerAdapter = super.requestMappingHandlerAdapter();
        requestMappingHandlerAdapter.setSupportedMethods("GET", "POST", "PUT", "DELETE", "COPY", "MOVE");
        return requestMappingHandlerAdapter;
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new CopyMethodDispatcher();
    }

}
