/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1.entities;

import java.time.ZoneId;

/**
 *
 * @author michael
 */
public class TimeZoneDisplay {
    String id;
    String description;
    
    public TimeZoneDisplay(String id, String description) {
        this.id = id;
        this.description = description;
    }
    
    public String getZone(){
        return this.id;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
