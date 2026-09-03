package me.mikebloc.chunkShield.languages;

import me.mikebloc.chunkShield.main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NonNull;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class ES
{
    //Click to copy coordinates.
    public static String ClickCopy = "Haga clic para copiar coordenadas.";

    // Primary Method to send out message.
    public static void sendMessageMethod(World world, int x, int z, int y, ClickEvent<ClickEvent.Payload.Text> copyCoords, HoverEvent<?> hoverCoords, Component primaryMessage)
    {
        Component STYLE = Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("- - - - - - - - - - - - - - - - - - - - - - - - - ", NamedTextColor.GRAY))
                .append(Component.text("■", NamedTextColor.RED))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();

        Component subMessage = Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("Ubicación", NamedTextColor.YELLOW))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(world.getName() + " ", NamedTextColor.GOLD))
                .append(Component.text("/ ", NamedTextColor.GRAY))
                .append(Component.text("[" + x + ", " + y + ", " + z + "]", NamedTextColor.GREEN))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();

        getServer().broadcast(STYLE, "chunkShield.alerts");
        getServer().broadcast(primaryMessage, "chunkShield.alerts");
        getServer().broadcast(subMessage, "chunkShield.alerts");
        getServer().broadcast(STYLE, "chunkShield.alerts");
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// blockPlaceCheck Class Messages.
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static @NonNull Component blockPlaceCheck_alertBlockLimitReached(String playerName, Block b, ClickEvent<ClickEvent.Payload.Text> copyCoords, HoverEvent<?> hoverCoords)
    {
        // Player reached STONE limit.
        return Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text(playerName + " ", NamedTextColor.GOLD))
                .append(Component.text("alcanzó ", NamedTextColor.YELLOW))
                .append(Component.text(b.getType() + " ", NamedTextColor.RED))
                .append(Component.text("límite.", NamedTextColor.YELLOW))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();
    }

    public static @NonNull Component blockPlaceCheck_alertDoorLimitReached(String playerName, ClickEvent<ClickEvent.Payload.Text> copyCoords, HoverEvent<?> hoverCoords)
    {
        // Player reached DOOR limit.
        return Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text(playerName + " ", NamedTextColor.GOLD))
                .append(Component.text("alcanzó ", NamedTextColor.YELLOW))
                .append(Component.text("Puerta Límite", NamedTextColor.RED))
                .append(Component.text(".", NamedTextColor.YELLOW))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// entitySummonsCheck Class Messages.
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static @NonNull Component entitySummonsCheck_EntityAlert_Named(EntityType type, int entitySummonNamedCount, ClickEvent<ClickEvent.Payload.Text> copyCoords, HoverEvent<?> hoverCoords)
    {
        // Removed x10 Named ZOMBIE_VILLAGER.
        return Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("Eliminado ", NamedTextColor.YELLOW))
                .append(Component.text("x" + entitySummonNamedCount + " ", NamedTextColor.GREEN))
                .append(Component.text("Denominado ", NamedTextColor.GREEN))
                .append(Component.text(type.name(), NamedTextColor.GOLD))
                .append(Component.text(".", NamedTextColor.YELLOW))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();
    }

    public static @NonNull Component entitySummonsCheck_EntityAlert_UnNamed(EntityType type, int entitySummonUnNamedCount, ClickEvent<ClickEvent.Payload.Text> copyCoords, HoverEvent<?> hoverCoords)
    {
        // Removed x10 ZOMBIE_VILLAGER.
        return Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("Eliminado ", NamedTextColor.YELLOW))
                .append(Component.text("x" + entitySummonUnNamedCount + " ", NamedTextColor.GREEN))
                .append(Component.text(type.name(), NamedTextColor.GOLD))
                .append(Component.text(".", NamedTextColor.YELLOW))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();
    }

    public static @NonNull Component entitySummonsCheck_EntityAlert_NotRemoved(int length, ClickEvent<ClickEvent.Payload.Text> copyCoords, HoverEvent<?> hoverCoords)
    {
        // CHUNK WARNING: Found x10 entities.
        return Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("ADVERTENCIA", NamedTextColor.RED))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text("encontrado ", NamedTextColor.YELLOW))
                .append(Component.text("x" + length + " ", NamedTextColor.GREEN))
                .append(Component.text("entidade(s)", NamedTextColor.RED))
                .append(Component.text(".", NamedTextColor.YELLOW))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// vehicleSummonsCheck Class Messages.
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Removed x10 Vehicle(s).
    public static @NonNull Component vehicleSummonsCheck_alertVehicleLimit(ClickEvent<ClickEvent.Payload.Text> copyCoords, HoverEvent<?> hoverCoords)
    {
        return Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("Eliminado ", NamedTextColor.YELLOW))
                .append(Component.text("x" + main.Global.Entity_vehicleCount + " ", NamedTextColor.GREEN))
                .append(Component.text("Vehículo(s)", NamedTextColor.GOLD))
                .append(Component.text(".", NamedTextColor.YELLOW))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// main Class Messages.
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Invalid entity type in config:
    public static void main_ConsoleInvalidConfigEntity(String key)
    {
        getLogger().warning("Tipo de entidad no válido en la configuración: " + key);
    }
    // Invalid entity type in named-entity-limits:
    public static void main_ConsoleInvalidConfigNamedEntity(String key)
    {
        getLogger().warning("Tipo de entidad no válido en named-entity-limits: " + key);
    }
    // Invalid block type in config:
    public static void main_ConsoleInvalidConfigBlock(String key)
    {
        getLogger().warning("Tipo de bloque no válido en la configuración: " + key);
    }
    // Invalid Action. No Permission.
    public static void main_NoPermission(CommandSender sender)
    {
        sender.sendMessage("§cAcción no válida. Sin permiso.");
    }
    // §7[§6ChunkShield§7] §aConfig reloaded.
    public static void main_ConfigReloaded(CommandSender sender)
    {
        sender.sendMessage("§7[§6ChunkShield§7] §aLa configuración se ha actualizado.");
    }
    // Stats Subcommand
    public static void main_Subcommand_Stats(CommandSender sender)
    {
        sender.sendMessage("§a■ §7- - - - - - - - - - - - - - - - - - - - - - - - - §a■"
                + "\n§a■ §6ChunkShield Stats §aDesde la última reinicio§7:" + "\n§a■"
                + "\n§a■ §3" + main.Global.chunkCount + " §6Áreas analizadas"
                + "\n§a■ §3" + main.Global.blocksPrevented + " §eBloques eliminados"
                + "\n§a■ §3" + main.Global.entitiesRemoved + " §6Entidades eliminadas"
                + "\n§a■ §3" + main.Global.vehiclesPrevented + " §eVehículos eliminadas"
                + "\n§a■ §7- - - - - - - - - - - - - - - - - - - - - - - - - §a■");
    }
    // A Loaded Chunk met 6 conditions and removed x10 entities.
    public static @NonNull Component main_AlertChunkScanRemovalSuccess(int totality, ClickEvent<ClickEvent.Payload.Text> copyCoords, HoverEvent<?> hoverCoords)
    {
        return Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("Un área tiene ", NamedTextColor.RED))
                .append(Component.text("6 condiciones ", NamedTextColor.GOLD))
                .append(Component.text("& eliminado ", NamedTextColor.RED))
                .append(Component.text("x" + totality + " ", NamedTextColor.GREEN))
                .append(Component.text("entidades", NamedTextColor.RED))
                .append(Component.text(".", NamedTextColor.YELLOW))
                .clickEvent(copyCoords)
                .hoverEvent(hoverCoords)
                .build();
    }
}
