/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.web.dto;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(name = "CustomerAddress")
public class AddressInfo implements Serializable {

  private static final long serialVersionUID = 1L;
  private String streetAddress1;
  private String streetAddress2;
  private String city;
  private String stateProvince;
  private String country;
  private String postalCode;

  public AddressInfo() {}

  public AddressInfo(String streetAddress1, String streetAddress2, String city,
      String stateProvince, String country, String postalCode) {
    super();
    this.streetAddress1 = streetAddress1;
    this.streetAddress2 = streetAddress2;
    this.city = city;
    this.stateProvince = stateProvince;
    this.country = country;
    this.postalCode = postalCode;
  }

  public String getStreetAddress1() {
    return streetAddress1;
  }

  public void setStreetAddress1(String streetAddress1) {
    this.streetAddress1 = streetAddress1;
  }

  public String getStreetAddress2() {
    return streetAddress2;
  }

  public void setStreetAddress2(String streetAddress2) {
    this.streetAddress2 = streetAddress2;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getStateProvince() {
    return stateProvince;
  }

  public void setStateProvince(String stateProvince) {
    this.stateProvince = stateProvince;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @Override
  public String toString() {
    return "CustomerAddress [streetAddress1=" + streetAddress1 + ", streetAddress2="
        + streetAddress2 + ", city=" + city + ", stateProvince=" + stateProvince + ", country="
        + country + ", postalCode=" + postalCode + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AddressInfo other = (AddressInfo) obj;
    if (city == null) {
      if (other.city != null)
        return false;
    } else if (!city.equals(other.city))
      return false;
    if (country == null) {
      if (other.country != null)
        return false;
    } else if (!country.equals(other.country))
      return false;
    if (postalCode == null) {
      if (other.postalCode != null)
        return false;
    } else if (!postalCode.equals(other.postalCode))
      return false;
    if (stateProvince == null) {
      if (other.stateProvince != null)
        return false;
    } else if (!stateProvince.equals(other.stateProvince))
      return false;
    if (streetAddress1 == null) {
      if (other.streetAddress1 != null)
        return false;
    } else if (!streetAddress1.equals(other.streetAddress1))
      return false;
    if (streetAddress2 == null) {
      if (other.streetAddress2 != null)
        return false;
    } else if (!streetAddress2.equals(other.streetAddress2))
      return false;
    return true;
  }

}
