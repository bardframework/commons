package org.bardframework.commons.security.context;

import org.bardframework.commons.security.token.AuthenticationAbstract;
import org.bardframework.commons.security.token.manager.TokenManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class RequestSecurityContextManagerAbstract<A extends AuthenticationAbstract<U>, U> extends SecurityContextManagerAbstract<A, U> {

    public RequestSecurityContextManagerAbstract(TokenManager<A> tokenManager) {
        super(tokenManager);
    }

    @Override
    public void setToken(HttpServletResponse response, String tokenId) {

    }

    @Override
    protected String getToken(HttpServletRequest request) {
        return request.getParameter(tokenName);
    }

    @Override
    protected void deleteTicket(HttpServletRequest request, HttpServletResponse response) {

    }
}