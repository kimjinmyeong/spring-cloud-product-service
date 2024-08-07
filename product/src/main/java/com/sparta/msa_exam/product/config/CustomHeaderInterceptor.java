package com.sparta.msa_exam.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomHeaderInterceptor implements Filter {

    @Value("${server.port}")
    private String port;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.addHeader("Server-Port", port);
        chain.doFilter(request, response);
    }
}
