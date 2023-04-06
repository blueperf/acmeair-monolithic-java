/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.web.dto;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(name = "Customer")
public class CustomerInfo implements Serializable {

  private static final long serialVersionUID = 1L;
  @XmlElement(name = "_id")
  private String _id;

  @XmlElement(name = "password")
  private String password;

  @XmlElement(name = "status")
  private String status;

  @XmlElement(name = "total_miles")
  private int total_miles;

  @XmlElement(name = "miles_ytd")
  private int miles_ytd;

  @XmlElement(name = "address")
  private AddressInfo address;

  @XmlElement(name = "phoneNumber")
  private String phoneNumber;

  @XmlElement(name = "phoneNumberType")
  private String phoneNumberType;

  public CustomerInfo() {}

  public CustomerInfo(String username, String password, String status, int total_miles,
      int miles_ytd, AddressInfo address, String phoneNumber, String phoneNumberType) {
    this._id = username;
    this.password = password;
    this.status = status;
    this.total_miles = total_miles;
    this.miles_ytd = miles_ytd;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.phoneNumberType = phoneNumberType;
  }

  public String get_id() {
    return _id;
  }

  public void set_id(String username) {
    this._id = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getTotal_miles() {
    return total_miles;
  }

  public void setTotal_miles(int total_miles) {
    this.total_miles = total_miles;
  }

  public int getMiles_ytd() {
    return miles_ytd;
  }

  public void setMiles_ytd(int miles_ytd) {
    this.miles_ytd = miles_ytd;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPhoneNumberType() {
    return phoneNumberType;
  }

  public void setPhoneNumberType(String phoneNumberType) {
    this.phoneNumberType = phoneNumberType;
  }

  public AddressInfo getAddress() {
    return address;
  }

  public void setAddress(AddressInfo address) {
    this.address = address;
  }

  @Override
  public String toString() {
    return "Customer [id=" + _id + ", password=" + password + ", status=" + status
        + ", total_miles=" + total_miles + ", miles_ytd=" + miles_ytd + ", address=" + address
        + ", phoneNumber=" + phoneNumber + ", phoneNumberType=" + phoneNumberType + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CustomerInfo other = (CustomerInfo) obj;
    if (address == null) {
      if (other.address != null)
        return false;
    } else if (!address.equals(other.address))
      return false;
    if (_id == null) {
      if (other._id != null)
        return false;
    } else if (!_id.equals(other._id))
      return false;
    if (miles_ytd != other.miles_ytd)
      return false;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    if (phoneNumber == null) {
      if (other.phoneNumber != null)
        return false;
    } else if (!phoneNumber.equals(other.phoneNumber))
      return false;
    if (phoneNumberType != other.phoneNumberType)
      return false;
    if (status != other.status)
      return false;
    if (total_miles != other.total_miles)
      return false;
    return true;
  }

}
