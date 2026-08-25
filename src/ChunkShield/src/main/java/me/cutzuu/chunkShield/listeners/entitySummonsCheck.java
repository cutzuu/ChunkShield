package me.cutzuu.chunkShield.listeners;

import me.cutzuu.chunkShield.main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Bukkit.*;

public final class entitySummonsCheck implements Listener
{
    ////////////////////////////////////////////////////////////////////////////
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e)
    {
        int x = (int) e.getEntity().getLocation().getX();
        int z = (int) e.getEntity().getLocation().getZ();
        int y = (int) e.getEntity().getLocation().getY();

        World world = e.getEntity().getWorld();

        if (main.Global.configToggleEntityCheck)
        {
            if (main.Global.configToggleEntityCheck_50)
            {
                if (ThreadLocalRandom.current().nextBoolean())
                {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> entityCheck(e, x, z, y, world), 1L);
                }
            }
            else Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> entityCheck(e, x, z, y, world), 1L);
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    private static void entityCheck(EntitySpawnEvent e, int x, int z, int y, World world)
    {
        // ===== ENTITIES ===== //
        Map<EntityType, List<Entity>> namedEntityList   = new HashMap<>();
        Map<EntityType, List<Entity>> unnamedEntityList = new HashMap<>();

        Entity entity1 = e.getEntity();
        Chunk chunk = entity1.getLocation().getChunk();

        boolean Removed;
        boolean Named;

        int length = chunk.getEntities().length;
        if (length > main.Global.configMinEntityWarning && main.Global.configToggleAlertChunkWarning)
        {
            Removed = false;
            Named = false;

            EntityType type = entity1.getType(); //can be ignored, just makes the method stop bitching.
            EntityAlertMessages(x, z, y, world, type, Removed, Named, length);
        }

        //Op 1: Did the owner clear their lists?
        if (main.Global.theEntityLimits.isEmpty() && main.Global.theNamedEntityLimits.isEmpty()) return;


        //Op 2: Grab entities in chunk.
        for (Entity entity : chunk.getEntities())
        {
            //If the chunk contains no listed entities, move on.
            if (!main.Global.theEntityLimits.containsKey(entity.getType())) continue;

            boolean isNamed = entity.customName() != null;
            (isNamed ? namedEntityList : unnamedEntityList)
                    .computeIfAbsent(entity.getType(), k -> new ArrayList<>())
                    .add(entity);
        }

        //Op 3: Grab for any entities that are not named.
        // Unnamed Limits
        for (Map.Entry<EntityType, List<Entity>> entry : unnamedEntityList.entrySet())
        {
            EntityType type = entry.getKey();
            Integer noNameOBJ = main.Global.theEntityLimits.get(type);
            List<Entity> list = entry.getValue();
            int limit = noNameOBJ;
            if (list.size() > limit)
            {
                int toRemove = list.size() - limit;
                for (main.Global.Entity_unnamedCount = 0; main.Global.Entity_unnamedCount < toRemove; main.Global.Entity_unnamedCount++)
                {
                    list.get(main.Global.Entity_unnamedCount).remove();
                    list.get(main.Global.Entity_unnamedCount).getLocation();
                    if (main.Global.Entity_unnamedCount < 10)
                    {
                        if(main.Global.configTogglePurgeEffect) world.spawnParticle(Particle.LAVA, list.get(main.Global.Entity_unnamedCount).getLocation().toCenterLocation(), 4);
                    }
                    main.Global.entitiesRemoved++;
                }
                if (main.Global.Entity_unnamedCount != 0)
                {
                    Removed = true;
                    Named = false;
                    if (main.Global.configToggleAlertEntityLimit) EntityAlertMessages(x, z, y, world, type, Removed, Named, length);
                }
            }
        }

        // Named Limits
        for (Map.Entry<EntityType, List<Entity>> entry : namedEntityList.entrySet())
        {
            EntityType type = entry.getKey();
            Integer namedOBJ = main.Global.theNamedEntityLimits.get(type);
            List<Entity> list = entry.getValue();
            int cap = namedOBJ;
            if (list.size() > cap)
            {
                int toRemove = list.size() - cap;
                for (main.Global.Entity_namedCount = 0; main.Global.Entity_namedCount < toRemove; main.Global.Entity_namedCount++)
                {
                    list.get(main.Global.Entity_namedCount).remove();
                    if (main.Global.Entity_namedCount < 10)
                    {
                        if(main.Global.configTogglePurgeEffect) world.spawnParticle(Particle.LAVA, list.get(main.Global.Entity_unnamedCount).getLocation().toCenterLocation(), 4);
                    }
                    main.Global.entitiesRemoved++;
                }
                if (main.Global.Entity_namedCount != 0)
                {
                    if (main.Global.configToggleAlertEntityLimit)
                    {
                        Removed = true;
                        Named = true;
                        EntityAlertMessages(x, z, y, world, type, Removed, Named, length);
                    }
                }
            }
        }
    }

    private static void EntityAlertMessages(int x, int z, int y, World world, EntityType type, boolean Removed, boolean Named, int length)
    {
        ClickEvent<ClickEvent.Payload.Text> copyCoords = ClickEvent.copyToClipboard(x + " " + y + " " + z);
        HoverEvent<?> hoverCoords = HoverEvent.showText(Component.text("Click to copy coordinates.", NamedTextColor.GREEN));

        Component STYLE = Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("- - - - - - - - - - - - - - - - - - - - - - - - - ", NamedTextColor.GRAY))
                .append(Component.text("■", NamedTextColor.RED))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();

        Component subMessage = Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("Location", NamedTextColor.YELLOW))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(world.getName() + " ", NamedTextColor.GOLD))
                .append(Component.text("/ ", NamedTextColor.GRAY))
                .append(Component.text("[" + x + ", " + y + ", " + z + "]", NamedTextColor.GREEN))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();

        if (Removed)
        {
            if (Named)
            {
                // Removed x10 Named ZOMBIE_VILLAGER.
                Component primaryMessage = Component.text()
                        .append(Component.text("■ ", NamedTextColor.RED))
                        .append(Component.text("Removed ", NamedTextColor.YELLOW))
                        .append(Component.text("x" + main.Global.Entity_namedCount + " ", NamedTextColor.GREEN))
                        .append(Component.text("Named ", NamedTextColor.GREEN))
                        .append(Component.text(type.name(), NamedTextColor.GOLD))
                        .append(Component.text(".", NamedTextColor.YELLOW))
                        .clickEvent(copyCoords)
                        .hoverEvent(hoverCoords)
                        .build();

                getServer().broadcast(STYLE, "chunkShield.alerts");
                getServer().broadcast(primaryMessage, "chunkShield.alerts");
                getServer().broadcast(subMessage, "chunkShield.alerts");
                getServer().broadcast(STYLE, "chunkShield.alerts");
            }
            else
            {
                // Removed x10 ZOMBIE_VILLAGER.
                Component message1 = Component.text()
                        .append(Component.text("■ ", NamedTextColor.RED))
                        .append(Component.text("Removed ", NamedTextColor.YELLOW))
                        .append(Component.text("x" + main.Global.Entity_unnamedCount + " ", NamedTextColor.GREEN))
                        .append(Component.text(type.name(), NamedTextColor.GOLD))
                        .append(Component.text(".", NamedTextColor.YELLOW))
                        .clickEvent(copyCoords)
                        .hoverEvent(hoverCoords)
                        .build();

                getServer().broadcast(STYLE, "chunkShield.alerts");
                getServer().broadcast(message1, "chunkShield.alerts");
                getServer().broadcast(subMessage, "chunkShield.alerts");
                getServer().broadcast(STYLE, "chunkShield.alerts");
            }
        }
        else
        {
            // CHUNK WARNING: Found x10 entities.
            Component message1 = Component.text()
                    .append(Component.text("■ ", NamedTextColor.RED))
                    .append(Component.text("CHUNK WARNING", NamedTextColor.RED))
                    .append(Component.text(": ", NamedTextColor.GRAY))
                    .append(Component.text("Found ", NamedTextColor.YELLOW))
                    .append(Component.text("x" + length + " ", NamedTextColor.GREEN))
                    .append(Component.text("entities", NamedTextColor.RED))
                    .append(Component.text(".", NamedTextColor.YELLOW))
                    .clickEvent(copyCoords)
                    .hoverEvent(hoverCoords)
                    .build();

            getServer().broadcast(STYLE, "chunkShield.alerts");
            getServer().broadcast(message1, "chunkShield.alerts");
            getServer().broadcast(subMessage, "chunkShield.alerts");
            getServer().broadcast(STYLE, "chunkShield.alerts");
        }






    }
}
