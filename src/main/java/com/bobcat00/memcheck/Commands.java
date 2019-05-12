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

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
            
            // TPS
            
            // Hook in to Essentials
            Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
            IEssentials ess = (IEssentials)essentials;
            double tps = ess.getTimer().getAverageTPS();
            
            ChatColor tpsColor;
            if (tps > 20.0)
            {
                tps = 20.0;
                tpsColor = ChatColor.GREEN;
            }
            else if (tps >= 18.0)
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
            
            // Used and free memory
            
            long freeMemory  = Runtime.getRuntime().freeMemory();
            long maxMemory   = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            
            long used = totalMemory - freeMemory;
            long free = maxMemory - used;
            
            // GC stats
            
            long gcAvg = plugin.gcStats.gcAvg;
            
            // Output message
            
            DecimalFormat df1 = new DecimalFormat("#.0");
            
            sender.sendMessage(ChatColor.GOLD + "TPS: "   + tpsColor + df1.format(tps)   +
                               ChatColor.GOLD + " Used: " + ChatColor.RED + used/1048576 + " MB (" + (used*100)/maxMemory + "%)" +
                               ChatColor.GOLD + " Free: " + ChatColor.RED + free/1048576 + " MB" +
                               ChatColor.GOLD + " GC: "   + ChatColor.RED + gcAvg        + " ms");

            // Normal return
            return true;
        }
        
        return false;
    }
    
}
