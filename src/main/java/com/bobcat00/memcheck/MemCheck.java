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
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.sun.management.OperatingSystemMXBean;

public class MemCheck extends JavaPlugin
{
    GcStatsTask gcStats;
    private BukkitTask gcStatsTask;
    
    @Override
    public void onEnable()
    {
        // Start periodic task
        gcStats = new GcStatsTask(this);
        gcStatsTask = gcStats.runTaskTimer(this,     // plugin
                                           0L,       // delay
                                           60L*20L); // period
        
        // Register commands
        this.getCommand("mem").setExecutor(new Commands(this));
        
        // Metrics
        int pluginId = 5018;
        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this, pluginId);
        getLogger().info("Metrics enabled if allowed by plugins/bStats/config.yml");
        
        // Log related items which are unchanging
        OperatingSystemMXBean os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        getLogger().info("Number of processors: " + Runtime.getRuntime().availableProcessors());
        getLogger().info("Physical memory: " + os.getTotalPhysicalMemorySize()/1048576L + " MB");
        getLogger().info("Maximum heap: " + Runtime.getRuntime().maxMemory()/1048576L + " MB");
        
        // Log maximum metaspace
        for (MemoryPoolMXBean memoryMXBean : ManagementFactory.getMemoryPoolMXBeans())
        {
            if ("Metaspace".equals(memoryMXBean.getName()))
            {
                long maxMetaspace = memoryMXBean.getUsage().getMax();
                if (maxMetaspace >= 0)
                {
                    getLogger().info("Maximum metaspace: " + maxMetaspace/1048576L + " MB");
                }
                break;
            }
        }
        
        // Log garbage collector name(s)
        for(GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans())
        {
            getLogger().info("GC name: " + gc.getName());
        }
        
        // Log server view distance
        getLogger().info("server.properties view-distance: " + getServer().getViewDistance());
        
        // Log command line options
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        getLogger().info("Command line options: ");
        for (String str : arguments)
        {
            getLogger().info("  " + str);
        }
    }
        
    @Override
    public void onDisable()
    {
        // HandlerList.unregisterAll(listeners);
        
        gcStatsTask.cancel();
    }

}
