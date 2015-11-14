/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hogeclass.airride;

import com.comphenix.packetwrapper.WrapperPlayClientSteerVehicle;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.protocol.events.PacketContainer;
import de.inventivegames.packetlistener.PacketListenerAPI;
import de.inventivegames.packetlistener.handler.PacketHandler;
import de.inventivegames.packetlistener.handler.ReceivedPacket;
import de.inventivegames.packetlistener.handler.SentPacket;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 *
 * @author hoge
 */
public class AirRide extends JavaPlugin implements Listener {

    // 定数
    public static float MIDDLE_SPEED = 0.2f;
    public static float MAX_SPEED = 0.6f;
    public static float FLY_SPEED = 0.5f;

    // パラメータ
    private Player ridePlayer;

    // 乗り物
    Minecart vehicle;
    public static AirRide airRide;
    public static float nowSpeed = 0f;  // 0.0f ~ 1.0f
    public static boolean isShiftClicked = false;

    // Thread
    public BukkitTask task;

    /**
     * プラグイン許可時の処理
     *
     */
    @Override
    public void onEnable() {

    }

    private void init() {
        AirRide.airRide = this;
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("spawnStar").setExecutor(new SpawnStarCmd());
        
        // 乗り物から降りようとする時のパケット処理
        PacketListenerAPI.addPacketHandler(new PacketHandler(this) {

            @Override
            public void onSend(SentPacket sp) {
            }

            @Override
            public void onReceive(ReceivedPacket rp) {
                if (rp.getPacket() instanceof PacketPlayInSteerVehicle) {
                    PacketPlayInSteerVehicle p = (PacketPlayInSteerVehicle) rp.getPacket();
                    rp.setCancelled(true);
                    WrapperPlayClientSteerVehicle wp = new WrapperPlayClientSteerVehicle(PacketContainer.fromPacket(p));
                    
                    // unmount ボタン押下フラグ
                    AirRide.isShiftClicked = wp.isUnmount();
                    
                    
                    if (AirRide.isShiftClicked && AirRide.nowSpeed > 0) {

                        AirRide.nowSpeed -= 0.01;
                        if (AirRide.nowSpeed < 0) {
                            AirRide.nowSpeed = 0;
                        }
                    }
                } else {

                }
            }
        });
    }

    @EventHandler
    public void onRide(PlayerInteractAtEntityEvent e) {
        Entity ent = e.getRightClicked();
        FallingBlock block = (FallingBlock) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.FALLING_BLOCK);
        if (ent instanceof ArmorStand) {
            ArmorStand as = (ArmorStand) ent;
            ItemStack helmet = as.getHelmet();

            if (helmet != null && helmet.getType() == Material.GOLD_BLOCK) {
                as.remove();
                rideStar(e.getPlayer());
            }
        }
        e.setCancelled(true);
    }

    private void rideStar(Player p) {
        if (vehicle != null) {
            vehicle.remove();
        }
        Location loc = p.getLocation();

        vehicle = (Minecart) p.getWorld().spawnEntity(loc, EntityType.MINECART);
        vehicle.setVelocity(new Vector(0, 0, 0));
        // プレイヤー搭乗
        vehicle.setPassenger(p);
        ridePlayer = p;

        FallingBlock fb = (FallingBlock) p.getWorld().spawnEntity(loc, EntityType.FALLING_BLOCK);
        fb.setVelocity(new Vector(0, 0, 0));
        fb.setDropItem(false);
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(fb, 70, 1);
        packet.sendPacket(p);

        task = getServer().getScheduler().runTaskTimer(this, new VehicleMove(vehicle, p), 0L, 0L);

    }

}
