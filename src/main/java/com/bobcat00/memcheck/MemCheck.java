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

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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
    }
        
    @Override
    public void onDisable()
    {
        // HandlerList.unregisterAll(listeners);
        
        gcStatsTask.cancel();
    }

}
