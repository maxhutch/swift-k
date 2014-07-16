/*
 * Copyright 2012 University of Chicago
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.griphyn.vdl.karajan.monitor;

import java.util.Timer;
import java.util.TimerTask;

public class Stats {
    public static final int PERIOD = 1000;
    
    private static Timer timer = new Timer();
    
    public Stats() {
        timer.schedule(new TimerTask() {
            public void run() {
                period = 0;
            }}, PERIOD, PERIOD);
    }
    
    private int total, current, period;
    
    public synchronized void add() {
        total++;
        current++;
        period++;
    }
    
    public synchronized void remove() {
        current--;
    }
    
    public int getTotal() {
        return total;
    }
    
    public int getCurrent() {
        return current;
    }
    
    public int getPeriod() {
        return period;
    }
}