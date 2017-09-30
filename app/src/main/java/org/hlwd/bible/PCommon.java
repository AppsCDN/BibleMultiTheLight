
package org.hlwd.bible;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.*;
import java.util.*;

/**
 * Project Common Class
 *
 * Special characters: { } [ ] ||
 */
final class PCommon implements IProject
{
    //<editor-fold defaultstate="collapsed" desc="-- Variables --">

    //The following variable should be false before putting on the Market and Debuggable=False in manifest

    final static boolean _isDebugVersion = true;

    final static LayoutParams _layoutParamsWrap = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    final static LayoutParams _layoutParamsMatchAndWrap = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    final static LayoutParams _layoutParamsMatch = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    @SuppressLint("StaticFieldLeak")
    private static SCommon _s = null;

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
        StringBuilder sb = new StringBuilder("");
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

        //LogR(context, ex, "");
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
            //TODO: what to do if not found?
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
            //TODO: what to do if not found?
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
     * Get count of threads running
     * @param context   Context
     * @return Count of threads running
     */
    private static int GetCountThreadRunning(final Context context)
    {
        int count = 0;

        try
        {
            final String threadName = context.getString(R.string.threadNfoPrefix);

            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            final Thread[] threadArr = threadSet.toArray(new Thread[threadSet.size()]);

            for (Thread thread : threadArr)
            {
                //TODO: ThreadGroup! => list group to find it?
                if (thread.getName().startsWith(threadName)) count++;
            }

            threadSet.clear();
            //noinspection UnusedAssignment
            threadSet = null;
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return count;
    }

    /***
     * Try to quit application
     * @param context   Context
     */
    static void TryQuitApplication(final Context context)
    {
        try
        {
            final int count = GetCountThreadRunning(context);
            if (count > 0)
            {
                ShowToast(context, R.string.installQuit, Toast.LENGTH_SHORT);
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
        try
        {
/*
            if (_s == null) CheckLocalInstance(context);

            _s.ShrinkDb(context);

            if (PCommon._isDebugVersion) System.out.print("Shrunk");
*/
            _s.CloseDb();
        }
        catch (Exception ex) { }
        finally
        {
            _s = null;
        }

        try
        {
            final int appId = android.os.Process.myPid();
            android.os.Process.killProcess(appId);
        }
        catch (Exception ex) { }
    }

    /**
     * Show notification
     * @param context       Context
     * @param title         Usually: appName
     * @param message       Message. Should be a (custom) message from resource file
     * @param drawable      Drawable Id
     */
    static void ShowNotification(final Context context, final String title, final String message, @SuppressWarnings("SameParameterValue") final int drawable)
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

    /***
     * Show Toast
     * @param context   Context
     * @param message   Message
     * @param duration  Duration (ex: Toast.LENGTH_SHORT...)
     */
    static void ShowToast(final Context context, final int message, final int duration)
    {
        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    /***
     * Copy text to clipboard
     * @param context   Context
     * @param label     Label
     * @param text      Text to copy
     */
    static void CopyTextToClipboard(final Context context, @SuppressWarnings("SameParameterValue") final String label, final String text)
    {
        final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
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
            Intent intent = new Intent(Intent.ACTION_SEND);
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
            final Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
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
            final int installStatus = (forceShowAllButtons) ? 4 : Integer.parseInt(PCommon.GetPref(context, APP_PREF_KEY.INSTALL_STATUS, "1"));

            final Button btnLanguageEN = (Button) view.findViewById(R.id.btnLanguageEN);
            if (installStatus < 1) btnLanguageEN.setVisibility(View.INVISIBLE);
            if (bbName.compareToIgnoreCase("k") == 0) btnLanguageEN.setTextColor(colorAccent);
            btnLanguageEN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "k");
                    builder.dismiss();
                }
            });
            final Button btnLanguageES = (Button) view.findViewById(R.id.btnLanguageES);
            if (installStatus < 2) btnLanguageES.setVisibility(View.INVISIBLE);
            if (bbName.compareToIgnoreCase("v") == 0) btnLanguageES.setTextColor(colorAccent);
            btnLanguageES.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "v");
                    builder.dismiss();
                }
            });
            final Button btnLanguageFR = (Button) view.findViewById(R.id.btnLanguageFR);
            if (installStatus < 3) btnLanguageFR.setVisibility(View.INVISIBLE);
            if (bbName.compareToIgnoreCase("l") == 0) btnLanguageFR.setTextColor(colorAccent);
            btnLanguageFR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "l");
                    builder.dismiss();
                }
            });
            final Button btnLanguageIT = (Button) view.findViewById(R.id.btnLanguageIT);
            if (installStatus < 4) btnLanguageIT.setVisibility(View.INVISIBLE);
            if (bbName.compareToIgnoreCase("d") == 0) btnLanguageIT.setTextColor(colorAccent);
            btnLanguageIT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "d");
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
            final int installStatus = (forceShowAllButtons) ? 4 : Integer.parseInt(PCommon.GetPref(context, APP_PREF_KEY.INSTALL_STATUS, "1"));

            final TextView tvTrad = (TextView) view.findViewById(R.id.tvTrad);
            final ToggleButton btnLanguageEN = (ToggleButton) view.findViewById(R.id.btnLanguageEN);
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
            final ToggleButton btnLanguageES = (ToggleButton) view.findViewById(R.id.btnLanguageES);
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
            final ToggleButton btnLanguageFR = (ToggleButton) view.findViewById(R.id.btnLanguageFR);
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
            final ToggleButton btnLanguageIT = (ToggleButton) view.findViewById(R.id.btnLanguageIT);
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
            final Button btnLanguageClear = (Button) view.findViewById(R.id.btnLanguageClear);
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
                    final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, 0, ""));
                    tvTrad.setText(languageStack);
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "");
                }
            });
            final Button btnLanguageContinue = (Button) view.findViewById(R.id.btnLanguageContinue);
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
            final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, 0, ""));
            tvTrad.setText(languageStack);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Select multiple bible language and chapter
     * Response in TRAD_BIBLE_NAME
     * @param context
     * @param msg
     * @param desc
     * @param isCancelable
     * @param forceShowAllButtons  Force to show all buttons
     * @param chapterMax
     */
    @SuppressWarnings("JavaDoc")
    static void SelectBibleLanguageMultiChapter(final AlertDialog builder, final Context context, final View view, final String msg, @SuppressWarnings({"UnusedParameters", "SameParameterValue"}) final String desc, @SuppressWarnings("SameParameterValue") final boolean isCancelable, @SuppressWarnings("SameParameterValue") final boolean forceShowAllButtons, final int chapterMax)
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
            final int installStatus = (forceShowAllButtons) ? 4 : Integer.parseInt(PCommon.GetPref(context, APP_PREF_KEY.INSTALL_STATUS, "1"));
            final TextView tvTrad = (TextView) view.findViewById(R.id.tvTrad);
            final NumberPicker npChapter = (NumberPicker) view.findViewById(R.id.npChapter);
            npChapter.setMinValue(1);
            npChapter.setMaxValue(chapterMax);

            final ToggleButton btnLanguageEN = (ToggleButton) view.findViewById(R.id.btnLanguageEN);
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
            final ToggleButton btnLanguageES = (ToggleButton) view.findViewById(R.id.btnLanguageES);
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
            final ToggleButton btnLanguageFR = (ToggleButton) view.findViewById(R.id.btnLanguageFR);
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
            final ToggleButton btnLanguageIT = (ToggleButton) view.findViewById(R.id.btnLanguageIT);
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
            final Button btnLanguageClear = (Button) view.findViewById(R.id.btnLanguageClear);
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
                    final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, 0, ""));
                    tvTrad.setText(languageStack);
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, "");
                }
            });
            final Button btnLanguageContinue = (Button) view.findViewById(R.id.btnLanguageContinue);
            if (installStatus <= 0) btnLanguageContinue.setEnabled(false);
            btnLanguageContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //all selected toggle
                    final String currentTrad = PCommon.GetPrefTradBibleName(context, false);
                    if (currentTrad.equals("")) return;
                    PCommon.SavePref(context, APP_PREF_KEY.BIBLE_NAME_DIALOG, currentTrad.substring(0, 1));
                    PCommon.SavePref(context, APP_PREF_KEY.BOOK_CHAPTER_DIALOG, String.valueOf(npChapter.getValue()));
                    builder.dismiss();
                }
            });

            final String tradInit = PCommon.GetPrefTradBibleName(context, false);
            if (tradInit.contains("k")) btnLanguageEN.setChecked(true);
            if (tradInit.contains("v")) btnLanguageES.setChecked(true);
            if (tradInit.contains("l")) btnLanguageFR.setChecked(true);
            if (tradInit.contains("d")) btnLanguageIT.setChecked(true);
            final String languageStack = PCommon.ConcaT(context.getString(R.string.tvTrad), " ", PCommon.ManageTradBibleName(context, 0, ""));
            tvTrad.setText(languageStack);
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
     * @param msgId
     */
    @SuppressWarnings("JavaDoc")
    static void ShowDialog(final Activity activity, final int titleId, final int msgId)
    {
        try
        {
            final LayoutInflater inflater = activity.getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_dialog, (ViewGroup) activity.findViewById(R.id.llDialog));

            final AlertDialog builder = new AlertDialog.Builder(activity).create();
            builder.setCancelable(false);
            builder.setTitle(titleId);
            builder.setView(view);

            final TextView tvMsg = (TextView) view.findViewById(R.id.tvMsg);
            tvMsg.setText(msgId);

            final Button btnClose = (Button) view.findViewById(R.id.btnClose);
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
}
