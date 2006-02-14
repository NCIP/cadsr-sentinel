// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/Timemarker.java,v 1.5 2006-02-14 21:38:12 hardingr Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import java.sql.Timestamp;
import java.util.Date;

/**
 * This is a simple utility class for tracking durations and timings. It operates like
 * a stop watch. The check() method will give the interval since the last call to check()
 * without changing the original start time. The reset() method will reset the original
 * start time for the object.
 * 
 * @author Larry Hebel Nov 8, 2005
 */
public class Timemarker
{
    /**
     * Constructor with default duration prefix.
     *
     */
    public Timemarker()
    {
        _prefix = "Duration ";
        _last = timeNow();
        _start = _last;
    }

    /**
     * Constructor with a caller supplied prefix.
     * 
     * @param prefix_ The prefix for the duration text.
     */
    public Timemarker(String prefix_)
    {
        _prefix = (prefix_ == null || prefix_.length() == 0) ? "" : prefix_;
        _last = timeNow();
        _start = _last;
    }
    
    /**
     * Return the duration since the construction of the object or the
     * last call to this method.
     * 
     * @return The string for the duration.
     */
    public String reset()
    {
        Timestamp end = timeNow();
        String txt = _prefix + timeDiff(end, _start);
        _last = end;
        _start = _last;
        return txt;
    }
    
    /**
     * Return a duration that does not reset the starting time of the object.
     * 
     * @return The string for the duration.
     */
    public String check()
    {
        Timestamp end = timeNow();
        String txt = _prefix + timeDiff(end, _last);
        _last = end;
        return txt;
    }
    
    /**
     * Get the current system time.
     * 
     * @return The current system time.
     */
    static public Timestamp timeNow()
    {
        Date now = new Date();
        return new Timestamp(now.getTime());
    }
    
    /**
     * Get a printable version of the difference between two times.
     * 
     * @param end_ The ending time.
     * @param start_ The starting time.
     * @return The string representation of the difference.
     */
    static public String timeDiff(Timestamp end_, Timestamp start_)
    {
        long elapsed = end_.getTime() - start_.getTime();
        int hrs = (int) (elapsed / (1000 * 60 * 60));
        elapsed -= hrs * 1000 * 60 * 60;
        hrs += 100;
        int mins = (int) (elapsed / (1000 * 60));
        elapsed -= mins * 1000 * 60;
        mins += 100;
        int secs = (int) (elapsed / 1000);
        elapsed -= secs * 1000;
        secs += 100;
        elapsed += 1000;
        String msg = String.valueOf(hrs).substring(1) + ":"
            + String.valueOf(mins).substring(1) + ":"
            + String.valueOf(secs).substring(1) + "."
            + String.valueOf(elapsed).substring(1);

        return msg;
    }

    private String _prefix;
    
    private Timestamp _start;
    
    private Timestamp _last;
}
