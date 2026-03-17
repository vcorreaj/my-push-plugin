var exec = require('cordova/exec');

var PLUGIN_NAME = 'MyPushPlugin';

var MyPushPlugin = {
    getToken: function(success, error) {
        exec(success, error, PLUGIN_NAME, 'getToken', []);
    },
    
    onNotification: function(callback) {
        exec(callback, null, PLUGIN_NAME, 'onNotification', []);
    },
    
    onTokenRefresh: function(callback) {
        exec(callback, null, PLUGIN_NAME, 'onTokenRefresh', []);
    }
};

module.exports = MyPushPlugin;