package com.kaifan.emloyeeManagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CookieToHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("access_token".equals(c.getName())) {

                    String token = c.getValue();
                    if (token != null) {
                        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
                            @Override
                            public String getHeader(String name) {
                                if (name.equals("Authorization")) {
                                    return "Bearer " + token;
                                }
                                return super.getHeader(name);
                            }
                        };
                        filterChain.doFilter(wrapper, response);
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}