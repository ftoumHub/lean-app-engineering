var webpack = require('webpack');

/*
 * Default webpack configuration for development
 */
var config = {
  devtool: 'eval-source-map',
  entry:  __dirname + "/app/App.js",
  output: {
    publicPath: 'public/',
    path: __dirname + "/public/",
    filename: "bundle.js"
  },
  module: {
    loaders: [
    {
      test: /\.jsx?$/,
      exclude: /node_modules/,
      loader: 'babel-loader',
      query: {
        presets: ['es2015','react']
      }
    }]
  },
  devServer: {
    proxy: {
      '/api/*': {
        target: 'http://localhost:9191'
      }
    }
  }
};

module.exports = config;
