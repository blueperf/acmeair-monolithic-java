/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.loader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.acmeair.service.AuthService;

@ApplicationScoped
public class SessionLoader {

  @Inject
  private AuthService authService;

  public void dropSessions() {
    authService.dropSessions();
  }
}
