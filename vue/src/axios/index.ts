import axios from 'axios'

const service = axios.create({
  // 独立部署，配置请求服务全路径
  baseURL: import.meta.env.PROD ? 'http://192.168.0.0:8090/web/api' : import.meta.env.VITE_AXIOS_BASE_URL,
  timeout: 10000
})

// 添加请求拦截器
service.interceptors.request.use(
  function (config) {
    // 在发送请求之前做些什么
    // console.log(config)
    return config
  },
  function (error) {
    // 对请求错误做些什么
    console.log(error)
    return Promise.reject(error)
  }
)

// 添加响应拦截器
service.interceptors.response.use(
  function (response) {
    // console.log(response)
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    // dataAxios 是 axios 返回数据中的 data
    const dataAxios = response.data
    return dataAxios
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    console.log(error)
    return Promise.reject(error)
  }
)

export default service

declare module 'axios' {
  interface AxiosInstance {
    (config: AxiosRequestConfig): Promise<any>
  }
}