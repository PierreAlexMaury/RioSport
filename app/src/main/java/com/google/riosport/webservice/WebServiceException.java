package com.google.riosport.webservice;

/**
 * Created by Rémi Prévost on 07/11/2014.
 */
public class WebServiceException extends Exception{
   public WebServiceException(String message) {
       super("WebServiceException : "+ message);
   }
}
