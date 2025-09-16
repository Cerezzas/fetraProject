package az.coders.fera_project.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Enumeration;

@Configuration
public class LoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(requestWrapper, responseWrapper);
        logger.info("request body: {}, requestWrapper: {}", request.getInputStream().read(), requestWrapper.getContentAsString());
        logger.info("request method: {}, uri: {}, queryParams: {}, headers: {}, body: {}",
                requestWrapper.getMethod(), requestWrapper.getRequestURI(), requestWrapper.getQueryString(), getHeader(requestWrapper), requestWrapper.getContentAsString());
        logger.info("response status: {}, body: {}", responseWrapper.getStatus(), getBody(responseWrapper.getContentAsByteArray()));
        responseWrapper.copyBodyToResponse();
    }

    private String getBody(byte[] buf) {
        if (buf == null) return "";
        System.out.println(new String(buf, 0, buf.length));
        return new String(buf, 0, buf.length);
    }

    private String getHeader(ContentCachingRequestWrapper requestWrapper) {
        String allHeaders = "";
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            allHeaders = headerName + ": " + requestWrapper.getHeader(headerName);
        }
        return allHeaders;
    }
}