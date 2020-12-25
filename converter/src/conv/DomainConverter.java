/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Christian
 */
public class DomainConverter {
    
    private static int TIME_DELTA = 30;
    private static TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC+8:00");
    private static int INDEX_TIMESTAMP = 0;
    private static int INDEX_PER_LINE = 1;
    private static int LENGTH_TIMESTAMP = 10;
    private static ConverterInterface USER_NAMING_INTERFACE = null;
    
    private final static ConverterInterface DEFAULT_NAMING_INTERFACE = new ConverterInterface() {
        @Override
        public File getOutputFile(Calendar c) {
            String fn = String.format("%03x%02x%02x.tdd", c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
            return new File(fn);
        }

        @Override
        public String getWriteLine(int pulses) {
            return pulses+"";
        }

        @Override
        public long getPreviousTimestamp() {
            return 0;
        }

        @Override
        public int getPreviousPulseCount() {
            return 0;
        }

        @Override
        public void savePostConversionData(long timestamp, int pulseCount) {
            
        }
    };
   
    private static ConverterInterface getConverterInterface(){
        return USER_NAMING_INTERFACE==null?DEFAULT_NAMING_INTERFACE:USER_NAMING_INTERFACE;
    }
    
    public static int convert(File pddFile) throws FileNotFoundException, IOException{
        long lastTimestamp = getConverterInterface().getPreviousTimestamp();
        int lastPulseCount = getConverterInterface().getPreviousPulseCount();
        int writeCount = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(pddFile))) {
            Calendar calendar = Calendar.getInstance(TIME_ZONE);
            int pulseCounter = lastPulseCount;
            long targetTimestamp = lastTimestamp;
            
            BufferedWriter bw;
            
            String line;
            while((line = br.readLine()) != null){
                long timestamp;
                if(INDEX_TIMESTAMP == 0) timestamp = Long.parseLong(line.substring(0, LENGTH_TIMESTAMP));
                else timestamp = Long.parseLong(line.split(",")[INDEX_TIMESTAMP].substring(0, 10));
                if(timestamp < lastTimestamp) continue;
                pulseCounter++;
                
                calendar.setTime(new Date(timestamp*1000));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                
                File tddFile = getConverterInterface().getOutputFile(calendar);
                if(!tddFile.exists()){
                    tddFile.createNewFile();
                    bw = new BufferedWriter(new FileWriter(tddFile, true));
                    targetTimestamp = (calendar.getTime().getTime()/1000) + TIME_DELTA;
                    
                    while(timestamp > targetTimestamp){
                        writeCount++;
                        bw.write(getConverterInterface().getWriteLine(0));
                        bw.newLine();
                        targetTimestamp += TIME_DELTA;
                    }
                    
                    bw.close();
                }
                
                while(timestamp > targetTimestamp){
                    bw = new BufferedWriter(new FileWriter(tddFile, true));
                    bw.write(getConverterInterface().getWriteLine(pulseCounter));
                    bw.newLine();
                    
                    writeCount++;
                    pulseCounter = 0;
                    targetTimestamp += TIME_DELTA;
                    bw.close();
                }
            }
            
            getConverterInterface().savePostConversionData(targetTimestamp, lastPulseCount);
        }
        
        return writeCount;
        
    }
    
    public static void setTimeDelta(int delta) throws IllegalArgumentException{
        if(delta < 1) throw new IllegalArgumentException("Delta time cannot be less than 1 second");
        TIME_DELTA = delta;
    }
    
    public static void setTimeZone(TimeZone tz){
        TIME_ZONE = tz;
    }
    
    public static void setTimestampIndex(int index) throws IllegalArgumentException{
        if(index < 0) throw new IllegalArgumentException("Timestamp index cannot be less than 1");
        if(index >= INDEX_PER_LINE) throw new IllegalArgumentException("Timestamp index cannot be greater than or equal to indices per line");
        INDEX_TIMESTAMP = index;
    }
    
    public static void setIndicesPerLine(int indices) throws IllegalAccessException{
        if(indices < 1) throw new IllegalArgumentException("Number of indices per line cannot be less than 1");
        if(indices <= INDEX_TIMESTAMP) throw new IllegalArgumentException("Number of indices per line cannot be less than or equal to timestamp index");
        INDEX_PER_LINE = indices;
    }
    
    public static void setConverterInterface(ConverterInterface ci){
        USER_NAMING_INTERFACE = ci;
    }
    
    public static int getTimeDelta(){
        return TIME_DELTA;
    }
    
    public static TimeZone getTimeZone(){
        return TIME_ZONE;
    }
    
    public static int getTimestampIndex(){
        return INDEX_TIMESTAMP;
    }
    
    public static int getIndexPerLine(){
        return INDEX_PER_LINE;
    }
    
}
