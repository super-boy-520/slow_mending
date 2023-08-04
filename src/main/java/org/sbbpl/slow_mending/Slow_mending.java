package org.sbbpl.slow_mending;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Slow_mending extends JavaPlugin implements Listener, TabCompleter {

    private boolean Mend, Slow_Mend,Limit,Change_Name,send;
    private int Mitigation_Coefficient,Max;
    private List<String> Ban_Item, Allow_item;
    private String Message;
    private String Item_Prefix;
    String Color;
    private boolean find=false;

    File comconfigfile;
    FileConfiguration comconfig;


    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("欢迎使用本插件！");
        this.getLogger().info("正在加载插件...");


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
                    if (list.length()>=9){
                        list = list.substring(0, 9);
                        if (list.equals("§"+Color+"剩余修补次数：")){//找到
                            find = true;
                            break;
                        }
                    }
                }
                if (!find){
                    ItemMeta mate = e.getItem().getItemMeta();
                    lore.add("§"+Color+"剩余修补次数："+(Max+1));
                    mate.setLore(lore);
                    e.getItem().setItemMeta(mate);
                }
            }else {
                ItemMeta mate = e.getItem().getItemMeta();
                lore.add("§"+Color+"剩余修补次数："+(Max+1));
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
                    int listnum = 0;
                    for (String list : lore) {//遍历一遍找指定
                        listnum++;
                        if (list.length()>=9){
                            String listt = list.substring(0, 9);
                            if (listt.equals("§"+Color+"剩余修补次数：")) {//找到
                                int num = Integer.parseInt(list.substring(9));
//                            Matcher p = Pattern.compile("[^0-9]").matcher(list);
//                            int num = Integer.parseInt(p.replaceAll(""));//获取数字
                                if (num <= 0) {//如果耗尽跳过
                                    e.setCancelled(true);
                                    break;
                                } else {
                                    num--;
                                    ItemMeta m = e.getItem().getItemMeta();
                                    lore.set((listnum-1),"§"+Color+"剩余修补次数：" + (num));
                                    m.setLore(lore);
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
                }

                //判断是否减缓
                if (Slow_Mend) {
                    //随机生效
                    Random r = new Random();
                    int radCoefficient = r.nextInt(Mitigation_Coefficient);
                    if ((radCoefficient == 0) && (!Ban_Item.contains(e.getItem().getType().toString()))) {
                    }else {
                        e.setCancelled(true);
                    }
                }

            } else {
                e.setCancelled(true);
            }
        }
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Player player = (Player) sender;
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以使用此命令！");
            return false;
        }

        if (!player.hasPermission("slowmending.command")){
            sender.sendMessage("你没有权限！");
            return false;
        }

        ItemMeta item = player.getEquipment().getItemInMainHand().getItemMeta();

        if (args.length<1){
            List<String> help;
            help = comconfig.getStringList("Command.text.help");
            for (String text : help) {
                sender.sendMessage(text);
            }
            return true;
        }

        switch (args[0]) {
            case ("reload") -> {
                sender.sendMessage("正在重载插件...");
                this.getLogger().info(sender.getName() + "正在重载插件...");
                try {
                    loadpl();
                    sender.sendMessage("§a重载插件成功！");
                    return true;
                } catch (Exception e) {
                    sender.sendMessage("§c重载插件失败！");
                    this.getLogger().warning("重载插件失败！" + e);
                    return false;
                }
            }
            case ("help"), ("") -> {
                List<String> help;
                help = comconfig.getStringList("Command.text.help");
                for (String text : help) {
                    sender.sendMessage(text);
                }
                return true;
            }
            case ("version"),("v") -> {
                List<String> version;
                version = comconfig.getStringList("Command.text.version");
                for (String text : version) {
                    sender.sendMessage(text);
                }
                return true;
            }
            case ("set") -> {
                if (args.length >= 2) {
                    int num;
                    try {
                        num = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("[Slow_mending] §c请输入一个有效数字");
                        return true;
                    }
                    try{
                        if (item.hasEnchant(Enchantment.MENDING) & item.hasLore()) {
                            List<String> lore = item.getLore();
                            int listnum = 0;
                            for (String list : lore) {
                                listnum++;
                                if (list.length() >= 9) {
                                    String listt = list.substring(0, 9);
                                    if (listt.equals("§" + Color + "剩余修补次数：")) {//找到
                                        lore.set((listnum - 1), "§" + Color + "剩余修补次数：" + (num));
                                        item.setLore(lore);
                                        player.getEquipment().getItemInMainHand().setItemMeta(item);
                                        sender.sendMessage("[Slow_mending] §b已将该物品剩余修补次数设置为:" + ChatColor.GOLD + num);
                                    }
                                }
                            }
                            return true;

                        } else {
                            sender.sendMessage("[Slow_mending] §c请手持需要编辑的物品！");
                            return true;
                        }
                    }catch (Exception e){
                        sender.sendMessage("[Slow_mending] §c请手持需要编辑的物品！");
                        return true;
                    }
                } else {
                    sender.sendMessage("[Slow_mending] §c请输入一个有效数字");
                    return true;
                }
            }
            default -> {
                sender.sendMessage("[Slow_mending] §c未知的指令！");
                List<String> helps;
                helps = comconfig.getStringList("Command.text.help");
                for (String text : helps) {
                    sender.sendMessage(text);
                }
                return true;
            }
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> com = new LinkedList<>();
        if (args.length==1){
            com.add("help");
            com.add("version");
            com.add("set");
            com.add("reload");
            return com;
        } else if (args.length==2 & args[0].equals("set")) {
            com.add(String.valueOf(Max+1));
            com.add("0");
            return com;
        }else {
            return null;
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
        saveDefaultConfig();
        this.saveResource("command.yml", false);

        Bukkit.getPluginManager().registerEvents(this, this);
//        Bukkit.getPluginCommand("slowmending").setExecutor(new Slow_mending_command(this));
        //getCommand("slowmending").setExecutor(new Slow_mending_command(this));
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
            Color = ifstr("Settings.Max_Mend_Limit.Color","9");
            if (Color.length()>1){
                this.getLogger().warning("Color设定值过长，已自动截取第一位。");
                Color = Color.substring(0,1);
            }
            comconfigfile = new File(this.getDataFolder(),"command.yml");
            comconfig =YamlConfiguration.loadConfiguration(comconfigfile);

        }
    }
}

