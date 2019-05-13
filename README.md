# MemCheck
**_Output CraftBukkit/Spigot memory statistics_**

I discovered that the memory values reported by Essentials come directly from Java, and don't really show what a server owner needs to know.  The explanation will be easier with the help of the following diagram I stole from Stackoverflow:

![alt text](https://raw.githubusercontent.com/Bobcat00/MemCheck/master/src/main/resources/java_memory2.png "Logo Title Text 1")

Java provides, and Essentials reports, the three values shown in red.  This is not really what a server owner needs.  The more relevant values are the two shown in blue at the bottom: **used**, which is the amount of the heap currently used by the program, and **totalFree**, which is the amount of memory available for additional objects on the heap.

This plugin adds a /mem command which outputs the amount of used memory in MB and as a percentage of the maximum memory, and the totalFree memory in MB.  As a bonus, the server's current TPS as calculated by Essentials and the average garbage collection time in the previous minute are shown.  At startup, the number of processors (threads), maximum memory, and Java command line options are written to the server log file.

This allows the server owner to see how much memory is being used and if the memory available for additional objects is too small, too big, or just right.
