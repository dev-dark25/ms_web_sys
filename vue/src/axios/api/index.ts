import service from '@/axios'

// 获取用户信息
export function login(param: object) {
  return service({
    url: '/login',
    method: 'post',
    data: param,
  })
}

export function logout() {
  return service({
    url: '/logout',
    method: 'post',
    // data: param,
  })
}

export function getUserInfo() {
  return service({
    url: '/getUserInfo',
    method: 'post',
    // data: param,
  })
}

export function getMenuInfo() {
  return service({
    url: '/getMenuInfo',
    method: 'post',
    // data: param,
  })
}

// Maple Story api
export function getMSAccounts() {
  return service({
    url: '/ms/getAccounts',
    method: 'post',
    // data: param,
  })
}

export function getMSCharacters(param: object) {
  return service({
    url: '/ms/getCharacters',
    method: 'post',
    data: param,
  })
}

export function getMSSkills(param: object) {
  return service({
    url: '/ms/getSkills',
    method: 'post',
    data: param,
  })
}

export function getMSRealtimeLoggedCount() {
  return service({
    url: '/ms/getRealtimeLoggedCount',
    method: 'post',
    // data: param,
  })
}

export function startMS() {
  return service({
    url: '/ms/start',
    method: 'post',
    // data: param,
  })
}

export function stopMS() {
  return service({
    url: '/ms/stop',
    method: 'post',
    // data: param,
  })
}

export function restartMS() {
  return service({
    url: '/ms/restart',
    method: 'post',
    // data: param,
  })
}

export function getMSConfig(param: object) {
  return service({
    url: '/ms/getConfig',
    method: 'post',
    data: param
  })
}

export function updataMSConfig(param: object) {
  return service({
    url: '/ms/updataConfig',
    method: 'post',
    data: param
  })
}

export function getMSCommand(param: object) {
  return service({
    url: '/ms/getCommand',
    method: 'post',
    data: param
  })
}

export function updataMSCommand(param: object) {
  return service({
    url: '/ms/updataCommand',
    method: 'post',
    data: param
  })
}


export function getMSPlayer(param: object) {
  return service({
    url: '/ms/getPlayer',
    method: 'post',
    data: param
  })
}

export function updataMSPlayer(param: object) {
  return service({
    url: '/ms/updataPlayer',
    method: 'post',
    data: param
  })
}