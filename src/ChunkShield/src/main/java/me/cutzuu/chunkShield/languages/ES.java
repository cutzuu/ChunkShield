package me.cutzuu.chunkShield.languages;

import me.cutzuu.chunkShield.main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NonNull;

import static org.bukkit.Bukkit.getServer;

public class ES
{
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
                .append(Component.text("Remoto ", NamedTextColor.YELLOW))
                .append(Component.text("x" + entitySummonNamedCount + " ", NamedTextColor.GREEN))
                .append(Component.text("Nombrado ", NamedTextColor.GREEN))
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
                .append(Component.text("Remoto ", NamedTextColor.YELLOW))
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
                .append(Component.text("ADVERTENCIA DE TROZOS", NamedTextColor.RED))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text("Encontrado ", NamedTextColor.YELLOW))
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
    public static @NonNull Component vehicleSummonsCheck_alertVehicleLimit(ClickEvent<ClickEvent.Payload.Text> copyCoords, HoverEvent<?> hoverCoords)
    {
        return Component.text()
                .append(Component.text("■ ", NamedTextColor.RED))
                .append(Component.text("Remoto ", NamedTextColor.YELLOW))
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
}
