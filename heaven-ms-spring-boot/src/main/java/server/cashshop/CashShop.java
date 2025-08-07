/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.cashshop;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import cn.nap.utils.exception.NapCvtEx;
import config.CommonConfig;

import net.server.Server;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import tools.Log;
import tools.MysqlConnection;
import tools.Pair;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.inventory.ItemConstants;

import java.util.Collections;

import net.server.audit.locks.MonitoredLockType;
import ui.dao.CashItemDao;

/*
 * @author Flav
 */
public class CashShop {
    public static class CashItem implements Cloneable {

        private int gender;
        private int sn, itemId, price, priority;
        private long period;
        private short count;
        private int onSale;

        public CashItem() {

        }

        public CashItem(int sn, int itemId, int price, int priority, long period, short count, boolean onSale, int gender) {
            this.sn = sn;
            this.itemId = itemId;
            this.price = price;
            // 去除默认90天的期限
            this.priority = priority;
            this.period = period;
            this.count = count;
            this.onSale = onSale ? 1 : 0;
            this.gender = gender;
        }

        public Item toItem() {
            Item item;

            int petid = -1;
            if (ItemConstants.isPet(itemId)) {
                petid = MaplePet.createPet(itemId);
                // 修复下面因为把永久时装设置成-1，导致宠物也受到影响的问题
                period = period == 0 ? 90 : period;
            }

            if (ItemConstants.getInventoryType(itemId).equals(MapleInventoryType.EQUIP)) {
                item = MapleItemInformationProvider.getInstance().getEquipById(itemId);
            } else {
                item = new Item(itemId, (byte) 0, count, petid);
            }

            if (ItemConstants.EXPIRING_ITEMS) {
                if (period == 1) {
                    if (itemId == 5211048 || itemId == 5360042) { // 4 Hour 2X coupons, the period is 1, but we don't want them to last a day.
                        item.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 4));
                            /*
                            } else if(itemId == 5211047 || itemId == 5360014) { // 3 Hour 2X coupons, unused as of now
                                    item.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 3));
                            */
                    } else if (itemId == 5211060) { // 2 Hour 3X coupons.
                        item.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 2));
                    } else {
                        item.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 24));
                    }
                } else {
                    if (period == 0) {
                        // 永久时装道具
                        item.setExpiration(-1);
                    } else {
                        item.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 24 * period));
                    }
                }
            }

            item.setSN(sn);
            return item;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getSn() {
            return sn;
        }

        public void setSn(int sn) {
            this.sn = sn;
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public long getPeriod() {
            return period;
        }

        public void setPeriod(long period) {
            this.period = period;
        }

        public short getCount() {
            return count;
        }

        public void setCount(short count) {
            this.count = count;
        }

        public boolean isOnSale() {
            return onSale == 1;
        }

        public int getOnSale() {
            return onSale;
        }

        public void setOnSale(int onSale) {
            this.onSale = onSale;
        }

        @Override
        public CashItem clone() {
            try {
                return (CashItem) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new NapCvtEx(e.getMessage());
            }
        }
    }

    public static class SpecialCashItem {

        public int sn;
        public int flag;
        public int itemId;
        public int count;
        public int priority;
        public int price;
        public int period;
        public int maplePoints;
        public int mesos;
        public boolean premiumUser;
        public int gender;
        public boolean sale;
        public int job;
        public int requiredLevel;
        public int cash;
        public int point;
        public int gift;
        public int limit;
        public List<Integer> items;

        public SpecialCashItem(int sn, int flag) {
            this.sn = sn;
            this.flag = flag;
        }

        public int getSN() {
            return sn;
        }

        public int getFlag() {
            return flag;
        }

        /**
         * This sets the itemId
         */
        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        /**
         * This returns the itemId
         *
         * @return int
         */
        public int getItemId() {
            return itemId;
        }

        /**
         * This returns the Count
         *
         * @return int
         */
        public int getCount() {
            return count;
        }

        /**
         * This sets the count of the item
         */
        public void setCount(int count) {
            this.count = count;
        }

        /**
         * This returns the Priority
         *
         * @return int
         */
        public int getPriority() {
            return priority;
        }

        /**
         * This sets the priority level
         */
        public void setPriority(int priority) {
            this.priority = priority;
        }

        /**
         * This returns the Period
         *
         * @return int
         */
        public int getPeriod() {
            return period;
        }

        /**
         * This sets the period of the item
         */
        public void setPeriod(int period) {
            this.period = period;
        }

        /**
         * This returns the Maple Points
         *
         * @return int
         */
        public int getMaplePoints() {
            return maplePoints;
        }

        /**
         * This sets the Maple Point amount
         */
        public void setMaplePoints(int maplePoints) {
            this.maplePoints = maplePoints;
        }

        /**
         * This returns the Mesos
         *
         * @return int
         */
        public int getMesos() {
            return mesos;
        }

        /**
         * This sets the meso amount
         */
        public void setMesos(int mesos) {
            this.mesos = mesos;
        }

        /**
         * This returns if the Package is only for Premium users
         *
         * @return boolean
         */
        public boolean isPremiumUser() {
            return premiumUser;
        }

        /**
         * This sets if the item required can be bought by a premium user
         */
        public void setPremiumUser(boolean premiumUser) {
            this.premiumUser = premiumUser;
        }

        /**
         * This returns the Gender
         *
         * @return int
         */
        public int getGender() {
            return gender;
        }

        /**
         * This sets the gender restriction on the item 0 - Male<br\>
         * 1 - Female
         */
        public void setGender(int gender) {
            this.gender = gender;
        }

        /**
         * This returns the Sale
         *
         * @return boolean
         */
        public boolean getSale() {
            return sale;
        }

        /**
         * This sets the sale amount of the item
         */
        public void setSale(boolean sale) {
            this.sale = sale;
        }

        /**
         * This returns the job tree
         *
         * @return int
         */
        public int getJob() {
            return job;
        }

        /**
         * This sets the job branch which can buy the item
         */
        public void setJob(int job) {
            this.job = job;
        }

        /**
         * This returns the Required Level
         *
         * @return int
         */
        public int getRequiredLevel() {
            return requiredLevel;
        }

        /**
         * This sets the required level of the item
         */
        public void setRequiredLevel(int requiredLevel) {
            this.requiredLevel = requiredLevel;
        }

        /**
         * This returns the Cash
         *
         * @return int
         */
        public int getCash() {
            return cash;
        }

        /**
         * This sets the NX cash amount needed to purchase the item
         */
        public void setCash(int cash) {
            this.cash = cash;
        }

        /**
         * This returns the point
         *
         * @return int
         */
        public int getPoint() {
            return point;
        }

        /**
         * This sets the points needed to purchase the item
         */
        public void setPoint(int point) {
            this.point = point;
        }

        /**
         * This returns the Gift
         *
         * @return int
         */
        public int getGift() {
            return gift;
        }

        /**
         * This sets the gift amount?
         */
        public void setGift(int gift) {
            this.gift = gift;
        }

        /**
         * This returns the limit
         *
         * @return int
         */
        public int getLimit() {
            return limit;
        }

        /**
         * This sets the purchase limit
         */
        public void setLimit(int limit) {
            this.limit = limit;
        }

        /**
         * This returns the price
         *
         * @return int
         */
        public int getPrice() {
            return price;
        }

        /**
         * This sets the price of the item
         */
        public void setPrice(int price) {
            this.price = price;
        }

        /**
         * This gets the list of item SN inside of a package
         *
         * @return List<Integer>
         */
        public List<Integer> getItems() {
            if (items == null) {
                items = new ArrayList<>();
            }
            return items;
        }

        /**
         * @param items the items to set
         */
        public void setItems(List<Integer> items) {
            this.items = items;
        }
    }

    public static class CashItemFactory {

        // 限制最大修改数，防止服务端发送的封包长度大于50000字节
        private static int MAX_CASH_MOD_ITEMS = 1800;
        private static final Map<Integer, CashItem> items = new HashMap<>();
        private static final Map<Integer, CashItem> customerItems = new HashMap<>();
        private static final Map<Integer, List<Integer>> packages = new HashMap<>();
        private static final List<SpecialCashItem> specialcashitems = new ArrayList<>();
        private static Map<Integer, CashModInfo> itemMods = new HashMap<Integer, CashModInfo>();

        private static Map<String, CategoryItem> categoryItems = new HashMap<>();
        private static List<CategoryDiscount> discountedCategories = new ArrayList<>();
        private static Set<LimitedGoods> limitedGoods = new HashSet<>();
        private static Set<ItemStock> stock = new HashSet<>();
        private static final List<Integer> randomitemsns = new ArrayList<>();

        public static void loadSpecialCashItems() {
            MapleDataProvider etc = MapleDataProviderFactory.getDataProvider(new File("wz/Etc.wz"));

            for (MapleData item : etc.getData("Commodity.img").getChildren()) {
                int sn = MapleDataTool.getIntConvert("SN", item);
                int itemId = MapleDataTool.getIntConvert("ItemId", item);
                int price = MapleDataTool.getIntConvert("Price", item, 0);
                int priority = MapleDataTool.getIntConvert("Priority", item, 0);
                long period = MapleDataTool.getIntConvert("Period", item, 1);
                int gender = MapleDataTool.getIntConvert("Gender", item, 2);
                short count = (short) MapleDataTool.getIntConvert("Count", item, 1);
                boolean onSale = MapleDataTool.getIntConvert("OnSale", item, 0) == 1;
                items.put(sn, new CashItem(sn, itemId, price, priority, period, count, onSale, gender));

            }

            for (MapleData cashPackage : etc.getData("CashPackage.img").getChildren()) {
                List<Integer> cPackage = new ArrayList<>();
                for (MapleData item : cashPackage.getChildByPath("SN").getChildren()) {
                    cPackage.add(Integer.parseInt(item.getData().toString()));
                }
                packages.put(Integer.parseInt(cashPackage.getName()), cPackage);
            }

            for (Entry<Integer, CashItem> e : items.entrySet()) {
                if (e.getValue().isOnSale()) {

                    randomitemsns.add(e.getKey());
                }
            }

            // 加载分类
            for (MapleData item : etc.getData("Category.img").getChildren()) {
                int category = MapleDataTool.getIntConvert("Category", item);
                int categorySub = MapleDataTool.getIntConvert("CategorySub", item);
                String name = MapleDataTool.getString("Name", item, "");
                categoryItems.put(String.format("%s%02d", category, categorySub),
                        new CategoryItem(categorySub, category, name));
            }

            loadCustomerItems();
//            loadCashMobItems();
        }

        public static void loadCashMobItems() {

            try (Connection con = MysqlConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");
                ResultSet rs = ps.executeQuery();
                int count = 0;
                List<CashItem> discardItems = new ArrayList<>();
                while (rs.next()) {
                    CashModInfo ret = new CashModInfo(rs.getInt("serial"), rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                    // 更新数据
                    if (ret.showUp) {
                        if (itemMods.size() < MAX_CASH_MOD_ITEMS) {
                            itemMods.put(ret.sn, ret);
                            CashItem cc = items.get(ret.sn);
                            if (cc == null) {
                                cc = new CashItem(ret.sn, ret.itemid, ret.discountPrice, ret.priority, ret.period, (short) ret.count, ret.showUp, ret.gender);
                                items.put(ret.sn, cc);
                                randomitemsns.add(ret.sn);
                            }
                            ret.toCItem(cc);
                        } else {
                            ret.showUp = false;
                            CashItem discardItem = new CashItem(ret.sn, ret.itemid, ret.discountPrice, ret.priority, ret.period, (short) ret.count, ret.showUp, ret.gender);
                            discardItems.add(discardItem);
                            count++;

                        }
                    }
                }
                if (count > 0) {
                    updateDiscardItemsToDB(discardItems);
                    System.out.println(String.format("商城mod超出预设置最大数:%s,已抛弃%s个商品的修改", MAX_CASH_MOD_ITEMS, count));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static void loadCustomerItems() {
            for (CashItem cashItem : CashItemDao.selectAllSale()) {
                customerItems.put(cashItem.getSn(), cashItem);
            }
        }

        private static void updateDiscardItemsToDB(List<CashItem> discardItems) {
            try {
                Connection connection = MysqlConnection.getConnection();
                String updateSql = "UPDATE cashshop_modified_items SET showup = ? WHERE serial = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(updateSql);

                for (CashItem cashItem : discardItems) {
                    int serial = cashItem.sn;
                    int showup = cashItem.isOnSale() ? 1 : 0;

                    preparedStatement.setInt(1, showup);
                    preparedStatement.setInt(2, serial);

                    preparedStatement.addBatch(); // 添加到批处理中
                }

                preparedStatement.executeBatch(); // 执行批处理更新操作
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static CashItem getRandomCashItem() {
            if (randomitemsns.isEmpty()) return null;

            int rnd = (int) (Math.random() * randomitemsns.size());
            return items.get(randomitemsns.get(rnd));
        }

        public static CashItem getItem(int sn) {
            CashItem stats = customerItems.get(sn);
            if (stats == null) {
                stats = items.get(sn);
            }
//            if (null == stats) {
//                return null;
//            }
//            System.out.println(String.format("测试道具：sn:%s count:%s gender:%s item_id: %s", sn, stats.count, stats.gender, stats.itemId));
//            final CashModInfo z = getModInfo(sn);
//            if (z != null && z.showUp) {
//                System.out.println(String.format("商城道具被服务端替换：序列号:%s 物品id: %s 现在价格：%s 原来价格：%s", stats.sn, stats.itemId, z.toCItem(stats).price, stats.price));
//                return z.toCItem(stats); //null doesnt matter
//            }
            //hmm
            return stats;
        }

        public static CashModInfo getModInfo(int sn) {
            return itemMods.get(sn);
        }


        public static List<Item> getPackage(int itemId) {
            List<Item> cashPackage = new ArrayList<>();

            for (int sn : packages.get(itemId)) {
                cashPackage.add(getItem(sn).toItem());
            }

            return cashPackage;
        }

        public static List<Integer> packagesByItemId(int itemId) {
            return packages.get(itemId);
        }

        public static boolean isPackage(int itemId) {
            return packages.containsKey(itemId);
        }

        public static List<SpecialCashItem> getSpecialCashItems() {
            return specialcashitems;
        }


        public static List<CategoryDiscount> getDiscountedCategories() {
            return discountedCategories;
        }

        public static Set<LimitedGoods> getLimitedGoods() {
            return limitedGoods;
        }

        public static Set<ItemStock> getStock() {
            return stock;
        }

        public static void reloadSpecialCashItems() {//Yay?
            specialcashitems.clear();
            PreparedStatement ps = null;
            ResultSet rs = null;
            Connection con = null;
//            try {
//                con = MysqlConnection.getConnection();
//                ps = con.prepareStatement("SELECT * FROM specialcashitems");
//                rs = ps.executeQuery();
//                while (rs.next()) {
//                    specialcashitems.add(new SpecialCashItem(rs.getInt("sn"), rs.getInt("modifier"), rs.getByte("info")));
//                }
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            } finally {
//                try {
//                    if (rs != null && !rs.isClosed()) rs.close();
//                    if (ps != null && !ps.isClosed()) ps.close();
//                    if (con != null && !con.isClosed()) con.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
        }

        public static Collection<CashModInfo> getAllModInfo() {
            return itemMods.values();
        }

        public static Map<Integer, CashModInfo> getItemMods() {
            return itemMods;
        }

        public static Map<Integer, CashItem> getItems() {
            return items;
        }

        public static Collection<CategoryItem> getAllCategory() {
            return categoryItems.values();
        }

        public static void clearItemMobData() {
            itemMods = new HashMap<>();
        }

        public static Map<Integer, CashItem> getCustomerItems() {
            return customerItems;
        }

    }

    private int accountId, characterId, nxCredit, maplePoint, nxPrepaid;
    private boolean opened;
    private ItemFactory factory;
    private List<Item> inventory = new ArrayList<>();
    private List<Integer> wishList = new ArrayList<>();
    private int notes = 0;
    private Lock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CASHSHOP);

    public CashShop(int accountId, int characterId, int jobType) throws SQLException {
        this.accountId = accountId;
        this.characterId = characterId;

        if (!CommonConfig.config.server.useJointCashShopInventory) {
            if (jobType == 0) {
                factory = ItemFactory.CASH_EXPLORER;
            } else if (jobType == 1) {
                factory = ItemFactory.CASH_CYGNUS;
            } else if (jobType == 2) {
                factory = ItemFactory.CASH_ARAN;
            }
        } else {
            factory = ItemFactory.CASH_OVERALL;
        }

        Connection con = MysqlConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT `nxCredit`, `maplePoint`, `nxPrepaid` FROM `accounts` WHERE `id` = ?");
            ps.setInt(1, accountId);
            rs = ps.executeQuery();

            if (rs.next()) {
                this.nxCredit = rs.getInt("nxCredit");
                this.maplePoint = rs.getInt("maplePoint");
                this.nxPrepaid = rs.getInt("nxPrepaid");
            }

            rs.close();
            ps.close();

            for (Pair<Item, MapleInventoryType> item : factory.loadItems(accountId, false)) {
                inventory.add(item.getLeft());
            }

            ps = con.prepareStatement("SELECT `sn` FROM `wishlists` WHERE `charid` = ?");
            ps.setInt(1, characterId);
            rs = ps.executeQuery();

            while (rs.next()) {
                wishList.add(rs.getInt("sn"));
            }

            rs.close();
            ps.close();
            con.close();
        } finally {
            if (ps != null && !ps.isClosed()) ps.close();
            if (rs != null && !rs.isClosed()) rs.close();
            if (con != null && !con.isClosed()) con.close();
        }
    }

    public int getCash(int type) {
        switch (type) {
            case 1:
                return nxCredit;
            case 2:
                return maplePoint;
            case 4:
                return nxPrepaid;
        }

        return 0;
    }

    public void gainNx(int cash) {
        long added = (long) nxCredit + cash;
        if (added > Integer.MAX_VALUE) {
            nxCredit = Integer.MAX_VALUE;
        } else {
            nxCredit = (int) added;
        }
    }

    public void gainCash(int type, int cash) {
        // 1-点券，2-皇家点券，4-代金券
        switch (type) {
            case 1:
                nxCredit += cash;
                break;
            case 2:
                maplePoint += cash;
                break;
            case 4:
                nxPrepaid += cash;
                break;
        }
    }

    public void gainCash(int type, CashItem buyItem, int world) {
        gainCash(type, -buyItem.getPrice());
        if (!CommonConfig.config.server.useEnforceItemSuggestion)
            Server.getInstance().getWorld(world).addCashItemBought(buyItem.getSn());
    }

    public boolean isOpened() {
        return opened;
    }

    public void open(boolean b) {
        opened = b;
    }

    public List<Item> getInventory() {
        lock.lock();
        try {
            return Collections.unmodifiableList(inventory);
        } finally {
            lock.unlock();
        }
    }

    public Item findByCashId(int cashId) {
        boolean isRing;
        Equip equip = null;
        for (Item item : getInventory()) {
            if (item.getInventoryType().equals(MapleInventoryType.EQUIP)) {
                equip = (Equip) item;
                isRing = equip.getRingId() > -1;
            } else {
                isRing = false;
            }

            if ((item.getPetId() > -1 ? item.getPetId() : isRing ? equip.getRingId() : item.getCashId()) == cashId) {
                return item;
            }
        }

        return null;
    }

    public void addToInventory(Item item) {
        lock.lock();
        try {
            inventory.add(item);
        } finally {
            lock.unlock();
        }
    }

    public void removeFromInventory(Item item) {
        lock.lock();
        try {
            inventory.remove(item);
        } finally {
            lock.unlock();
        }
    }

    public List<Integer> getWishList() {
        return wishList;
    }

    public void clearWishList() {
        wishList.clear();
    }

    public void addToWishList(int sn) {
        wishList.add(sn);
    }

    public void gift(int recipient, String from, String message, int sn) {
        gift(recipient, from, message, sn, -1);
    }

    public void gift(int recipient, String from, String message, int sn, int ringid) {
        PreparedStatement ps = null;
        Connection con = null;
        try {
            con = MysqlConnection.getConnection();
            ps = con.prepareStatement("INSERT INTO `gifts` VALUES (DEFAULT, ?, ?, ?, ?, ?)");
            ps.setInt(1, recipient);
            ps.setString(2, from);
            ps.setString(3, message);
            ps.setInt(4, sn);
            ps.setInt(5, ringid);
            ps.executeUpdate();
            con.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                if (ps != null && !ps.isClosed()) ps.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<Pair<Item, String>> loadGifts() {
        List<Pair<Item, String>> gifts = new ArrayList<>();
        Connection con = null;

        try {
            con = MysqlConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `gifts` WHERE `to` = ?");
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                notes++;
                CashItem cItem = CashItemFactory.getItem(rs.getInt("sn"));
                Item item = cItem.toItem();
                Equip equip = null;
                item.setGiftFrom(rs.getString("from"));
                if (item.getInventoryType().equals(MapleInventoryType.EQUIP)) {
                    equip = (Equip) item;
                    equip.setRingId(rs.getInt("ringid"));
                    gifts.add(new Pair<Item, String>(equip, rs.getString("message")));
                } else
                    gifts.add(new Pair<>(item, rs.getString("message")));

                if (CashItemFactory.isPackage(cItem.getItemId())) { //Packages never contains a ring
                    for (Item packageItem : CashItemFactory.getPackage(cItem.getItemId())) {
                        packageItem.setGiftFrom(rs.getString("from"));
                        addToInventory(packageItem);
                    }
                } else {
                    addToInventory(equip == null ? item : equip);
                }
            }

            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM `gifts` WHERE `to` = ?");
            ps.setInt(1, characterId);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return gifts;
    }

    public int getAvailableNotes() {
        return notes;
    }

    public void decreaseNotes() {
        notes--;
    }

    public void save(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `nxCredit` = ?, `maplePoint` = ?, `nxPrepaid` = ? WHERE `id` = ?");
        ps.setInt(1, nxCredit);
        ps.setInt(2, maplePoint);
        ps.setInt(3, nxPrepaid);
        ps.setInt(4, accountId);
        ps.executeUpdate();
        ps.close();
        List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();

        List<Item> inv = getInventory();
        for (Item item : inv) {
            itemsWithType.add(new Pair<>(item, item.getInventoryType()));
        }

        factory.saveItems(itemsWithType, accountId, con);
        ps = con.prepareStatement("DELETE FROM `wishlists` WHERE `charid` = ?");
        ps.setInt(1, characterId);
        ps.executeUpdate();
        ps.close();
        ps = con.prepareStatement("INSERT INTO `wishlists` VALUES (DEFAULT, ?, ?)");
        ps.setInt(1, characterId);

        for (int sn : wishList) {
            ps.setInt(2, sn);
            ps.executeUpdate();
        }

        ps.close();
    }

    private Item getCashShopItemByItemid(int itemid) {
        lock.lock();
        try {
            for (Item it : inventory) {
                if (it.getItemId() == itemid) {
                    return it;
                }
            }
        } finally {
            lock.unlock();
        }

        return null;
    }

    public synchronized Pair<Item, Item> openCashShopSurprise() {
        Item css = getCashShopItemByItemid(5222000);

        if (css != null) {
            CashItem cItem = CashItemFactory.getRandomCashItem();

            if (cItem != null) {
                if (css.getQuantity() > 1) {
                    /* if(NOT ENOUGH SPACE) { looks like we're not dealing with cash inventory limit whatsoever, k then
                        return null;
                    } */

                    css.setQuantity((short) (css.getQuantity() - 1));
                } else {
                    removeFromInventory(css);
                }

                Item item = cItem.toItem();
                addToInventory(item);

                return new Pair<>(item, css);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    public static Item generateCouponItem(int itemId, short quantity) {
        CashItem it = new CashItem(77777777, itemId, 7777, 0, ItemConstants.isPet(itemId) ? 30 : 0, quantity, true, 2);
        return it.toItem();
    }
}
