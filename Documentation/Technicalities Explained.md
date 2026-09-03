*This document is meant to be transparent on how checks are performed.*

If you run a larger server of 20+ then it is recommended to enable 50% Chance to cut events in half from occurring.

# Measure 1 - Chunk Scan Upon Loading
When a chunk is loaded, it has to meet a criteria before getting scanned for entities.
- Is it Enabled?
- Is it a new chunk? If not, continue. 
- Are there any entities at all?
- Are there the minimum amount of entities set by the player met?
- Are the entity lists in the config empty?
- Finally, are the listed entities even in the chunk?

Once we can confirm these six criteria are met then the chunk will be scanned. 


# Measure 2 - Block Placement
When a block is placed, it checks for the following:
- Is the block placed listed in the config? If not, stop.

If its a listed block, the chunk will receive an instantaneous scan.


# Measure 3 - Entity Spawning
Every time an entity spawns or a projectile is flung, the chunk it occured in is scanned if it meets the following:
- Is it Enabled?
- Is it a new chunk? If not, continue.
- Are there the minimum amount of entities set by the player met?
- Are the entity lists in the config empty?
- Finally, are the listed entities even in the chunk?

Once all four criteria are met, the scan will be made and perform as needed. 

# Measure 4 - Boat/Minecart Limitation
While a boat or minecart is moving or gets placed, a check is made for how many other boats and minecarts are in the vicinity.

The limit and the vicinity range are set by you. 

Default: 20 Vechicles can only exist in a 32 block range. 

# Measure 5 - Optional Miscellaneous Scans
There are three optional events that can also trigger a chunk scan. 
- PlayerCraftEvent
- MobDeathEvent
- ContainerOpenEvent

They are disabled by default but have a 10% chance to trigger.

They run off the same six Criteria that Measure 1 operates by.
