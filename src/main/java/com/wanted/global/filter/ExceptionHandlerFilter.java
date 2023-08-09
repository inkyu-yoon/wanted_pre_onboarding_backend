package com.wanted.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.global.Response;
import com.wanted.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            Response<String> error = Response.error(ErrorCode.INVALID_TOKEN.getMessage());

            ObjectMapper mapper = new ObjectMapper();
            String responseBody = mapper.writeValueAsString(error);

            response.getWriter().write(responseBody);
        }
    }
}
