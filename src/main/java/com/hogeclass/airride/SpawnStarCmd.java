/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hogeclass.airride;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author hoge
 */
public class SpawnStarCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {

        // プレイヤー以外はコマンド実行不可
        if (!(cs instanceof Player)) {
            return false;
        }

        Player player = (Player) cs;
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setSmall(true);
        stand.setHelmet(new ItemStack(Material.GOLD_BLOCK));
        stand.setVisible(false);
        return true;
    }

}
