package SwordofMagic11.Player.Market;

import SwordofMagic11.Command.Player.Lock;
import SwordofMagic11.Component.SomSQL;
import SwordofMagic11.Component.SomSound;
import SwordofMagic11.Custom.CustomItemStack;
import SwordofMagic11.DataBase.DataBase;
import SwordofMagic11.DataBase.MaterialDataLoader;
import SwordofMagic11.Item.Material.MaterialData;
import SwordofMagic11.Player.Menu.GUIManager;
import SwordofMagic11.Player.PlayerData;
import SwordofMagic11.Player.Setting.PlayerSetting;
import SwordofMagic11.Player.Statistics.Statistics;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static SwordofMagic11.Component.Function.*;
import static SwordofMagic11.DataBase.DataBase.*;

public class MarketOrderMaterial extends GUIManager {

    public CustomItemStack icon() {
        HashMap<String, Integer> map = new LinkedHashMap<>();
        CustomItemStack item = new CustomItemStack(Material.CHEST);
        item.setDisplay("マーケット注文 [素材]");
        ResultSet resultSet = SomSQL.getSqlList(DataBase.Table.MarketOrderMaterial, "*", "ORDER BY `MaterialID` ASC");
        for (RowData objects : resultSet) {
            String materialId = objects.getString("MaterialID");
            if (MaterialDataLoader.getComplete().contains(materialId)) {
                map.merge(materialId, 1, Integer::sum);
            }
        }
        int register = 0;
        int index = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            MaterialData material = MaterialDataLoader.getMaterialData(entry.getKey());
            item.addLore("§7・§f" + material.getDisplay() + " §a" + entry.getValue() + "件の注文");
            register += entry.getValue();
            if (playerData.setting().is(PlayerSetting.BooleanEnum.StorageViewShort) && index > ContainerViewSize) {
                item.addLore("§7その他..." + (map.size() - ContainerViewSize) + "種," + (resultSet.size() - register) + "件");
                break;
            }
            index++;
        }
        item.setCustomData("Market", "MarketOrderMaterial");
        return item;
    }

    private int scroll = 0;
    private int amount = 0;

    public MarketOrderMaterial(PlayerData playerData) {
        super(playerData, "マーケット注文 [素材]", 6);
    }

    private int amount() {
        return (int) Math.pow(10, amount);
    }

    private CustomItemStack amountIcon() {
        CustomItemStack item = new CustomItemStack(Material.GOLD_NUGGET);
        item.setNonDecoDisplay(decoLore("売却個数") + amount());
        item.setAmountReturn(amount+1);
        item.setCustomData("AmountIcon", true);
        return item;
    }

    private static final String[] key = new String[]{"UUID", "MaterialID"};

    private String[] value(String id) {
        return new String[]{playerData.getUUID(), id};
    }

    public void set(MarketPlayer.Pack pack) {
        setAmount(pack.id(), pack.amount());
        setMel(pack.id(), pack.mel());
    }

    public static void set(String uuid, MarketPlayer.Pack pack) {
        String[] value = new String[]{uuid, pack.id()};
        SomSQL.setSql(DataBase.Table.MarketOrderMaterial, key, value, "Amount", pack.amount());
        SomSQL.setSql(DataBase.Table.MarketOrderMaterial, key, value, "Mel", pack.mel());
    }

    public void setAmount(MaterialData material, int amount) {
        setAmount(material.getId(), amount);
    }

    public void setAmount(String id, int amount) {
        setAmount(playerData.getUUID(), id, amount);
    }

    public static void setAmount(String uuid, String id, int amount) {
        if (amount <= 0) {
            delete(uuid, id);
        } else {
            String[] value = new String[]{uuid, id};
            SomSQL.setSql(DataBase.Table.MarketOrderMaterial, key, value, "Amount", amount);
        }
    }

    public void setMel(MaterialData material, int mel) {
        setMel(material.getId(), mel);
    }

    public void setMel(String id, int mel) {
        SomSQL.setSql(DataBase.Table.MarketOrderMaterial, key, value(id), "Mel", mel);
    }

    public static void delete(String uuid, String id) {
        String[] value = new String[]{uuid, id};
        SomSQL.delete(DataBase.Table.MarketOrderMaterial, key, value);
    }

    public void delete(String id) {
        delete(playerData.getUUID(), id);
    }

    public static MarketPlayer.Pack get(String uuid, String id) {
        String[] value = new String[]{uuid, id};
        RowData objects = SomSQL.getSql(DataBase.Table.MarketOrderMaterial, key, value, "*");
        int amount = objects.getInt("Amount");
        int mel = objects.getInt("Mel");
        MarketPlayer.Pack pack = new MarketPlayer.Pack(id, amount, mel);
        pack.setOwner(uuid);
        return pack;
    }

    public MarketPlayer.Pack get(String id) {
        return new MarketPlayer.Pack(id, getAmount(id), getMel(id));
    }

    public int getAmount(MaterialData material) {
        return getAmount(material.getId());
    }

    public int getAmount(String id) {
        if (SomSQL.exists(DataBase.Table.MarketOrderMaterial, key, value(id), "Amount")) {
            return SomSQL.getInt(DataBase.Table.MarketOrderMaterial, key, value(id), "Amount");
        } else return 0;
    }

    public int getMel(MaterialData material) {
        return getMel(material.getId());
    }

    public int getMel(String id) {
        if (SomSQL.exists(DataBase.Table.MarketOrderMaterial, key, value(id), "Mel")) {
            return SomSQL.getInt(DataBase.Table.MarketOrderMaterial, key, value(id), "Mel");
        } else return 0;
    }

    public boolean has(String id) {
        return SomSQL.exists(DataBase.Table.MarketOrderMaterial, key, value(id));
    }

    private String materialId = null;

    @Override
    public void updateContainer() {
        if (materialId == null) {
            HashMap<String, Set<MarketPlayer.Pack>> map = new LinkedHashMap<>();
            Set<String> keys = new HashSet<>();
            ResultSet resultSet = SomSQL.getSqlList(DataBase.Table.MarketOrderMaterial, "*", "ORDER BY `MaterialID` ASC");
            for (RowData objects : resultSet) {
                String materialId = objects.getString("MaterialID");
                keys.add(materialId);
                if (keys.size() >= scroll * 8) {
                    int amount = objects.getInt("Amount");
                    int mel = objects.getInt("Mel");
                    if (!map.containsKey(materialId)) map.put(materialId, new HashSet<>());
                    MarketPlayer.Pack pack = new MarketPlayer.Pack(materialId, amount, mel);
                    pack.setOwner(objects.getString("UUID"));
                    map.get(materialId).add(pack);
                    if (map.size() >= 48) break;
                }
            }
            int slot = 0;
            for (Map.Entry<String, Set<MarketPlayer.Pack>> entry : map.entrySet()) {
                String id = entry.getKey();
                MaterialData material = MaterialDataLoader.getMaterialData(id);
                CustomItemStack item = material.viewItem();
                item.addSeparator("マーケット情報");
                item.addLore(decoLore("平均取引価格") + (MarketPlayer.hasAverage(id) ? MarketPlayer.getAverageMel(id)  + "メル" : "§c取引情報少量"));
                item.addLore(decoLore("過去取引個数") + MarketPlayer.getAverageAmount(id) + "個");
                item.addSeparator("注文者一覧");
                int indexPlayer = 0;
                for (MarketPlayer.Pack pack : entry.getValue()) {
                    item.addLore("§7・§f" + pack.getOwnerName() + " §e" + pack.amount() + "個 単価" + pack.mel() + "メル");
                    indexPlayer++;
                    if (playerData.setting().is(PlayerSetting.BooleanEnum.StorageViewShort) && indexPlayer > ContainerViewSize) {
                        item.addLore("§7その他..." + (entry.getValue().size() - ContainerViewSize) + "名");
                        break;
                    }
                }
                item.setCustomData("OrderMaterial", id);
                setItem(slot, item);
                slot++;
                if (isInvalidSlot(slot)) slot++;
            }
        } else {
            MaterialData material = MaterialDataLoader.getMaterialData(materialId);
            CustomItemStack itemBase = material.viewItem();
            itemBase.addSeparator("マーケット情報");
            ResultSet resultSet = SomSQL.getSqlList(DataBase.Table.MarketOrderMaterial, "MaterialID", materialId, "*", "ORDER BY `Mel` DESC");
            int slot = 0;
            for (RowData objects : resultSet) {
                String uuid = objects.getString("UUID");
                CustomItemStack item = itemBase.clone();
                item.addLore(decoLore("注文個数") + objects.getInt("Amount") + "個");
                item.addLore(decoLore("注文単価") + objects.getInt("Mel") + "メル");
                item.addLore(decoLore("注文者") + PlayerData.Username(uuid));
                item.setCustomData("OrderOwner", uuid);
                setItem(slot, item);
                slot++;
                if (isInvalidSlot(slot)) slot++;
                if (slot >= 53) break;
            }
        }
        setItem(8, UpScroll());
        setItem(17, amountIcon());
        setItem(26, ItemFlame);
        setItem(35, ItemFlame);
        setItem(44, ItemFlame);
        setItem(53, DownScroll());
    }

    @Override
    public void topClick(int slot, ItemStack clickedItem, ClickType clickType) {
        if (CustomItemStack.hasCustomData(clickedItem, "AmountIcon")) {
            amount++;
            if (amount >= 6) amount = 0;
            SomSound.Tick.play(playerData);
            update();
        } else if (CustomItemStack.hasCustomData(clickedItem, "UpScroll")) {
            scroll = scrollUp(scroll);
            update();
        } else if (CustomItemStack.hasCustomData(clickedItem, "DownScroll")) {
            scroll = scrollDown(inventory, scroll);
            update();
        }
        if (CustomItemStack.hasCustomData(clickedItem, "OrderMaterial")) {
            materialId = CustomItemStack.getCustomData(clickedItem, "OrderMaterial");
            if (Lock.check(playerData, materialId)) {
                materialId = null;
                return;
            }
            update();
        } else if (CustomItemStack.hasCustomData(clickedItem, "OrderOwner")) {
            String uuid = CustomItemStack.getCustomData(clickedItem, "OrderOwner");
            if (playerData.getUUID().equals(uuid)) {
                playerData.sendMessage("§a自身の§e注文§aです", SomSound.Nope);
                return;
            }
            MaterialData material = MaterialDataLoader.getMaterialData(materialId);
            MarketPlayer.Pack pack = get(uuid, materialId);
            int amount = Math.min(Math.min(pack.amount(), amount()), playerData.getMaterial(materialId));
            int mel = pack.mel() * amount;
            if (amount > 0) {
                playerData.addMel(mel);
                playerData.removeMaterial(materialId, amount);
                MarketPlayer.addMaterial(uuid, materialId, amount);
                setAmount(uuid, materialId,pack.amount() - amount);
                MarketPlayer.applyAverage(materialId, amount, pack.mel());
                playerData.statistics().add(Statistics.IntEnum.MarketSellMel, mel);
                playerData.sendMessage(material.getDisplay() + "x" + amount + "§aを§e" + mel + "メル§aで§b売却§aしました", SomSound.Tick);
                update();
                MarketPlayer.Log(playerData, "SellMaterial:" + materialId + ", Amount:" + amount + ", Mel:" + mel + ", Buyer:" + pack.getOwnerName());
            } else {
                playerData.sendMessage(material.getDisplay() + "§aを§e所持§aしていません", SomSound.Nope);
            }
        }
    }

    @Override
    public void bottomClick(int slot, ItemStack clickedItem, ClickType clickType) {

    }

    @Override
    public void close() {
        materialId = null;
    }
}
