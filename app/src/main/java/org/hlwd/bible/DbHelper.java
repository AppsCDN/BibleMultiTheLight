
package org.hlwd.bible;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

//<editor-fold defaultstate="collapsed" desc="-- History --">
// PROD: Bible 3.10,   DbVersion: 29 (11)2019-05-05
// PROD: Bible 3.10,   DbVersion: 29 (11)2019-04-28
// PROD: Bible 3.9,    DbVersion: 28 (10)2019-01-27
// PROD: Bible 3.8,    DbVersion: 27 (10)2018-12-16
// PROD: Bible 3.7,    DbVersion: 26 (10)2018-11-25
// PROD: Bible 3.6,    DbVersion: 25 (10)2018-11-18
// PROD: Bible 3.5,    DbVersion: 24 (10)2018-11-10
// PROD: Bible 3.4,    DbVersion: 23 (9) 2018-10-14
// PROD: Bible 3.3,    DbVersion: 22 (8) 2018-06-10
// PROD: Bible 3.2,    DbVersion: 21 (8) 2018-05-05
// PROD: Bible 3.1,    DbVersion: 20 (8) 2018-05-01
// PROD: Bible 3.0,    DbVersion: 19 (8) 2018-04-22
// PROD: Bible 2.13,   DbVersion: 18 (8) 2018-03-04
// PROD: Bible 2.12,   DbVersion: 17 (8) 2018-02-03
// PROD: Bible 2.11,   DbVersion: 16 (8) 2018-01-14
// PROD: Bible 2.10,   DbVersion: 15 (8) 2018-01-07
// PROD: Bible 2.9,    DbVersion: 14 (8) 2017-12-12
// PROD: Bible 2.8,    DbVersion: 13 (8) 2017-10-10
// PROD: Bible 2.7,    DbVersion: 12 (8) 2017-09-05
// PROD: Bible 2.6,    DbVersion: 11 (8) 2017-08-27
// PROD: Bible 2.5,    DbVersion: 10 (7) 2017-08-15
// PROD: Bible 2.4,    DbVersion: 9  (6) 2017-07-09
// PROD: Bible 2.3,    DbVersion: 8  (6) 2017-07-02
// PROD: Bible 2.2,    DbVersion: 7  (6) 2017-06-20
// PROD: Bible 2.1,    DbVersion: 6      2017-04-17
// PROD: Bible 2.0,    DbVersion: 5      2017-03-21
// PROD: Bible 1.9,    DbVersion: 4      2017-02-26
// PROD: Bible 1.7,    DbVersion: 3      2017-02...
// PROD: Bible 1.6,    DbVersion; 3      2017-01-04
// PROD: Bible 1.5,    DbVersion: 2      2016-10-xx
// PROD: Bible 1.0,    DbVersion: 1      2016-10-07
//-------------------------------------------------

//</editor-fold>

/***
 * Use only LogD and LogE for logs, all is "hidden"
 */
class DbHelper extends SQLiteOpenHelper
{
    //<editor-fold defaultstate="collapsed" desc="-- Variables --">

    @SuppressWarnings("UnusedAssignment")
    private Context _context = null;
    private SQLiteDatabase _db = null;
    private static final int _version = 29;

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Constructor --">

    DbHelper(final Context context)
    {
        super(context, "bible.db", null, _version);
        _context = context;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Events --">

    @Override
    public void onCreate(final SQLiteDatabase database)
    {
        try
        {
            _db = database;

            //Upgrade to latest version
            onUpgrade(database, -1, -1);
        }
        catch (Exception ex)
        {
            LogE(ex);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion)
    {
        try
        {
            String sql;

            if (PCommon._isDebugVersion)
            {
                LogD(PCommon.ConcaT("onUpgrade: from ", oldVersion, " => ", newVersion, "\nDbHelper version: ", _version, ", Db GetVersion: ", database.getVersion()));
            }

            _db = database;

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            if (oldVersion < 1)
            {
                //This should be the latest version (in case of 1st installation)

                SetGlobalSettings();
                PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.INSTALL_STATUS, 1);

                sql = DropTable("log");
                _db.execSQL(sql);

                sql = "CREATE TABLE log (msg TEXT)";
                _db.execSQL(sql);

                sql = "DROP INDEX IF EXISTS cacheSearch_ndx";
                _db.execSQL(sql);

                sql = DropTable("cacheSearch");
                _db.execSQL(sql);

                sql = "DROP INDEX IF EXISTS cacheTab0_ndx";
                _db.execSQL(sql);

                sql = "DROP INDEX IF EXISTS cacheTab1_ndx";
                _db.execSQL(sql);

                sql = DropTable("cacheTab");
                _db.execSQL(sql);

                sql = DropTable("bibleNote");
                _db.execSQL(sql);

                sql = DropTable("bibleRef");
                _db.execSQL(sql);

                sql = "DROP INDEX IF EXISTS bible_ndx";
                _db.execSQL(sql);

                sql = "DROP INDEX IF EXISTS bibleNumber_ndx";
                _db.execSQL(sql);

                sql = DropTable("bible");
                _db.execSQL(sql);

                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                sql = "CREATE TABLE bibleRef (bbName TEXT NOT NULL, bNumber INTEGER NOT NULL, bName TEXT NOT NULL, bsName TEXT NOT NULL, PRIMARY KEY (bbName, bNumber))";
                _db.execSQL(sql);

                sql = "CREATE TABLE bible (id INTEGER NOT NULL, bbName TEXT NOT NULL, bNumber INTEGER NOT NULL, cNumber INTEGER NOT NULL, vNumber INTEGER NOT NULL, vText TEXT NOT NULL, PRIMARY KEY (bbName, bNumber, cNumber, vNumber))";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX bible_ndx on bible (id)";
                _db.execSQL(sql);

                //23
                sql = "CREATE UNIQUE INDEX bibleNumber_ndx on bible (bbName, bNumber, cNumber, vNumber)";
                _db.execSQL(sql);

                //Mark: 1=Fav, 2=Bookmark | rating=0..5 | note=personal note | links? //was (never used): sql = "CREATE TABLE bibleNote (bNumber INTEGER, cNumber INTEGER, vNumber INTEGER, changeDt TEXT, mark INTEGER, rating INTEGER, note TEXT, PRIMARY KEY (bNumber, cNumber, vNumber))";
                sql = "CREATE TABLE bibleNote (bNumber INTEGER NOT NULL, cNumber INTEGER NOT NULL, vNumber INTEGER NOT NULL, changeDt TEXT NOT NULL, mark INTEGER CHECK(mark >= 1 AND mark <= 2), note TEXT NOT NULL, PRIMARY KEY (bNumber, cNumber, vNumber))";
                _db.execSQL(sql);

                //noinspection SyntaxError
                sql = "CREATE TABLE cacheTab (tabId INTEGER NOT NULL, tabType TEXT CHECK(tabType='S' OR tabType='F' or tabType='A' or tabType='P'), tabTitle TEXT NOT NULL, fullQuery TEXT NOT NULL, scrollPosY INTEGER NOT NULL, bbName TEXT NOT NULL, isBook INTEGER NOT NULL, isChapter INTEGER NOT NULL, isVerse INTEGER NOT NULL, bNumber INTEGER NOT NULL, cNumber INTEGER NOT NULL, vNumber INTEGER NOT NULL, trad TEXT NULL, PRIMARY KEY (tabId))";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX cacheTab0_ndx on cacheTab (tabId)";
                _db.execSQL(sql);

                sql = "CREATE INDEX cacheTab1_ndx on cacheTab (tabType)";
                _db.execSQL(sql);

                sql = "CREATE TABLE cacheSearch (tabId INTEGER NOT NULL, bibleId INTEGER NOT NULL)";
                _db.execSQL(sql);

                sql = "CREATE INDEX cacheSearch_ndx on cacheSearch (tabId)";
                _db.execSQL(sql);

                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                //Load Genesis
                sql = PCommon.ConcaT("INSERT INTO cacheTab VALUES (", 0, ",", PCommon.AQ("S"), ",", PCommon.AQ("Gen1"), ",", PCommon.AQ("1 1"), ",", 0, ",", PCommon.AQ("k"), ",", 1, ",", 1, ",", 0, ",", 1, ",", 1, ",", 0, ",", PCommon.AQ("k"), ")");
                _db.execSQL(sql);

                sql = PCommon.ConcaT("INSERT INTO bibleNote VALUES (", 1, ",", 1, ",", 1, ",", PCommon.AQ(PCommon.NowYYYYMMDD()), ",", 2, ",", PCommon.AQ(""), ")");
                _db.execSQL(sql);

                sql = PCommon.ConcaT("INSERT INTO bibleNote VALUES (", 1, ",", 1, ",", 5, ",", PCommon.AQ(PCommon.NowYYYYMMDD()), ",", 1, ",", PCommon.AQ(""), ")");
                _db.execSQL(sql);

                //10
                sql = "CREATE TABLE bibleCi (ciId INTEGER NOT NULL, bNumber INTEGER NOT NULL, cNumber INTEGER NOT NULL, vCount INTEGER NOT NULL, PRIMARY KEY (bNumber, cNumber))";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX bibleCi_ndx on bibleCi (ciId)";
                _db.execSQL(sql);

                sql = "CREATE TABLE planDesc (planId INTEGER NOT NULL, planRef TEXT NOT NULL, startDt TEXT NOT NULL, endDt TEXT NOT NULL, bCount INTEGER NOT NULL, cCount INTEGER NOT NULL, vCount INTEGER NOT NULL, vDayCount INTEGER NOT NULL, dayCount INTEGER NOT NULL, PRIMARY KEY (planRef))";
                _db.execSQL(sql);

                sql = "CREATE TABLE planCal (planId INTEGER NOT NULL, dayNumber INTEGER NOT NULL, dayDt TEXT NOT NULL, isRead INTEGER NOT NULL, bNumberStart INTEGER NOT NULL, cNumberStart INTEGER NOT NULL, vNumberStart INTEGER NOT NULL, bNumberEnd INTEGER NOT NULL, cNumberEnd INTEGER NOT NULL, vNumberEnd INTEGER NOT NULL, PRIMARY KEY (planId, bNumberStart, cNumberStart, vNumberStart))";
                _db.execSQL(sql);

                sql = "CREATE INDEX planCal0_ndx on planCal (planId, dayNumber)";
                _db.execSQL(sql);

                sql = "CREATE TABLE artDesc (artId INTEGER NOT NULL, artUpdatedDt TEXT NOT NULL, artTitle TEXT NOT NULL, artSrc TEXT NOT NULL, PRIMARY KEY (artTitle))";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX artDesc0_ndx on artDesc (artId)";
                _db.execSQL(sql);

                FillDbWithAll();

                //Last version
                _db.setVersion(_version);

                //Let return here (all has been done)
                return;
            }
            if (oldVersion < 3)    //1, 2 => 3
            {
                sql = "DROP INDEX IF EXISTS cacheTab0_ndx";
                _db.execSQL(sql);

                sql = "DROP INDEX IF EXISTS cacheTab1_ndx";
                _db.execSQL(sql);

                sql = "ALTER TABLE cacheTab RENAME TO temp_cacheTab";
                _db.execSQL(sql);

                sql = "CREATE TABLE cacheTab (tabId INTEGER NOT NULL, tabType TEXT CHECK(tabType='S' OR tabType='F' or tabType='A'), tabTitle TEXT NOT NULL, fullQuery TEXT NOT NULL, scrollPosY INTEGER NOT NULL, bbName TEXT NOT NULL, isBook INTEGER NOT NULL, isChapter INTEGER NOT NULL, isVerse INTEGER NOT NULL, bNumber INTEGER NOT NULL, cNumber INTEGER NOT NULL, vNumber INTEGER NOT NULL, PRIMARY KEY (tabId))";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX cacheTab0_ndx on cacheTab (tabId)";
                _db.execSQL(sql);

                sql = "CREATE INDEX cacheTab1_ndx on cacheTab (tabType)";
                _db.execSQL(sql);

                sql = "INSERT INTO cacheTab SELECT tabId, tabType, tabTitle, fullQuery, scrollPosY, bbName, isBook, isChapter, isVerse, bNumber, cNumber, vNumber FROM temp_cacheTab";
                _db.execSQL(sql);

                sql = DropTable("temp_cacheTab");
                _db.execSQL(sql);
            }
            //noinspection StatementWithEmptyBody
            if (oldVersion < 4)    //1..3 => 4
            {
                //Code REMOVED: PCommon.SavePref(_context, IProject.APP_PREF_KEY.LAYOUT_COLUMN, "1");
            }
            if (oldVersion < 5)    //1..4 => 5
            {
                //--- New settings
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.TRAD_BIBLE_NAME, "");
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_1, "1");
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_2, "2");
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_3, "3");
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_4, "2");

                sql = "DROP INDEX IF EXISTS cacheTab0_ndx";
                _db.execSQL(sql);

                sql = "DROP INDEX IF EXISTS cacheTab1_ndx";
                _db.execSQL(sql);

                sql = "ALTER TABLE cacheTab RENAME TO temp_cacheTab";
                _db.execSQL(sql);

                //noinspection SyntaxError
                sql = "CREATE TABLE cacheTab (tabId INTEGER NOT NULL, tabType TEXT CHECK(tabType='S' OR tabType='F' or tabType='A'), tabTitle TEXT NOT NULL, fullQuery TEXT NOT NULL, scrollPosY INTEGER NOT NULL, bbName TEXT NOT NULL, isBook INTEGER NOT NULL, isChapter INTEGER NOT NULL, isVerse INTEGER NOT NULL, bNumber INTEGER NOT NULL, cNumber INTEGER NOT NULL, vNumber INTEGER NOT NULL, trad TEXT NULL, PRIMARY KEY (tabId))";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX cacheTab0_ndx on cacheTab (tabId)";
                _db.execSQL(sql);

                sql = "CREATE INDEX cacheTab1_ndx on cacheTab (tabType)";
                _db.execSQL(sql);

                sql = "INSERT INTO cacheTab SELECT *, bbName FROM temp_cacheTab";
                _db.execSQL(sql);

                sql = DropTable("temp_cacheTab");
                _db.execSQL(sql);
            }
            if (oldVersion < 6)
            {
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.FONT_NAME, "");
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.FONT_SIZE, "14");
            }
            if (oldVersion < 7)
            {
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.BOOK_CHAPTER_DIALOG, "1");
            }
            if (oldVersion < 10)     //1..9 => 10
            {
                //--- New setting
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.PLAN_ID, "-1");
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.PLAN_PAGE, "-1");

                //10 new tables & index
                sql = "CREATE TABLE bibleCi (ciId INTEGER NOT NULL, bNumber INTEGER NOT NULL, cNumber INTEGER NOT NULL, vCount INTEGER NOT NULL, PRIMARY KEY (bNumber, cNumber))";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX bibleCi_ndx on bibleCi (ciId)";
                _db.execSQL(sql);

                sql = "CREATE TABLE planDesc (planId INTEGER NOT NULL, planRef TEXT NOT NULL, startDt TEXT NOT NULL, endDt TEXT NOT NULL, bCount INTEGER NOT NULL, cCount INTEGER NOT NULL, vCount INTEGER NOT NULL, vDayCount INTEGER NOT NULL, dayCount INTEGER NOT NULL, dayRead INTEGER NOT NULL, progressPerc INTEGER NOT NULL, PRIMARY KEY (planRef))";
                _db.execSQL(sql);

                sql = "CREATE TABLE planCal (planId INTEGER NOT NULL, dayNumber INTEGER NOT NULL, dayDt TEXT NOT NULL, isRead INTEGER NOT NULL, bNumberStart INTEGER NOT NULL, cNumberStart INTEGER NOT NULL, vNumberStart INTEGER NOT NULL, bNumberEnd INTEGER NOT NULL, cNumberEnd INTEGER NOT NULL, vNumberEnd INTEGER NOT NULL, PRIMARY KEY (planId, bNumberStart, cNumberStart, vNumberStart))";
                _db.execSQL(sql);

                sql = "CREATE INDEX planCal0_ndx on planCal (planId, dayNumber)";
                _db.execSQL(sql);

                //10 alter table
                sql = "DROP INDEX IF EXISTS cacheTab0_ndx";
                _db.execSQL(sql);

                sql = "DROP INDEX IF EXISTS cacheTab1_ndx";
                _db.execSQL(sql);

                sql = "ALTER TABLE cacheTab RENAME TO temp_cacheTab";
                _db.execSQL(sql);

                //noinspection SyntaxError
                sql = "CREATE TABLE cacheTab (tabId INTEGER NOT NULL, tabType TEXT CHECK(tabType='S' OR tabType='F' or tabType='A' or tabType='P'), tabTitle TEXT NOT NULL, fullQuery TEXT NOT NULL, scrollPosY INTEGER NOT NULL, bbName TEXT NOT NULL, isBook INTEGER NOT NULL, isChapter INTEGER NOT NULL, isVerse INTEGER NOT NULL, bNumber INTEGER NOT NULL, cNumber INTEGER NOT NULL, vNumber INTEGER NOT NULL, trad TEXT NULL, PRIMARY KEY (tabId))";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX cacheTab0_ndx on cacheTab (tabId)";
                _db.execSQL(sql);

                sql = "CREATE INDEX cacheTab1_ndx on cacheTab (tabType)";
                _db.execSQL(sql);

                sql = "INSERT INTO cacheTab SELECT tabId, tabType, tabTitle, fullQuery, scrollPosY, bbName, isBook, isChapter, isVerse, bNumber, cNumber, vNumber, trad FROM temp_cacheTab";
                _db.execSQL(sql);

                sql = DropTable("temp_cacheTab");
                _db.execSQL(sql);

//sql = "CREATE TABLE planHistory (startDt TEXT NOT NULL, endDt TEXT NOT NULL, desc TEXT NOT NULL)";
//_db.execSQL(sql);

                ImportCi();
            }
            if (oldVersion < 11)    //1..10 => 11
            {
                //11 alter table
                sql = "ALTER TABLE planDesc RENAME TO temp_planDesc";
                _db.execSQL(sql);

                sql = "CREATE TABLE planDesc (planId INTEGER NOT NULL, planRef TEXT NOT NULL, startDt TEXT NOT NULL, endDt TEXT NOT NULL, bCount INTEGER NOT NULL, cCount INTEGER NOT NULL, vCount INTEGER NOT NULL, vDayCount INTEGER NOT NULL, dayCount INTEGER NOT NULL, PRIMARY KEY (planRef))";
                _db.execSQL(sql);

                sql = "INSERT INTO planDesc SELECT planId, planRef, startDt, endDt, bCount, cCount, vCount, vDayCount, dayCount FROM temp_planDesc";
                _db.execSQL(sql);

                sql = DropTable("temp_planDesc");
                _db.execSQL(sql);
            }
            if (oldVersion < 18)    //1..17 => 18
            {
                final String themeName = PCommon.GetPref(_context, IProject.APP_PREF_KEY.THEME_NAME, "LIGHT");
                if (themeName.equalsIgnoreCase("LIGHT_AND_BLUE"))
                {
                    PCommon.SavePref(_context, IProject.APP_PREF_KEY.THEME_NAME, "LIGHT");
                }
            }
            if (oldVersion < 20)    //1..19 => 20
            {
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.UI_LAYOUT, "C");
            }
            if (oldVersion < 23)    //1..22 => 23
            {
                sql = "DROP INDEX IF EXISTS bibleNumber_ndx";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX bibleNumber_ndx on bible (bbName, bNumber, cNumber, vNumber)";
                _db.execSQL(sql);
            }
            if (oldVersion < 24)   //1..23 ==> 24
            {
                //--- New setting
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_5, "1");
            }
            if (oldVersion < 29)   //1..28 => 29
            {
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.EDIT_DIALOG, "");
                PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.EDIT_STATUS, 0);
                PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.EDIT_ART_ID, -1);
                PCommon.SavePref(_context, IProject.APP_PREF_KEY.EDIT_SELECTION, "");

                sql = "CREATE TABLE artDesc (artId INTEGER NOT NULL, artUpdatedDt TEXT NOT NULL, artTitle TEXT NOT NULL, artSrc TEXT NOT NULL, PRIMARY KEY (artTitle))";
                _db.execSQL(sql);

                sql = "CREATE UNIQUE INDEX artDesc0_ndx on artDesc (artId)";
                _db.execSQL(sql);
            }

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            //Last
            if (oldVersion < 24)   //1..23 ==> 24.  This check should be the last before the version
            {
                //--- PT
                FillDbWithPt();
            }
            if (oldVersion < _version)    //1..(last-1) => last
            {
                //=== FOR LAST VERSION
                PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.UPDATE_STATUS, 0);

                if (PCommon._isDebugVersion) PrintGlobalSettings();

                if (oldVersion >= 24)
                {
                    PCommon.ShowToast(_context, R.string.installFinish, Toast.LENGTH_LONG);
                }

                _db.setVersion(_version);
                //=== END FOR LAST VERSION
            }
        }
        catch (Exception ex)
        {
            LogE(ex);
        }
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Db Methods --">

    private void LogD(final String msg)
    {
        if (PCommon._isDebugVersion) Log.d("DB", msg);
    }

    /***
     * Log error
     * @param ex    Exception
     */
    private void LogE(Exception ex)
    {
        if (PCommon._isDebugVersion) Log.e("DB", "Failure", ex);
    }

    private void SetGlobalSettings()
    {
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.UI_LAYOUT, "C");
        final boolean isUiTelevision = PCommon.DetectIsUiTelevision(_context);

        PCommon.SavePrefInt(_context,   IProject.APP_PREF_KEY.INSTALL_STATUS, 1);                   //Will be updated
        PCommon.SavePrefInt(_context,   IProject.APP_PREF_KEY.UPDATE_STATUS, 1);
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.LOG_STATUS, "");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.EDIT_DIALOG, "");
        PCommon.SavePrefInt(_context,   IProject.APP_PREF_KEY.EDIT_STATUS, 0);
        PCommon.SavePrefInt(_context,   IProject.APP_PREF_KEY.EDIT_ART_ID, -1);
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.EDIT_SELECTION, "");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.BIBLE_NAME, "");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, "k");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.BOOK_CHAPTER_DIALOG, "1");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.TRAD_BIBLE_NAME, "");
        PCommon.SavePrefInt(_context,   IProject.APP_PREF_KEY.BIBLE_ID, 0);
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_1, "1");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_2, "2");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_3, "3");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_4, "2");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_5, "1");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.THEME_NAME, "DARK");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.FONT_NAME, isUiTelevision ? "RobotoCondensed.regular" : "");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.FONT_SIZE, isUiTelevision ? "20" : "14");
        PCommon.SavePref(_context,      IProject.APP_PREF_KEY.FAV_SYMBOL, _context.getString(R.string.favSymbolFavDefault));
        PCommon.SavePrefInt(_context,   IProject.APP_PREF_KEY.FAV_ORDER, 1);
        PCommon.SavePrefInt(_context,   IProject.APP_PREF_KEY.VIEW_POSITION, 0);
        PCommon.SavePrefInt(_context,   IProject.APP_PREF_KEY.PLAN_ID, -1);
        PCommon.SavePrefInt(_context,   IProject.APP_PREF_KEY.PLAN_PAGE, -1);
    }

    private void PrintGlobalSettings()
    {
        try
        {
            if (!PCommon._isDebugVersion) return;

            System.out.println(PCommon.ConcaT("INSTALL_STATUS:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.INSTALL_STATUS)));
            System.out.println(PCommon.ConcaT("UPDATE_STATUS:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.UPDATE_STATUS)));
            System.out.println(PCommon.ConcaT("LOG_STATUS:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.LOG_STATUS)));
            System.out.println(PCommon.ConcaT("EDIT_DIALOG:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.EDIT_DIALOG)));
            System.out.println(PCommon.ConcaT("EDIT_STATUS:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.EDIT_STATUS)));
            System.out.println(PCommon.ConcaT("EDIT_ART_ID:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.EDIT_ART_ID)));
            System.out.println(PCommon.ConcaT("EDIT_SELECTION:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.EDIT_SELECTION)));
            System.out.println(PCommon.ConcaT("BIBLE_NAME:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.BIBLE_NAME)));
            System.out.println(PCommon.ConcaT("BIBLE_NAME_DIALOG:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG)));
            System.out.println(PCommon.ConcaT("TRAD_BIBLE_NAME:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.TRAD_BIBLE_NAME)));
            System.out.println(PCommon.ConcaT("BIBLE_ID:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.BIBLE_ID)));
            System.out.println(PCommon.ConcaT("LAYOUT_DYNAMIC_1:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_1)));
            System.out.println(PCommon.ConcaT("LAYOUT_DYNAMIC_2:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_2)));
            System.out.println(PCommon.ConcaT("LAYOUT_DYNAMIC_3:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_3)));
            System.out.println(PCommon.ConcaT("LAYOUT_DYNAMIC_4:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_4)));
            System.out.println(PCommon.ConcaT("LAYOUT_DYNAMIC_5:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_5)));
            System.out.println(PCommon.ConcaT("THEME_NAME:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.THEME_NAME)));
            System.out.println(PCommon.ConcaT("FONT_NAME:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.FONT_NAME)));
            System.out.println(PCommon.ConcaT("FONT_SIZE:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.FONT_SIZE)));
            System.out.println(PCommon.ConcaT("FAV_SYMBOL:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.FAV_SYMBOL)));
            System.out.println(PCommon.ConcaT("FAV_ORDER:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.FAV_ORDER)));
            System.out.println(PCommon.ConcaT("VIEW_POSITION:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.VIEW_POSITION)));
            System.out.println(PCommon.ConcaT("PLAN_ID:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.PLAN_ID)));
            System.out.println(PCommon.ConcaT("PLAN_PAGE:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.PLAN_PAGE)));
            System.out.println(PCommon.ConcaT("UI_LAYOUT:", PCommon.GetPref(_context, IProject.APP_PREF_KEY.UI_LAYOUT)));
        }
        catch (Exception ex)
        {
            LogE(ex);
        }
    }

    private String DropTable(final String tblName)
    {
        try
        {
            return PCommon.ConcaT("DROP TABLE IF EXISTS ", tblName);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Bible Methods --">

    /***
     * Fill db with All
     */
    private void FillDbWithAll()
    {
        try
        {
            final ThreadGroup threadGroup = new ThreadGroup(_context.getString(R.string.threadNfoGroup));
            final String threadName = PCommon.ConcaT(_context.getString(R.string.threadNfoPrefix), PCommon.TimeFuncShort());
            final Thread thread = new Thread(threadGroup, threadName)
            {
                int id = 0;
                final Handler handler = new Handler();

                @Override
                public void run()
                {
                    FillDbTask();
                }

                private void FillDbTask()
                {
                    try
                    {
                        ImportBibleRef();

                        ImportXmlBible("k");
                        PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.INSTALL_STATUS, 1);

                        ImportXmlBible("v");
                        PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.INSTALL_STATUS, 2);

                        ImportXmlBible("l");
                        PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.INSTALL_STATUS, 3);

                        ImportXmlBible("d");
                        PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.INSTALL_STATUS, 4);

                        ImportXmlBible("a");
                        PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.INSTALL_STATUS, 5);

                        ImportCi();

                        final String bbname = PCommon.GetPref(_context, IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, "k");
                        PCommon.SavePref(_context, IProject.APP_PREF_KEY.BIBLE_NAME, bbname);
                    }
                    catch(Exception ex)
                    {
                        LogE(ex);
                    }
                    finally
                    {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                PCommon.ShowToast(_context, R.string.installFinish, Toast.LENGTH_LONG);
                            }
                        });
                    }
                }

                private void ImportXmlBible(final String bbName) throws Exception
                {
                    @SuppressWarnings("UnusedAssignment") int size = 0;
                    int tot = 0;
                    final String xmlName = bbName + ".xml";

                    AssetManager am = _context.getAssets();
                    InputStream is = am.open(xmlName);

                    do
                    {
                        size = is.read();
                        if (size >= 0) tot++;
                    }
                    while(size >= 0);

                    is.close();
                    is = am.open(xmlName);

                    @SuppressWarnings("UnusedAssignment")
                    VTDGen vg = null;
                    {
                        byte[] b = new byte[tot];
                        if (is.read(b, 0, tot) != -1)
                        {
                            vg = new VTDGen();
                            vg.setDoc(b);
                            vg.parse(false);
                        }
                        //noinspection UnusedAssignment
                        b = null;
                    }
                    is.close();

                    //Chapter infos
                    is = am.open("ci.txt");
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String row;
                    String[] cols;

                    //Verses
                    @SuppressWarnings("ConstantConditions") VTDNav vn = vg.getNav();
                    AutoPilot apVerses = new AutoPilot();
                    apVerses.bind(vn);

                    String xpath, sql, vText;
                    final String bbname = PCommon.AQ(bbName);
                    final String insertSql = "INSERT INTO bible (id, bbName, bNumber, cNumber, vNumber, vText) VALUES (";

                    xpath = PCommon.ConcaT("//BIBLEBOOK/CHAPTER/VERS");
                    apVerses.selectXPath( xpath );

                    int b, c, v, vCount, i;
                    while ((row = br.readLine()) != null)
                    {
                        cols = row.split("\\|");
                        if (cols.length != 3) break;

                        b = Integer.parseInt( cols[0] );
                        c = Integer.parseInt( cols[1] );
                        vCount = Integer.parseInt( cols[2] );
                        v = 0;

                        for (i = 0; i < vCount; i++)
                        {
                            apVerses.evalXPath();

                            v++;
                            id++;
                            vText = PCommon.AQ(PCommon.RQ(vn.getXPathStringVal()));
                            sql = PCommon.ConcaT(insertSql, id, ",", bbname, ",", b, ",", c, ",", v, ",", vText, ")");

                            //_db.beginTransaction();
                            _db.execSQL(sql);
                            //_db.setTransactionSuccessful();

                            //noinspection UnusedAssignment
                            sql = null;
                            //noinspection UnusedAssignment
                            vText = null;
                        }

                        //noinspection UnusedAssignment
                        cols = null;
                        //noinspection UnusedAssignment
                        row = null;
                    }

                    apVerses.resetXPath();
                    //noinspection UnusedAssignment
                    apVerses = null;
                    //noinspection UnusedAssignment
                    vn = null;

                    br.close();
                    is.close();
                }

                private void ImportBibleRef() throws Exception
                {
                    final String fileName = "b.txt";
                    String row;
                    String[] cols;
                    BibleRefBO ref = new BibleRefBO();

                    AssetManager am = _context.getAssets();
                    InputStream is = am.open(fileName);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));

                    while ((row = br.readLine()) != null)
                    {
                        cols = row.split("\\|");
                        if (cols.length != 4) break;

                        ref.bbName = cols[0];
                        ref.bNumber = Integer.parseInt(cols[1]);
                        ref.bName = cols[2];
                        ref.bsName = cols[3];

                        AddBibleRef(ref);
                    }

                    br.close();
                    is.close();
                }

                /***
                 * Add bible reference
                 * @param r Reference
                 */
                private void AddBibleRef(final BibleRefBO r)
                {
                    final String sql = PCommon.ConcaT("INSERT INTO bibleRef (bbName, bNumber, bName, bsName) VALUES (",
                                PCommon.AQ(r.bbName), ",",
                                r.bNumber, ",",
                                PCommon.AQ(r.bName), ",",
                                PCommon.AQ(r.bsName), ")");

                    _db.execSQL(sql);
                }
            };
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
        catch(Exception ex)
        {
            LogE(ex);
        }
    }

    /***
     * Fill db with PT
     */
    private void FillDbWithPt()
    {
        try
        {
            final ThreadGroup threadGroup = new ThreadGroup(_context.getString(R.string.threadNfoGroup));
            final String threadName = PCommon.ConcaT(_context.getString(R.string.threadNfoPrefix), PCommon.TimeFuncShort());
            //noinspection SameParameterValue
            final Thread thread = new Thread(threadGroup, threadName)
            {
                int id = 124408;
                final Handler handler = new Handler();

                @Override
                public void run()
                {
                    FillDbWithPtTask();
                }

                private void FillDbWithPtTask()
                {
                    try
                    {
                        ImportBiblePtRef();

                        ImportXmlBible("a");
                        PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.INSTALL_STATUS, 5);
                    }
                    catch(Exception ex)
                    {
                        LogE(ex);
                    }
                    finally
                    {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                PCommon.ShowToast(_context, R.string.installFinish, Toast.LENGTH_LONG);
                            }
                        });
                    }
                }

                @SuppressWarnings("SameParameterValue")
                private void ImportXmlBible(final String bbName) throws Exception
                {
                    @SuppressWarnings("UnusedAssignment") int size = 0;
                    int tot = 0;
                    final String xmlName = bbName + ".xml";

                    AssetManager am = _context.getAssets();
                    InputStream is = am.open(xmlName);

                    do
                    {
                        size = is.read();
                        if (size >= 0) tot++;
                    }
                    while(size >= 0);

                    is.close();
                    is = am.open(xmlName);

                    @SuppressWarnings("UnusedAssignment")
                    VTDGen vg = null;
                    {
                        byte[] b = new byte[tot];
                        if (is.read(b, 0, tot) != -1)
                        {
                            vg = new VTDGen();
                            vg.setDoc(b);
                            vg.parse(false);
                        }
                        //noinspection UnusedAssignment
                        b = null;
                    }
                    is.close();

                    //Chapter infos
                    is = am.open("ci.txt");
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String row;
                    String[] cols;

                    //Verses
                    @SuppressWarnings("ConstantConditions") VTDNav vn = vg.getNav();
                    AutoPilot apVerses = new AutoPilot();
                    apVerses.bind(vn);

                    String xpath, sql, vText;
                    final String bbname = PCommon.AQ(bbName);
                    final String insertSql = "INSERT INTO bible (id, bbName, bNumber, cNumber, vNumber, vText) VALUES (";

                    xpath = PCommon.ConcaT("//BIBLEBOOK/CHAPTER/VERS");
                    apVerses.selectXPath( xpath );

                    int b, c, v, vCount, i;
                    while ((row = br.readLine()) != null)
                    {
                        cols = row.split("\\|");
                        if (cols.length != 3) break;

                        b = Integer.parseInt( cols[0] );
                        c = Integer.parseInt( cols[1] );
                        vCount = Integer.parseInt( cols[2] );
                        v = 0;

                        for (i = 0; i < vCount; i++)
                        {
                            apVerses.evalXPath();

                            v++;
                            id++;
                            vText = PCommon.AQ(PCommon.RQ(vn.getXPathStringVal()));
                            sql = PCommon.ConcaT(insertSql, id, ",", bbname, ",", b, ",", c, ",", v, ",", vText, ")");

                            //_db.beginTransaction();
                            _db.execSQL(sql);
                            //_db.setTransactionSuccessful();

                            //noinspection UnusedAssignment
                            sql = null;
                            //noinspection UnusedAssignment
                            vText = null;
                        }

                        //noinspection UnusedAssignment
                        cols = null;
                        //noinspection UnusedAssignment
                        row = null;
                    }

                    apVerses.resetXPath();
                    //noinspection UnusedAssignment
                    apVerses = null;
                    //noinspection UnusedAssignment
                    vn = null;

                    br.close();
                    is.close();
                }

                private void ImportBiblePtRef() throws Exception
                {
                    final String fileName = "a.txt";
                    String row;
                    String[] cols;
                    BibleRefBO ref = new BibleRefBO();

                    AssetManager am = _context.getAssets();
                    InputStream is = am.open(fileName);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));

                    while ((row = br.readLine()) != null)
                    {
                        cols = row.split("\\|");
                        if (cols.length != 4) break;

                        ref.bbName = cols[0];
                        ref.bNumber = Integer.parseInt(cols[1]);
                        ref.bName = cols[2];
                        ref.bsName = cols[3];

                        AddBibleRef(ref);
                    }

                    br.close();
                    is.close();
                }

                /***
                 * Add bible reference
                 * @param r Reference
                 */
                private void AddBibleRef(final BibleRefBO r)
                {
                    final String sql = PCommon.ConcaT("INSERT INTO bibleRef (bbName, bNumber, bName, bsName) VALUES (",
                            PCommon.AQ(r.bbName), ",",
                            r.bNumber, ",",
                            PCommon.AQ(r.bName), ",",
                            PCommon.AQ(r.bsName), ")");

                    _db.execSQL(sql);
                }
            };
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
        catch(Exception ex)
        {
            LogE(ex);
        }
    }

    private void ImportCi()
    {
        try
        {
            final ThreadGroup threadGroup = new ThreadGroup(_context.getString(R.string.threadNfoGroup));
            final String threadName = PCommon.ConcaT(_context.getString(R.string.threadNfoPrefix), PCommon.TimeFuncShort());
            final Thread thread = new Thread(threadGroup, threadName)
            {
                @Override
                public void run()
                {
                    ImportCi();
                }

                private void ImportCi()
                {
                    try
                    {
                        final String fileName = "ci.txt";
                        final String insertSql = "INSERT INTO bibleCi (ciId, bNumber, cNumber, vCount) VALUES (";
                        String row, sql;
                        String[] cols;
                        int b, c, vCount, id = 0;

                        AssetManager am = _context.getAssets();
                        InputStream is = am.open(fileName);
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));

                        while ((row = br.readLine()) != null)
                        {
                            cols = row.split("\\|");
                            if (cols.length != 3)
                                break;

                            id++;
                            b = Integer.parseInt( cols[0] );
                            c = Integer.parseInt( cols[1] );
                            vCount = Integer.parseInt( cols[2] );
                            sql = PCommon.ConcaT(insertSql, id, ",", b, ",", c, ",", vCount, ")");

                            _db.execSQL(sql);

                            //noinspection UnusedAssignment
                            sql = null;
                            //noinspection UnusedAssignment
                            row = null;
                        }

                        br.close();
                        is.close();
                    }
                    catch (Exception ex)
                    {
                        LogE(ex);
                    }
                }
            };
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
        catch(Exception ex)
        {
            LogE(ex);
        }
    }

    //</editor-fold>
}

