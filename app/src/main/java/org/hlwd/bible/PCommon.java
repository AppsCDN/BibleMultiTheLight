
package org.hlwd.bible;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Project Common Class
 *
 * Special characters: { } [ ] ||
 */
final class PCommon implements IProject
{
    //<editor-fold defaultstate="collapsed" desc="-- Variables --">

    //The following variable should be false before putting on the Market and Debuggable=False in manifest

    final static boolean _isDebugVersion = false;

    final static LayoutParams _layoutParamsWrap = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    final static LayoutParams _layoutParamsMatchAndWrap = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    final static LayoutParams _layoutParamsMatch = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    @SuppressLint("StaticFieldLeak")
    private static SCommon _s = null;

    private static boolean isShowMyArt = false;

    enum ARTICLE_ACTION
    {
        CREATE_ARTICLE,
        RENAME_ARTICLE,
        DELETE_ARTICLE
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Initializer --">

    /***
     * Initializer
     */
    private PCommon()
    {
    }

    //</editor-fold>

    /***
     * Check local instance
     * @param context
     */
    @SuppressWarnings("JavaDoc")
    private static void CheckLocalInstance(final Context context)
    {
        try
        {
            if (_s == null) _s = SCommon.GetInstance(context);
        }
        catch(Exception ex)
        {
            LogR(context, ex);
        }
    }

    /**
     * Convert stack trace to string
     * @param stackTrace    Stack trace
     * @return string
     */
    private static String StackTraceToString(final StackTraceElement[] stackTrace)
    {
        final StringWriter sw = new StringWriter();
        PrintStackTrace(stackTrace, new PrintWriter(sw));

        return sw.toString();
    }

    private static void PrintStackTrace(final StackTraceElement[] stackTrace, final PrintWriter pw)
    {
        for(StackTraceElement stackTraceElement : stackTrace)
        {
            pw.println(stackTraceElement);
        }
    }

    /**
     * Concatenate objects (generic) with StringBuilder
     * @param args  Arguments
     * @return string
     */
    static String ConcaT(final Object... args)
    {
        final StringBuilder sb = new StringBuilder();

        for (final Object obj : args)
        {
            if (obj != null) sb.append(obj.toString());
        }

        return sb.toString();
    }

    /***
     * Add quotes at start & Stop of string
     * @param value Field value
     * @return Quotated string
     */
    static String AQ(final String value)
    {
        return PCommon.ConcaT("'", value, "'");
    }

    /***
     * Replace quotes (') by double quotes in fields
     * @param value     Field value
     * @return Field value ready to be concatenated in sql query
     */
    static String RQ(final String value)
    {
        if (!value.contains("'"))
            return value;

        return value.replaceAll("'", "''");
    }

    /**
     * Get current date YYYYMMDD (E.G.: 20160818)
     * @return YYYYMMDD
     */
    static String NowYYYYMMDD()
    {
        return DateFormat.format("yyyyMMdd", new Date()).toString();
    }

    /**
     * Get current time (E.G.: 14:34:20)
     * @return NowFunc
     */
    private static String TimeFunc()
    {
        return DateFormat.format("kk:mm:ss", new Date()).toString();
    }

    /**
     * Get current time (E.G.: 143420)
     * @return NowFunc
     */
    static String TimeFuncShort()
    {
        return DateFormat.format("kkmmss", new Date()).toString();
    }

    /**
     * Save key in SharedPreferences
     * @param context
     * @param key
     * @param value
     */
    @SuppressWarnings("JavaDoc")
    static void SavePref(final Context context, final APP_PREF_KEY key, final String value)
    {
        //SharedPreferences appPrefs = context.getSharedPreferences("task1", MODE_PRIVATE);
        final SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        final SharedPreferences.Editor editor = appPrefs.edit();
        editor.putString(key.toString(), value);
        editor.apply();
    }

    /**
     * Save key in SharedPreferences
     * @param context
     * @param key   integer logs as String
     * @param value
     */
    @SuppressWarnings("JavaDoc")
    static void SavePrefInt(final Context context, final APP_PREF_KEY key, final int value)
    {
        SavePref(context, key, ConcaT(value));

        //LogD(context, key);
    }

    /**
     * Get key from SharedPreferences (defaultValue is "")
     * @param context
     * @param key
     * @return
     */
    @SuppressWarnings("JavaDoc")
    static String GetPref(final Context context, final APP_PREF_KEY key)
    {
        return GetPref(context, key, "");
    }

    /**
     * Get key from SharedPreferences
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    @SuppressWarnings("JavaDoc")
    static String GetPref(final Context context, final APP_PREF_KEY key, final String defaultValue)
    {
        final SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return appPrefs.getString(key.toString(), defaultValue);
    }

    /***
     * Get BIBLE_NAME
     * @param context
     */
    @SuppressWarnings("JavaDoc")
    static String GetPrefBibleName(final Context context)
    {
        String bbName = PCommon.GetPref(context, APP_PREF_KEY.BIBLE_NAME);
        if (bbName == null || bbName.equals("")) bbName = "k";

        return bbName;
    }

    /***
     * Get TRAD_BIBLE_NAME
     * @param context
     * @param canReturnDefaultValue  False => its real value (can be empty), True => if empty: fill with a default value
     */
    @SuppressWarnings("JavaDoc")
    static String GetPrefTradBibleName(final Context context, final boolean canReturnDefaultValue)
    {
        String trad = PCommon.GetPref(context, IProject.APP_PREF_KEY.TRAD_BIBLE_NAME);
        if (trad == null || trad.equals(""))
        {
            if (canReturnDefaultValue)
            {
                trad = GetPrefBibleName(context);
            }
            else
            {
                trad = "";
            }
        }

        return trad;
    }

    /***
     * Get theme ID
     * @param context
     * @return theme ID
     */
    @SuppressWarnings("JavaDoc")
    static int GetPrefThemeId(final Context context)
    {
        final String THEME_NAME = PCommon.GetPrefThemeName(context);
        final int themeId;

        switch (THEME_NAME)
        {
            case "LIGHT":
                themeId = R.style.AppThemeLight;
                break;

            case "LIGHT_AND_BLUE":
                themeId = R.style.AppThemeDev;
                break;

            case "DARK":
                themeId = R.style.AppThemeDark;
                break;

            case "KAKI":
                themeId = R.style.AppThemeLight;
                break;

            default:
                themeId = R.style.AppThemeLight;
                break;
        }

        return themeId;
    }

    /**
     * Manage TRAD_BIBLE_NAME (set)
     * @param context
     * @param operation When = 0 then return lang stack, when > 0 then add else remove
     * @param bbName    bbName
     * @return String with selected language names
     */
    @SuppressWarnings("JavaDoc")
    private static String ManageTradBibleName(final Context context, final int operation, final String bbName)
    {
        boolean valueChanged = false;
        String trad = PCommon.GetPrefTradBibleName(context, false);
        if (operation > 0)
        {
            //Add
            if (!trad.contains(bbName))
            {
                trad = PCommon.ConcaT(trad, bbName);
                valueChanged = true;
            }
        }
        else if (operation < 0)
        {
            //Remove
            if (trad.contains(bbName))
            {
                trad = trad.replace(bbName, "");
                valueChanged = true;
            }
        }
        //Save TRAD
        if (valueChanged) PCommon.SavePref(context, APP_PREF_KEY.TRAD_BIBLE_NAME, trad);

        //Returns lang stack
        final int size = trad.length();
        StringBuilder sb = new StringBuilder();
        String bb;
        for (int i = 0; i < size; i++)
        {
            bb = trad.substring(i, i + 1);
            if (bb.compareToIgnoreCase("k") == 0)
            {
                sb.append(context.getString(R.string.languageEnShort));
                sb.append(" ");
            }
            else if (bb.compareToIgnoreCase("v") == 0)
            {
                sb.append(context.getString(R.string.languageEsShort));
                sb.append(" ");
            }
            else if (bb.compareToIgnoreCase("l") == 0)
            {
                sb.append(context.getString(R.string.languageFrShort));
                sb.append(" ");
            }
            else if (bb.compareToIgnoreCase("d") == 0)
            {
                sb.append(context.getString(R.string.languageItShort));
                sb.append(" ");
            }
            else if (bb.compareToIgnoreCase("a") == 0)
            {
                sb.append(context.getString(R.string.languagePtShort));
                sb.append(" ");
            }
        }

        return sb.toString().trim().replaceAll(" ", ", ");
    }

    /**
     * Get THEME_NAME
     * @param context
     * @return LIGHT as default
     */
    @SuppressWarnings("JavaDoc")
    private static String GetPrefThemeName(final Context context)
    {
        String themeName = "DARK";

        try
        {
            themeName = PCommon.GetPref(context, APP_PREF_KEY.THEME_NAME, "LIGHT");

            switch (themeName)
            {
                case "LIGHT":
                    break;

                case "LIGHT_AND_BLUE":
                    break;

                case "DARK":
                    break;

                case "KAKI":
                    themeName = "LIGHT";
                    break;

                default:
                    themeName = "LIGHT";
                    break;
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return themeName;
    }

    /**
     * Set THEME_NAME
     * @param context   Context
     * @param themeName Theme
     */
    static void SetThemeName(final Context context, String themeName)
    {
        try
        {
            switch (themeName)
            {
                case "LIGHT":
                    break;

                case "LIGHT_AND_BLUE":
                    break;

                case "DARK":
                    break;

                case "KAKI":
                    themeName = "LIGHT";
                    break;

                default:
                    themeName = "LIGHT";
                    break;
            }

            PCommon.SavePref(context, APP_PREF_KEY.THEME_NAME, themeName);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /**
     * Log (Release mode)
     * @param msg   Message
     */
    private static void LogR(final Context context, final String msg)
    {
        final String newMsg = ConcaT(TimeFunc(), "  ", msg);

        //AddLog implementation
        CheckLocalInstance(context);
        _s.AddLog(newMsg);
    }

    /**
     * Log (Release mode)
     * @param resId RessourceId
     * @param args  Arguments (for String.format)
     */
    private static void LogR(final Context context, @SuppressWarnings("SameParameterValue") final int resId, final Object... args) {
        LogR(context, String.format(context.getResources().getText(resId).toString(), args));
    }

    /**
     * Log (Release mode) stack trace
     * @param context   Context
     * @param ex    Exception
     */
    static void LogR(final Context context, final Exception ex)
    {
        LogR(context, R.string.logErr, StackTraceToString(ex.getStackTrace()));
    }

    /*
    protected static void LogR(final Context context, final Exception ex, String addMsg)
    {
        String msg = null;
        String logs = null;
        String logsCut = null;

        try
        {
            //was LogR(context, R.string.logErr, StackTraceToString(ex.getStackTrace()));

            final String now = NowFunc();

            if (!_isDebugVersion)
            {
                //Release
                msg = ex.getMessage();
                logs = ConcaT(GetPref(context, APP_PREF_KEY.LOG_STATUS), "\n", now, "  ", msg);
            }
            else
            {
                //Debug
                if (addMsg == null || addMsg.length() == 0)
                {
                    addMsg = "?";
                }

                msg = StackTraceToString(ex.getStackTrace());
                logs = ConcaT(GetPref(context, APP_PREF_KEY.LOG_STATUS), "\n", now, "  ", addMsg, " <\n", msg);
            }

            logsCut = LogCut(logs);
            SavePref(context, APP_PREF_KEY.LOG_STATUS, logsCut);
        }
        catch(Exception ex2)
        {
            //Should do nothing
        }
        finally
        {
            msg = null;
            logs = null;
            logsCut = null;
        }
    }
*/

    /*
    protected static String LogCut(final String logs)
    {
        //TODO: external maxSize and put above
        final int size = logs.length();
        final int maxSize = 10000;

        if (size >= maxSize) {
            //Cut
            //TODO: begin after first /n, #### seems ok for end of string but check it.
            return logs.substring(size - maxSize);
        }

        return logs;
    }
    */

    /***
     * Get resource threadId
     * @param context   Context
     * @param resName   Resource name
     * @return Resource Id (-1 by default)
     */
    static int GetResId(final Context context, final String resName)
    {
        int id = -1;

        try
        {
            if (resName == null || resName.length() == 0)
            {
                return id;
            }

            id = org.hlwd.bible.R.string.class.getField( resName ).getInt(null);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return id;
    }

    /***
     * Get drawable ciId
     * @param context   Context
     * @param resName   Resource name
     * @return Resource Id (-1 by default)
     */
    static int GetDrawableId(final Context context, final String resName)
    {
        int id = -1;

        try
        {
            if (resName == null || resName.length() == 0)
            {
                return id;
            }

            id = org.hlwd.bible.R.drawable.class.getField( resName ).getInt(null);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return id;
    }

    /***
     * Get drawable
     * @param context   Context
     * @param id    drawable Id
     * @return drawable
     */
    static Drawable GetDrawable(final Context context, @SuppressWarnings("SameParameterValue") final int id)
    {
        try
        {
            final int version = Build.VERSION.SDK_INT;
            return (version >= 22) ? ContextCompat.getDrawable(context, id) : context.getResources().getDrawable(id);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return null;
    }

    /***
     * Get thread type running
     * @param context   Context
     * @param findThreadType    Thread to find (0=ANY, 1=LISTEN)
     * @return Get thread type running (0=DEFAUT, 1=INSTALL, 2=LISTEN)
     */
    private static int GetThreadTypeRunning(final Context context, final int findThreadType)
    {
        int threadType = 0;

        try
        {
            final String threadName = context.getString(R.string.threadNfoPrefix);
            final String threadNameInstall = context.getString(R.string.threadNfoInstall);
            final String threadNameListen = context.getString(R.string.threadNfoListen);

            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            final Thread[] threadArr = threadSet.toArray(new Thread[0]);
            for (Thread thread : threadArr)
            {
                //TODO: ThreadGroup! => list group to find it?
                if (thread.getName().startsWith(threadName))
                {
                    if (findThreadType == 0)
                    {
                        if (thread.getName().contains(threadNameInstall))
                        {
                            threadType = 1;
                            break;
                        }
                        else if (thread.getName().contains(threadNameListen))
                        {
                            threadType = 2;
                            break;
                        }
                    }
                    else
                    {
                        if (thread.getName().contains(threadNameListen))
                        {
                            threadType = 2;
                            break;
                        }
                    }
                }
            }

            threadSet.clear();
            //noinspection UnusedAssignment
            threadSet = null;
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return threadType;
    }

    /***
     * Try to quit application
     * @param context   Context
     */
    static void TryQuitApplication(final Context context)
    {
        try
        {
            final int threadType = PCommon.GetThreadTypeRunning(context, 0);
            if (threadType > 0)
            {
                ShowToast(context, threadType == 1 ? R.string.installQuit : R.string.toastListenQuit, Toast.LENGTH_SHORT);
                return;
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        QuitApplication();
    }

    /***
     * Quit application
     */
    private static void QuitApplication()
    {
        //noinspection EmptyCatchBlock
        try
        {
/*
            PCommon.SavePrefInt(context, APP_PREF_KEY.EDIT_STATUS, 0);
            Thread.sleep(300);

            //if (_s == null) CheckLocalInstance(context);
            //_s.ShrinkDb(context);
            //if (PCommon._isDebugVersion) System.out.print("Shrunk");
*/
            _s.CloseDb();
        }
        catch (Exception ex) { }
        finally
        {
            _s = null;
        }


        /* This code has been removed since version 3.7
        //noinspection EmptyCatchBlock
        try
        {
            PCommon.SetSound(context, false);
        }
        catch (Exception ex) { }
        */


        //noinspection EmptyCatchBlock
        try
        {
            final int appId = android.os.Process.myPid();
            android.os.Process.killProcess(appId);
        }
        catch (Exception ex) { }
    }

/* NOT USED
     * Show notification
     * @param context       Context
     * @param title         Usually: appName
     * @param message       Message. Should be a (custom) message from resource file
     * @param drawable      Drawable Id
     **
    private static void ShowNotification(final Context context, final String title, final String message, @SuppressWarnings("SameParameterValue") final int drawable)
    {
        @SuppressWarnings("UnusedAssignment") NotificationManager nm = null;
        @SuppressWarnings("UnusedAssignment") NotificationCompat.Builder notification = null;
        @SuppressWarnings("UnusedAssignment") Intent intent = null;
        @SuppressWarnings("UnusedAssignment") PendingIntent pIntent = null;

        try
        {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            intent = new Intent(context, MainActivity.class);
            pIntent = PendingIntent.getActivity(context, 0, intent, 0);

            notification = new NotificationCompat.Builder(context)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(title)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(drawable);

            nm.notify(0, notification.build());
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) LogR(context, ex);
        }
        finally
        {
            //Cleaning
            //noinspection UnusedAssignment
            notification = null;
            //noinspection UnusedAssignment
            intent = null;
            //noinspection UnusedAssignment
            pIntent = null;
            //noinspection UnusedAssignment
            nm = null;
        }
    }
*/

    /***
     * Show Toast
     * @param context   Context
     * @param message   Message
     * @param duration  Duration (ex: Toast.LENGTH_SHORT...)
     */
    static void ShowToast(final Context context, final int message, final int duration)
    {
        try
        {
            final Toast toast = Toast.makeText(context, message, duration);
            toast.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Show Toast
     * @param context   Context
     * @param message   Message
     * @param duration  Duration (ex: Toast.LENGTH_SHORT...)
     */
    static void ShowToast(final Context context, final String message, @SuppressWarnings("SameParameterValue") final int duration)
    {
        try
        {
            final Toast toast = Toast.makeText(context, message, duration);
            toast.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Copy text to clipboard
     * @param context   Context
     * @param label     Label
     * @param text      Text to copy
     */
    static void CopyTextToClipboard(final Context context, @SuppressWarnings("SameParameterValue") final String label, final String text)
    {
        try
        {
            final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(label, text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Send email
     * @param context   Context
     * @param toList    Array of email address
     * @param subject   Email subject
     * @param body      Email body
     */
    static void SendEmail(final Context context, final String[] toList, final String subject, @SuppressWarnings("SameParameterValue") final String body)
    {
        try
        {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL  , toList);
            intent.putExtra(Intent.EXTRA_SUBJECT, (subject == null) ? "" : subject);
            intent.putExtra(Intent.EXTRA_TEXT   , (body == null) ? "" : body);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.emailChooser)));
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Share text
     * @param context   Context
     * @param text      Text to share
     */
    static void ShareText(final Context context, final String text)
    {
        try
        {
            final String mimeTypeTextPlain = "text/plain";
            final Intent intentQuery = new Intent(Intent.ACTION_SEND);
            intentQuery.setType(mimeTypeTextPlain);
            intentQuery.putExtra(Intent.EXTRA_TEXT, text);

            String packageName;
            Intent intent;
            final List<Intent> lstShareIntent = new ArrayList<>();
            final List<ResolveInfo> resInfos = context.getPackageManager().queryIntentActivities(intentQuery, 0);
            if (!resInfos.isEmpty())
            {
                for (ResolveInfo resInfo : resInfos)
                {
                    packageName = resInfo.activityInfo.packageName;
                    //TODO: UPDATE PACKAGE NAME
                    if (!packageName.toLowerCase().startsWith("org.hlwd.bible"))
                    {
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                        intent.setType(mimeTypeTextPlain);
                        intent.putExtra(Intent.EXTRA_TEXT, text);
                        //intent.setPackage(packageName);

                        lstShareIntent.add(intent);
                    }
                }
                context.startActivity(Intent.createChooser(lstShareIntent.get(0), context.getResources().getString(R.string.mnuShare)));
            }
            else
            {
                PCommon.ShowToast(context, R.string.toastNoAppsToShare, Toast.LENGTH_LONG);
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Open url
     * @param context   Context
     * @param url       Url
     */
    static void OpenUrl(final Context context, @SuppressWarnings("SameParameterValue") final String url)
    {
        try
        {
            final Uri webpage = Uri.parse(url);
            final Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(context.getPackageManager()) == null)
            {
                PCommon.ShowToast(context, R.string.toastNoBrowser, Toast.LENGTH_SHORT);
                return;
            }
            context.startActivity(intent);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Select bible language
     * Response in BIBLE_NAME_DIALOG
     * @param context
     * @param msg
     * @param desc
     * @param isCancelable
     * @param forceShowAllButtons  Force to show all buttons
     * @return builder
     */
    @SuppressWarnings("JavaDoc")
    static void SelectBibleLanguage(final AlertDialog builder, final Context context, final View view, final String msg, @SuppressWarnings("UnusedParameters") final String desc, @SuppressWarnings("SameParameterValue") final boolean isCancelable, @SuppressWarnings("SameParameterValue") final boolean forceShowAllButtons)
    {
        try
        {
            builder.setCancelable(isCancelable);
            if (isCancelable) {
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "");
                    }
                });
            }
            builder.setTitle(msg);
            builder.setView(view);

            final int colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
            final String bbName = PCommon.GetPref(context, APP_PREF_KEY.BIBLE_NAME, "");
            final int installStatus = (forceShowAllButtons) ? 5 : Integer.parseInt(PCommon.GetPref(context, APP_PREF_KEY.INSTALL_STATUS, "1"));

            final Button btnLanguageEN = view.findViewById(R.id.btnLanguageEN);
            if (installStatus < 1) btnLanguageEN.setVisibility(View.INVISIBLE);
            if (bbName.compareToIgnoreCase("k") == 0) btnLanguageEN.setTextColor(colorAccent);
            btnLanguageEN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "k");
                    builder.dismiss();
                }
            });
            final Button btnLanguageES = view.findViewById(R.id.btnLanguageES);
            if (installStatus < 2) btnLanguageES.setVisibility(View.INVISIBLE);
            if (bbName.compareToIgnoreCase("v") == 0) btnLanguageES.setTextColor(colorAccent);
            btnLanguageES.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "v");
                    builder.dismiss();
                }
            });
            final Button btnLanguageFR = view.findViewById(R.id.btnLanguageFR);
            if (installStatus < 3) btnLanguageFR.setVisibility(View.INVISIBLE);
            if (bbName.compareToIgnoreCase("l") == 0) btnLanguageFR.setTextColor(colorAccent);
            btnLanguageFR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "l");
                    builder.dismiss();
                }
            });
            final Button btnLanguageIT = view.findViewById(R.id.btnLanguageIT);
            if (installStatus < 4) btnLanguageIT.setVisibility(View.INVISIBLE);
            if (bbName.compareToIgnoreCase("d") == 0) btnLanguageIT.setTextColor(colorAccent);
            btnLanguageIT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "d");
                    builder.dismiss();
                }
            });
            final Button btnLanguagePT = view.findViewById(R.id.btnLanguagePT);
            if (installStatus < 5) btnLanguagePT.setVisibility(View.INVISIBLE);
            if (bbName.compareToIgnoreCase("a") == 0) btnLanguagePT.setTextColor(colorAccent);
            btnLanguagePT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "a");
                    builder.dismiss();
                }
            });
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Select multiple bible language
     * Response in TRAD_BIBLE_NAME
     * @param context
     * @param msg
     * @param desc
     * @param isCancelable
     * @param forceShowAllButtons  Force to show all buttons
     */
    @SuppressWarnings("JavaDoc")
    static void SelectBibleLanguageMulti(final AlertDialog builder, final Context context, final View view, final String msg, @SuppressWarnings({"UnusedParameters", "SameParameterValue"}) final String desc, @SuppressWarnings("SameParameterValue") final boolean isCancelable, @SuppressWarnings("SameParameterValue") final boolean forceShowAllButtons)
    {
        try
        {
            builder.setCancelable(isCancelable);
            if (isCancelable) {
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "");
                    }
                });
            }
            builder.setTitle(msg);
            builder.setView(view);

            final int colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
            final String bbName = PCommon.GetPrefBibleName(context);
            final int installStatus = (forceShowAllButtons) ? 5 : Integer.parseInt(PCommon.GetPref(context, APP_PREF_KEY.INSTALL_STATUS, "1"));

            final TextView tvTrad = view.findViewById(R.id.tvTrad);
            final ToggleButton btnLanguageEN = view.findViewById(R.id.btnLanguageEN);
            if (installStatus < 1) btnLanguageEN.setEnabled(false);
            if (bbName.compareToIgnoreCase("k") == 0) btnLanguageEN.setTextColor(colorAccent);
            btnLanguageEN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    final int op = compoundButton.isChecked() ? 1 : -1;
                    final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, op, "k"));
                    tvTrad.setText(languageStack);
                }
            });
            final ToggleButton btnLanguageES = view.findViewById(R.id.btnLanguageES);
            if (installStatus < 2) btnLanguageES.setEnabled(false);
            if (bbName.compareToIgnoreCase("v") == 0) btnLanguageES.setTextColor(colorAccent);
            btnLanguageES.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    final int op = compoundButton.isChecked() ? 1 : -1;
                    final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, op, "v"));
                    tvTrad.setText(languageStack);
                }
            });
            final ToggleButton btnLanguageFR = view.findViewById(R.id.btnLanguageFR);
            if (installStatus < 3) btnLanguageFR.setEnabled(false);
            if (bbName.compareToIgnoreCase("l") == 0) btnLanguageFR.setTextColor(colorAccent);
            btnLanguageFR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    final int op = compoundButton.isChecked() ? 1 : -1;
                    final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, op, "l"));
                    tvTrad.setText(languageStack);
                }
            });
            final ToggleButton btnLanguageIT = view.findViewById(R.id.btnLanguageIT);
            if (installStatus < 4) btnLanguageIT.setEnabled(false);
            if (bbName.compareToIgnoreCase("d") == 0) btnLanguageIT.setTextColor(colorAccent);
            btnLanguageIT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    final int op = compoundButton.isChecked() ? 1 : -1;
                    final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, op, "d"));
                    tvTrad.setText(languageStack);
                }
            });
            final ToggleButton btnLanguagePT = view.findViewById(R.id.btnLanguagePT);
            if (installStatus < 5) btnLanguagePT.setEnabled(false);
            if (bbName.compareToIgnoreCase("a") == 0) btnLanguagePT.setTextColor(colorAccent);
            btnLanguagePT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    final int op = compoundButton.isChecked() ? 1 : -1;
                    final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, op, "a"));
                    tvTrad.setText(languageStack);
                }
            });
            final Button btnLanguageClear = view.findViewById(R.id.btnLanguageClear);
            if (installStatus <= 0) btnLanguageClear.setEnabled(false);
            btnLanguageClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //clear all & select default
                    final String currentTrad = "";
                    PCommon.SavePref(context, APP_PREF_KEY.TRAD_BIBLE_NAME, currentTrad);
                    btnLanguageEN.setChecked(false);
                    btnLanguageES.setChecked(false);
                    btnLanguageFR.setChecked(false);
                    btnLanguageIT.setChecked(false);
                    btnLanguagePT.setChecked(false);
                    final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, 0, ""));
                    tvTrad.setText(languageStack);
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "");
                }
            });
            final Button btnLanguageContinue = view.findViewById(R.id.btnSearchContinue);
            if (installStatus <= 0) btnLanguageContinue.setEnabled(false);
            btnLanguageContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //all selected toggle
                    final String currentTrad = PCommon.GetPrefTradBibleName(context, false);
                    if (currentTrad.equals("")) return;
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, currentTrad.substring(0, 1));
                    builder.dismiss();
                }
            });

            final String tradInit = PCommon.GetPrefTradBibleName(context, false);
            if (tradInit.contains("k")) btnLanguageEN.setChecked(true);
            if (tradInit.contains("v")) btnLanguageES.setChecked(true);
            if (tradInit.contains("l")) btnLanguageFR.setChecked(true);
            if (tradInit.contains("d")) btnLanguageIT.setChecked(true);
            if (tradInit.contains("a")) btnLanguagePT.setChecked(true);
            final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, 0, ""));
            tvTrad.setText(languageStack);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Select item
     * Response in BOOK_CHAPTER_DIALOG
     * @param context
     * @param title
     * @param fieldTitleId
     * @param desc
     * @param isCancelable
     * @param itemMax
     * @param shouldAddAllitem  Should add ALL item?
     */
    @SuppressWarnings("JavaDoc")
    static void SelectItem(final AlertDialog builder, final Context context, final View view, final String title, @SuppressWarnings("SameParameterValue") final int fieldTitleId, @SuppressWarnings({"UnusedParameters", "SameParameterValue"}) final String desc, @SuppressWarnings("SameParameterValue") final boolean isCancelable, final int itemMax, final boolean shouldAddAllitem)
    {
        try
        {
            final Typeface typeface = PCommon.GetTypeface(context);
            final int fontSize = PCommon.GetFontSize(context);

            final TextView tvFieldTitle = view.findViewById(R.id.tvTitle);
            tvFieldTitle.setText(fieldTitleId);

            builder.setCancelable(isCancelable);
            if (isCancelable) {
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "");
                    }
                });
            }
            builder.setTitle(title);
            builder.setView(view);

            final LinearLayout llItem = view.findViewById(R.id.llItem);
            llItem.setTag(0);
            final int itemMin = shouldAddAllitem ? 0 : 1;
            for (int i = itemMin; i <= itemMax; i++)
            {
                final TextView tvItem = new TextView(context);
                tvItem.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvItem.setPadding(10, 15, 10, 15);
                tvItem.setText( i != 0 ? String.valueOf(i) : context.getString(R.string.itemAll));
                tvItem.setTag( i );
                tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int cnumber = (int) v.getTag();
                        PCommon.SavePref(context, APP_PREF_KEY.BOOK_CHAPTER_DIALOG, String.valueOf(cnumber));
                        builder.dismiss();
                    }
                });
                tvItem.setFocusable(true);
                tvItem.setBackground(PCommon.GetDrawable(context, R.drawable.focus_text));

                //Font
                if (typeface != null) { tvItem.setTypeface(typeface); }
                tvItem.setTextSize(fontSize);

                llItem.addView(tvItem);
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Show articles
     * @param context   Context
     */
    static void ShowArticles(final Context context)
    {
        ShowArticles(context, isShowMyArt, false);
    }

    /***
     * Show articles
     * @param context   Context
     * @param isMyArticleType   Is MyArt type?
     * @param isForSelection    Is for selection? (False=to open it)
     */
    static void ShowArticles(final Context context, final boolean isMyArticleType, final boolean isForSelection)
    {
        try
        {
            CheckLocalInstance(context);

            isShowMyArt = isMyArticleType;

            final AlertDialog builder = new AlertDialog.Builder(context).create();                     //R.style.DialogStyleKaki
            final ScrollView sv = new ScrollView(context);
            sv.setLayoutParams(PCommon._layoutParamsMatchAndWrap);

            final LinearLayout llArt = new LinearLayout(context);
            llArt.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            llArt.setOrientation(LinearLayout.VERTICAL);
            llArt.setPadding(0, 15, 0, 15);

            final Typeface typeface = PCommon.GetTypeface(context);
            final int fontSize = PCommon.GetFontSize(context);

            if (!isForSelection)
            {
                final Button btnSwitchArt = new Button(context);
                btnSwitchArt.setLayoutParams(PCommon._layoutParamsWrap);
                btnSwitchArt.setText(isShowMyArt ? R.string.switchToArt : R.string.switchToMyArt);
                btnSwitchArt.setOnClickListener(new View.OnClickListener() {
                    public void onClick(final View vw)
                    {
                        isShowMyArt = !isShowMyArt;

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                builder.dismiss();
                                ShowArticles(vw.getContext());
                            }
                        }, 0);
                    }
                });
                btnSwitchArt.setFocusable(true);
                btnSwitchArt.setBackground(PCommon.GetDrawable(context, R.drawable.focus_button));
                llArt.addView(btnSwitchArt);
            }

            final Button btnCreateArt = new Button(context);
            btnCreateArt.setLayoutParams(PCommon._layoutParamsWrap);
            btnCreateArt.setVisibility(isShowMyArt ? View.VISIBLE : View.GONE);
            btnCreateArt.setText(R.string.btnCreate);
            btnCreateArt.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View vw)
                {
                    EditArticleDialog(builder, R.string.btnCreate, -1, ARTICLE_ACTION.CREATE_ARTICLE, isForSelection);
                }
            });
            btnCreateArt.setFocusable(true);
            btnCreateArt.setBackground(PCommon.GetDrawable(context, R.drawable.focus_button));
            llArt.addView(btnCreateArt);

            int resId;
            int nr = 0;
            TextView tvArt;
            TextView tvArtStatus;
            String text;

            final String[] arrArt = (isShowMyArt) ? _s.GetListMyArticlesId() : context.getResources().getStringArray(R.array.ART_ARRAY);
            for (final String artRef : arrArt)
            {
                if (!isShowMyArt)
                {
                    if (nr == 2 || nr == 10)
                    {
                        TextView tvSep = new TextView(context);
                        tvSep.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                        tvSep.setText(R.string.mnuEmpty);
                        llArt.addView(tvSep);

                        final View vwSep = new View(context);
                        vwSep.setPadding(20, 0, 20, 0);
                        vwSep.setLayoutParams(new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
                        vwSep.setBackgroundColor(tvSep.getCurrentTextColor());
                        llArt.addView(vwSep);

                        tvSep = new TextView(context);
                        tvSep.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                        tvSep.setText(R.string.mnuEmpty);
                        llArt.addView(tvSep);
                    }

                    resId = PCommon.GetResId(context, artRef);
                    text = PCommon.ConcaT(context.getString(R.string.bulletDefault), " ", context.getString(resId));
                }
                else
                {
                    resId = Integer.parseInt(artRef);
                    text = PCommon.ConcaT(context.getString(R.string.bulletDefault), " ", _s.GetMyArticleName(resId));
                }

                tvArt = new TextView(context);
                tvArt.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvArt.setPadding(10, nr == 0 ? 30 : 15, 10, isShowMyArt ? 0 : 15);
                tvArt.setText(text);
                tvArt.setTag( artRef );
                tvArt.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View vw)
                    {
                        try
                        {
                            final String fullQuery = (String) vw.getTag();
                            if (PCommon._isDebugVersion) System.out.println(fullQuery);
                            if (isForSelection)
                            {
                                final int artId = Integer.parseInt(fullQuery.replace(vw.getContext().getString(R.string.tabMyArtPrefix), ""));
                                if (artId < 0) return;
                                PCommon.SavePrefInt(vw.getContext(), IProject.APP_PREF_KEY.EDIT_STATUS, 1);
                                PCommon.SavePrefInt(vw.getContext(), IProject.APP_PREF_KEY.EDIT_ART_ID, artId);
                                PCommon.SavePref(vw.getContext(), IProject.APP_PREF_KEY.EDIT_SELECTION, "");
                            }
                            else
                            {
                                if (isMyArticleType)
                                {
                                    PCommon.ShowMyArticleMenu(builder, fullQuery);
                                }
                                else
                                {
                                    ShowArticle(context, fullQuery);
                                }
                            }
                        }
                        catch (Exception ex)
                        {
                            if (PCommon._isDebugVersion) PCommon.LogR(vw.getContext(), ex);
                        }
                        finally
                        {
                            builder.dismiss();
                        }
                    }
                });
                tvArt.setFocusable(true);
                tvArt.setBackground(PCommon.GetDrawable(context, R.drawable.focus_text));

                //Font
                if (typeface != null) { tvArt.setTypeface(typeface); }
                tvArt.setTextSize(fontSize);
                llArt.addView(tvArt);

                if (isShowMyArt)
                {
                    text = PCommon.ConcaT("<blockquote>&nbsp;<small>(", context.getString(R.string.tabMyArtPrefix), artRef,")</small></blockquote>");
                    tvArtStatus = new TextView(context);
                    tvArtStatus.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                    tvArtStatus.setPadding(50, 0, 10, 0);
                    tvArtStatus.setText(Html.fromHtml(text));
                    llArt.addView(tvArtStatus);
                }

                nr++;
            }
            sv.addView(llArt);

            builder.setTitle(R.string.mnuArticles);
            builder.setCancelable(true);
            builder.setView(sv);
            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Show article
     * @param context   Context
     * @param artName   Article name
     */
    static void ShowArticle(final Context context, final String artName)
    {
        try
        {
            CheckLocalInstance(context);

            final int artNameTabId = _s.GetArticleTabId(artName);
            if (artNameTabId >= 0)
            {
                MainActivity.Tab.SelectTabByArtName(artName);

                return;
            }
            final String bbName = PCommon.GetPref(context, IProject.APP_PREF_KEY.BIBLE_NAME, "k");
            MainActivity.Tab.AddTab(context, "A", bbName, artName);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Show MYART menu
     * @param dlgMyArticles  Dialog
     * @param artName     Article Name
     */
    private static void ShowMyArticleMenu(final AlertDialog dlgMyArticles, final String artName)
    {
        final Context context = dlgMyArticles.getContext();

        try
        {
            final int artId = Integer.parseInt(artName.replace(context.getString(R.string.tabMyArtPrefix),""));
            if (artId < 0) return;

            CheckLocalInstance(context);

            //final Typeface typeface = PCommon.GetTypeface(context);
            //final int fontSize = PCommon.GetFontSize(context);

            final LayoutInflater inflater = dlgMyArticles.getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_myart_menu, (ViewGroup) dlgMyArticles.findViewById(R.id.svMyArtMenu));

            final String myartTitle = PCommon.ConcaT(context.getString(R.string.tabMyArtPrefix), artId);

            final AlertDialog builder = new AlertDialog.Builder(context).create();
            builder.setCancelable(true);
            builder.setTitle(myartTitle);
            builder.setView(view);

            final Button btnOpen = view.findViewById(R.id.btnOpen);
            btnOpen.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            builder.dismiss();
                            dlgMyArticles.dismiss();
                            ShowArticle(context, artName);
                        }
                    }, 0);
                }
            });
            final Button btnRename = view.findViewById(R.id.btnRename);
            btnRename.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            EditArticleDialog(builder, R.string.mnuRename, artId, ARTICLE_ACTION.RENAME_ARTICLE, false);
                            builder.dismiss();
                            dlgMyArticles.dismiss();
                        }
                    }, 0);
                }
            });
            final Button btnDelete = view.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            EditArticleDialog(builder, R.string.mnuDelete, artId, ARTICLE_ACTION.DELETE_ARTICLE, false);
                            builder.dismiss();
                            dlgMyArticles.dismiss();
                        }
                    }, 0);
                }
            });
            final Button btnCopySourceToClipboard = view.findViewById(R.id.btnCopySourceToClipboard);
            btnCopySourceToClipboard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            final String text = _s.GetMyArticleSource(artId);
                            PCommon.CopyTextToClipboard(context, "", text);
                            builder.dismiss();
                            dlgMyArticles.dismiss();
                        }
                    }, 0);
                }
            });
            final Button btnEmailSourceToDeveloper = view.findViewById(R.id.btnEmailSourceToDeveloper);
            btnEmailSourceToDeveloper.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            final String app =  PCommon.ConcaT("Bible Multi ", context.getString(R.string.appName));
                            final String devEmail = context.getString(R.string.devEmail).replaceAll("r", "");
                            final String text = _s.GetMyArticleSource(artId);
                            PCommon.SendEmail(context,
                                    new String[]{ devEmail },
                                    app,
                                    text);
                            builder.dismiss();
                            dlgMyArticles.dismiss();
                        }
                    }, 0);
                }
            });
            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Edit article dialog (globally for action)
     * @param dlg       Dialog of myarticles
     * @param titleId   Main title
     * @param artId     Article Id (-1 if not used)
     * @param action    Action
     * @param isForSelection    CREATE_ARTICLE can be called in 2 ways: during the selection of an article or to open an article
     */
    private static void EditArticleDialog(final AlertDialog dlg, final int titleId, final int artId, final ARTICLE_ACTION action, final boolean isForSelection)
    {
        final Context context = dlg.getContext();

        try
        {
            final LayoutInflater inflater = dlg.getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_edit_dialog, (ViewGroup) dlg.findViewById(R.id.svEdition));
            final TextView tvTitle = view.findViewById(R.id.tvTitle);
            final EditText etEdition = view.findViewById(R.id.etEdition);
            final AlertDialog builder = new AlertDialog.Builder(context).create();
            builder.setCancelable(true);
            builder.setTitle(titleId);
            builder.setView(view);

            tvTitle.setText(titleId);

            //EditText
            final String artName = artId > 0 ? _s.GetMyArticleName(artId) : "";
            etEdition.setText(artName);
            if (action == ARTICLE_ACTION.CREATE_ARTICLE || action == ARTICLE_ACTION.RENAME_ARTICLE) etEdition.setSingleLine(true);
            if (action == ARTICLE_ACTION.DELETE_ARTICLE) etEdition.setEnabled(false);

            //BtnClear
            final Button btnClear = view.findViewById(R.id.btnEditionClear);
            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etEdition.setText("");
                }
            });
            if (action == ARTICLE_ACTION.DELETE_ARTICLE) btnClear.setVisibility(View.GONE);

            //BtnContinue
            final Button btnContinue = view.findViewById(R.id.btnEditionContinue);
            btnContinue.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View view)
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            CheckLocalInstance(context);

                            final String title = etEdition.getText().toString().replaceAll("\n", "").trim();
                            switch (action)
                            {
                                case RENAME_ARTICLE:
                                {
                                    if (title.length() == 0) return;

                                    _s.UpdateMyArticleTitle(artId, title);
                                    break;
                                }
                                case DELETE_ARTICLE:
                                {
                                    _s.DeleteMyArticle(artId);

                                    final int currentEditStatus = PCommon.GetEditStatus(context);
                                    if (currentEditStatus == 0) break;
                                    final int currentEditArtId = PCommon.GetEditArticleId(context);
                                    if (currentEditArtId == artId)
                                    {
                                        //Stop editing
                                        PCommon.SavePrefInt(context, APP_PREF_KEY.EDIT_STATUS, 0);
                                        PCommon.SavePrefInt(context, APP_PREF_KEY.EDIT_ART_ID, -1);
                                    }
                                    break;
                                }
                                case CREATE_ARTICLE:
                                {
                                    if (title.length() == 0) return;

                                    final ArtDescBO ad = new ArtDescBO();
                                    ad.artId = _s.GetNewMyArticleId();
                                    ad.artUpdatedDt = PCommon.NowYYYYMMDD();
                                    ad.artTitle = title;
                                    ad.artSrc = "...";

                                    _s.AddMyArticle(ad);
                                    break;
                                }
                            }

                            builder.dismiss();
                            dlg.dismiss();
                            ShowArticles(context, true, isForSelection);
                        }
                    }, 0);
                }
            });

            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Show simple dialog
     * @param activity
     * @param titleId
     * @param msgIds
     */
    @SuppressWarnings("JavaDoc")
    static void ShowDialog(final Activity activity, final int titleId, final int... msgIds)
    {
        try
        {
            final Context context = activity.getApplicationContext();
            final Typeface typeface = PCommon.GetTypeface(context);
            final int fontSize = PCommon.GetFontSize(context);

            final LayoutInflater inflater = activity.getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_show_dialog, (ViewGroup) activity.findViewById(R.id.llDialog));
            final LinearLayout llMsg = view.findViewById(R.id.llMsg);
            final AlertDialog builder = new AlertDialog.Builder(activity).create();
            builder.setCancelable(false);
            builder.setTitle(titleId);
            builder.setView(view);

            for (int msgId : msgIds)
            {
                final TextView tvMsg = new TextView(context);
                tvMsg.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvMsg.setText(msgId);
                if (typeface != null) { tvMsg.setTypeface(typeface); }
                tvMsg.setTextSize(fontSize);
                tvMsg.setFocusable(true);
                tvMsg.setBackground(PCommon.GetDrawable(context, R.drawable.focus_text));
                tvMsg.setTextColor(Color.GRAY);    //Let this, was an issue on many Android versions
                llMsg.addView(tvMsg);
            }

            final Button btnClose = view.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder.dismiss();
                }
            });

            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(activity.getApplicationContext(), ex);
        }
    }

    /***
     * Get random int value in ranges [min, max]
     * @param context
     * @param minRange  Minimum
     * @param maxRange  Maximum
     * @return Random int
     */
    @SuppressWarnings("JavaDoc")
    static int GetRandomInt(final Context context, final int minRange, final int maxRange)
    {
        int rndValue = minRange;

        try
        {
            if (maxRange <= 0) throw new Exception("Range invalid!");

            final int range = maxRange - minRange + 1;
            final Random randomGenerator = new Random(System.currentTimeMillis());
            rndValue = randomGenerator.nextInt(range);  //from 0..n-1
            rndValue += minRange;
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return rndValue;
    }

    /***
     * Get typeface
     * @param context
     * @return null has default typeface, so don't set it. Roboto?
     */
    @SuppressWarnings("JavaDoc")
    static Typeface GetTypeface(final Context context)
    {
        try
        {
            final String tfName = PCommon.GetPref(context, APP_PREF_KEY.FONT_NAME, "");

            return (tfName == null || tfName.length() == 0)
                    ? Typeface.defaultFromStyle(Typeface.NORMAL)
                    : Typeface.createFromAsset(context.getAssets(), PCommon.ConcaT("fonts/", tfName, ".ttf"));
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return null;
    }

    /***
     * Get font size (verse)
     * @param context
     */
    @SuppressWarnings("JavaDoc")
    static int GetFontSize(final Context context)
    {
        try
        {
            return Integer.parseInt(PCommon.GetPref(context, APP_PREF_KEY.FONT_SIZE, "14"));
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return 14;
    }

    /***
     * Get install status
     * @param context
     * @return
     */
    @SuppressWarnings("JavaDoc")
    static int GetInstallStatus(final Context context)
    {
        return Integer.parseInt(PCommon.GetPref(context, IProject.APP_PREF_KEY.INSTALL_STATUS, "1"));
    }

    /***
     * Get edit status
     * @param context
     * @return
     */
    @SuppressWarnings("JavaDoc")
    static int GetEditStatus(final Context context)
    {
        return Integer.parseInt(PCommon.GetPref(context, APP_PREF_KEY.EDIT_STATUS, "0"));
    }

    /***
     * Get edit article id
     * @param context
     * @return < 0 if not used
     */
    @SuppressWarnings("JavaDoc")
    static int GetEditArticleId(final Context context)
    {
        return Integer.parseInt(PCommon.GetPref(context, APP_PREF_KEY.EDIT_ART_ID, "-1"));
    }

    /***
     * Save listen position
     */
    static void SetListenPosition(final Context context, final String bbName, final int bNumber, final int cNumber)
    {
        final String listenPosition = PCommon.ConcaT(bbName, ",", bNumber, ",", cNumber);

        PCommon.SavePref(context, IProject.APP_PREF_KEY.LISTEN_POSITION, listenPosition);
    }

    /***
     * Get listen position
     * @return array of string with bbname, bnumber, cnumber OR null
     */
    static String[] GetListenPosition(final Context context)
    {
        final String listenPosition = PCommon.GetPref(context, IProject.APP_PREF_KEY.LISTEN_POSITION, "");

        return listenPosition.isEmpty() ? null : listenPosition.split(",");
    }

    /***
     * Get listen status
     * @param context
     * @return (1=Active, 0=Inactive)
     */
    @SuppressWarnings("JavaDoc")
    static int GetListenStatus(final Context context)
    {
        return PCommon.GetThreadTypeRunning(context, 1) > 0 ? 1 : 0;
    }

    /***
     * Is UI Television?
     * @param context
     * @return true/false
     */
    @SuppressWarnings("JavaDoc")
    static boolean IsUiTelevision(final Context context)
    {
        boolean isUiTelevision = false;
        @SuppressWarnings("unused") final String logHeader = "org.hlwd.bible: ";

        try
        {
            //No check needed
            final String UI_LAYOUT = PCommon.GetPref(context, APP_PREF_KEY.UI_LAYOUT, "C");
            isUiTelevision = UI_LAYOUT.equalsIgnoreCase("T");
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) System.out.println( PCommon.ConcaT(logHeader, "IsUiTelevision (exception)=", ex ));
        }
        finally
        {
            //TODO FAB: bug of Scommon dbOpening => to review. Set isDebug=true to get errors when installing app on emulator.
            if (PCommon._isDebugVersion) System.out.println( PCommon.ConcaT(logHeader, "isUiTelevision=", isUiTelevision ));
        }

        return isUiTelevision;
    }

    /***
     * Detect if it's running on a television
     * @param context
     * @return true/false
     */
    @SuppressWarnings("JavaDoc")
    static boolean DetectIsUiTelevision(final Context context)
    {
        boolean isUiTelevision = false;
        final String logHeader = "org.hlwd.bible: ";

        try
        {
            try
            {
                final UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
                //noinspection ConstantConditions
                final boolean isUiModeTypeTelevision = (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);
                System.out.println( PCommon.ConcaT(logHeader, "isUiModeTypeTelevision=", isUiModeTypeTelevision ));

                if (isUiModeTypeTelevision)
                {
                    isUiTelevision = true;
                    PCommon.SavePref(context, APP_PREF_KEY.UI_LAYOUT, "T");
                    return true;
                }
            }
            catch(Exception ex)
            {
                System.out.println( PCommon.ConcaT(logHeader, "DetectIsUiTelevision (exception)=", ex ));
            }

            final PackageManager pm = context.getPackageManager();

            //TODO FAB: FUTURE => CHECK TO REMOVE (DEPRECATED)
            try
            {
                final boolean hasFeatureTelevision = pm.hasSystemFeature(PackageManager.FEATURE_TELEVISION);
                System.out.println( PCommon.ConcaT(logHeader, "hasFeatureTelevision=", hasFeatureTelevision ));
                if (hasFeatureTelevision)
                {
                    isUiTelevision = true;
                    PCommon.SavePref(context, APP_PREF_KEY.UI_LAYOUT, "T");
                    return true;
                }
            }
            catch (Exception ex)
            {
                System.out.println( PCommon.ConcaT(logHeader, "DetectIsUiTelevision (exception)=", ex ));
            }

            try
            {
                if (Build.VERSION.SDK_INT >= 21)
                {
                    final boolean hasFeatureLeanback = pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK);
                    System.out.println( PCommon.ConcaT(logHeader, "hasFeatureLeanback=", hasFeatureLeanback ));
                    if (hasFeatureLeanback)
                    {
                        isUiTelevision = true;
                        PCommon.SavePref(context, APP_PREF_KEY.UI_LAYOUT, "T");
                        return true;
                    }
                }
            }
            catch (Exception ex)
            {
                System.out.println( PCommon.ConcaT(logHeader, "DetectIsUiTelevision (exception)=", ex ));
            }

            //Default
            PCommon.SavePref(context, APP_PREF_KEY.UI_LAYOUT, "C");
        }
        catch (Exception ex)
        {
            //TODO FAB: add check isDebug=true... (see TODO in finally)
            System.out.println( PCommon.ConcaT(logHeader, "DetectIsUiTelevision (exception)=", ex ));
        }
        finally
        {
            //TODO FAB: add check isDebug=true, but there is a bug of Scommon dbOpening => to review. Set isDebug=true to get errors when installing app on emulator.
            System.out.println( PCommon.ConcaT(logHeader, "DetectIsUiTelevision=", isUiTelevision ));
        }

        return isUiTelevision;
    }

    /***
     * Set UI layout
     * @param context
     * @param classicLayoutId
     * @param tvLayoutId
     * @return classic or tv layout
     */
    @SuppressWarnings("JavaDoc")
    static int SetUILayout(final Context context, final int classicLayoutId, final int tvLayoutId)
    {
        final boolean isUiTelevision = PCommon.IsUiTelevision(context);

        return (isUiTelevision) ? tvLayoutId : classicLayoutId;
    }

    /*
     * Rem: This code has been removed since version 3.7
     * Enable/disable sound
     * @param context
     * @param isSoundOff
     * @SuppressWarnings("JavaDoc")
    static void SetSound(final Context context, final boolean isSoundOff)
    {
        try
        {
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            //noinspection ConstantConditions
            audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, isSoundOff);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }
    */

    /***
     * Set text appareance
     * @param tv
     * @param context
     * @param resId
     */
    @SuppressWarnings("JavaDoc")
    static void SetTextAppareance(final TextView tv, final Context context, @SuppressWarnings("SameParameterValue") final int resId)
    {
        try
        {
            final int version = Build.VERSION.SDK_INT;
            if (version < 23) {
                tv.setTextAppearance(context, resId);
            } else {
                tv.setTextAppearance(resId);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }
}
