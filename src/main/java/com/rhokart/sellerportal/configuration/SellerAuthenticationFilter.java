package com.rhokart.sellerportal.configuration;

import com.rhokart.sellerportal.util.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class SellerAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;

    public SellerAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(GlobalConstants.PUBLIC_URL.contains(request.getRequestURI())) {
            log.info("public url :{}",request.getRequestURI());
           // handlerExceptionResolver.resolveException(request, response, null, new AccessDeniedException("error"));
            filterChain.doFilter(request,response);// allow everything here as these ...
            return;
        }
        String token = request.getHeader("Authorization");
        log.info("token:{}",token);
        log.info("token validation is not completed");
        filterChain.doFilter(request,response);// allow everything here as these ...
        return;
    }
}
