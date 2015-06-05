(function() {
	'use strict';

	/**
	 * Construtor do plugin - Stub somente para prototypes
	 */
	function PluginVivo () {}

	PluginVivo.prototype.getLaunchers = function(options, successCallback, errorCallback) {
		options = options || {};
		options.successCallback = options.successCallback || successCallback;
		options.errorCallback = options.errorCallback || errorCallback;
		cordova.exec(options.successCallback || null, options.errorCallback || null, "ProjetoVivoPlugin", "getLaunchers", [options]);
	};

	PluginVivo.prototype.launchApp = function(options, successCallback, errorCallback) {
		options = options || {};
		options.successCallback = options.successCallback || successCallback;
		options.errorCallback = options.errorCallback || errorCallback;
		cordova.exec(options.successCallback || null, options.errorCallback || null, "ProjetoVivoPlugin", "launchApp", [options]);
	};

	PluginVivo.install = function() {
		if (!window.plugins) {
			window.plugins = {};
		}

		window.plugins.pluginVivo = new PluginVivo();
		return window.plugins.pluginVivo;
	};

	cordova.addConstructor(PluginVivo.install);
})();
