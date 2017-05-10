/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1.entities;

import java.util.Objects;

/**
 *
 * @author michael
 */
public class AppointmentTypeRpt {
    Integer year;
    Integer month;
    String apptType;
    
    public AppointmentTypeRpt(int year, int month, String apptType) {
        this.year = year;
        this.month = month;
        this.apptType = apptType;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public void setMonth(int month) {
        this.month = month;
    }
    
    public void setApptType(String apptType) {
        this.apptType = apptType;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public Integer getMonth() {
        return month;
    }
    
    public String getApptType() {
        return apptType;
    }
    
        @Override
    public String toString() {
        return String.format("%d(%d,%s)", year, month, apptType);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.apptType);
        hash = 79 * hash + this.year;
        hash = 79 * hash + this.month;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppointmentTypeRpt other = (AppointmentTypeRpt) obj;
        if (!Objects.equals(this.apptType, other.apptType)) {
            return false;
        }
        if (this.year != other.year) {
            return false;
        }
        if (this.month != other.month) {
            return false;
        }
        return true;
    }
}
