/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.web;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.acmeair.service.AuthService;

public class RESTCookieSessionFilter implements Filter {

  static final String LOGIN_USER = "acmeair.login_user";

  private static final String LOGIN_PATH = "/rest/api/login";
  // private static final String LOGOUT_PATH = "/rest/api/login/logout";
  private static final String LOADDB_PATH = "/rest/api/loader/load";
  private static final String QUERY_PATH = "/rest/api/flights/queryflights";

  @Inject
  AuthService authService;

  @Override
  public void destroy() {}

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) resp;

    String path;
    String pathInfo = request.getPathInfo();
    if (pathInfo == null) {
      path = request.getContextPath() + request.getServletPath();
    } else {
      path = request.getContextPath() + request.getServletPath() + request.getPathInfo();
    }

    if (path.endsWith(LOGIN_PATH) || path.endsWith(LOADDB_PATH) || path.endsWith(QUERY_PATH)) {
      // if logging in, logging out, or loading the database, let the request flow
      chain.doFilter(req, resp);
      return;
    }

    Cookie cookies[] = request.getCookies();
    Cookie sessionCookie = null;

    if (cookies != null) {
      for (Cookie c : cookies) {
        if (c.getName().equals(LoginREST.SESSIONID_COOKIE_NAME)) {
          sessionCookie = c;
        }
        if (sessionCookie != null)
          break;
      }
      String sessionId = "";
      if (sessionCookie != null) // We need both cookie to work
        sessionId = sessionCookie.getValue().trim();
      // did this check as the logout currently sets the cookie value to "" instead of
      // aging it out
      // see comment in LogingREST.java
      if (sessionId.equals("")) {

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      JSONObject jsonObject = authService.validateSession(sessionId);
      if (jsonObject != null) {
        String loginUser = (String) jsonObject.get("customerid");
        request.setAttribute(LOGIN_USER, loginUser);
        chain.doFilter(req, resp);
        return;
      } else {

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }

    // if we got here, we didn't detect the session cookie, so we need to return 404
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  @Override
  public void init(FilterConfig config) throws ServletException {}
}
