/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hogeclass.airride;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 *
 * @author hoge
 */
public class VehicleMove implements Runnable {

    private final Entity entity;
    private final Player player;
    private Location location;
    private Vector vector;
    private int num = 0;

    public VehicleMove(Entity e, Player p) {
        this.entity = e;
        this.player = p;
    }

    @Override
    public void run() {
        net.minecraft.server.v1_8_R3.Entity e = ((CraftEntity) entity).getHandle();
        
        // 超えることができる高さ設定
        e.S = 2f;
        e.setSneaking(true);
        
        location = player.getLocation();
        location.setPitch(0f);

        vector = location.getDirection();

        // 最大速度よりも遅い
        AirRide ar = AirRide.airRide;

        if (!AirRide.isShiftClicked && AirRide.nowSpeed < AirRide.MIDDLE_SPEED) {
            AirRide.nowSpeed += 0.1f;
            if(AirRide.nowSpeed > AirRide.MIDDLE_SPEED){
                AirRide.nowSpeed = AirRide.MIDDLE_SPEED;
            }
        } else if (!AirRide.isShiftClicked && AirRide.nowSpeed < AirRide.MAX_SPEED) {
            AirRide.nowSpeed += 0.001f;
            if(AirRide.nowSpeed > AirRide.MAX_SPEED){
                AirRide.nowSpeed = AirRide.MAX_SPEED;
            }
        }

        // 速度が規定値かつ、地面についていない場合
        if (AirRide.nowSpeed > AirRide.FLY_SPEED && !e.onGround) {
            entity.setVelocity(new Vector(0, 0.2, 0));
        }
            System.out.println(AirRide.nowSpeed);
        if(AirRide.nowSpeed > 0f){
            e.move(vector.getX() * AirRide.nowSpeed, 0, vector.getZ() * AirRide.nowSpeed);
            e.yaw = location.getYaw();
            num = 0;
        }else if(e.onGround){
            num++;
        }
        
        // 降りる処理
        if(num == 10){
            entity.remove();
            Location loc = player.getLocation().add(0, 1, 0);
            player.teleport(loc);
            player.getWorld().playEffect(loc, Effect.SNOW_SHOVEL, 5);
            ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            
            
            stand.setSmall(true);
            stand.setHelmet(new ItemStack(Material.GOLD_BLOCK));
            stand.setVisible(false);
            AirRide.airRide.task.cancel();
        }
    }

}
