package server.cashshop;

public class CashModInfo implements Cloneable {

    public int discountPrice, mark, priority, sn, itemid, flags, period, gender, count, meso, unk_1, unk_2, unk_3, extra_flags;
    public boolean showUp, packagez;
    private CashShop.CashItem cii;

    public CashModInfo(int sn, int discount, int mark, boolean show, int itemid, int priority, boolean packagez, int period, int gender, int count, int meso, int unk_1, int unk_2, int unk_3, int extra_flags) {
        this.sn = sn;
        this.itemid = itemid;
        this.discountPrice = discount;
        this.mark = mark; //0 = new, 1 = sale, 2 = hot, 3 = event
        this.showUp = show;
        this.priority = priority;
        this.packagez = packagez;
        this.period = period;
        this.gender = gender;
        this.count = count;
        this.meso = meso;
        this.unk_1 = unk_1; //0 = doesn't have, 1 = has, but false, 2 = has and true
        this.unk_2 = unk_2;
        this.unk_3 = unk_3;
        this.extra_flags = extra_flags;
        this.flags = extra_flags;

        if (this.itemid > 0) {
            this.flags |= 0x1;
        }
        if (this.count > 0) {
            this.flags |= 0x2;
        }
        if (this.discountPrice > 0) {
            this.flags |= 0x4;
        }
        if (this.unk_1 > 0) {
            this.flags |= 0x8;
        }
        if (this.priority >= 0) {
            this.flags |= 0x10;
        }
        if (this.period > 0) {
            this.flags |= 0x20;
        }
        //0x40 = ?
        if (this.meso > 0) {
            this.flags |= 0x80;
        }
        if (this.unk_2 > 0) {
            this.flags |= 0x100;
        }
        if (this.gender >= 0) {
            this.flags |= 0x200;
        }
        if (this.showUp) {
            this.flags |= 0x400;
        }
        if (this.mark >= -1 || this.mark <= 3) {
            this.flags |= 0x800;
        }
        if (this.unk_3 > 0) {
            this.flags |= 0x1000;
        }
        //0x2000, 0x4000, 0x8000, 0x10000, 0x20000, 0x100000, 0x80000 - ?
        if (this.packagez) {
            this.flags |= 0x40000;
        }
    }

    public CashShop.CashItem toCItem(CashShop.CashItem backup) {
        if (cii != null) {
            return cii;
        }
        final int item, count, price, expire, gender;
        final boolean onSale;
        if (itemid <= 0) {
            item = (backup == null ? 0 : backup.getItemId());
        } else {
            item = itemid;
        }
        if (this.count <= 0) {
            count = (backup == null ? 0 : backup.getCount());
        } else {
            count = this.count;
        }
        if (meso <= 0) {
            if (discountPrice <= 0) {
                price = (backup == null ? 0 : backup.getPrice());
            } else {
                price = discountPrice;
            }
        } else {
            price = meso;
        }
        if (period <= 0) {
            expire = backup == null ? 0 : (int) backup.getPeriod();
        } else {
            expire = period;
        }
        if (this.gender < 0) {
            gender = (backup == null ? 0 : backup.getGender());
        } else {
            gender = this.gender;
        }
        if (!showUp) {
            onSale = (backup == null ? false : backup.isOnSale());
        } else {
            onSale = showUp;
        }
        cii = new CashShop.CashItem(sn, itemid, price, priority, expire, (short) count, onSale, gender);
        return cii;
    }

    @Override
    public CashModInfo clone() throws CloneNotSupportedException {
        return (CashModInfo) super.clone();
    }
}