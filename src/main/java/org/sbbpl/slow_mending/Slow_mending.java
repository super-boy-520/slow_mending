package org.sbbpl.slow_mending;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

public final class Slow_mending extends JavaPlugin implements Listener {

    private boolean Mend, Slow_Mend;
    private int Mitigation_Coefficient;
    private List<String> Ban_Item, Allow_item;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("欢迎使用本插件！");
        this.getLogger().info("正在加载插件...");
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        //判断是否启用
        if (getConfig().contains("Settings.Mend")) {
            if (getConfig().getBoolean("Settings.Mend")) {
                Mend = true;
                this.getLogger().info("Mend:设置为true");
            } else {
                Mend = false;
                this.getLogger().info("Mend:设置为false");
            }
        } else {
            this.getLogger().warning("配置文件：Mend 未填写，将默认启用");
            Mend = true;
        }
        //判断是否减缓
        if (getConfig().contains("Settings.Slow_Mend")) {
            if (getConfig().getBoolean("Settings.Slow_Mend")) {
                Slow_Mend = true;
                this.getLogger().info("Slower_Mend:设置为true");
            } else {
                Slow_Mend = false;
                this.getLogger().info("Slower_Mend:设置为false");
            }
        } else {
            this.getLogger().warning("配置文件：Slower_Mend 未填写，将默认启用");
            Slow_Mend = true;
        }

        Ban_Item = getConfig().getStringList("Settings.Ban_Item");
        Allow_item = getConfig().getStringList("Settings.Allow_Item");

        if (getConfig().contains("Settings.Mitigation_Coefficient")) {
            Mitigation_Coefficient = getConfig().getInt("Settings.Mitigation_Coefficient") - 1;
            this.getLogger().info("Mitigation_Coefficient:设置为" + (Mitigation_Coefficient + 1));
        } else {
            this.getLogger().warning("配置文件:Mitigation_Coefficient 未填写，将默认设置为5");
            Mitigation_Coefficient = 4;
        }

        this.getLogger().info("插件加载成功！");
        this.getLogger().info("作者：super_boy_520");
    }

    @EventHandler
    public void onPlayerItemMend(PlayerItemMendEvent e) {
        //判断白名单
        if (!Allow_item.contains(e.getItem().getType().toString())) {
            //判断是否启用
            if (Mend) {
                //判断是否减缓
                if (Slow_Mend) {
                    //随机生效
                    Random r = new Random();
                    int radCoefficient = r.nextInt(Mitigation_Coefficient);
                    if ((radCoefficient == 0) && (!(Ban_Item.contains(e.getItem().getType().toString())))) {
                        e.setCancelled(false);
                    } else {
                        e.setCancelled(true);
                    }
                }
            } else {
                e.setCancelled(true);
            }
        }
    }
}
