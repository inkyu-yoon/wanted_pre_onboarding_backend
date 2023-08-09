package com.wanted.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.global.Response;
import com.wanted.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            Response<String> error = Response.error(ErrorCode.INVALID_TOKEN.getMessage());

            ObjectMapper mapper = new ObjectMapper();
            String responseBody = mapper.writeValueAsString(error);

            response.getWriter().write(responseBody);        }
    }
}
