/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michael
 */
public class Appointment {
    public int appointmentId;
    public int customerId;
    public String title;
    public String description;
    public String location;
    public String contact;
    public String url;
    public Date start;
    public Date end;
    public Date createDate;
    public String createdBy;
    public Date lastUpdate;
    public String lastUpdateBy;

    public Appointment() {
    
    }
    public Appointment(int apptId, Customer customer, String apptType, String description, String location, String contact, String url, LocalDate apptDate, String apptDuration, String apptTime, TimeZone tz) {
        this.appointmentId = apptId;
        this.customerId = customer.CustomerId;
        this.title = apptType;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.url = url;
        
        //format calandar and start date
        try {
        String string = apptDate.getMonth().name() + " " + apptDate.getDayOfMonth() + ", " + apptDate.getYear() + " " + Arrays.toString(apptTime.getBytes());
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy hh:mm", Locale.ENGLISH);
        format.setTimeZone(tz);
        Date startDate = format.parse(string);
        this.start = startDate;
        
        //Adding end date.
        final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
        long t = startDate.getTime();
        this.end = new Date(t + (Integer.parseInt(apptDuration) * ONE_MINUTE_IN_MILLIS));
        
        } catch (ParseException ex) {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
