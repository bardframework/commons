package org.bardframework.commons.security.context;

import org.bardframework.commons.security.token.AuthenticationAbstract;
import org.bardframework.commons.security.token.manager.TokenManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class HeaderSecurityContextManagerAbstract<A extends AuthenticationAbstract<U>, U> extends SecurityContextManagerAbstract<A, U> {

    public HeaderSecurityContextManagerAbstract(TokenManager<A> tokenManager) {
        super(tokenManager);
    }

    @Override
    public void setToken(HttpServletResponse response, String tokenId) {

    }

    @Override
    protected String getToken(HttpServletRequest request) {
        return request.getHeader(tokenName);
    }

    @Override
    public void deleteTicket(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 1L);
        response.setHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
    }
}