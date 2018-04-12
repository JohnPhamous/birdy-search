const ProxyLists = require('proxy-lists')

const options = {
  countries: ['us'],
  protocols: ['https']
}

const proxiesList = []
const gettingProxies = ProxyLists.getProxies(options)

gettingProxies.on('data', proxies => {
  proxies.forEach(proxy => {
    let currentProxy = {
      ipAddress: proxy.ipAddress,
      port: proxy.port
    }
    proxiesList.push(currentProxy)
  })
})

gettingProxies.on('error', function(error) {
  // Some error has occurred.
  console.error(error)
  console.log(proxiesList)
})

gettingProxies.once('end', function() {
  // Done getting proxies.
})
