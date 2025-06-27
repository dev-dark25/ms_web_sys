import type { MenuInfo } from '@/interface/common'

export function assembleMenuList(paramList: MenuInfo[]): MenuInfo[] {
  let menuList: MenuInfo[] = [];
  let secondMenuList: MenuInfo[] = [];
  paramList.forEach((item, index) => {
    if (!item.isMenu && item.menuNum) {
      secondMenuList.push(item); // 二级目录
    } else {
      const menu: MenuInfo = {
        path: item.path,
        icon: item.icon,
        title: item.title,
        isMenu: item.isMenu,
        menuNum: item.menuNum
      }
      menuList.push(menu);  // 一级目录      
      // menuList.push(item);  // 一级目录
    }
  })
  menuList.forEach(item => {
    if (item.isMenu) {
      let list: MenuInfo[] = [];
      secondMenuList.forEach(item1 => {
        if (item.menuNum === item1.menuNum) {
          const menu: MenuInfo = {
            path: item1.path,
            icon: item1.icon,
            title: item1.title,
            isMenu: item1.isMenu,
            menuNum: item1.menuNum
          }
          list.push(menu);
        }
      })
      item.list = list;
    }
  })
  return menuList;
}

export function assembleRouteList(paramList: MenuInfo[]): MenuInfo[] {
  let map = new Map();
  let routelist: MenuInfo[] = [];

  paramList.forEach(item => {
    if (!item.isMenu) {
      if (!map.has(item.path)) {
        map.set(item.path, 1);
        const route: MenuInfo = {
          path: item.path,
          name: item.name,
          vuePage: item.vuePage,
          directory: item.directory,
        }
        routelist.push(route);
      }
    }
  })
  return routelist;
}