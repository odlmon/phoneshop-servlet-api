package com.es.phoneshop.web.filter;

import com.es.phoneshop.service.DosProtectionService;
import com.es.phoneshop.service.impl.DefaultDosProtectionService;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DosFilter implements Filter {
    private DosProtectionService dosProtectionService;
    private static final int TOO_MANY_REQUESTS_CODE = 429;

    @Override
    public void init(FilterConfig filterConfig) {
        dosProtectionService = DefaultDosProtectionService.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (dosProtectionService.isAllowed(request.getRemoteAddr())) {
            filterChain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(TOO_MANY_REQUESTS_CODE);
        }
    }

    @Override
    public void destroy() {
    }

    public void setDosProtectionService(DosProtectionService dosProtectionService) {
        this.dosProtectionService = dosProtectionService;
    }
}
