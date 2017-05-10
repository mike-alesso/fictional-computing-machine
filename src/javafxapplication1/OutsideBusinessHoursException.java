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
public class OutsideBusinessHoursException extends Exception
{
      public OutsideBusinessHoursException() {}

      //Constructor that accepts a message
      public OutsideBusinessHoursException(String message)
      {
         super(message);
      }
 }