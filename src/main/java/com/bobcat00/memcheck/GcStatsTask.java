// MemCheck - Output CraftBukkit/Spigot memory statistics
// Copyright 2019 Bobcat00
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.bobcat00.memcheck;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import org.bukkit.scheduler.BukkitRunnable;

public class GcStatsTask extends BukkitRunnable
{
    public long gcAvg = 0L; // msec
    
    private long lastGcCount = 0L;
    private long lastGcTime = 0L;
    
    @SuppressWarnings("unused")
    private MemCheck plugin;
    
    public GcStatsTask(MemCheck plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public void run()
    {
        long gcCount = 0L;
        long gcTime = 0L;

        for(GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans())
        {
            long count = gc.getCollectionCount();
            if(count >= 0L)
            {
                gcCount += count;
            }
            long time = gc.getCollectionTime();
            if(time >= 0L)
            {
                gcTime += time;
            }
        }
        
        long deltaGcCount = gcCount - lastGcCount;
        long deltaGcTime = gcTime - lastGcTime;
        
        if (deltaGcCount > 0L)
        {
            gcAvg = deltaGcTime / deltaGcCount;
        }
        else
        {
            gcAvg = 0L;
        }
        
        lastGcCount = gcCount;
        lastGcTime = gcTime;

    }

}
