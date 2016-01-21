package com.mssdevlab.baselib;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Manages common menu items
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class CommonMenuHelper {
    private static final String LOG_TAG = "CommonMenuHelper";
    private static int companyURL;

    public static void setUpCommonMenu(@StringRes int companyUrl){
        companyURL = companyUrl;
    }

    /*
    *Add company menu item to the menu
    */
    public static void addCompanyMenu(@NonNull Menu menu, boolean darkTheme){
        Log.v(LOG_TAG, "addApplicationsMenu");
        MenuItem companyItem = menu.findItem(R.id.menu_action_play_app);
        if (companyItem == null) {
            MenuItem itemSettings = menu.add(10, R.id.menu_action_play_app, 100, R.string.common_menu_play_apps);
            if (darkTheme)
                itemSettings.setIcon(R.drawable.ic_action_our_apps_dark);
            else
                itemSettings.setIcon(R.drawable.ic_action_our_apps_light);

            MenuItemCompat.setShowAsAction(itemSettings, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    /*
    * Check and action on common menu item selected
    */
    public static boolean onOptionsItemSelected(@NonNull MenuItem item, @NonNull final Context context) {
        Log.v(LOG_TAG, "onOptionsItemSelected");
        if (item.getItemId() == R.id.menu_action_play_app) {
            try {
                Log.v(LOG_TAG, "menu_action_play_app");
                Uri uri = Uri.parse(context.getString(companyURL));
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(goToMarket);

            } catch (Exception ex) {
                Log.e(LOG_TAG, "menu_action_play_app fails: " + ex.getMessage());
            }
            return true;
        }
        return false;
    }

}
