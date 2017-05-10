package javafxapplication1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author michael
 */
public class InvalidCustomerException extends Exception
{
      public InvalidCustomerException() {}

      //Constructor that accepts a message
      public InvalidCustomerException(String message)
      {
         super(message);
      }
 }