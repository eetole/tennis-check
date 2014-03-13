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

    public static void main(String args[]) {
        new TennisCheck();
    }

    public TennisCheck() {
        try {
//            System.out.println("Setting proxy");
//            System.getProperties().put("proxySet", "true");
//            System.getProperties().put("http.proxyHost", "http://10.32.235.40");
//            System.getProperties().put("http.proxyPort", "8080");
//            System.setProperty("java.net.useSystemProxies", "true");
            Document doc = Jsoup.connect("http://www.slsystems.fi/tampereentenniskeskus/ftpages/ft-varaus-table-01.php?laji=1&pvm=2014-03-13").get();
            
            System.out.println("f11:"+doc.getElementsByClass("f11"));
            Elements els =  doc.getElementsByClass("f11");
            for (Element element : els) {
                String html = element.html();
                System.out.println("text:"+element.text());
                if(element.text().equals("Varaa")){
                    System.out.println("time:"+html.substring(html.indexOf("klo")+4, html.indexOf("klo")+9));
                    
                }
                        
            }
/*
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication("domain\\user", "password".toCharArray());
                }
            });

            url = new URL("http://www.slsystems.fi/tampereentenniskeskus/ftpages/ft-varaus-table-01.php?laji=1&pvm=2014-03-13");
            URLConnection con = url.openConnection();

            is = con.getInputStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            
            */
        
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

    }
    
    private void sendMail(String time){
        System.out.println("Sending mail for"+time);
    }

}
