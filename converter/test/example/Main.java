/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import conv.ConverterInterface;
import conv.DomainConverter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 *
 * @author Christian
 */
public class Main {
    
    private final static ConverterInterface CONVERTER_INTERFACE = new ConverterInterface() {
        @Override
        public File getOutputFile(Calendar calendar) {
            //specify how you want the output file to be named and located
            
            String s = new SimpleDateFormat("YYYYddMM").format(calendar.getTime())+".csv";
            return new File(s);
        }

        @Override
        public String getWriteLine(long timestamp, int pulses) {
            //you may also want to write the timestamp at which the pulse is attributed to
            
            return pulses+"";
        }

        @Override
        public long getPreviousTimestamp() {
            //retrieve the previous saved timestamp from a file
            
            return 0;
        }

        @Override
        public int getPreviousPulseCount() {
            //retrieve the previous saved pulse count from a file
            
            return 0;
        }

        @Override
        public void savePostConversionData(long timestamp, int pulseCount) {
            //save the last timestamp and pulse count into a file for next conversion use
        }
    };
    
    public static void main(String[] args) throws IllegalAccessException, IOException {
        DomainConverter.setTimeDelta(120);
        DomainConverter.setIndicesPerLine(1);
        DomainConverter.setTimestampIndex(0);
        DomainConverter.setConverterInterface(CONVERTER_INTERFACE);
        DomainConverter.setTimeZone(TimeZone.getTimeZone("UTC+8:00"));
        
        File yourPulseDomainFile = new File("test.csv");
        DomainConverter.convert(yourPulseDomainFile);
    }
}
