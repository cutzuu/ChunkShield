package me.cutzuu.chunkShield.listeners;

import me.cutzuu.chunkShield.main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import static org.bukkit.Bukkit.getServer;

public final class vehicleSummonsCheck implements Listener
{
    public static class vehicleGlobal
    {
        public static Vehicle theVehicle;
        public static boolean vehicleFlagged;
    }

    ////////////////////////////////////////////////////////////////////////////
    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent e)
    {
        int x = (int) e.getVehicle().getLocation().getX();
        int y = (int) e.getVehicle().getLocation().getY();
        int z = (int) e.getVehicle().getLocation().getZ();

        World world = e.getVehicle().getWorld();

        if (main.Global.configToggleVehicleRadiusCheck)
        {
            if (main.Global.configCollectiveVehicletLimit == -1) return;

            vehicleGlobal.theVehicle = e.getVehicle();
            vehicleRadiusScan();

            if(vehicleGlobal.vehicleFlagged)
            {
                main.Global.vehiclesPrevented++;
                cleanupProcess(world, x, y, z);
                vehicleGlobal.vehicleFlagged = false;
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    @EventHandler
    public void vehicleMoveCheck (VehicleMoveEvent e)
    {
        int x = (int) e.getVehicle().getLocation().getX();
        int y = (int) e.getVehicle().getLocation().getY();
        int z = (int) e.getVehicle().getLocation().getZ();

        World world = e.getVehicle().getWorld();

        if (main.Global.configToggleVehicleRadiusCheck)
        {
            if (main.Global.configCollectiveVehicletLimit == -1) return;

            vehicleGlobal.theVehicle = e.getVehicle();

            vehicleRadiusScan();
            if(vehicleGlobal.vehicleFlagged)
            {
                cleanupProcess(world, x, y, z);
                vehicleGlobal.vehicleFlagged = false;
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    private void vehicleRadiusScan()
    {
        int count = 0;

        for (Entity entity : vehicleGlobal.theVehicle.getNearbyEntities(main.Global.configRadiusLimit, main.Global.configRadiusLimit, main.Global.configRadiusLimit))
        {
            if (entity instanceof Minecart) count++;
            else if (entity instanceof Boat) count++;
        }
        if (count >= main.Global.configCollectiveVehicletLimit) vehicleGlobal.vehicleFlagged = true;
    }

    /////////////////////////////////////////////////////////////////////////////
    private static void cleanupProcess(World world, int x, int y, int z)
    {
        // ---- Category totals: BOATS & MINE CARTS
        if (main.Global.configCollectiveVehicletLimit > 0)
        {
            int total = 1;
            for (Entity entity : vehicleGlobal.theVehicle.getNearbyEntities(main.Global.configRadiusLimit, main.Global.configRadiusLimit, main.Global.configRadiusLimit))
            {
                if (entity instanceof Boat || entity instanceof Minecart) total++;
            }

            int toRemove = total - main.Global.configCollectiveVehicletLimit;
            if (toRemove > 0)
            {
                for (Entity entity : vehicleGlobal.theVehicle.getNearbyEntities(main.Global.configRadiusLimit, main.Global.configRadiusLimit, main.Global.configRadiusLimit))
                {
                    if (entity instanceof Boat || entity instanceof Minecart)
                    {
                        if (main.Global.configToggleAlertVehicleLimit)
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
                                    .append(Component.text("Vehicle Limit ", NamedTextColor.RED))
                                    .append(Component.text("was reached.", NamedTextColor.YELLOW))
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
                        entity.remove();
                        if(main.Global.configTogglePurgeEffect) world.spawnParticle(Particle.LAVA, entity.getLocation().toCenterLocation(), 4);
                        main.Global.vehiclesRemoved++;
                        if (--toRemove == 0) break; // don't return; let minecart check run
                    }
                }
            }
        }
    }











}