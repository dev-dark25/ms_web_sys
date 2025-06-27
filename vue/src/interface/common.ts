// 属性类接口
interface UserInfo {
  name: string | null | undefined;
  age: number | null | undefined;
}

interface MenuInfo {
  path: string;
  icon?: string;
  title?: string;
  name?: string;
  vuePage?: string;
  isMenu?: boolean;
  menuNum?: string;
  list?: MenuInfo[];
  directory?: string;
  isMultiLevelDirectory?: boolean;
  multiLevelDirectory?: string;
}

interface Test {
  param: string;
  param1?: string;  // ?表示可选
  readonly param2: number; // 只读
  param3: Test1;
}

interface Test1 {
  param1: string;
  param2: number;
}

export type { UserInfo, MenuInfo, Test };