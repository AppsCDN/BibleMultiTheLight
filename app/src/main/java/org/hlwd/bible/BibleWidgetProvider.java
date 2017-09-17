package org.hlwd.bible;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;

public class BibleWidgetProvider extends AppWidgetProvider
{
    private SCommon _s = null;
    private final String WIDGET_LANG_CLICK    = "org.hlwd.bible.WIDGET_LANG_CLICK";
    private final String WIDGET_PREV_CLICK    = "org.hlwd.bible.WIDGET_PREV_CLICK";
    private final String WIDGET_FORWARD_CLICK = "org.hlwd.bible.WIDGET_FORWARD_CLICK";
    private final String WIDGET_REFRESH_CLICK = "org.hlwd.bible.WIDGET_REFRESH_CLICK";
    private final String WIDGET_DOWN_CLICK    = "org.hlwd.bible.WIDGET_DOWN_CLICK";
    private final String WIDGET_FAV_CLICK     = "org.hlwd.bible.WIDGET_FAV_CLICK";

    private int    WIDGET_LAYOUT_ID = R.layout.bible_widget_row_light;
    private boolean IS_WIDGET_LAYOUT_DARK = false;

    private static int INSTALL_STATUS = 1;

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds)
    {
        try
        {
            if (PCommon._isDebugVersion) System.out.println("onUpdate => count: " + appWidgetIds.length);

            for (int appWidgetId : appWidgetIds)
            {
                UpdateAppWidget(context, appWidgetId, null);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        try
        {
            super.onReceive(context, intent);

            CheckLocalInstance(context);

            if (WIDGET_DOWN_CLICK.equals(intent.getAction()))
            {
                //*** Get params
                final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                final int appWidgetId = intent.getIntExtra(appWidgetManager.EXTRA_APPWIDGET_ID, -1);
                final int id = intent.getIntExtra("ID", -1);
                final String vText = intent.getStringExtra("VTEXT");

                final String[] words = vText.split("\\s");
                if (words.length < 5) return;

                int i = 0;
                final StringBuilder sbText = new StringBuilder("");
                for (String word : words)
                {
                    if (i >= 3)
                    {
                        sbText.append(word);
                        sbText.append(" ");
                    }
                    else i++;
                }
                final String newVText = sbText.toString();

                //*** Update event
                final Intent intent_DOWN_CLICK = new Intent(WIDGET_DOWN_CLICK);
                intent_DOWN_CLICK.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                intent_DOWN_CLICK.putExtra("ID", id);
                intent_DOWN_CLICK.putExtra("VTEXT", newVText);

                final PendingIntent pendingIntent_DOWN_CLICK = PendingIntent.getBroadcast(context, appWidgetId, intent_DOWN_CLICK, PendingIntent.FLAG_UPDATE_CURRENT);

                //*** Set components
                SaveTheme(context);
                final RemoteViews views = new RemoteViews(context.getPackageName(), WIDGET_LAYOUT_ID);
                views.setTextViewText(R.id.widget_tv_text, newVText);
                views.setOnClickPendingIntent(R.id.widget_btn_down, pendingIntent_DOWN_CLICK);

                //*** Update widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
            else if (WIDGET_LANG_CLICK.equals(intent.getAction()))
            {
                //*** Get params
                final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                final int appWidgetId = intent.getIntExtra(appWidgetManager.EXTRA_APPWIDGET_ID, -1);

                final String bbName = intent.getStringExtra("BBNAME");
                final int bNumber = intent.getIntExtra("BNUMBER", -1);
                final int cNumber = intent.getIntExtra("CNUMBER", -1);
                final int vNumber = intent.getIntExtra("VNUMBER", -1);

                //*** Update verse
                final String rolledBBNAME = RollBookName(context, bbName);
                final WidgetVerseBO widgetVerse = GetWidgetVerse(context, rolledBBNAME, bNumber, cNumber, vNumber);
                if (PCommon._isDebugVersion) System.out.println("onReceive => appWidgetId: " + appWidgetId + ", bbname: " + bbName + ", rolledbbname: " + rolledBBNAME);

                //*** Update widget
                UpdateAppWidget(context, appWidgetId, widgetVerse);
            }
            else if (WIDGET_REFRESH_CLICK.equals(intent.getAction()))
            {
                //*** Get params
                final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                final int appWidgetId = intent.getIntExtra(appWidgetManager.EXTRA_APPWIDGET_ID, -1);

                //*** Update widget
                UpdateAppWidget(context, appWidgetId, null);
            }
            else if (WIDGET_PREV_CLICK.equals(intent.getAction()))
            {
                //*** Get params
                final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                final int appWidgetId = intent.getIntExtra(appWidgetManager.EXTRA_APPWIDGET_ID, -1);
                final int id = intent.getIntExtra("ID", -1) - 1;

                //*** Update verse
                final VerseBO verse = _s.GetVerse(id);
                final WidgetVerseBO widgetVerse = (verse == null) ? null : GetWidgetVerse(context, verse.bbName, verse.bNumber, verse.cNumber, verse.vNumber);

                //*** Update widget
                UpdateAppWidget(context, appWidgetId, widgetVerse);
            }
            else if (WIDGET_FORWARD_CLICK.equals(intent.getAction()))
            {
                //*** Get params
                final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                final int appWidgetId = intent.getIntExtra(appWidgetManager.EXTRA_APPWIDGET_ID, -1);
                final int id = intent.getIntExtra("ID", -1) + 1;

                //*** Update verse
                final VerseBO verse = _s.GetVerse(id);
                final WidgetVerseBO widgetVerse = (verse == null) ? null : GetWidgetVerse(context, verse.bbName, verse.bNumber, verse.cNumber, verse.vNumber);

                //*** Update widget
                UpdateAppWidget(context, appWidgetId, widgetVerse);
            }
            else if (WIDGET_FAV_CLICK.equals(intent.getAction()))
            {
                //*** Get params
                final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                final int appWidgetId = intent.getIntExtra(appWidgetManager.EXTRA_APPWIDGET_ID, -1);
                final int id = intent.getIntExtra("ID", -1);

                //*** Update verse
                final VerseBO verse = _s.GetVerse(id);
                if (verse == null) return;

                if (verse.mark > 0)
                {
                    _s.DeleteNote(verse.bNumber, verse.cNumber, verse.vNumber);
                }
                else
                {
                    final String changeDt = PCommon.NowYYYYMMDD();
                    final NoteBO note = new NoteBO(verse.bNumber, verse.cNumber, verse.vNumber, changeDt, 1);
                    _s.SaveNote(note);
                }
                final WidgetVerseBO widgetVerse = GetWidgetVerse(context, verse.bbName, verse.bNumber, verse.cNumber, verse.vNumber);

                //*** Update widget
                UpdateAppWidget(context, appWidgetId, widgetVerse);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    protected void UpdateAppWidget(final Context context, final int appWidgetId, WidgetVerseBO widgetVerse)
    {
        try
        {
            if (PCommon._isDebugVersion) System.out.println("My UpdateAppWidget => " + appWidgetId);

            //*** Set Verse
            if (widgetVerse == null) widgetVerse = GetWidgetVerse(context);

            //*** Set params
            final Intent intent_LANG_CLICK = new Intent(WIDGET_LANG_CLICK);
            intent_LANG_CLICK.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent_LANG_CLICK.putExtra("ID",      widgetVerse.id);
            intent_LANG_CLICK.putExtra("BBNAME",  widgetVerse.bbName);
            intent_LANG_CLICK.putExtra("BNUMBER", widgetVerse.bNumber);
            intent_LANG_CLICK.putExtra("CNUMBER", widgetVerse.cNumber);
            intent_LANG_CLICK.putExtra("VNUMBER", widgetVerse.vNumber);

            final Intent intent_REFRESH_CLICK = new Intent(WIDGET_REFRESH_CLICK);
            intent_REFRESH_CLICK.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            final Intent intent_PREV_CLICK = new Intent(WIDGET_PREV_CLICK);
            intent_PREV_CLICK.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent_PREV_CLICK.putExtra("ID", widgetVerse.id);

            final Intent intent_FORWARD_CLICK = new Intent(WIDGET_FORWARD_CLICK);
            intent_FORWARD_CLICK.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent_FORWARD_CLICK.putExtra("ID", widgetVerse.id);

            final Intent intent_DOWN_CLICK = new Intent(WIDGET_DOWN_CLICK);
            intent_DOWN_CLICK.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent_DOWN_CLICK.putExtra("ID", widgetVerse.id);
            intent_DOWN_CLICK.putExtra("VTEXT", widgetVerse.vText);

            final Intent intent_FAV_CLICK = new Intent(WIDGET_FAV_CLICK);
            intent_FAV_CLICK.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent_FAV_CLICK.putExtra("ID", widgetVerse.id);

            //*** Set events
            final PendingIntent pendingIntent_LANG_CLICK = PendingIntent.getBroadcast(context, appWidgetId, intent_LANG_CLICK, PendingIntent.FLAG_UPDATE_CURRENT);
            final PendingIntent pendingIntent_REFRESH_CLICK = PendingIntent.getBroadcast(context, appWidgetId, intent_REFRESH_CLICK, PendingIntent.FLAG_UPDATE_CURRENT);
            final PendingIntent pendingIntent_PREV_CLICK = PendingIntent.getBroadcast(context, appWidgetId, intent_PREV_CLICK, PendingIntent.FLAG_UPDATE_CURRENT);
            final PendingIntent pendingIntent_FORWARD_CLICK = PendingIntent.getBroadcast(context, appWidgetId, intent_FORWARD_CLICK, PendingIntent.FLAG_UPDATE_CURRENT);
            final PendingIntent pendingIntent_DOWN_CLICK = PendingIntent.getBroadcast(context, appWidgetId, intent_DOWN_CLICK, PendingIntent.FLAG_UPDATE_CURRENT);
            final PendingIntent pendingIntent_FAV_CLICK = PendingIntent.getBroadcast(context, appWidgetId, intent_FAV_CLICK, PendingIntent.FLAG_UPDATE_CURRENT);

            //*** Set components
            SaveTheme(context);
            final String lang = widgetVerse.bbName.compareToIgnoreCase("k") == 0 ? "EN"
                    : widgetVerse.bbName.compareToIgnoreCase("l") == 0 ? "FR"
                    : widgetVerse.bbName.compareToIgnoreCase("d") == 0 ? "IT"
                    : "ES";

            final RemoteViews views = new RemoteViews(context.getPackageName(), WIDGET_LAYOUT_ID);
            views.setTextViewText(R.id.widget_tv_lang, lang);
            views.setTextViewText(R.id.widget_tv_ref, widgetVerse.vRef);
            views.setTextViewText(R.id.widget_tv_text, widgetVerse.vText);

            if (!IS_WIDGET_LAYOUT_DARK) {
                views.setImageViewResource(R.id.widget_iv_fav, (widgetVerse.mark <= 0) ? PCommon.GetDrawableId(context, "ic_star_border_black_18dp") : PCommon.GetDrawableId(context, "ic_star_black_18dp"));
            }
            else {
                views.setImageViewResource(R.id.widget_iv_fav, (widgetVerse.mark <= 0) ? PCommon.GetDrawableId(context, "ic_star_border_white_18dp") : PCommon.GetDrawableId(context, "ic_star_white_18dp"));
            }

            views.setOnClickPendingIntent(R.id.widget_tv_lang, pendingIntent_LANG_CLICK);
            views.setOnClickPendingIntent(R.id.widget_iv_refresh, pendingIntent_REFRESH_CLICK);
            views.setOnClickPendingIntent(R.id.widget_btn_back, pendingIntent_PREV_CLICK);
            views.setOnClickPendingIntent(R.id.widget_btn_forward, pendingIntent_FORWARD_CLICK);
            views.setOnClickPendingIntent(R.id.widget_btn_down, pendingIntent_DOWN_CLICK);
            views.setOnClickPendingIntent(R.id.widget_iv_fav, pendingIntent_FAV_CLICK);

            //*** Update widget
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    private void CheckLocalInstance(final Context context)
    {
        try
        {
            if (_s == null)
            {
                _s = SCommon.GetInstance(context);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    private WidgetVerseBO GetWidgetVerse(final Context context)
    {
        try
        {
            CheckLocalInstance(context);

            String bbName = PCommon.GetPref(context, IProject.APP_PREF_KEY.BIBLE_NAME, "k");
            if (bbName == "")
                bbName = "k";

            final int min = _s.GetBibleIdMin(bbName);
            final int max = _s.GetBibleIdMax(bbName);

            int rndBibleId = PCommon.GetRandomInt(context, min, max);
            VerseBO verse = _s.GetVerse(rndBibleId);
            if (verse == null)
            {
                rndBibleId = PCommon.GetRandomInt(context, 1, 31);
                verse = _s.GetVerse(rndBibleId);
            }

            final String verseRef = PCommon.ConcaT(verse.bsName, " ", verse.cNumber, ".", verse.vNumber);
            final String verseText = verse.vText;
            final WidgetVerseBO widgetVerse = new WidgetVerseBO(rndBibleId, verseRef, verseText, verse.bbName, verse.bNumber, verse.cNumber, verse.vNumber, verse.mark);

            return widgetVerse;
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
        finally
        {
            if (_s != null) _s = null;
        }

        return null;
    }

    private String RollBookName(final Context context, final String bbName)
    {
        try
        {
            if (INSTALL_STATUS != 4) INSTALL_STATUS = _s.GetInstallStatus(context);
            switch (INSTALL_STATUS)
            {
                case 1:
                {
                    return "k";
                }
                case 2:
                {
                    return (bbName.compareToIgnoreCase("v") == 0) ? "k" : "v";
                }
                case 3:
                {
                    return (bbName.compareToIgnoreCase("l") == 0) ? "k" : (bbName.compareToIgnoreCase("v") == 0) ? "l" : "v";
                }
                case 4:
                {
                    return (bbName.compareToIgnoreCase("l") == 0) ? "d" : (bbName.compareToIgnoreCase("v") == 0) ? "l" : (bbName.compareToIgnoreCase("k") == 0) ? "v" : "k";
                }
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return "k";
    }

    private WidgetVerseBO GetWidgetVerse(final Context context, final String bbName, final int bNumber, final int cNumber, final int vNumber)
    {
        try
        {
            CheckLocalInstance(context);

            final ArrayList<VerseBO> lstVerse = _s.GetVerse(bbName, bNumber, cNumber, vNumber);
            final VerseBO verse = lstVerse.get(0);
            final String verseRef = PCommon.ConcaT(verse.bsName, " ", verse.cNumber, ".", verse.vNumber);
            final String verseText = verse.vText;
            final WidgetVerseBO widgetVerse = new WidgetVerseBO(verse.id, verseRef, verseText, verse.bbName, verse.bNumber, verse.cNumber, verse.vNumber, verse.mark);

            return widgetVerse;
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);

            return null;
        }
    }

    private void SaveTheme(final Context context)
    {
        try
        {
            final int themeId = PCommon.GetPrefThemeId(context);
            if (themeId == R.style.AppThemeDark)
            {
                WIDGET_LAYOUT_ID = R.layout.bible_widget_row_dark;
                IS_WIDGET_LAYOUT_DARK = true;
            }
            else
            {
                WIDGET_LAYOUT_ID = R.layout.bible_widget_row_light;
                IS_WIDGET_LAYOUT_DARK = false;
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

/*
    private Bitmap CreateBitmap(final Context context, final String vText)
    {
        final Bitmap bitmap = Bitmap.createBitmap(160, 84, Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        final Typeface clock = PCommon.GetTypeface(context);

        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        //paint.setTextSize(14);
        //paint.setTextAlign(Align.CENTER);
        canvas.drawText(vText, 80, 60, paint);
        return bitmap;
    }
*/
}
