/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KeyGenerator {

  public Object generate() {
    return java.util.UUID.randomUUID().toString();
  }
}
