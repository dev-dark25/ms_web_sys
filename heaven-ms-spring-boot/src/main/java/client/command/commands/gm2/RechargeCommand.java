package client.command.commands.gm2;

import client.MapleCharacter;
import client.command.Command;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;

public class RechargeCommand extends Command {
    {
        setDescription("充能您的使用道具。");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (Item toRecharge : c.getPlayer().getInventory(MapleInventoryType.USE).list()) {
            if (ItemConstants.isThrowingStar(toRecharge.getItemId())){
                toRecharge.setQuantity(ii.getSlotMax(c, toRecharge.getItemId()));
                c.getPlayer().forceUpdateItem(toRecharge);
            } else if (ItemConstants.isArrow(toRecharge.getItemId())){
                toRecharge.setQuantity(ii.getSlotMax(c, toRecharge.getItemId()));
                c.getPlayer().forceUpdateItem(toRecharge);
            } else if (ItemConstants.isBullet(toRecharge.getItemId())){
                toRecharge.setQuantity(ii.getSlotMax(c, toRecharge.getItemId()));
                c.getPlayer().forceUpdateItem(toRecharge);
            } else if (ItemConstants.isConsumable(toRecharge.getItemId())){
                toRecharge.setQuantity(ii.getSlotMax(c, toRecharge.getItemId()));
                c.getPlayer().forceUpdateItem(toRecharge);
            }
        }
        player.dropMessage(5, "使用道具已充能。");
    }
}