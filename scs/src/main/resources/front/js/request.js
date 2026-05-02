(function (win) {
  axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
  
  // 请求缓存
  const cache = new Map();
  const CACHE_TIME = 5 * 60 * 1000; // 5分钟缓存
  
  // 创建axios实例
  const service = axios.create({
    // axios中请求配置有baseURL选项,表示请求URL公共部分
    baseURL: '/',
    // 优化: 超时时间从1000000ms(16分钟)改为10000ms(10秒),避免长时间等待
    timeout: 10000,
    // 优化：减少重试次数
    maxRedirects: 3
  })
  
  // 生成缓存key
  function getCacheKey(config) {
    return `${config.method}:${config.url}:${JSON.stringify(config.params || {})}`;
  }
  
  // request拦截器
  service.interceptors.request.use(config => {
    // GET请求缓存
    if (config.method === 'get') {
      const cacheKey = getCacheKey(config);
      const cached = cache.get(cacheKey);
      
      if (cached && Date.now() - cached.timestamp < CACHE_TIME) {
        // 返回缓存数据
        return Promise.reject({
          __CANCEL__: true,
          cachedData: cached.data
        });
      }
    }
    
    // get请求映射params参数
    if (config.method === 'get' && config.params) {
      let url = config.url + '?';
      for (const propName of Object.keys(config.params)) {
        const value = config.params[propName];
        var part = encodeURIComponent(propName) + "=";
        if (value !== null && typeof(value) !== "undefined") {
          if (typeof value === 'object') {
            for (const key of Object.keys(value)) {
              let params = propName + '[' + key + ']';
              var subPart = encodeURIComponent(params) + "=";
              url += subPart + encodeURIComponent(value[key]) + "&";
            }
          } else {
            url += part + encodeURIComponent(value) + "&";
          }
        }
      }
      url = url.slice(0, -1);
      config.params = {};
      config.url = url;
    }
    return config
  }, error => {
      return Promise.reject(error)
  })

  // 响应拦截器
  service.interceptors.response.use(res => {
      console.log('---响应拦截器---',res)
      
      // 缓存GET请求结果
      if (res.config.method === 'get') {
        const cacheKey = getCacheKey(res.config);
        cache.set(cacheKey, {
          data: res.data,
          timestamp: Date.now()
        });
        
        // 清理过期缓存（保留最近100条）
        if (cache.size > 100) {
          const oldestKey = cache.keys().next().value;
          cache.delete(oldestKey);
        }
      }
      
      if (res.data.code === 0 && res.data.msg === 'NOTLOGIN') {// 返回登录页面
        window.top.location.href = '/front/page/login.html'
      } else {
        return res.data
      }
    },
    error => {
      // 处理缓存命中
      if (error.__CANCEL__) {
        return Promise.resolve(error.cachedData);
      }
      
      let { message } = error;
      if (message == "Network Error") {
        message = "后端接口连接异常";
      }
      else if (message.includes("timeout")) {
        message = "系统接口请求超时";
      }
      else if (message.includes("Request failed with status code")) {
        message = "系统接口" + message.substr(message.length - 3) + "异常";
      }
      
      // 优化：只在有vant时才调用
      if (window.vant && window.vant.Notify) {
        window.vant.Notify({
          message: message,
          type: 'warning',
          duration: 3000
        });
      } else {
        console.error('Request Error:', message);
      }
      
      return Promise.reject(error)
    }
  )
  win.$axios = service
})(window);
