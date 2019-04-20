
package org.hlwd.bible;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;

class Dal
{
    //<editor-fold defaultstate="collapsed" desc="-- Variables --">

    private SQLiteDatabase _db = null;
    private DbHelper _dbHelper = null;
    private Context _context = null;

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Constructors --">

    /***
     * Using specified context
     * @param ctx   context
     */
    Dal(final Context ctx)
    {
        //TODO: method with context may be deprecated
        Init(ctx);
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Open/Close/Utils --">

    private void Init(Context ctx)
    {
        try
        {
            _context = ctx;
            _dbHelper = new DbHelper(_context);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Open db connection
     */
    void OpenReadWrite()
    {
        try
        {
            _db = _dbHelper.getWritableDatabase();
            //deprecated _db.setLockingEnabled(false);
            _db.execSQL("PRAGMA synchronous=OFF");
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * CloseDb db connection
     */
    void CloseDb()
    {
        try
        {
            if (_dbHelper != null) _dbHelper.close();
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Db is open?
     * @return  true/false
     */
    boolean IsDbOpen()
    {
        boolean isDbOpen = false;

        try
        {
            if (_db == null)
            {
                return false;
            }

            isDbOpen = _db.isOpen();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return isDbOpen;
    }

    /**
     * Shrink db
     */
    void ShrinkDb(final Context context)
    {
        String sql = "VACUUM";

        try
        {
            _db.execSQL(sql);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

   /***
     * Get db version
     * @return db version
     */
    int GetDbVersion()
    {
        int dbVersion = -1;

        try
        {
            dbVersion = _db.getVersion();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return dbVersion;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Log --">

    /***
     * Add a act
     * @param msg
     */
    @SuppressWarnings("JavaDoc")
    void AddLog(final String msg)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            final String formatedSqlField = PCommon.RQ(msg);
            sql = PCommon.ConcaT("INSERT INTO log (msg) VALUES ('", formatedSqlField, "')");

            _db.execSQL(sql);
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Delete all logs
     */
    void DeleteAllLogs()
    {
        //TODO: check act size, _db size
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = "delete from log";

            _db.execSQL(sql);
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Bible --">

    /***
     * Get tabId of an article
     * @param artName   ART_NAME
     * @return Negative value if not found
     */
    int GetArticleTabId(final String artName)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int tabId = -1;

        try
        {
            sql = PCommon.ConcaT("SELECT tabId FROM cacheTab WHERE fullQuery=", PCommon.AQ(artName), " LIMIT 1");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                tabId = c.getInt(0);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return tabId;
    }

    /***
     * Get a verse
     * @param bibleId
     * @return verse
     */
    @SuppressWarnings("JavaDoc")
    VerseBO GetVerse(final int bibleId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        VerseBO verse = null;

        try
        {
            sql = PCommon.ConcaT("SELECT b.bbName, b.bNumber, b.cNumber, b.vNumber, b.vText, n.mark, r.bName, r.bsName FROM bible b",
                    " INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber",
                    " LEFT OUTER JOIN bibleNote n ON n.bNumber=b.bNumber AND n.cNumber=b.cNumber AND n.vNumber=b.vNumber",
                    " WHERE b.id=", bibleId);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                verse = new VerseBO();
                verse.id = bibleId;
                verse.bbName = c.getString(0);
                verse.bName = c.getString(6);
                verse.bsName = c.getString(7);
                verse.bNumber = c.getInt(1);
                verse.cNumber = c.getInt(2);
                verse.vNumber = c.getInt(3);
                verse.vText = c.getString(4);

                verse.mark = c.getInt(5);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return verse;
    }

    /***
     * Get a verse
     * @param tbbName
     * @param bNumber
     * @param cNumber
     * @param vNumber
     * @return verse
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<VerseBO> GetVerse(final String tbbName, final int bNumber, final int cNumber, final int vNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            VerseBO verse;

            sql = PCommon.ConcaT("SELECT b.id, b.vText, r.bName, r.bsName, n.mark, b.bbName, ", this.CaseBible("b.bbName", tbbName),
                    " FROM bible b",
                    " INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber",
                    " LEFT OUTER JOIN bibleNote n ON n.bNumber=b.bNumber AND n.cNumber=b.cNumber AND n.vNumber=b.vNumber",
                    " WHERE b.bbName IN ", this.InBible(tbbName),
                    " AND b.bNumber=", bNumber,
                    " AND b.cNumber=", cNumber,
                    " AND b.vNumber=", vNumber,
                    " ORDER BY bbNameOrder ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                verse = new VerseBO();
                verse.id = c.getInt(0);
                verse.bbName = c.getString(5);
                verse.bName = c.getString(2);
                verse.bsName = c.getString(3);
                verse.bNumber = bNumber;
                verse.cNumber = cNumber;
                verse.vNumber = vNumber;
                verse.vText = c.getString(1);
                verse.mark = c.getInt(4);
                lstVerse.add(verse);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lstVerse;
    }

    /***
     * Get a list of verses
     * @param tbbName
     * @param bNumber
     * @param cNumber
     * @param vNumberFrom
     * @param vNumberTo
     * @return list of verses
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<VerseBO> GetVerses(final String tbbName, final int bNumber, final int cNumber, final int vNumberFrom, final int vNumberTo)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            VerseBO verse;

            sql = PCommon.ConcaT("SELECT b.id, b.vNumber, b.vText, r.bName, r.bsName, n.mark, b.bbName, ", this.CaseBible("b.bbName", tbbName),
                    " FROM bible b",
                    " INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber",
                    " LEFT OUTER JOIN bibleNote n ON n.bNumber=b.bNumber AND n.cNumber=b.cNumber AND n.vNumber=b.vNumber",
                    " WHERE b.bbName IN ", this.InBible(tbbName),
                    " AND b.bNumber=", bNumber,
                    " AND b.cNumber=", cNumber,
                    " AND b.vNumber >= ", vNumberFrom,
                    " AND b.vNumber <= ", vNumberTo,
                    " ORDER BY b.vNumber ASC, bbNameOrder ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                verse = new VerseBO();
                verse.id = c.getInt(0);
                verse.bbName = c.getString(6);
                verse.bName = c.getString(3);
                verse.bsName = c.getString(4);
                verse.bNumber = bNumber;
                verse.cNumber = cNumber;
                verse.vNumber = c.getInt(1);
                verse.vText = c.getString(2);
                verse.mark = c.getInt(5);
                lstVerse.add(verse);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lstVerse;
    }

    /***
     * Copy cache search for other bible (data are deleted before copying)
     * @param tabIdTo
     * @param tbbName
     * @param bNumberStart
     * @param cNumberStart
     * @param vNumberStart
     * @param bNumberEnd
     * @param cNumberEnd
     * @param vNumberEnd
     * @return true if copy was successful
     */
    @SuppressWarnings("JavaDoc")
    boolean CopyCacheSearchForOtherBible(final int tabIdTo, final String tbbName, final int bNumberStart, final int cNumberStart, final int vNumberStart, final int bNumberEnd, final int cNumberEnd, final int vNumberEnd)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            this.DeleteCacheSearch(tabIdTo);

            VerseBO verse;
            ArrayList<VerseBO> lstVerseK;
            lstVerseK = this.GetVerse("k", bNumberStart, cNumberStart, vNumberStart);
            if (lstVerseK.size() <= 0) return false;
            verse = lstVerseK.get(0);
            final int idKStart = verse.id;
            lstVerseK.clear();
            //noinspection UnusedAssignment
            lstVerseK = null;

            lstVerseK = this.GetVerse("k", bNumberEnd, cNumberEnd, vNumberEnd);
            if (lstVerseK.size() <= 0) return false;
            verse = lstVerseK.get(0);
            final int idKEnd = verse.id;
            lstVerseK.clear();
            //noinspection UnusedAssignment
            lstVerseK = null;

            String bbnameTo;
            int size = tbbName.length();
            for (int i = 0; i < size; i++)
            {
                bbnameTo = tbbName.substring(i, i + 1);

                sql = PCommon.ConcaT(
                        "INSERT INTO cacheSearch (tabId, bibleId) ",
                        "SELECT ", tabIdTo ,", (SELECT i.id FROM bible i WHERE i.bbName=", PCommon.AQ(bbnameTo)," AND i.bNumber=r.bNumber AND i.cNumber=r.cNumber AND i.vNumber=r.vNumber) ",
                        "FROM (",
                        " SELECT distinct b.bNumber, b.cNumber, b.vNumber",
                        " FROM bible b",
                        " WHERE b.id >= ", idKStart, " AND b.id <= ", idKEnd,
                        " ORDER BY b.bNumber, b.cNumber, b.vNumber ASC) r" );

                _db.execSQL(sql);
            }

            return true;
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }

        return false;
    }

    /***
     * Get verse text
     * @param tbbName
     * @param bNumber
     * @param cNumber
     * @param vNumber
     * @return Text with verse
     */
    @SuppressWarnings("JavaDoc")
    String GetVerseText(final String tbbName, final int bNumber, final int cNumber, final int vNumber)
    {
        ArrayList<VerseBO> lstVerse = null;
        StringBuilder sb = new StringBuilder();

        try
        {
            String verseText;

            lstVerse = GetVerse(tbbName, bNumber, cNumber, vNumber);

            if (lstVerse != null)
            {
                for (VerseBO v : lstVerse)
                {
                    verseText =  PCommon.ConcaT(v.bsName, " ", cNumber, ".", v.vNumber, ": ", v.vText, "\n\n");

                    sb.append(verseText);
                }
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            if (lstVerse != null)
            {
                lstVerse.clear();
                //noinspection UnusedAssignment
                lstVerse = null;
            }
        }

        return sb.toString();
    }

    /***
     * Get a chapter
     * @param tbbName
     * @param bNumber
     * @param cNumber
     * @return list of verses
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<VerseBO> GetChapter(final String tbbName, final int bNumber, final int cNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            VerseBO verse;

            sql = PCommon.ConcaT("SELECT b.id, b.vNumber, b.vText, r.bName, r.bsName, n.mark, b.bbName, ", this.CaseBible("b.bbName", tbbName),
                    " FROM bible b",
                    " INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber",
                    " LEFT OUTER JOIN bibleNote n ON n.bNumber=b.bNumber AND n.cNumber=b.cNumber AND n.vNumber=b.vNumber",
                    " WHERE b.bbName IN ", this.InBible(tbbName),
                    " AND b.bNumber=", bNumber,
                    " AND b.cNumber=", cNumber,
                    " ORDER BY b.vNumber ASC, bbNameOrder ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                verse = new VerseBO();
                verse.id = c.getInt(0);
                verse.bbName = c.getString(6);  //Was: bbName;
                verse.bName = c.getString(3);
                verse.bsName = c.getString(4);
                verse.bNumber = bNumber;
                verse.cNumber = cNumber;
                verse.vNumber = c.getInt(1);
                verse.vText = c.getString(2);
                verse.mark = c.getInt(5);
                lstVerse.add(verse);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lstVerse;
    }

    /***
     * Get chapter text
     * @param tbbName
     * @param bNumber
     * @param cNumber
     * @return text of chapter
     */
    @SuppressWarnings("JavaDoc")
    String GetChapterText(final String tbbName, final int bNumber, final int cNumber)
    {
        ArrayList<VerseBO> lstVerse = null;
        StringBuilder sb = new StringBuilder();

        try
        {
            String verseText;

            lstVerse = GetChapter(tbbName, bNumber, cNumber);

            if (lstVerse != null)
            {
                for (VerseBO v : lstVerse)
                {
                    verseText =  PCommon.ConcaT(v.bsName, " ", cNumber, ".", v.vNumber, ": ", v.vText, "\n\n");

                    sb.append(verseText);
                }
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            if (lstVerse != null)
            {
                lstVerse.clear();
                //noinspection UnusedAssignment
                lstVerse = null;
            }
        }

        return sb.toString();
    }

    /***
     * Search bible
     * @param bbName
     * @param bNumber
     * @param cNumber
     * @param searchString
     * @return list of verses
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<VerseBO> SearchBible(final String bbName, final int bNumber, final int cNumber, String searchString)
    {
        //TODO: maybe add ID in this call :)  for SearchFragment and GetVerse

        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            //TODO: check if % was entered and SQL INJECTION with '
            searchString = PCommon.ConcaT("%", searchString, "%");
            VerseBO verse;

            sql = PCommon.ConcaT("SELECT b.id, b.vNumber, b.vText, r.bName, r.bsName, n.mark FROM bible b",
                    " INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber",
                    " LEFT OUTER JOIN bibleNote n ON n.bNumber=b.bNumber AND n.cNumber=b.cNumber AND n.vNumber=b.vNumber",
                    " WHERE b.bbName=", PCommon.AQ(bbName),
                    " AND b.bNumber=", bNumber,
                    " AND b.cNumber=", cNumber,
                    " AND b.vText like ", PCommon.AQ(PCommon.RQ(searchString)),
                    " ORDER BY b.bNumber, b.cNumber, b.vNumber ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                verse = new VerseBO();
                verse.id = c.getInt(0);
                verse.bbName = bbName;
                verse.bName = c.getString(3);
                verse.bsName = c.getString(4);
                verse.bNumber = bNumber;
                verse.cNumber = cNumber;
                verse.vNumber = c.getInt(1);
                verse.vText = c.getString(2);

                verse.mark = c.getInt(5);
                lstVerse.add(verse);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lstVerse;
    }

    /***
     * Search bible
     * @param bbName
     * @param bNumber
     * @param searchString
     * @return list of verses
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<VerseBO> SearchBible(final String bbName, final int bNumber, String searchString)
    {
        //TODO: maybe add ID in this call :)  for SearchFragment and GetVerse

        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            //TODO: check if % was entered and SQL INJECTION with '
            searchString = PCommon.ConcaT("%", searchString, "%");
            VerseBO verse;

            sql = PCommon.ConcaT("SELECT b.id, b.cNumber, b.vNumber, b.vText, r.bName, r.bsName, n.mark FROM bible b",
                    " INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber",
                    " LEFT OUTER JOIN bibleNote n ON n.bNumber=b.bNumber AND n.cNumber=b.cNumber AND n.vNumber=b.vNumber",
                    " WHERE b.bbName=", PCommon.AQ(bbName),
                    " AND b.bNumber=", bNumber,
                    " AND b.vText like ", PCommon.AQ(PCommon.RQ(searchString)),
                    " ORDER BY b.bNumber, b.cNumber, b.vNumber ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                verse = new VerseBO();
                verse.id = c.getInt(0);
                verse.bbName = bbName;
                verse.bName = c.getString(4);
                verse.bsName = c.getString(5);
                verse.bNumber = bNumber;
                verse.cNumber = c.getInt(1);
                verse.vNumber = c.getInt(2);
                verse.vText = c.getString(3);

                verse.mark = c.getInt(6);
                lstVerse.add(verse);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lstVerse;
    }

    /***
     * Search bible
     * @param bbName
     * @param searchString
     * @return list of verses
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<VerseBO> SearchBible(final String bbName, String searchString)
    {
        //TODO: maybe add ID in this call :)  for SearchFragment and GetVerse

        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            //TODO: check if % was entered and SQL INJECTION with '
            searchString = PCommon.ConcaT("%", searchString, "%");
            VerseBO verse;

            sql = PCommon.ConcaT("SELECT b.id, b.bNumber, b.cNumber, b.vNumber, b.vText, r.bName, r.bsName, n.mark FROM bible b",
                    " INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber",
                    " LEFT OUTER JOIN bibleNote n ON n.bNumber=b.bNumber AND n.cNumber=b.cNumber AND n.vNumber=b.vNumber",
                    " WHERE b.bbName=", PCommon.AQ(bbName),
                    " AND b.vText like ", PCommon.AQ(PCommon.RQ(searchString)),
                    " ORDER BY b.bNumber, b.cNumber, b.vNumber ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                verse = new VerseBO();
                verse.id = c.getInt(0);
                verse.bbName = bbName;
                verse.bName = c.getString(5);
                verse.bsName = c.getString(6);
                verse.bNumber = c.getInt(1);
                verse.cNumber = c.getInt(2);
                verse.vNumber = c.getInt(3);
                verse.vText = c.getString(4);

                verse.mark = c.getInt(7);
                lstVerse.add(verse);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lstVerse;
    }

    /***
     * Search notes
     * @param bbName
     * @param searchString  Give NULL to get all notes
     * @param orderBy       Order by
     * @param markType      Mark type (NULL to get all types)
     * @return list of verses
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<VerseBO> SearchNotes(final String bbName, String searchString, final int orderBy, final String markType)
    {
        //TODO: maybe add ID in this call :)  for SearchFragment and GetVerse

        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            //TODO: check if % was entered and SQL INJECTION with '

            final String orderByClause;
            switch (orderBy)
            {
                case 0:
                    orderByClause = "n.mark DESC, n.changeDt DESC, b.bNumber ASC, b.cNumber ASC, b.vNumber ASC";
                    break;

                case 1:
                    orderByClause = "n.changeDt DESC, b.bNumber ASC, b.cNumber ASC, b.vNumber ASC";
                    break;

                case 2:
                    orderByClause = "b.bNumber ASC, b.cNumber ASC, b.vNumber ASC";
                    break;

                default:
                    orderByClause = "b.bNumber ASC, b.cNumber ASC, b.vNumber ASC";
                    break;
            }

            sql = PCommon.ConcaT("SELECT b.id, b.bNumber, b.cNumber, b.vNumber, b.vText, r.bName, r.bsName, n.mark FROM bible b",
                    " INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber",
                    " INNER JOIN bibleNote n ON n.bNumber=b.bNumber AND n.cNumber=b.cNumber AND n.vNumber=b.vNumber",
                    " WHERE b.bbName=", PCommon.AQ(bbName));

            if (searchString.length() > 0)
            {
                searchString = PCommon.ConcaT("%", searchString, "%");

                sql = PCommon.ConcaT(sql, " AND b.vText like ", PCommon.AQ(PCommon.RQ(searchString)));
            }

            if (markType != null)
            {
                sql = PCommon.ConcaT(sql, " AND n.mark=", markType);
            }

            sql = PCommon.ConcaT(sql, " ORDER BY ", orderByClause);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            VerseBO verse;
            while (!c.isAfterLast())
            {
                verse = new VerseBO();
                verse.id = c.getInt(0);
                verse.bbName = bbName;
                verse.bName = c.getString(5);
                verse.bsName = c.getString(6);
                verse.bNumber = c.getInt(1);
                verse.cNumber = c.getInt(2);
                verse.vNumber = c.getInt(3);
                verse.vText = c.getString(4);
                verse.mark = c.getInt(7);
                lstVerse.add(verse);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lstVerse;
    }

    /***
     * Search bible (from cache)
     * @param searchId  tabId
     * @return
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<VerseBO> SearchBible(final int searchId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            VerseBO verse;

            sql = PCommon.ConcaT("SELECT b.id, b.bNumber, b.cNumber, b.vNumber, b.vText, b.bbName, r.bName, r.bsName, n.mark FROM bible b",
                    " INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber",
                    " INNER JOIN cacheSearch s ON s.bibleId=b.id",
                    " LEFT OUTER JOIN bibleNote n ON n.bNumber=b.bNumber AND n.cNumber=b.cNumber AND n.vNumber=b.vNumber",
                    " WHERE s.tabId=", searchId,
                    " ORDER BY b.bNumber, b.cNumber, b.vNumber ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                verse = new VerseBO();
                verse.id = c.getInt(0);
                verse.bbName = c.getString(5);
                verse.bName = c.getString(6);
                verse.bsName = c.getString(7);
                verse.bNumber = c.getInt(1);
                verse.cNumber = c.getInt(2);
                verse.vNumber = c.getInt(3);
                verse.vText = c.getString(4);

                verse.mark = c.getInt(8);
                lstVerse.add(verse);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lstVerse;
    }

    /***
     * Get list of books by name
     * @param bbName
     * @param searchString
     * @return list of books
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<BibleRefBO> GetListBookByName(final String bbName, String searchString)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<BibleRefBO> lst = new ArrayList<>();

        try
        {
            //TODO: check if % was entered and SQL INJECTION with '
            searchString = PCommon.ConcaT("%", searchString, "%");
            BibleRefBO r;

            sql = PCommon.ConcaT("SELECT bNumber, bName from bibleRef WHERE",
                    " bbName=", PCommon.AQ(bbName),
                    " AND bName like ", PCommon.AQ(PCommon.RQ(searchString)),
                    " ORDER BY bName ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                r = new BibleRefBO();
                r.bNumber = c.getInt(0);
                r.bName = c.getString(1);

                lst.add(r);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lst;
    }

    /***
     * Get list of my articles Id
     * @return list of articles Id
     */
    @SuppressWarnings("JavaDoc")
    String[] GetListMyArticlesId()
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<String> arr = new ArrayList<>();

        try
        {
            ArtDescBO r;

            sql = PCommon.ConcaT("SELECT artId from artDesc ORDER BY artUpdatedDt DESC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                r = new ArtDescBO();
                r.artId = c.getInt(0);

                arr.add(String.valueOf(r.artId));

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        final String[] lst = arr.toArray(new String[0]);
        return lst;
    }

    /***
     * Get my article name
     * @param artId
     * @return article name
     */
    @SuppressWarnings("JavaDoc")
    String GetMyArticleName(final int artId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        String artDesc = "";

        try
        {
            sql = PCommon.ConcaT("SELECT artTitle from artDesc WHERE artId=", artId);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                artDesc = c.getString(0);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return artDesc;
    }

    /***
     * Get my article source
     * @param artId
     * @return article source
     */
    @SuppressWarnings("JavaDoc")
    String GetMyArticleSource(final int artId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        String artSrc = "";

        try
        {
            sql = PCommon.ConcaT("SELECT artSrc from artDesc WHERE artId=", artId);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                artSrc = c.getString(0);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return artSrc;
    }

    /***
     * Get new MyArticle Id
     * @return bibleId
     */
    @SuppressWarnings("JavaDoc")
    int GetNewMyArticleId()
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int max = 0;

        try
        {
            sql = PCommon.ConcaT("SELECT MAX(id) from artDesc");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                max = c.getInt(0);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return max + 1;
    }

    /***
     * Update my article source
     * @param artId         Article Id
     * @param substSource   Source substitued
     */
    @SuppressWarnings("JavaDoc")
    void UpdateMyArticleSource(final int artId, final String substSource)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            final String quotedSource = PCommon.AQ(PCommon.RQ(substSource));

            sql = PCommon.ConcaT("UPDATE artDesc SET artSrc=", quotedSource, " WHERE artId=", artId);
            _db.execSQL(sql);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Add my article
     * @param ad    Article description
     */
    @SuppressWarnings("JavaDoc")
    void AddMyArticle(final ArtDescBO ad)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("INSERT INTO artDesc (artId, artUpdatedDt, artTitle, artSrc) VALUES (",
                ad.artId, ",",
                PCommon.AQ(ad.artUpdatedDt), ",",
                PCommon.AQ(PCommon.RQ(ad.artTitle)), ",",
                PCommon.AQ(PCommon.RQ(ad.artSrc)),
                ")");

            _db.execSQL(sql);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Delete my article
     * @param artId         Article Id
     */
    @SuppressWarnings("JavaDoc")
    void DeleteMyArticle(final int artId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("DELETE FROM artDesc WHERE artId=", artId);
            _db.execSQL(sql);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Get list of books by name
     * @param bbName
     * @return list all books
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<BibleRefBO> GetListAllBookByName(final String bbName)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<BibleRefBO> lst = new ArrayList<>();

        try
        {
            BibleRefBO r;

            sql = PCommon.ConcaT("SELECT bNumber, bName, bsName from bibleRef WHERE",
                    " bbName=", PCommon.AQ(bbName),
                    " ORDER BY bNumber ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                r = new BibleRefBO();
                r.bNumber = c.getInt(0);
                r.bName = c.getString(1);
                r.bsName = c.getString(2);

                lst.add(r);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lst;
    }

    /***
     * Get book number by name
     * @param bbName
     * @param bName
     * @return book number (0 if not found)
     */
    @SuppressWarnings("JavaDoc")
    int GetBookNumberByName(final String bbName, final String bName)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int bNumber = 0;

        try
        {
            sql = PCommon.ConcaT("SELECT bNumber from bibleRef WHERE",
                    " bbName=", PCommon.AQ(bbName),
                    " AND bName=", PCommon.AQ(PCommon.RQ(bName)),
                    " LIMIT 1");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                bNumber = c.getInt(0);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return bNumber;
    }

    /***
     * Get book number by name
     * @param bName
     * @return book number (0 if not found)
     */
    @SuppressWarnings("JavaDoc")
    int GetBookNumberByName(final String bName)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int bNumber = 0;

        try
        {
            sql = PCommon.ConcaT("SELECT bNumber from bibleRef WHERE",
                    " bName=", PCommon.AQ(PCommon.RQ(bName)),
                    " LIMIT 1");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                bNumber = c.getInt(0);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return bNumber;
    }

    /***
     * Get book ref
     * @param bbName
     * @param bNumber
     * @return book ref
     */
    @SuppressWarnings("JavaDoc")
    BibleRefBO GetBookRef(final String bbName, final int bNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        BibleRefBO ref = null;

        try
        {
            sql = PCommon.ConcaT("SELECT bName, bsName from bibleRef WHERE",
                    " bbName=", PCommon.AQ(bbName),
                    " AND bNumber=", bNumber);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                ref = new BibleRefBO();
                ref.bbName = bbName;
                ref.bNumber = bNumber;
                ref.bName = c.getString(0);
                ref.bsName = c.getString(1);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return ref;
    }

    /***
     * Get cache tab
     * @param tabId
     */
    @SuppressWarnings("JavaDoc")
    CacheTabBO GetCacheTab(final int tabId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        CacheTabBO t = null;
        Cursor c = null;

        try
        {
            sql = PCommon.ConcaT("SELECT tabType, tabTitle, fullQuery, scrollPosY, bbName, isBook, isChapter, isVerse, bNumber, cNumber, vNumber, trad from cacheTab WHERE tabId=", tabId);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                t = new CacheTabBO();
                t.tabNumber = tabId;
                t.tabType = c.getString(0);
                t.tabTitle = c.getString(1);
                t.fullQuery = c.getString(2);
                t.scrollPosY = c.getInt(3);
                t.bbName = c.getString(4);
                t.isBook = (c.getInt(5) == 1);
                t.isChapter =(c.getInt(6) == 1);
                t.isVerse = (c.getInt(7) == 1);
                t.bNumber = c.getInt(8);
                t.cNumber = c.getInt(9);
                t.vNumber = c.getInt(10);
                t.trad = c.getString(11);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return t;
    }

    /***
     * Get cache tab Fav
     */
    CacheTabBO GetCacheTabFav()
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        CacheTabBO t = null;
        Cursor c = null;

        try
        {
            sql = PCommon.ConcaT("SELECT tabId, tabType, tabTitle, fullQuery, scrollPosY, bbName, isBook, isChapter, isVerse, bNumber, cNumber, vNumber, trad from cacheTab WHERE tabType='F'");
            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                t = new CacheTabBO();
                t.tabNumber = c.getInt(0);
                t.tabType = c.getString(1);
                t.tabTitle = c.getString(2);
                t.fullQuery = c.getString(3);
                t.scrollPosY = c.getInt(4);
                t.bbName = c.getString(5);
                t.isBook = (c.getInt(6) == 1);
                t.isChapter = (c.getInt(7) == 1);
                t.isVerse = (c.getInt(8) == 1);
                t.bNumber = c.getInt(9);
                t.cNumber = c.getInt(10);
                t.vNumber = c.getInt(11);
                t.trad = c.getString(12);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return t;
    }

    /***
     * Get cache tab title
     * @param tabId
     */
    @SuppressWarnings("JavaDoc")
    String GetCacheTabTitle(final int tabId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        String title = null;
        Cursor c = null;

        try
        {
            sql = PCommon.ConcaT("SELECT tabTitle from cacheTab WHERE tabId=", tabId);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                title = c.getString(0);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return title;
    }

    /***
     * Save cache tab (insert or replace)
     * @param t
     */
    @SuppressWarnings("JavaDoc")
    void SaveCacheTab(final CacheTabBO t)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("INSERT OR REPLACE INTO cacheTab (tabId, tabType, tabTitle, fullQuery, scrollPosY, bbName, isBook, isChapter, isVerse, bNumber, cNumber, vNumber, trad) VALUES (",
                    t.tabNumber, ",",
                    PCommon.AQ(PCommon.RQ(t.tabType)), ",",
                    PCommon.AQ(PCommon.RQ(t.tabTitle)), ",",
                    PCommon.AQ(PCommon.RQ(t.fullQuery)), ",",
                    t.scrollPosY, ",",
                    PCommon.AQ(PCommon.RQ(t.bbName)), ",",
                    (t.isBook) ? "1" : "0", ",",
                    (t.isChapter) ? "1" : "0", ",",
                    (t.isVerse) ? "1" : "0", ",",
                    t.bNumber, ",",
                    t.cNumber, ",",
                    t.vNumber, ",",
                    PCommon.AQ(t.trad), ")" );

            //_db.beginTransaction();
            _db.execSQL(sql);
            //_db.setTransactionSuccessful();
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Save cache tab fav (delete and insert), should be unique
     * @param t
     */
    @SuppressWarnings("JavaDoc")
    void SaveCacheTabFav(final CacheTabBO t)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = "DELETE FROM cacheSearch WHERE tabId IN (SELECT tabId FROM cacheTab WHERE tabType='F')";
            _db.execSQL(sql);

            sql = "DELETE FROM cacheTab WHERE tabType='F'";
            _db.execSQL(sql);

            sql = PCommon.ConcaT("INSERT INTO cacheTab (tabId, tabType, tabTitle, fullQuery, scrollPosY, bbName, isBook, isChapter, isVerse, bNumber, cNumber, vNumber, trad) VALUES (",
                    t.tabNumber, ",",
                    PCommon.AQ("F"), ",",
                    PCommon.AQ(PCommon.RQ(t.tabTitle)), ",",
                    PCommon.AQ(PCommon.RQ(t.fullQuery)), ",",
                    t.scrollPosY, ",",
                    PCommon.AQ(PCommon.RQ(t.bbName)), ",",
                    (t.isBook) ? "1" : "0", ",",
                    (t.isChapter) ? "1" : "0", ",",
                    (t.isVerse) ? "1" : "0", ",",
                    t.bNumber, ",",
                    t.cNumber, ",",
                    t.vNumber, ",",
                    PCommon.AQ(t.trad), ")" );

            //_db.beginTransaction();
            _db.execSQL(sql);
            //_db.setTransactionSuccessful();
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Save cache search (before deleting data)
     * @param tabId
     * @param lstBibleId
     */
    @SuppressWarnings("JavaDoc")
    void SaveCacheSearch(final int tabId, final ArrayList<Integer> lstBibleId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            this.DeleteCacheSearch(tabId);

            if (lstBibleId == null) return;

            final String tmp = PCommon.ConcaT("INSERT INTO cacheSearch (tabId, bibleId) VALUES (", tabId, ",");
            for (int bibleId:lstBibleId)
            {
                sql = PCommon.ConcaT(tmp, bibleId, ")");
                _db.execSQL(sql);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Copy cache search for other bible (data are deleted before copying)
     * @param tabIdFrom
     * @param tabIdTo
     * @param tbbName
     * @return true if copy was successful
     */
    @SuppressWarnings("JavaDoc")
    boolean CopyCacheSearchForOtherBible(final int tabIdFrom, final int tabIdTo, final String tbbName)
    {
        @SuppressWarnings("UnusedAssignment") boolean insert = false;

        try
        {
            insert = this.AddCacheSearch(tabIdFrom, tabIdTo, tbbName);

            return insert;
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);

            return false;
        }
    }

    /***
     * Add cache search for other bible (data are deleted before copying)
     * @param tabIdFrom
     * @param tabIdTo
     * @param tbbName
     * @return true if copy was successful
     */
    @SuppressWarnings("JavaDoc")
    private boolean AddCacheSearch(final int tabIdFrom, final int tabIdTo, final String tbbName)
    {
        try
        {
            this.DeleteCacheSearch(tabIdTo);

            String sql, bbnameTo;
            int size = tbbName.length();

            for (int i = 0; i < size; i++)
            {
                bbnameTo = tbbName.substring(i, i+1);

                sql = PCommon.ConcaT(
                        "INSERT INTO cacheSearch (tabId, bibleId) ",
                        "SELECT ", tabIdTo ,", (SELECT i.id FROM bible i WHERE i.bbName=", PCommon.AQ(bbnameTo)," AND i.bNumber=r.bNumber AND i.cNumber=r.cNumber AND i.vNumber=r.vNumber) ",
                        "FROM (",
                        " SELECT distinct b.bNumber, b.cNumber, b.vNumber",
                        " FROM cacheSearch s",
                        " INNER JOIN bible b ON b.Id=s.bibleId",
                        " WHERE s.tabId=", tabIdFrom,
                        " ORDER BY b.bNumber, b.cNumber, b.vNumber ASC) r" );

                _db.execSQL(sql);
            }

            return true;
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);

            return false;
        }
    }

    /***
     * Get result (all) text
     * @param tbbName
     * @param tabIdFrom
     * @param tabIdTo
     * @return text of all
     */
    @SuppressWarnings("JavaDoc")
    String GetResultText(final int tabIdFrom, final int tabIdTo, final String tbbName)
    {
        Cursor c = null;
        String text = "";
        StringBuilder sb = new StringBuilder();

        try
        {
            final boolean isCopySuccessful = this.CopyCacheSearchForOtherBible(tabIdFrom, tabIdTo, tbbName);
            if (!isCopySuccessful) return text;

            final String sql = PCommon.ConcaT("SELECT r.bsName || ' ' || b.cNumber || '.' || b.vNumber || ': ' || b.vText ",
                    "FROM cacheSearch s ",
                    "INNER JOIN bible b ON b.Id=s.bibleId ",
                    "INNER JOIN bibleRef r ON r.bbName=b.bbName AND r.bNumber=b.bNumber ",
                    "WHERE s.tabId=", tabIdTo, " ORDER BY b.bNumber, b.cNumber, b.vNumber ASC");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            final String nl = "\n\n";
            while (!c.isAfterLast())
            {
                sb.append(c.getString(0));
                sb.append(nl);

                c.moveToNext();
            }
            text = sb.toString();

            return text;
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);

            return "";
        }
        finally
        {
            //noinspection UnusedAssignment
            sb = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }
    }

    /***
     * Delete cache search
     * @param tabId
     */
    @SuppressWarnings("JavaDoc")
    private void DeleteCacheSearch(final int tabId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("DELETE FROM cacheSearch WHERE tabId=", tabId);
            _db.execSQL(sql);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Delete cache
     * @param tabId
     */
    @SuppressWarnings("JavaDoc")
    void DeleteCache(final int tabId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("DELETE FROM cacheSearch WHERE tabId=", tabId);
            _db.execSQL(sql);

            sql = PCommon.ConcaT("DELETE FROM cacheTab WHERE tabId=", tabId);
            _db.execSQL(sql);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Update cache Id
     * @param fromTabId
     * @param toTabId
     */
    @SuppressWarnings("JavaDoc")
    void UpdateCacheId(final int fromTabId, final int toTabId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("UPDATE cacheSearch SET tabId=", toTabId, " WHERE tabId=", fromTabId);
            _db.execSQL(sql);

            sql = PCommon.ConcaT("UPDATE cacheTab SET tabId=", toTabId, " WHERE tabId=", fromTabId);
            _db.execSQL(sql);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Get cache tab visible count
     */
    int GetCacheTabVisibleCount()
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int count = 0;
        int max = 0;
        int res = 0;

        try {
            sql = PCommon.ConcaT("SELECT MAX(tabId) FROM cacheTab WHERE tabId >= 0");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                max = c.getInt(0);
            }

            sql = PCommon.ConcaT("SELECT COUNT(*) FROM cacheTab WHERE tabId >= 0");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                count = c.getInt(0);
            }

            if (count == 0)
            {
                res = 1;
            }
            else
            {
                if (max == 0)
                {
                    res = 1;
                }
                else
                {
                    res = max + 1;
                }
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return res;
    }

    /***
     * Save note
     * @param noteBO
     */
    @SuppressWarnings("JavaDoc")
    void SaveNote(final NoteBO noteBO)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("INSERT OR REPLACE INTO bibleNote (bNumber, cNumber, vNumber, changeDt, mark, note) VALUES(",
                    noteBO.bNumber, ",",
                    noteBO.cNumber, ",",
                    noteBO.vNumber, ",",
                    PCommon.AQ(noteBO.changeDt), ",",
                    noteBO.mark, ",",
                    PCommon.AQ(noteBO.note),
                    ")" );

            _db.execSQL(sql);
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Delete note
     * @param bNumber
     * @param cNumber
     * @param vNumber
     */
    @SuppressWarnings("JavaDoc")
    void DeleteNote(final int bNumber, final int cNumber, final int vNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("DELETE FROM bibleNote WHERE",
                    " bNumber=", bNumber,
                    " AND cNumber=", cNumber,
                    " AND vNumber=", vNumber);

            _db.execSQL(sql);
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Get bibleId min
     * @param bbName
     * @return bibleId
     */
    @SuppressWarnings("JavaDoc")
    int GetBibleIdMin(final String bbName)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int min = 0;

        try
        {
            sql = PCommon.ConcaT("SELECT MIN(id) from bible b WHERE b.bbName=", PCommon.AQ(bbName));

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                min = c.getInt(0);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return min;
    }

    /***
     * Get bibleId max
     * @param bbName
     * @return bibleId
     */
    @SuppressWarnings("JavaDoc")
    int GetBibleIdMax(final String bbName)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int max = 0;

        try
        {
            sql = PCommon.ConcaT("SELECT MAX(id) from bible b WHERE b.bbName=", PCommon.AQ(bbName));

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                max = c.getInt(0);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return max;
    }

    /***
     * Get Bible Id count
     * @return bibleId
     */
    @SuppressWarnings("JavaDoc")
    int GetBibleIdCount()
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int count = 0;

        try
        {
            sql = PCommon.ConcaT("SELECT COUNT(*) from bible");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                count = c.getInt(0);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return count;
    }


    /***
     * Get number of chapters in a book
     * @param bbName
     * @param bNumber
     * @return bibleId
     */
    @SuppressWarnings("JavaDoc")
    int GetBookChapterMax(@SuppressWarnings("SameParameterValue") final String bbName, final int bNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int max = -1;

        try
        {
            sql = PCommon.ConcaT("SELECT MAX(b.cNumber) from bible b WHERE b.bbName=", PCommon.AQ(bbName),
                    " AND b.bNumber=", bNumber);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                max = c.getInt(0);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return max;
    }

    /***
     * Does a book exist?
     * @param bbName
     * @param bNumber
     * @return true/false
     */
    @SuppressWarnings("JavaDoc")
    boolean IsBookExist(@SuppressWarnings("SameParameterValue") final String bbName, final int bNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;

        try
        {
            sql = PCommon.ConcaT("SELECT b.bNumber from bible b WHERE b.bbName=", PCommon.AQ(bbName),
                    " AND b.bNumber=", bNumber, " LIMIT 1");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                return true;
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return false;
    }

    /**
     * Construct CASE clause Bible
     * @param trad  TRAD
     * @param fld   Table field to case (ex: b.bbName)
     * @return string with CASE clause for bbNameOrder
     */
    private String CaseBible(@SuppressWarnings("SameParameterValue") final String fld, final String trad)
    {
        //EX: CASE b.bbName WHEN 'f' THEN 1 WHEN 'k' THEN 2 END bbNameOrder
        final int size = trad.length();
        final StringBuilder sb = new StringBuilder(PCommon.ConcaT("CASE ", fld));
        for (int i = 0; i < size; i++)
        {
            sb.append(" WHEN ");
            sb.append("'");
            sb.append(trad.charAt(i));
            sb.append("'");
            sb.append(" THEN ");
            sb.append(i+1);
        }
        sb.append(" END bbNameOrder");

        return sb.toString();
    }

    /**
     * Construct IN clause Bible with ( ).
     * @param str   Trad
     * @return string for IN clause
     * Rem: there is no check of the content, '". Works only for chars.
     */
    private String InBible(final String str)
    {
        final int size = str.length();
        if (size <= 1) return PCommon.ConcaT("('", str, "')");

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++)
        {
            if (sb.length() > 0) sb.append(",");
            sb.append("'");
            sb.append(str.charAt(i));
            sb.append("'");
        }
        sb.insert(0, "(");
        sb.append(")");

        return sb.toString();
    }

    /***
     * Get plan id max
     * @return Plan id max
     */
    int GetPlanDescIdMax()
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int max = 0;

        try
        {
            sql = PCommon.ConcaT("SELECT MAX(planId) from planDesc");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                max = c.getInt(0);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return max;
    }

    /***
     * Get bible chapter info by book
     * @param bNumber
     * @return chapter count, verse count of the book
     */
    @SuppressWarnings("JavaDoc")
    Integer[] GetBibleCiByBook(final int bNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        final Integer[] ci = { 0, 0 };

        try
        {
            //cCount, vCount
            sql = PCommon.ConcaT("SELECT COUNT(b.cNumber), SUM(b.vCount) from bibleCi b WHERE b.bNumber=", bNumber,
                    " GROUP BY b.bNumber");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                ci[ 0 ] = c.getInt(0);
                ci[ 1 ] = c.getInt(1);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return ci;
    }

    /***
     * Get start id of a book in King James
     * @param bNumber
     * @return First Id of a book, -1 in case of error
     */
    @SuppressWarnings("JavaDoc")
    private int GetBookMinId(final int bNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int id = -1;

        try
        {
            sql = PCommon.ConcaT("SELECT MIN(id) FROM bible WHERE bbName='k' AND bNumber=", bNumber);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                id = c.getInt(0);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return id;
    }

    /***
     * Get End id of a book in King James
     * @param bNumber
     * @return Max Id of a book, -1 in case of error
     */
    @SuppressWarnings("JavaDoc")
    private int GetBookMaxId(final int bNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int id = -1;

        try
        {
            sql = PCommon.ConcaT("SELECT MAX(id) FROM bible WHERE bbName='k' AND bNumber=", bNumber);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                id = c.getInt(0);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return id;
    }

    /***
     * Delete a plan
     * @param planId    Plan Id
     */
    void DeletePlan(final int planId)
    {
        String sql;

        try
        {
            sql = PCommon.ConcaT("DELETE FROM planCal WHERE planId=", planId);
            _db.execSQL(sql);

            sql = PCommon.ConcaT("DELETE FROM planDesc WHERE planId=", planId);
            _db.execSQL(sql);
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Create plan calendar
     * @param pd    Plan description
     * @param strBookNumbers List of book numbers
     */
    void AddPlan(final PlanDescBO pd, final String strBookNumbers)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            final String insertSqlCal = "INSERT INTO planCal (planId, dayNumber, dayDt, isRead, bNumberStart, cNumberStart, vNumberStart, bNumberEnd, cNumberEnd, vNumberEnd) VALUES (";

            //Delete
            this.DeletePlan(pd.planId);

            //Add Desc
            sql = PCommon.ConcaT("INSERT INTO planDesc (planId, planRef, startDt, endDt, bCount, cCount, vCount, vDayCount, dayCount) VALUES (",
                        pd.planId, ",",
                        PCommon.AQ(pd.planRef), ",",
                        PCommon.AQ(pd.startDt), ",",
                        PCommon.AQ(pd.endDt), ",",
                        pd.bCount, ",",
                        pd.cCount, ",",
                        pd.vCount, ",",
                        pd.vDayCount, ",",
                        pd.dayCount,
                    ")");
            _db.execSQL(sql);

            //Plan Calendar
            @SuppressWarnings("UnusedAssignment") int bNumberStart = 0, cNumberStart = 0, vNumberStart = 0;
            @SuppressWarnings("UnusedAssignment") int bNumberEnd = 0, cNumberEnd = 0, vNumberEnd = 0;
            int dayNumber = 1;
            boolean shouldBreak = false;

            final String[] books = strBookNumbers.split(",");
            final int firstbNumber = Integer.parseInt( books[0] );
            final int lastbNumber = (books.length > 1) ? Integer.parseInt(books[ books.length - 1]) : firstbNumber;
            final int idMin = this.GetBookMinId(firstbNumber);
            final int idMax = this.GetBookMaxId(lastbNumber);
            final int dCount = pd.vDayCount - 1;
            final String dtFormat = "yyyyMMdd";
            final Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(pd.startDt.substring(0, 4)),
                    Integer.parseInt(pd.startDt.substring(4, 6)) - 1,
                    Integer.parseInt(pd.startDt.substring(6, 8)));
            String startDtStr;
            int idCurrent = idMin;
            VerseBO v;

            while(true)
            {
                //Start
                v = this.GetVerse(idCurrent);
                if (idCurrent > idMax)
                {
                    break;
                }
                bNumberStart = v.bNumber;
                cNumberStart = v.cNumber;
                vNumberStart = v.vNumber;

                //End
                idCurrent += dCount;
                if (idCurrent > idMax)
                {
                    shouldBreak = true;
                    idCurrent = idMax;
                }

                v = this.GetVerse(idCurrent);
                bNumberEnd = v.bNumber;
                cNumberEnd = v.cNumber;
                vNumberEnd = v.vNumber;

                //Add Calendar
                startDtStr = PCommon.AQ(DateFormat.format(dtFormat, cal).toString());
                sql = PCommon.ConcaT(insertSqlCal,
                        pd.planId, ",",
                        dayNumber, ",",
                        startDtStr, ",",
                        0, ",",
                        bNumberStart, ",",
                        cNumberStart, ",",
                        vNumberStart, ",",
                        bNumberEnd, ",",
                        cNumberEnd, ",",
                        vNumberEnd,
                        ")");
                _db.execSQL(sql);
                //noinspection UnusedAssignment
                sql = null;
                //noinspection UnusedAssignment
                startDtStr = null;

                if (shouldBreak) break;

                dayNumber++;
                idCurrent++;
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Get all plan descriptions
     * @return List of all plan descriptions
     */
    ArrayList<PlanDescBO> GetAllPlanDesc()
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<PlanDescBO> lstPd = new ArrayList<>();

        try
        {
            PlanDescBO pd;
            sql = PCommon.ConcaT("SELECT d.planId, d.planRef, d.startDt, d.endDt, d.bCount, d.cCount, d.vCount, d.vDayCount, d.dayCount, ",
                                "(SELECT MIN(c.bNumberStart) FROM planCal c WHERE c.planId = d.planId) firstbNumberStart ",
                                "FROM planDesc d ",
                                "ORDER BY firstbNumberStart DESC, d.bCount ASC");
            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                pd = new PlanDescBO();
                pd.planId = c.getInt(0);
                pd.planRef = c.getString(1);
                pd.startDt = c.getString(2);
                pd.endDt = c.getString(3);
                pd.bCount = c.getInt(4);
                pd.cCount = c.getInt(5);
                pd.vCount = c.getInt(6);
                pd.vDayCount = c.getInt(7);
                pd.dayCount = c.getInt(8);
                lstPd.add(pd);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lstPd;
    }

    /***
     * Get a plan desc
     * @return Plan description
     */
    PlanDescBO GetPlanDesc(final int planId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        PlanDescBO pd = null;

        try
        {
            sql = PCommon.ConcaT("SELECT planId, planRef, startDt, endDt, bCount, cCount, vCount, vDayCount, dayCount ",
                    "FROM planDesc ",
                    "WHERE planId=", planId);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                pd = new PlanDescBO();
                pd.planId = c.getInt(0);
                pd.planRef = c.getString(1);
                pd.startDt = c.getString(2);
                pd.endDt = c.getString(3);
                pd.bCount = c.getInt(4);
                pd.cCount = c.getInt(5);
                pd.vCount = c.getInt(6);
                pd.vDayCount = c.getInt(7);
                pd.dayCount = c.getInt(8);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return pd;
    }

    /***
     * Is a specific plan exist?
     * @param planRef
     * @return true/false
     */
    @SuppressWarnings("JavaDoc")
    boolean IsPlanDescExist(final String planRef)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;

        try
        {
            sql = PCommon.ConcaT("SELECT planId from planDesc WHERE planRef=", PCommon.AQ(planRef),
                    " LIMIT 1");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                return true;
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null) {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return false;
    }

    /***
     * Get plan calendar
     * @param bbName    Bible name
     * @param planId    Plan Id
     * @param pageNr    Page number
     * @return list of days
     */
    ArrayList<PlanCalBO> GetPlanCal(final String bbName, final int planId, final int pageNr)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        ArrayList<PlanCalBO> lst = new ArrayList<>();

        try
        {
            PlanCalBO pc;

            sql = PCommon.ConcaT("SELECT c.planId, c.dayNumber, c.dayDt, c.isRead,",
                                " c.bNumberStart, c.cNumberStart, c.vNumberStart, c.bNumberEnd, c.cNumberEnd, c.vNumberEnd,",
                                " rs.bsName, re.bsName",
                                " FROM planCal c",
                                " INNER JOIN bibleRef rs ON rs.bbName=", PCommon.AQ(bbName), " AND c.bNumberStart=rs.bNumber",
                                " INNER JOIN bibleRef re ON rs.bbName=", PCommon.AQ(bbName), " AND c.bNumberEnd=re.bNumber",
                                " WHERE c.planId=", planId, " AND rs.bbName=", PCommon.AQ(bbName), " AND re.bbName=", PCommon.AQ(bbName),
                                " ORDER BY c.dayNumber",
                                " LIMIT 31 OFFSET ", pageNr * 31);

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            while (!c.isAfterLast())
            {
                pc = new PlanCalBO();
                pc.planId = c.getInt(0);
                pc.dayNumber = c.getInt(1);
                pc.dayDt = c.getString(2);
                if (pc.dayDt == null)
                {
                    pc.dayDt = "";
                }
                else if (pc.dayDt.length() == 8)
                {
                    pc.dayDt = PCommon.ConcaT(pc.dayDt.substring(2, 4), "/", pc.dayDt.substring(4, 6), "/", pc.dayDt.substring(6, 8));
                }
                pc.isRead = c.getInt(3);
                pc.bNumberStart = c.getInt(4);
                pc.cNumberStart = c.getInt(5);
                pc.vNumberStart = c.getInt(6);
                pc.bNumberEnd = c.getInt(7);
                pc.cNumberEnd = c.getInt(8);
                pc.vNumberEnd = c.getInt(9);
                pc.bsNameStart = c.getString(10);
                pc.bsNameEnd = c.getString(11);
                lst.add(pc);

                c.moveToNext();
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return lst;
    }

    /***
     * Get current day number of a plan calendar (for today)
     * @param planId
     * @return day number (0 if not found)
     */
    @SuppressWarnings("JavaDoc")
    int GetCurrentDayNumberOfPlanCal(final int planId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int dayNumber = 0;
        final String dtFormat = "yyyyMMdd";

        try
        {
            final Calendar now = Calendar.getInstance();
            final String dayDt = DateFormat.format(dtFormat, now).toString();

            sql = PCommon.ConcaT("SELECT c.dayNumber ",
                    " FROM planCal c",
                    " WHERE c.planId=", planId, " AND c.dayDt=", PCommon.AQ(dayDt), " limit 1");

            c = _db.rawQuery(sql, null);
            if (c.getCount() > 0)
            {
                c.moveToFirst();
                if (!c.isAfterLast())
                {
                    dayNumber = c.getInt(0);
                }
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return dayNumber;
    }

    /***
     * Get plan calendar of day
     * @param bbName    Bible name
     * @param planId    Plan Id
     * @param dayNumber Day number
     * @return Plan cal of day
     */
    PlanCalBO GetPlanCalByDay(final String bbName, final int planId, final int dayNumber)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        PlanCalBO pc = null;

        try
        {
            sql = PCommon.ConcaT("SELECT c.planId, c.dayNumber, c.dayDt, c.isRead,",
                    " c.bNumberStart, c.cNumberStart, c.vNumberStart, c.bNumberEnd, c.cNumberEnd, c.vNumberEnd,",
                    " rs.bsName, re.bsName",
                    " FROM planCal c",
                    " INNER JOIN bibleRef rs ON rs.bbName=", PCommon.AQ(bbName), " AND c.bNumberStart=rs.bNumber",
                    " INNER JOIN bibleRef re ON rs.bbName=", PCommon.AQ(bbName), " AND c.bNumberEnd=re.bNumber",
                    " WHERE c.planId=", planId, " AND c.dayNumber=", dayNumber, " AND rs.bbName=", PCommon.AQ(bbName), " AND re.bbName=", PCommon.AQ(bbName));

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                pc = new PlanCalBO();
                pc.planId = c.getInt(0);
                pc.dayNumber = c.getInt(1);
                pc.dayDt = c.getString(2);
                pc.isRead = c.getInt(3);
                pc.bNumberStart = c.getInt(4);
                pc.cNumberStart = c.getInt(5);
                pc.vNumberStart = c.getInt(6);
                pc.bNumberEnd = c.getInt(7);
                pc.cNumberEnd = c.getInt(8);
                pc.vNumberEnd = c.getInt(9);
                pc.bsNameStart = c.getString(10);
                pc.bsNameEnd = c.getString(11);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return pc;
    }

    /***
     * Get plan calendar row count of a plan
     * @param bbName    Bible name
     * @param planId    Plan Id
     * @return Row count for this calendar
     */
    int GetPlanCalRowCount(final String bbName, final int planId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;

        try
        {
            sql = PCommon.ConcaT("SELECT COUNT(*)",
                    " FROM planCal c",
                    " INNER JOIN bibleRef rs ON rs.bbName=", PCommon.AQ(bbName), " AND c.bNumberStart=rs.bNumber",
                    " INNER JOIN bibleRef re ON rs.bbName=", PCommon.AQ(bbName), " AND c.bNumberEnd=re.bNumber",
                    " WHERE c.planId=", planId, " AND rs.bbName=", PCommon.AQ(bbName), " AND re.bbName=", PCommon.AQ(bbName),
                    " ORDER BY c.dayNumber");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                return c.getInt(0);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return 0;
    }

    /***
     * Get number of days read
     * @param planId
     * @return Count of days read
     */
    @SuppressWarnings("JavaDoc")
    int GetPlanCalDaysReadCount(final int planId)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;
        Cursor c = null;
        int count = 0;

        try
        {
            sql = PCommon.ConcaT("SELECT COUNT(*) ",
                    " FROM planCal c",
                    " WHERE c.planId=", planId, " AND c.isRead=1",
                    " GROUP BY c.isRead");

            c = _db.rawQuery(sql, null);
            c.moveToFirst();

            if (!c.isAfterLast())
            {
                count = c.getInt(0);
            }
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;

            if (c != null)
            {
                c.close();
                //noinspection UnusedAssignment
                c = null;
            }
        }

        return count;
    }

    /***
     * Mark plan calendar of day
     * @param planId    Plan Id
     * @param dayNumber Day number
     * @param isRead    Is read
     */
    void MarkPlanCal(final int planId, final int dayNumber, final int isRead)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("UPDATE planCal SET isRead=", isRead," WHERE planId=", planId, " AND dayNumber=", dayNumber);

            _db.execSQL(sql);
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    /***
     * Mark plan calendar of day (all above)
     * @param planId    Plan Id
     * @param dayNumber Day number
     * @param isRead    Is read
     */
    void MarkAllAbovePlanCal(final int planId, final int dayNumber, final int isRead)
    {
        @SuppressWarnings("UnusedAssignment") String sql = null;

        try
        {
            sql = PCommon.ConcaT("UPDATE planCal SET isRead=", isRead," WHERE planId=", planId, " AND dayNumber<=", dayNumber);

            _db.execSQL(sql);
        }
        catch (SQLException ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            //noinspection UnusedAssignment
            sql = null;
        }
    }

    //</editor-fold>
}

//Works:
//select b1.bnumber, b1.cnumber, b1.vnumber, b1.vtext, (select vtext from bible b2 where b2.bbName='d' and b2.bnumber=b1.bnumber and b2.cnumber=b1.cnumber and b2.vnumber=b1.vnumber) vtext2 from bible b1 where b1.bnumber=1 and b1.cnumber=1 order by b1.bnumber asc, b1.cnumber asc, b1.vnumber asc, b1.bbname asc

