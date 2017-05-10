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
public class InvalidLoginException extends Exception
{
      public InvalidLoginException() {}

      //Constructor that accepts a message
      public InvalidLoginException(String message)
      {
         super(message);
      }
 }