/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.util.stat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.util.NanoTimer;

/**
 * This class acts as a centralized data store for statistics. As data is added
 * to the collector, a sum total is kept as well as the total number of data
 * samples given for the particular stat.
 * 
 * @author Joshua Slack
 */
public class StatCollector {
    private static final Logger logger = Logger.getLogger(StatCollector.class
            .getName());

    private static final double TO_MS = 1.0/1000000;

    /**
     * How many distinct past aggregate samples are kept before the oldest one
     * is dropped on add. You can multiply this by the time sample rate to
     * determine the total history length in time.
     */
    protected static int maxSamples = 100;

    /**
     * Our map of current stat values. Current means values that have been
     * collected within the current time sample. For example, if sampleRate =
     * 1.0, then current will hold values collected since the last 1 second
     * ping.
     */
    protected static HashMap<StatType, StatValue> current = new HashMap<StatType, StatValue>();

    protected static List<MultiStatSample> historical = Collections
            .synchronizedList(new LinkedList<MultiStatSample>());

    /**
     * How long to gather stats as a single unit before pushing them onto the historical stack.
     */
    protected static double sampleRateMS = 1000;

    protected static double lastSampleTime = 0;
    
    protected static double lastTimeCheckMS = 0;

    protected static ArrayList<WeakReference<StatListener>> listeners = new ArrayList<WeakReference<StatListener>>();

    protected static double startOffset = 0;

    protected static boolean ignoreStats = false;
    
    protected static Stack<StatType> timeStatStack = new Stack<StatType>();
    
    protected static HashSet<StatType> timedStats = new HashSet<StatType>();
    
    protected static NanoTimer timer = new NanoTimer();

    private static long pausedTime;

    private static long pausedStartTime;

    protected StatCollector() { }
    
    /**
     * Construct a new StatCollector.
     * 
     * @param sampleRateMS
     *            The amount of time between aggregated samples in milliseconds.
     */
    public static void init(final long sampleRateMS, int maxHistorical) {
        StatCollector.sampleRateMS = sampleRateMS;
        StatCollector.maxSamples = maxHistorical;
    }

    public static void addStat(final StatType type, double statValue) {
        if (ignoreStats) return;
        
        synchronized (current) {
            StatValue val = current.get(type);
            if (val == null) {
                val = new StatValue(0,0);
                current.put(type, val);
            }
            val.val += statValue;
            val.iterations++;
        }
    }

    public static void startStat(final StatType type) {
        if (ignoreStats || !timedStats.contains(type)) return;

        synchronized (current) {
            StatType top = !timeStatStack.isEmpty() ? timeStatStack.peek() : null;

            double timeMS = timer.getTime() * TO_MS;
            if (top != null) {
                // tally timer and include in stats.
                StatValue val = current.get(top);
                val.val += (timeMS - lastTimeCheckMS);
            } else {
                StatValue val = current.get(StatType.STAT_UNSPECIFIED_TIMER);
                if (val == null) {
                    val = new StatValue(0,1);
                    current.put(StatType.STAT_UNSPECIFIED_TIMER, val);
                }
                val.val += (timeMS - lastTimeCheckMS);
            }

            lastTimeCheckMS = timeMS;
            timeStatStack.push(type);

            if (type != null) {
                StatValue val = current.get(type);
                if (val == null) {
                    val = new StatValue(0,0);
                    current.put(type, val);
                }
                val.iterations++;
            }
        }
    }

    public static void endStat(final StatType type) {
        if (ignoreStats || !timedStats.contains(type)) return;

        synchronized (current) {
            // This will throw error if called out of turn.
            StatType top = timeStatStack.pop();

            double timeMS = timer.getTime() * TO_MS;

            // tally timer and include in stats.
            StatValue val = current.get(top);
            val.val += (timeMS - lastTimeCheckMS);

            lastTimeCheckMS = timeMS;

            // Pop until we find our stat type
            while (!top.equals(type)) {
                logger.log(Level.WARNING, "Mismatched endStat, found {0}.  Expected \"{1}\"", new StatType[] {top, type});
                top = timeStatStack.pop();
            }
        }
    }

    public static synchronized void update() {
        double timeMS = timer.getTime() * TO_MS;
        double elapsed = timeMS - lastSampleTime;

        // Only continue if we've gone past our sample time threshold
        if (elapsed < sampleRateMS) {
            return;
        }

        synchronized (current) {
            // Check if we have a timed stat in tracking... if so, add it in
            if (!timeStatStack.isEmpty()) {
                // tally timer and include in stats.
                StatValue val = current.get(timeStatStack.peek());
                val.val += (timeMS - lastTimeCheckMS);
                lastTimeCheckMS = timeMS;
                // reset iterations of all stack to 0
                for (int x = timeStatStack.size(); --x >= 0; ) {
                    StatValue val2 = current.get(timeStatStack.get(x));
                    if (val2 != null) {
                        val2.iterations = 0;
                    }
                }
                
                // current iterations is 1 (for the current call.)
                val.iterations = 1;
            } else {
                StatValue val = current.get(StatType.STAT_UNSPECIFIED_TIMER);
                if (val != null) {
                    val.val += (timeMS - lastTimeCheckMS);
                    lastTimeCheckMS = timeMS;
                    val.iterations = 1;
                }
            }

            StatValue val = current.get(StatType.STAT_UNSPECIFIED_TIMER);
            if (val != null) {
                val.val -= (pausedTime * TO_MS);
            }
            
            // Add "current" hash into historical stat list
            MultiStatSample sample = MultiStatSample.createNew(current);
            sample.actualTime = elapsed - (pausedTime * TO_MS);
            historical.add(sample); // adds onto tail

            // reset the "current" hash... basically set things to 0 to decrease
            // object recreation
            for (StatValue value : current.values()) {
                value.iterations = 0;
                value.val = 0;
            }
        }

        // reset startOffset
        startOffset = 0;
        pausedTime = 0;
        
        // stat list should drop old stats from list when greater than a certain
        // threshold.
        while (historical.size() > maxSamples) {
            MultiStatSample removed = historical.remove(0); // removes from head
            if (removed != null) {
                startOffset += removed.actualTime;
            }
        }

        lastSampleTime = timeMS;
        fireActionEvent();
    }

    /**
     * Add a listener to the pool of listeners that are notified when a new
     * stats aggregate is created (at the end of each time sample).
     * 
     * @param listener
     *            the listener to add
     */
    public static void addStatListener(final StatListener listener) {
        listeners.add(new WeakReference<StatListener>(listener));
    }

    /**
     * Removes a listener from the pool of listeners that are notified when a
     * new stats aggregate is created (at the end of each time sample).
     * 
     * @param listener
     *            the listener to remove
     */
    public static boolean removeStatListener(final StatListener listener) {
        return listeners.remove(new WeakReference<StatListener>(listener));
    }

    /**
     * Cleans the listener pool of all listeners.
     */
    public static void removeAllListeners() {
        listeners.clear();
    }

    /**
     * Add a type to the set of stat types that are paid attention to when doing
     * timed stat checking.
     * 
     * @param type
     *            the listener to add
     */
    public static void addTimedStat(final StatType type) {
        timedStats.add(type);
    }

    /**
     * Removes a type from the set of stat types that are paid attention to when
     * doing timed stat checking.
     * 
     * @param type
     *            the listener to remove
     */
    public static boolean removeTimedStat(final StatType type) {
        return timedStats.remove(type);
    }

    /**
     * Cleans the set of stat types we paid attention to when doing timed stat
     * checking.
     */
    public static void removeAllTimedStats() {
        timedStats.clear();
    }

    /**
     * Notifies all registered listeners that a new stats aggregate was created.
     */
    public static void fireActionEvent() {
    	for (Iterator<WeakReference<StatListener>> it = listeners.iterator(); it.hasNext(); ) {
    		WeakReference<StatListener> ref = it.next();
    		StatListener l = ref.get();
    		if (l == null) {
    			it.remove();
    			continue;
    		} else {
    			l.statsUpdated();
    		}
        }
    }

    public static double getStartOffset() {
        return startOffset;
    }

    public static double getSampleRate() {
        return sampleRateMS;
    }

    public static void setSampleRate(long sampleRateMS) {
        StatCollector.sampleRateMS = sampleRateMS;
    }

    public static void setMaxSamples(int samples) {
        StatCollector.maxSamples = samples;
    }

    public static int getMaxSamples() {
        return maxSamples;
    }

    public static List<MultiStatSample> getHistorical() {
        return historical;
    }

    public static boolean isIgnoreStats() {
        return ignoreStats;
    }

    public static void setIgnoreStats(boolean ignoreStats) {
        StatCollector.ignoreStats = ignoreStats;
    }

    public static boolean hasHistoricalStat(StatType type) {
        for (MultiStatSample mss : historical) {
            if (mss.values.containsKey(type)) return true;
        }
        return false;
    }

    /**
     * Call this if you've caught an error, etc and you need to reset timed
     * stats collecting. 
     * 
     * NOTE: You must ensure you are not inside a START/END
     * timed block, (or you recreate any necessary start calls) otherwise when
     * endStat is called a stack exception will occur.
     */
    public static void resetTimedStack() {
        timeStatStack.clear();
    }

    /**
     * TODO: consider a way to pause just a set of stats?
     */
    public static void pause() {
        setIgnoreStats(true);
        pausedStartTime = timer.getTime();
    }

    public static void resume() {
        setIgnoreStats(false);
        pausedTime += (timer.getTime() - pausedStartTime);
    }
}
