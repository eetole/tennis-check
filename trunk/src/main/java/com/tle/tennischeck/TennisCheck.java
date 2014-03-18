/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tle.tennischeck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author leppaton
 */
public class TennisCheck {

    URL url;
    InputStream is;
    BufferedReader br;
    String line;
    boolean firstRun = true;

    List<Date> freeTimes = new ArrayList<Date>();
    
    public static void main(String args[]) {
        new TennisCheck();
    }

    public TennisCheck() {
        
        
        TimerTask task;
        task = new TimerTask() {
            
            @Override
            public void run() {
                Date today = new Date();
                today.setSeconds(59);
                for(int i = 0; i<2; i++){
                    int add = 24*60*60*1000*i;
                    Date d = new Date(today.getTime()+add);
                    if(firstRun){
                        freeTimes.addAll(getFreeSlots(d));
                    }
                    else{
                        List<Date> newFreeTimes = getFreeSlots(d);
                        List<Date> toAdd = new ArrayList<>();
                        for (Date newDate : newFreeTimes) {
                            Iterator<Date> it = freeTimes.iterator();
                            boolean found = false;
                            while(it.hasNext()){
                                Date oldDate = it.next();
                                System.out.println("new:"+newDate);
                                System.out.println("old:"+oldDate);
                                if(newDate.compareTo(oldDate) == 0){
                                    System.out.println("Already exists: "+newDate);
                                    found = true;
                                    break;
                                }
                            }
                            if(!found && newDate.after(today)){
                                System.out.println("Sending mail for "+newDate);
                                freeTimes.add(newDate);
                            }
                        }
                        
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TennisCheck.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                firstRun = false;
                
                //clean old entries
                Iterator<Date> it = freeTimes.iterator();
                while (it.hasNext()) {
                    Date date = it.next();
                    if(date.before(today)){
                        it.remove();
                        System.out.println("Remove:"+date);
                    }
                }
                System.out.println("freeTime:\n"+freeTimes.size()+freeTimes);
            }
            
            private int findCourtNumber(String str){
                String tmp = str.substring(1, 3).trim();
                //System.out.println("tmp:"+tmp);
                int rv = 0;
                try{
                    rv = Integer.parseInt(tmp);
                }catch(NumberFormatException nfe){
                    rv = Integer.parseInt(tmp.substring(0,1));
                }
                return rv;
            }
            
            private List<Date> getFreeSlots(Date date){
                System.out.println("Fetching "+date);
                String dayToCheck = (date.getYear()+1900)+"-"+addLeadingZero(date.getMonth()+1)+"-"+addLeadingZero(date.getDate());
                List<Date> times = new ArrayList<>();
                try {
                    Document doc = Jsoup.connect("http://www.slsystems.fi/tampereentenniskeskus/ftpages/ft-varaus-table-01.php?laji=1&pvm="+dayToCheck).get();

                    //System.out.println(doc.html());
                    Elements els = doc.getElementsByClass("f11");
                    for (Element element : els) {
                        String html = element.html();
                        
                        if (element.text().equals("Varaa")) {
                            //System.out.println("Court:" + findCourtNumber(element.parent().html()));
                            int court = findCourtNumber(element.parent().html());
                            String time = html.substring(html.indexOf("klo") + 4, html.indexOf("klo") + 9);
                            //System.out.println("time:" + time);
                            String[] parts = time.split(":");
                            Date timeInDate = new Date(date.getTime());
                            timeInDate.setHours(Integer.parseInt(parts[0]));
                            timeInDate.setMinutes(Integer.parseInt(parts[1]));
                            timeInDate.setSeconds(court);
                            times.add(timeInDate);
                        }
                    }
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException ioe) {
                        // nothing to see here
                    }
                }
                
                return times;
            }
            
            private String addLeadingZero(int i){
                if(i<10){
                    return "0"+i;
                }
                else{
                    return ""+i;
                }
            }
        };
        
        
        Timer timer = new Timer("TennisChecker");
        timer.schedule(task, 0, 2*60*1000);

    }
    
    private void sendMail(String time){
        System.out.println("Sending mail for"+time);
    }

    
    
}
