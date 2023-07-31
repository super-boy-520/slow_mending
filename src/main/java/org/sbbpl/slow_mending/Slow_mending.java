package org.sbbpl.slow_mending;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Slow_mending extends JavaPlugin implements Listener {

    private boolean Mend, Slow_Mend,Limit,Change_Name,send;
    private int Mitigation_Coefficient,Max;
    private List<String> Ban_Item, Allow_item;
    private String Message,Item_Prefix;
    private boolean find=false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("欢迎使用本插件！");
        this.getLogger().info("正在加载插件...");
        saveDefaultConfig();

        loadpl();

        this.getLogger().info("插件加载成功！");
        this.getLogger().info("作者：super_boy_520");
    }

    @EventHandler
    public void onPlayerItemMend(PlayerItemMendEvent e) {
        if (Limit){
            List<String> lore = new LinkedList<>();
            if (e.getItem().getItemMeta().hasLore()){
                lore = e.getItem().getItemMeta().getLore();
                for (String list : lore) {//遍历一遍找指定
                    list = list.substring(0,7);
                    System.out.println(list);
                    if (list.equals("剩余修补次数：")){//找到
                        find = true;
                        break;
                    }
                }
                System.out.println(find);
                if (!find){
                    ItemMeta mate = e.getItem().getItemMeta();
                    lore.add("剩余修补次数： "+(Max+1));
                    mate.setLore(lore);
                    e.getItem().setItemMeta(mate);
                }
            }else {
                ItemMeta mate = e.getItem().getItemMeta();
                lore.add("剩余修补次数： "+(Max+1));
                mate.setLore(lore);
                e.getItem().setItemMeta(mate);
            }
        }
        find = false;


        //判断白名单
        if (!Allow_item.contains(e.getItem().getType().toString())) {
            //判断是否启用
            if (Mend) {

                //判断是否限制
                if (Limit) {
                    List<String> lore = e.getItem().getItemMeta().getLore();//获取
                    for (String list : lore) {//遍历一遍找指定
                        String listt = list.substring(0, 7);
                        if (listt.equals("剩余修补次数：")) {//找到
                            Matcher p = Pattern.compile("[^0-9]").matcher(list);
                            int num = Integer.parseInt(p.replaceAll(""));//获取数字
                            if (num <= 0) {//如果耗尽跳过
                                e.setCancelled(true);
                                break;
                            } else {
                                num--;
                                List<String> l = new LinkedList<>();
                                ItemMeta m = e.getItem().getItemMeta();
                                l.add("剩余修补次数：" + (num));
                                m.setLore(l);
                                e.getItem().setItemMeta(m);
                                if ((num <= 0) && send) {
                                    e.setCancelled(true);
                                    e.getPlayer().sendMessage(Message);
                                }
                                if ((num <= 0) && Change_Name) {
                                    m = e.getItem().getItemMeta();
                                    String name;
                                    if (e.getItem().getItemMeta().hasDisplayName()) {
                                        name = e.getItem().getItemMeta().getDisplayName();
                                        name = Item_Prefix + name;
                                    } else {
                                        name = Item_Prefix + "工具";
                                    }
                                    m.setDisplayName(name);
                                    e.getItem().setItemMeta(m);
                                    e.setCancelled(true);
                                }
                                break;
                            }
                        }
                    }
                }

                //判断是否减缓
                if (Slow_Mend) {
                    System.out.println("fc");
                    //随机生效
                    Random r = new Random();
                    int radCoefficient = r.nextInt(Mitigation_Coefficient);
                    System.out.println(radCoefficient);
                    if ((radCoefficient == 0) && (!Ban_Item.contains(e.getItem().getType().toString()))) {
                        System.out.println("激活");
                    }else {
                        e.setCancelled(true);
                    }
                }

            } else {
                e.setCancelled(true);
            }
        }
    }

    private boolean ifboolean(String config,boolean moren){
        boolean bl;
        if (getConfig().contains(config)) {
            if (getConfig().getBoolean(config)) {
                bl = true;
                this.getLogger().info(config+":设置为true");
            } else {
                bl = false;
                this.getLogger().info(config+":设置为false");
            }
        } else {
            this.getLogger().warning("配置文件："+config+" 未填写，将默认"+moren);
            bl = moren;
        }
        return bl;
    }

    private int ifint(String config,int moren){
        int in;
        if (getConfig().contains(config)) {
            in = getConfig().getInt(config) - 1;
            this.getLogger().info(config+":设置为" + (in + 1));
        } else {
            this.getLogger().warning("配置文件:"+config+" 未填写，将默认设置为"+moren);
            in = moren-1;
        }
        return in;
    }

    private String ifstr(String config,String moren){
        String str;
        if(getConfig().contains(config)){
            str = getConfig().getString(config);
            this.getLogger().info(config+":设置为" + str);
        }else {
            str = moren;
            this.getLogger().warning("配置文件："+config+"未填写，将默认设置为"+moren);
        }
        return str;
    }

    public void loadpl(){
        Bukkit.getPluginManager().registerEvents(this, this);
        //判断是否启用
        Mend = ifboolean("Settings.Mend",true);
        //判断是否减缓
        Slow_Mend=ifboolean("Settings.Slow_Mend",true);
        if (Slow_Mend){
            Mitigation_Coefficient = ifint("Settings.Mitigation_Coefficient",5);
        }

        Ban_Item = getConfig().getStringList("Settings.Ban_Item");
        this.getLogger().info("读取黑名单");
        Allow_item = getConfig().getStringList("Settings.Allow_Item");
        this.getLogger().info("读取白名单");


        Limit=ifboolean("Settings.Max_Mend_Limit.Enable",true);
        if (Limit){
            Max=ifint("Settings.Max_Mend_Limit.Max_Number",500);
            send = ifboolean("Settings.Max_Mend_Limit.Deactivate_Message",true);
            if (send){
                Message=ifstr("Settings.Max_Mend_Limit.Message","你的这件装备已经很破旧了，是时候换一个了。");
            }
            Change_Name = ifboolean("Settings.Max_Mend_Limit.Change_Item_Name",true);
            if (Change_Name){
                Item_Prefix = ifstr("Settings.Max_Mend_Limit.Item_Prefix","破损的-");
            }
        }
    }
}

//if (!e.getItem().getItemMeta().hasLore()){
//
//        }else {
//        List<String> lore;
//        lore = e.getItem().getItemMeta().getLore();
//        for (String i:lore){
//        Matcher p = Pattern.compile("\\[CDATA\\[(.*?)\\]\\]>").matcher(i);
//        if (i.equals("剩余修补次数：")){
//        break;
//        }
//        else {
//        ItemMeta mate = e.getItem().getItemMeta();
//        lore.add("剩余修补次数： "+(Max+1));
//        mate.setLore(lore);
//        e.getItem().setItemMeta(mate);
//        break;
//        }
//        }
//        }