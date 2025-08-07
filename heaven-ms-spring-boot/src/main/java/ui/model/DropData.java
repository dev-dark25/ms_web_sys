package ui.model;

public class DropData {
    private Long id;
    private Integer dropperid;
    private Integer itemid;
    private Integer minimumQuantity;
    private Integer maximumQuantity;
    private Integer questid;
    private Integer chance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDropperid() {
        return dropperid;
    }

    public void setDropperid(Integer dropperid) {
        this.dropperid = dropperid;
    }

    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Integer getMaximumQuantity() {
        return maximumQuantity;
    }

    public void setMaximumQuantity(Integer maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    public Integer getQuestid() {
        return questid;
    }

    public void setQuestid(Integer questid) {
        this.questid = questid;
    }

    public Integer getChance() {
        return chance;
    }

    public void setChance(Integer chance) {
        this.chance = chance;
    }
}
