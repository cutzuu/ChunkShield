package me.cutzuu.chunkShield.listeners;

import me.cutzuu.chunkShield.main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import static org.bukkit.Bukkit.getServer;


public final class blockPlaceChecker implements Listener
{
    // Some blocks/items do not translate correctly when limited.
    // Ex: REDSTONE is not REDSTONE_WIRE


    public static List<Material> nonItemBlocks = List.of
            (
                    Material.REDSTONE_WIRE,
                    Material.TRIPWIRE
            );

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e)
    {
        Material placedType = e.getBlock().getType();
        Chunk chunk = e.getBlock().getChunk();
        Block block = e.getBlock();
        Material material = block.getType();
        Player player = e.getPlayer();
        PlayerInventory inventory = e.getPlayer().getInventory();
        ItemStack secondHand = inventory.getItemInOffHand();



        int x = e.getBlock().getX();
        int y = e.getBlock().getY();
        int z = e.getBlock().getZ();

        @NotNull String playerName = e.getPlayer().getName();

        // Patch to prevent unnecessary portal destruction.
        if (block.getType() == Material.END_PORTAL_FRAME)
        {
            if (main.Global.configToggleEndPortalFix)
            {
                againstEndPortalCheck(e, player, secondHand, inventory);
            }
            else if (main.Global.configToggleBlockCheck)
            {
                if (main.Global.configToggleBlockCheck_50)
                {
                    if (ThreadLocalRandom.current().nextBoolean())
                    {blockChunkCheck(chunk, placedType, x, y, z, playerName, material, block, player);}
                }
                else blockChunkCheck(chunk, placedType, x, y, z, playerName, material, block, player);
            }

        }
        else if (main.Global.configToggleBlockCheck)
        {
            if (main.Global.configToggleBlockCheck_50)
            {
                if (ThreadLocalRandom.current().nextBoolean())
                {blockChunkCheck(chunk, placedType, x, y, z, playerName, material, block, player);}
            }
            else blockChunkCheck(chunk, placedType, x, y, z, playerName, material, block, player);
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    private static void againstEndPortalCheck(BlockPlaceEvent e, Player player, ItemStack secondHand, PlayerInventory inventory)
    {
        Block against = e.getBlockAgainst();
        if (against.getType() == Material.END_PORTAL_FRAME)
        {
            // Main Hand Check
            if (player.getInventory().getItemInMainHand().getType() == Material.ENDER_EYE)
            {
                // Check1
                if (secondHand.getType() == Material.END_PORTAL_FRAME)
                {
                    e.setCancelled(true);
                    main.Global.blocksPrevented++;
                    inventory.setItemInOffHand(null);
                    inventory.remove(Material.END_PORTAL_FRAME);
                }
            }
            // Offhand Check
            else if (player.getInventory().getItemInOffHand().getType() == Material.ENDER_EYE)
            {
                // Check1
                if (player.getInventory().getItemInMainHand().getType() == Material.END_PORTAL_FRAME)
                {
                    e.setCancelled(true);
                    main.Global.blocksPrevented++;
                    inventory.remove(Material.END_PORTAL_FRAME);
                }
            }
        }
        else
        {
            e.setCancelled(true);
            main.Global.blocksPrevented++;
            inventory.remove(Material.END_PORTAL_FRAME);
            if (secondHand.getType() == Material.END_PORTAL_FRAME) inventory.setItemInOffHand(null);
        }
    }
    /////////////////////////////////////////////////////////////////////////////
    private static void blockChunkCheck(Chunk chunk, Material placedType, int x, int y, int z, String playerName, Material material, Block block, Player player)
    {
        // 1.0.5 Fix. If the list contains the block placed then check, otherwise don't.
        if (main.Global.theBlockLimits.containsKey(material))
        {
            World world = chunk.getWorld();
            World.Environment environment = chunk.getWorld().getEnvironment();

            // Patch to make sure we don't destroy natural Bedrock.
            if (environment == World.Environment.NORMAL)
            {
                main.Global.minY = -59;
                main.Global.maxY = world.getMaxHeight();
            }
            else if (environment == World.Environment.NETHER)
            {
                main.Global.minY = 5;
                main.Global.maxY = 122;
            }
            else if (environment == World.Environment.THE_END)
            {
                main.Global.minY = 0;
                main.Global.maxY = 128;
            }

            // Iterate each configured block type and prune extras within this chunk
            for (Map.Entry<Material, Integer> rule : main.Global.theBlockLimits.entrySet())
            {
                Material target = rule.getKey();
                int limit = Math.max(0, rule.getValue());

                // Collect all blocks of this type in deterministic order
                List<Block> matches = new ArrayList<>();
                for (int yLevel = main.Global.minY; yLevel < main.Global.maxY; yLevel++)
                {
                    for (int xLevel = 0; xLevel < 16; xLevel++)
                    {
                        for (int zLevel = 0; zLevel < 16; zLevel++)
                        {
                            Block b = chunk.getBlock(xLevel, yLevel, zLevel);
                            if (b.getType() == target) matches.add(b);
                        }
                    }
                }

                // Remove extras beyond the limit
                if (matches.size() > limit)
                {
                    for (int i = limit; i < matches.size(); i++)
                    {
                        Block b = matches.get(i);
                        if (placedType.isItem())
                        {
                            if (main.Global.configToggleAlertBlockLimit)
                            {
                                ClickEvent<ClickEvent.Payload.Text> copyCoords = ClickEvent.copyToClipboard(x + " " + y + " " + z);
                                HoverEvent<?> hoverCoords = HoverEvent.showText(Component.text("Click to copy coordinates.", NamedTextColor.GREEN));

                                Component messageA = Component.text()
                                        .append(Component.text("■ ", NamedTextColor.RED))
                                        .append(Component.text("- - - - - - - - - - - - - - - - - - - - - - - - - ", NamedTextColor.GRAY))
                                        .append(Component.text("■", NamedTextColor.RED))
                                        .clickEvent(copyCoords)
                                        .hoverEvent(hoverCoords)
                                        .build();

                                Component message1 = Component.text()
                                        .append(Component.text("■ ", NamedTextColor.RED))
                                        .append(Component.text(playerName + " ", NamedTextColor.GOLD))
                                        .append(Component.text("reached ", NamedTextColor.YELLOW))
                                        .append(Component.text(b.getType() + " ", NamedTextColor.RED))
                                        .append(Component.text("limit.", NamedTextColor.YELLOW))
                                        .clickEvent(copyCoords)
                                        .hoverEvent(hoverCoords)
                                        .build();

                                Component message2 = Component.text()
                                        .append(Component.text("■ ", NamedTextColor.RED))
                                        .append(Component.text("Location", NamedTextColor.YELLOW))
                                        .append(Component.text(": ", NamedTextColor.GRAY))
                                        .append(Component.text(world.getName() + " ", NamedTextColor.GOLD))
                                        .append(Component.text("/ ", NamedTextColor.GRAY))
                                        .append(Component.text("[" + x + ", " + y + ", " + z + "]", NamedTextColor.GREEN))
                                        .clickEvent(copyCoords)
                                        .hoverEvent(hoverCoords)
                                        .build();

                                getServer().broadcast(messageA, "chunkShield.alerts");
                                getServer().broadcast(message1, "chunkShield.alerts");
                                getServer().broadcast(message2, "chunkShield.alerts");
                                getServer().broadcast(messageA, "chunkShield.alerts");
                            }

                            if (player.hasPermission("chunkShield.blockBypass")) return;
                            if (player.getGameMode() != GameMode.CREATIVE)b.breakNaturally();
                            else b.setType(Material.AIR, false);
                            if(main.Global.configTogglePurgeEffect) world.spawnParticle(Particle.LAVA, b.getLocation().toCenterLocation(), 4);

                        }
                        else
                        {
                            if (main.Global.configToggleAlertBlockLimit)
                            {
                                ClickEvent<ClickEvent.Payload.Text> copyCoords = ClickEvent.copyToClipboard(x + " " + y + " " + z);
                                HoverEvent<?> hoverCoords = HoverEvent.showText(Component.text("Click to copy coordinates.", NamedTextColor.GREEN));

                                Component messageA = Component.text()
                                        .append(Component.text("■ ", NamedTextColor.RED))
                                        .append(Component.text("- - - - - - - - - - - - - - - - - - - - - - - - - ", NamedTextColor.GRAY))
                                        .append(Component.text("■", NamedTextColor.RED))
                                        .clickEvent(copyCoords)
                                        .hoverEvent(hoverCoords)
                                        .build();

                                Component message1 = Component.text()
                                        .append(Component.text("■ ", NamedTextColor.RED))
                                        .append(Component.text(playerName + " ", NamedTextColor.GOLD))
                                        .append(Component.text("reached ", NamedTextColor.YELLOW))
                                        .append(Component.text(b.getType() + " ", NamedTextColor.RED))
                                        .append(Component.text("limit.", NamedTextColor.YELLOW))
                                        .clickEvent(copyCoords)
                                        .hoverEvent(hoverCoords)
                                        .build();

                                Component message2 = Component.text()
                                        .append(Component.text("■ ", NamedTextColor.RED))
                                        .append(Component.text("Location", NamedTextColor.YELLOW))
                                        .append(Component.text(": ", NamedTextColor.GRAY))
                                        .append(Component.text(world.getName() + " ", NamedTextColor.GOLD))
                                        .append(Component.text("/ ", NamedTextColor.GRAY))
                                        .append(Component.text("[" + x + ", " + y + ", " + z + "]", NamedTextColor.GREEN))
                                        .clickEvent(copyCoords)
                                        .hoverEvent(hoverCoords)
                                        .build();

                                getServer().broadcast(messageA, "chunkShield.alerts");
                                getServer().broadcast(message1, "chunkShield.alerts");
                                getServer().broadcast(message2, "chunkShield.alerts");
                                getServer().broadcast(messageA, "chunkShield.alerts");
                            }
                            if (player.hasPermission("chunkShield.blockBypass")) return;

                            // 1.0.7 Fix - Material.REDSTONE_WIRE is not considered TRUE in Material.isItem().
                            // Paper Staff consider it a technicality but I disagree. This logic clearly highlights an issue with the API...
                            // Material.REDSTONE does not register placing down redstone which then gets turned into REDSTONE_WIRE.
                            // I dont even know what the hell Material.REDSTONE references. It does nothing when tested...
                            // Will revisit one day.....................................



                            if (nonItemBlocks.contains(b.getType()) && main.Global.theBlockLimits.containsKey(b.getType()))
                            {
                                if (player.getGameMode() == GameMode.CREATIVE) b.setType(Material.AIR);
                                else b.breakNaturally();
                                if(main.Global.configTogglePurgeEffect) world.spawnParticle(Particle.LAVA, b.getLocation().toCenterLocation(), 4);
                            }
                            else b.setType(Material.STONE, false); // no physics to avoid cascades
                        }
                        main.Global.blocksPrevented++;
                    }
                }
            }
        }
        // ===== 2) BLOCKS: collective DOOR/TRAPDOOR cap =====
        else if (block.getBlockData() instanceof Door || block.getBlockData() instanceof TrapDoor)
        {
            if (main.Global.configCollectiveDoorLimit >= 0)
            {
                World world = chunk.getWorld();
                World.Environment environment = chunk.getWorld().getEnvironment();

                if (environment == World.Environment.NORMAL)
                {
                    main.Global.minY = -59;
                    main.Global.maxY = world.getMaxHeight();
                }
                else if (environment == World.Environment.NETHER)
                {
                    main.Global.minY = 2;
                    main.Global.maxY = 125;
                }
                else if (environment == World.Environment.THE_END)
                {
                    main.Global.minY = world.getMinHeight();
                    main.Global.maxY = world.getMaxHeight();
                }

                int doorCount = 0;

                // We will remove anything beyond the cap. Deterministic scan order.
                for (int yLevel = main.Global.minY; yLevel < main.Global.maxY; yLevel++)
                {
                    for (int xLevel = 0; xLevel < 16; xLevel++)
                    {
                        for (int zLevel = 0; zLevel < 16; zLevel++)
                        {
                            Block b = chunk.getBlock(xLevel, yLevel, zLevel);
                            BlockData data = b.getBlockData();

                            // Count bottom halves of doors once
                            if (data instanceof Door d)
                            {
                                if (d.getHalf() == Door.Half.TOP) continue;
                                doorCount++;
                                if (doorCount > main.Global.configCollectiveDoorLimit)
                                {
                                    if (main.Global.configToggleAlertBlockLimit)
                                    {
                                        ClickEvent<ClickEvent.Payload.Text> copyCoords = ClickEvent.copyToClipboard(x + " " + y + " " + z);
                                        HoverEvent<?> hoverCoords = HoverEvent.showText(Component.text("Click to copy coordinates.", NamedTextColor.GREEN));

                                        Component messageA = Component.text()
                                                .append(Component.text("■ ", NamedTextColor.RED))
                                                .append(Component.text("- - - - - - - - - - - - - - - - - - - - - - - - - ", NamedTextColor.GRAY))
                                                .append(Component.text("■", NamedTextColor.RED))
                                                .clickEvent(copyCoords)
                                                .hoverEvent(hoverCoords)
                                                .build();

                                        Component message1 = Component.text()
                                                .append(Component.text("■ ", NamedTextColor.RED))
                                                .append(Component.text(playerName + " ", NamedTextColor.GOLD))
                                                .append(Component.text("reached ", NamedTextColor.YELLOW))
                                                .append(Component.text("Door Limit", NamedTextColor.RED))
                                                .append(Component.text(".", NamedTextColor.YELLOW))
                                                .clickEvent(copyCoords)
                                                .hoverEvent(hoverCoords)
                                                .build();

                                        Component message2 = Component.text()
                                                .append(Component.text("■ ", NamedTextColor.RED))
                                                .append(Component.text("Location", NamedTextColor.YELLOW))
                                                .append(Component.text(": ", NamedTextColor.GRAY))
                                                .append(Component.text(world.getName() + " ", NamedTextColor.GOLD))
                                                .append(Component.text("/ ", NamedTextColor.GRAY))
                                                .append(Component.text("[" + x + ", " + y + ", " + z + "]", NamedTextColor.GREEN))
                                                .clickEvent(copyCoords)
                                                .hoverEvent(hoverCoords)
                                                .build();

                                        getServer().broadcast(messageA, "chunkShield.alerts");
                                        getServer().broadcast(message1, "chunkShield.alerts");
                                        getServer().broadcast(message2, "chunkShield.alerts");
                                        getServer().broadcast(messageA, "chunkShield.alerts");
                                    }

                                    if (player.hasPermission("chunkShield.blockBypass")) return;
                                    if (player.getGameMode() != GameMode.CREATIVE)b.breakNaturally();
                                    else b.setType(Material.AIR, false);
                                    if(main.Global.configTogglePurgeEffect) world.spawnParticle(Particle.LAVA, b.getLocation().toCenterLocation(), 4);
                                    main.Global.blocksPrevented++;
                                }
                            }
                            else if (data instanceof TrapDoor)
                            {
                                doorCount++;
                                if (doorCount > main.Global.configCollectiveDoorLimit)
                                {
                                    if (main.Global.configToggleAlertBlockLimit)
                                    {
                                        ClickEvent<ClickEvent.Payload.Text> copyCoords = ClickEvent.copyToClipboard(x + " " + y + " " + z);
                                        HoverEvent<?> hoverCoords = HoverEvent.showText(Component.text("Click to copy coordinates.", NamedTextColor.GREEN));

                                        Component messageA = Component.text()
                                                .append(Component.text("■ ", NamedTextColor.RED))
                                                .append(Component.text("- - - - - - - - - - - - - - - - - - - - - - - - - ", NamedTextColor.GRAY))
                                                .append(Component.text("■", NamedTextColor.RED))
                                                .clickEvent(copyCoords)
                                                .hoverEvent(hoverCoords)
                                                .build();

                                        Component message1 = Component.text()
                                                .append(Component.text("■ ", NamedTextColor.RED))
                                                .append(Component.text(playerName + " ", NamedTextColor.GOLD))
                                                .append(Component.text("reached ", NamedTextColor.YELLOW))
                                                .append(Component.text("Door Limit", NamedTextColor.RED))
                                                .append(Component.text(".", NamedTextColor.YELLOW))
                                                .clickEvent(copyCoords)
                                                .hoverEvent(hoverCoords)
                                                .build();

                                        Component message2 = Component.text()
                                                .append(Component.text("■ ", NamedTextColor.RED))
                                                .append(Component.text("Location", NamedTextColor.YELLOW))
                                                .append(Component.text(": ", NamedTextColor.GRAY))
                                                .append(Component.text(world.getName() + " ", NamedTextColor.GOLD))
                                                .append(Component.text("/ ", NamedTextColor.GRAY))
                                                .append(Component.text("[" + x + ", " + y + ", " + z + "]", NamedTextColor.GREEN))
                                                .clickEvent(copyCoords)
                                                .hoverEvent(hoverCoords)
                                                .build();

                                        getServer().broadcast(messageA, "chunkShield.alerts");
                                        getServer().broadcast(message1, "chunkShield.alerts");
                                        getServer().broadcast(message2, "chunkShield.alerts");
                                        getServer().broadcast(messageA, "chunkShield.alerts");
                                    }

                                    if (player.hasPermission("chunkShield.blockBypass")) return;
                                    if (player.getGameMode() != GameMode.CREATIVE)b.breakNaturally();
                                    else b.setType(Material.AIR, false);
                                    if(main.Global.configTogglePurgeEffect) world.spawnParticle(Particle.LAVA, b.getLocation().toCenterLocation(), 4);
                                    main.Global.blocksPrevented++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
