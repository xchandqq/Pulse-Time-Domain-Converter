/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conv;

import java.io.File;
import java.util.Calendar;

/**
 *
 * @author Christian
 */
public interface ConverterInterface {
    public File getOutputFile(Calendar calendar);
    public String getWriteLine(int pulses);
    public long getPreviousTimestamp();
    public int getPreviousPulseCount();
    public void savePostConversionData(long timestamp, int pulseCount);
}
