package ui.dao;

import cn.nap.utils.common.NapComUtils;
import cn.nap.utils.common.NapMapUtils;
import tools.Log;
import tools.SqliteConnection;
import ui.model.KookProp;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class KookConfigDao {
    static {
        if (!SqliteConnection.isInitialized()) {
            SqliteConnection.init();
        }
    }

    public static KookProp readKookProp() {
        KookProp kookProp = new KookProp();
        List<Map<String, Object>> selectList = SqliteConnection.select("select * from kook_config");
        if (NapComUtils.isEmpty(selectList)) {
            return kookProp;
        }
        for (Map<String, Object> map : selectList) {
            try {
                Field field = kookProp.getClass().getDeclaredField(NapMapUtils.getString(map, "config_name"));
                field.setAccessible(true);
                field.set(kookProp, NapMapUtils.getString(map, "config_data"));
            } catch (Exception e) {
                System.out.println("½âÎökook×Ö¶ÎÊ§°Ü");
            }
        }
        return kookProp;
    }

    public static void updateKookProp(KookProp kookProp) {
        for (Field field : kookProp.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                SqliteConnection.update("update kook_config set config_data = ? where config_name = ?", field.get(kookProp), field.getName());
            } catch (Exception e) {
                System.out.println("¸üÐÂkook×Ö¶ÎÊ§°Ü");
            }
        }
    }
}
