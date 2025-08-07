package server.cashshop;

public class CategoryItem {
    public int category;
    public int categorySub;

    public String name;


    public CategoryItem(int categorySub, int category, String name) {
        this.category = category;
        this.categorySub = categorySub;
        this.name = category == 1 ? name + "£¨»Ê¼Ò£©" : name;
    }

    public boolean matches(int sn) {
        String str = String.format("%s%s", category, categorySub >= 10 ? categorySub + "" : "0" + categorySub);
        String rightCategoryStr = String.valueOf(sn).substring(0, 3);
        return str.equals(rightCategoryStr);
    }

    public String getCategory() {
        return String.format("%s%02d", category, categorySub);
    }

}
