package br.com.uaiti.cordova.plugins.vivo;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import java.util.List;
import java.lang.Object;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProjetoVivoPlugin extends CordovaPlugin {
	public static final String TAG = "ProjetoVivoPlugin";
	public static final String ACTION_GET_LAUCNCHERS = "getLaunchers";
	public static final String ACTION_LAUNCH_APP = "launchApp";
	public static final String PACKAGE_NAME = "packageName";
	public static final String ACTIVITY_NAME = "activity";

	/**
	* Constructor.
	*/
	public ProjetoVivoPlugin() {}
	/**
	* Sets the context of the Command. This can then be used to do things like
	* get file paths associated with the Activity.
	*
	* @param cordova The context of the main Activity.
	* @param webView The CordovaWebView Cordova is running in.
	*/
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {

		super.initialize(cordova, webView);
		Log.v(TAG,"Init ProjetoVivoPlugin");

	}
	@Override
	public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		Log.v(TAG,"ProjetoVivoPlugin received:"+ action);

		if(action.equals(ACTION_GET_LAUCNCHERS)){
			JSONArray launchers = listLaunchers(this.cordova.getActivity());

			if(launchers.length() == 0){
				callbackContext.error("Nenhum launcher home foi encontrado.");
				return false;
			}

			callbackContext.success(launchers);			
			return true;
		}

		if(action.equals(ACTION_LAUNCH_APP)){
			if(args.length() != 1){				
				callbackContext.error("Selecione apenas um aplicativo");
				return false;
			}

			Log.d(TAG,"Args: " + args);

			JSONObject appToLaunch = args.getJSONObject(0);
			Log.d(TAG,"AppToLaunch: " + appToLaunch);

			if(!appToLaunch.has(PACKAGE_NAME) || !appToLaunch.has(ACTIVITY_NAME)){
				callbackContext.error("launchApp json inválido.");
				return false;	
			}			
			
			//Faz um validação do app a ser lançado
			if(!appInstalled(this.cordova.getActivity().getApplicationContext(), appToLaunch.get("packageName"))){
				callbackContext.error("Aplicativo selecionado não está instalado");
			}

			Intent launcher = new Intent(Intent.ACTION_MAIN);
	        launcher.setClassName(appToLaunch.getString(PACKAGE_NAME), appToLaunch.getString(ACTIVITY_NAME));
	        this.cordova.getActivity().startActivity(launcher);

	        callbackContext.success();
			return true;
		}

		callbackContext.error("Ação selecionada não está definida neste plugin");
		return false;
	}

	public JSONArray listLaunchers(Context ctx) throws JSONException{
		//Lista de launchers home instalados no device
		JSONArray launchers = new JSONArray();
		
		//Cria uma intent e adiciona um filtro para lista apenas o apps home
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);		
				
		List<ResolveInfo> lst = ctx.getPackageManager().queryIntentActivities(intent, 0);
		if (!lst.isEmpty()) {
		   for (ResolveInfo resolveInfo : lst) {
			   if(!resolveInfo.activityInfo.packageName.equals("com.uaiti.precificador") && !resolveInfo.activityInfo.packageName.equals("br.com.uaiti.vivomagazine")){
				   JSONObject jsonLauncher = new JSONObject();
				   jsonLauncher.put(PACKAGE_NAME, resolveInfo.activityInfo.packageName);
				   jsonLauncher.put(ACTIVITY_NAME,resolveInfo.activityInfo.name);
				   
				   Log.d(TAG, "PackageName: " + resolveInfo.activityInfo.packageName + " -- Main Activity: " + resolveInfo.activityInfo.name);
				   launchers.put(jsonLauncher);				   
				}
		   }
		}
		
		return launchers;
	}

	public static boolean appInstalled(Context ctx, String uri) {
		  PackageManager pm = ctx.getPackageManager();
		  boolean app_installed = false;
		  try { 
		      pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
		      app_installed = true;
		  } 
		  catch (PackageManager.NameNotFoundException e) {
		      app_installed = false;
		  } 
		  return app_installed ;
	} 
}
