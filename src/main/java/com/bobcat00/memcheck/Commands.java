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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sun.management.OperatingSystemMXBean;

import net.ess3.api.IEssentials;

public class Commands implements CommandExecutor
{
    private MemCheck plugin;
    
    public Commands(MemCheck plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("mem"))
        {
            if (sender instanceof Player && !sender.hasPermission("memcheck.mem"))
            {
                sender.sendMessage("You do not have permission for this command");
                return true;
            }
            
            final DecimalFormat df1 = new DecimalFormat("#.0");
            final DecimalFormat df0 = new DecimalFormat("#");
            
            // TPS
            
            // Hook in to Essentials
            Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
            
            double tps = -1.0;
            ChatColor tpsColor = ChatColor.RED;
            
            if (essentials != null && essentials.isEnabled())
            {
                IEssentials ess = (IEssentials)essentials;
                tps = ess.getTimer().getAverageTPS();

                if (tps >= 18.0)
                {
                    tpsColor = ChatColor.GREEN;
                }
                else if (tps >= 15.0)
                {
                    tpsColor = ChatColor.YELLOW;
                }
                else
                {
                    tpsColor = ChatColor.RED;
                }
            }
            
            // CPU load
            
            OperatingSystemMXBean os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            
            double cpuLoad = os.getProcessCpuLoad(); // may be < 0.0
            
            // GC stats
            
            long gcAvg = plugin.gcStats.gcAvg;
            
            StringBuilder cpu = new StringBuilder();
            if (tps >= 0.0)
            {
                cpu.append(ChatColor.GOLD + "TPS: " + tpsColor + df1.format(tps) + " ");
            }
            if (cpuLoad >= 0.0)
            {
                cpu.append(ChatColor.GOLD + "CPU: " + ChatColor.RED + df0.format(cpuLoad*100.0) + "% ");
            }
            cpu.append(ChatColor.GOLD + "GC: " + ChatColor.RED + gcAvg + " ms ");
            
            // Chunks
            
            int chunks = 0;
            for (World world : plugin.getServer().getWorlds())
            {
                chunks += world.getLoadedChunks().length;
            }
            cpu.append(ChatColor.GOLD + "Chunks: " + ChatColor.RED + chunks);
            
            // Heap
            
            long freeMemory  = Runtime.getRuntime().freeMemory();
            long maxMemory   = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            
            long used = totalMemory - freeMemory;
            long free = maxMemory - used;
            
            String heap = ChatColor.GOLD + "Heap Used: " + ChatColor.RED + used/1048576L        + " MB (" + (used*100L)/maxMemory + "%)" +
                          ChatColor.GOLD + " Free: "     + ChatColor.RED + free/1048576L        + " MB" +
                          ChatColor.GOLD + " Alloc: "    + ChatColor.RED + totalMemory/1048576L + " MB";
            
            // Metaspace
            
            long usedMetaspace  = 0;
            long allocMetaspace = 0;
            long maxMetaspace   = -1;
            long freeMetaspace  = 0; // max - used
            
            for (MemoryPoolMXBean memoryMXBean : ManagementFactory.getMemoryPoolMXBeans())
            {
                if ("Metaspace".equals(memoryMXBean.getName()))
                {
                    usedMetaspace = memoryMXBean.getUsage().getUsed();
                    allocMetaspace = memoryMXBean.getUsage().getCommitted();
                    maxMetaspace   = memoryMXBean.getUsage().getMax(); // may be -1
                    break;
                }
            }
            
            StringBuilder meta = new StringBuilder(ChatColor.GOLD + "Metaspace Used: " + ChatColor.RED + usedMetaspace/1048576L + " MB");
            if (maxMetaspace > 0)
            {
                meta.append(" (" + (usedMetaspace*100L)/maxMetaspace + "%)");
                freeMetaspace = maxMetaspace - usedMetaspace;
                meta.append(ChatColor.GOLD + " Free: " + ChatColor.RED + freeMetaspace/1048576L + " MB");
            }
            meta.append(ChatColor.GOLD + " Alloc: " + ChatColor.RED + allocMetaspace/1048576L + " MB");
            
            // Output messages
            
            String[] strArray = new String[] {cpu.toString(), heap, meta.toString()};
            
            sender.sendMessage(strArray);

            // Normal return
            return true;
        }
        
        return false;
    }
    
}
