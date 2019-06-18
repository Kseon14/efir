// vue.config.js
module.exports = {
  // proxy all webpack dev-server requests starting with /api
  // to our Spring Boot backend (localhost:8088) using http-proxy-middleware
  // see https://cli.vuejs.org/config/#devserver-proxy
  devServer: {
    proxy: {
      '/api/workers': {
        target: 'http://134.249.123.246:9005',
        ws: true,
        changeOrigin: true
      },
      '/api/shifts': {
        target: 'http://134.249.123.246:9005',
        ws: true,
        changeOrigin: true
      },
      '/api/salaries': {
        target: 'http://localhost:9005',
        ws: true,
        changeOrigin: true
      }
    }
  },
  // Change build paths to make them Maven compatible
  // see https://cli.vuejs.org/config/
  outputDir: 'target/dist',
  assetsDir: 'static'
}


