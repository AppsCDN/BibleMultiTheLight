
package org.hlwd.bible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/***
 * Singleton
 */
class SCommon
{
    //<editor-fold defaultstate="collapsed" desc="-- Variables --">

    @SuppressLint("StaticFieldLeak")
    private static SCommon uniqInstance = null;
    @SuppressLint("StaticFieldLeak")
    private static Dal _dal = null;
    @SuppressLint("StaticFieldLeak")
    private static Context _context = null;
    private static TtsManager ttsManager = null;
    private static Thread ttsThread = null;

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Constructor(s) --">

    private SCommon(Context ctx)
    {
        try
        {
            InitDbOpening(ctx);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(ctx, ex);
        }
    }

    //</editor-fold>

    static synchronized SCommon GetInstance(Context ctx)
    {
        if (_context == null)
        {
            _context = ctx.getApplicationContext();
        }

        if (uniqInstance == null)
        {
            uniqInstance = new SCommon(_context);
        }
        else
        {
            InitDbOpening(_context);
        }

        return uniqInstance;
    }

    //<editor-fold defaultstate="collapsed" desc="-- Open/Close/Utils --">

    private static void InitDbOpening(Context ctx)
    {
        try
        {
            if (_dal == null)
            {
                _dal = new Dal(ctx);
            }

            if (!_dal.IsDbOpen())
            {
                _dal.OpenReadWrite();
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * CloseDb db
     */
    void CloseDb()
    {
        try
        {
            if (_dal != null)
            {
                _dal.CloseDb();
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Shrink db
     */
    @SuppressWarnings("unused")
    private void ShrinkDb(final Context context)
    {
        try
        {
            _dal.ShrinkDb(context);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
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
            dbVersion = _dal.GetDbVersion();
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
     * Add a log (db should be open for writing)
     * @param msg
     */
    @SuppressWarnings("JavaDoc")
    void AddLog(final String msg)
    {
        try
        {
            _dal.AddLog(msg);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Delete all logs (db should be open for writing)
     */
    void DeleteAllLogs()
    {
        try
        {
            _dal.DeleteAllLogs();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="-- Bible --">

    /***
     * Get a verse
     * @param bibleId
     * @return verse
     */
    @SuppressWarnings("JavaDoc")
    VerseBO GetVerse(final int bibleId)
    {
        VerseBO verse = null;

        try
        {
            verse = _dal.GetVerse(bibleId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            lstVerse = _dal.GetVerse(tbbName, bNumber, cNumber, vNumber);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return lstVerse;
    }

    /***
     * Get list of verses
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
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            lstVerse = _dal.GetVerses(tbbName, bNumber, cNumber, vNumberFrom, vNumberTo);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        try
        {
            return _dal.CopyCacheSearchForOtherBible(tabIdTo, tbbName, bNumberStart, cNumberStart, vNumberStart, bNumberEnd, cNumberEnd, vNumberEnd);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return false;
    }

    /***
     * Get list of verses in Html
     * @param bbName
     * @param bNumber
     * @param cNumber
     * @param vNumberFrom
     * @param vNumberTo
     * @return list of verses
     */
    @SuppressWarnings("JavaDoc")
    String GetVersesHtml(final String bbName, final int bNumber, final int cNumber, final int vNumberFrom, final int vNumberTo)
    {
        final StringBuilder sbVerses = new StringBuilder("<blockquote>");

        try
        {
            final ArrayList<VerseBO> lstVerse = _dal.GetVerses(bbName, bNumber, cNumber, vNumberFrom, vNumberTo);

            for (VerseBO v : lstVerse)
            {
                sbVerses.append(PCommon.ConcaT("<b>", v.bName, " ", v.cNumber, ".", v.vNumber, ": </b><br>", v.vText, "<br><br>"));
            }

            if (lstVerse.size() > 0)
            {
                final int start = sbVerses.length() - 8;
                sbVerses.delete(start, sbVerses.length());
            }

            sbVerses.append("</blockquote>");
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return sbVerses.toString();
    }

    /***
     * Get verse text
     * @param tbbName
     * @param bNumber
     * @param cNumber
     * @param vNumber
     * @return text of verse
     */
    @SuppressWarnings("JavaDoc")
    String GetVerseText(final String tbbName, final int bNumber, final int cNumber, final int vNumber)
    {
        String text = "";

        try
        {
            text = _dal.GetVerseText(tbbName, bNumber, cNumber, vNumber);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return text;
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
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            lstVerse = _dal.GetChapterFromPos(tbbName, bNumber, cNumber, 1);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        String text = "";

        try
        {
            text = _dal.GetChapterText(tbbName, bNumber, cNumber);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return text;
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
        String text = "";

        try
        {
            text = _dal.GetResultText(tabIdFrom, tabIdTo, tbbName);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return text;
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
    ArrayList<VerseBO> SearchBible(final String bbName, final int bNumber, final int cNumber, final String searchString)
    {
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            lstVerse = _dal.SearchBible(bbName, bNumber, cNumber, searchString);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
    ArrayList<VerseBO> SearchBible(final String bbName, final int bNumber, final String searchString)
    {
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            lstVerse = _dal.SearchBible(bbName, bNumber, searchString);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
    ArrayList<VerseBO> SearchBible(final String bbName, final String searchString)
    {
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            lstVerse = _dal.SearchBible(bbName, searchString);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
    ArrayList<VerseBO> SearchNotes(final String bbName, final String searchString, final int orderBy, final String markType)
    {
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            lstVerse = _dal.SearchNotes(bbName, searchString, orderBy, markType);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return lstVerse;
    }

    /***
     * Get list book by name
     * @param bbName
     * @param searchString
     * @return list of books
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<BibleRefBO> GetListBookByName(final String bbName, final String searchString)
    {
        return _dal.GetListBookByName(bbName, searchString);
    }

    /***
     * Get list all book by name
     * @param bbName
     * @return list all books
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<BibleRefBO> GetListAllBookByName(final String bbName)
    {
        return _dal.GetListAllBookByName(bbName);
    }

    /***
     * Get list of my articles Id
     * @return list of articles Id
     */
    @SuppressWarnings("JavaDoc")
    String[] GetListMyArticlesId()
    {
        return _dal.GetListMyArticlesId();
    }

    /***
     * Get my article name
     * @param artId
     * @return article name
     */
    @SuppressWarnings("JavaDoc")
    String GetMyArticleName(final int artId)
    {
        return _dal.GetMyArticleName(artId);
    }

    /***
     * Get my article source
     * @param artId
     * @return article source
     */
    @SuppressWarnings("JavaDoc")
    String GetMyArticleSource(final int artId)
    {
        return _dal.GetMyArticleSource(artId);
    }

    /***
     * Get my article substitution
     * @param source    Source without substitution (except <R></R>)
     * @return  String with edit tags
     */
    @SuppressWarnings("JavaDoc")
    private String GetMyArticleSourceSubstition(final String source)
    {
        return source
                .replaceAll("</u></h1>", "</H>")
                .replaceAll("<h1><u>", "<H>")
                .replaceAll("</u></span>", "</HS>")
                .replaceAll("<br><span><u>", "<HS>");
    }

    /***
     * Get new MyArticle Id
     * @return new article Id
     */
    int GetNewMyArticleId()
    {
        return _dal.GetNewMyArticleId();
    }

    /***
     * Update my article source
     * @param artId
     * @param source
     */
    @SuppressWarnings("JavaDoc")
    void UpdateMyArticleSource(final int artId, final String source)
    {
        try
        {
            final String substSource = GetMyArticleSourceSubstition(source);
            _dal.UpdateMyArticleSource(artId, substSource);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Update my article title
     * @param artId     Article Id
     * @param title     Title
     */
    @SuppressWarnings("JavaDoc")
    void UpdateMyArticleTitle(final int artId, final String title)
    {
        try
        {
            _dal.UpdateMyArticleTitle(artId, title);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Add my article
     * @param ad    Article description
     */
    @SuppressWarnings("JavaDoc")
    void AddMyArticle(final ArtDescBO ad)
    {
        try
        {
            _dal.AddMyArticle(ad);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Delete my article
     * @param artId     Article Id
     */
    @SuppressWarnings("JavaDoc")
    void DeleteMyArticle(final int artId)
    {
        try
        {
            _dal.DeleteMyArticle(artId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Search bible (cache)
     * @param searchId
     * @return list of verses
     */
    @SuppressWarnings("JavaDoc")
    ArrayList<VerseBO> SearchBible(final int searchId)
    {
        ArrayList<VerseBO> lstVerse = new ArrayList<>();

        try
        {
            lstVerse = _dal.SearchBible(searchId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return lstVerse;
    }

    /***
     * Get tabId of an article
     * @param artName   ART_NAME
     * @return Negative value if not found
     */
    int GetArticleTabId(final String artName)
    {
        int tabId = -1;

        try
        {
            tabId = _dal.GetArticleTabId(artName);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return tabId;
    }

    /***
     * Get book number
     * @param bbName
     * @param bName
     * @return book number (0 if not found)
     */
    @SuppressWarnings("JavaDoc")
    int GetBookNumberByName(final String bbName, final String bName)
    {
        int bNumber = 0;

        try
        {
            bNumber = _dal.GetBookNumberByName(bbName, bName);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return bNumber;
    }

    /***
     * Get book number
     * @param bName
     * @return book number (0 if not found)
     */
    @SuppressWarnings("JavaDoc")
    int GetBookNumberByName(final String bName)
    {
        int bNumber = 0;

        try
        {
            bNumber = _dal.GetBookNumberByName(bName);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        return _dal.GetBookRef(bbName, bNumber);
    }

    /***
     * Get cache tab
     * @param tabId
     */
    @SuppressWarnings("JavaDoc")
    CacheTabBO GetCacheTab(final int tabId)
    {
        CacheTabBO t = null;

        try
        {
            t = _dal.GetCacheTab(tabId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return t;
    }

    /***
     * Get cache tab Fav
     */
    CacheTabBO GetCacheTabFav()
    {
        CacheTabBO t = null;

        try
        {
            t = _dal.GetCacheTabFav();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        String title = null;

        try
        {
            title = _dal.GetCacheTabTitle(tabId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return title;
    }

    /***
     * Save cache tab
     * @param t
     */
    @SuppressWarnings("JavaDoc")
    void SaveCacheTab(final CacheTabBO t)
    {
        try
        {
            _dal.SaveCacheTab(t);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Save cache tab fav
     * @param t
     */
    @SuppressWarnings("JavaDoc")
    void SaveCacheTabFav(final CacheTabBO t)
    {
        try
        {
            _dal.SaveCacheTabFav(t);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Get current cache tab
     * @return current cache
     */
    CacheTabBO GetCurrentCacheTab()
    {
        final int tabId = MainActivity.Tab.GetCurrentTabPosition();
        if (tabId < 0) return null;

        final CacheTabBO t = GetCacheTab(tabId);
        if (t == null) return null;

        return t;
    }

    /***
     * Save cache search
     */
    @SuppressWarnings("JavaDoc")
    void SaveCacheSearch(final ArrayList<Integer> lstBibleId)
    {
        try
        {
            final int tabId = MainActivity.Tab.GetCurrentTabPosition();

            _dal.SaveCacheSearch(tabId, lstBibleId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Copy cache search (deleting data) for other bible
     * @param tabIdFrom
     * @param tabIdTo
     * @param bbNameTo
     */
    @SuppressWarnings("JavaDoc")
    void CopyCacheSearchForOtherBible(final int tabIdFrom, final int tabIdTo, final String bbNameTo)
    {
        try
        {
            @SuppressWarnings({"UnusedAssignment", "unused"})
            final boolean res = _dal.CopyCacheSearchForOtherBible(tabIdFrom, tabIdTo, bbNameTo);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Get cache tab count visible
     */
    int GetCacheTabCount()
    {
        int count = 0;

        try
        {
            count = _dal.GetCacheTabVisibleCount();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return count;
    }

    /***
     * Delete cache
     * @param tabId
     */
    @SuppressWarnings("JavaDoc")
    void DeleteCache(final int tabId)
    {
        try
        {
            _dal.DeleteCache(tabId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        try
        {
            _dal.UpdateCacheId(fromTabId, toTabId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Save note
     * @param noteBO
     */
    @SuppressWarnings("JavaDoc")
    void SaveNote(final NoteBO noteBO)
    {
        try
        {
            _dal.SaveNote(noteBO);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        try
        {
            _dal.DeleteNote(bNumber, cNumber, vNumber);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        int min = 0;

        try
        {
            min = _dal.GetBibleIdMin(bbName);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        int max = 0;

        try
        {
            max = _dal.GetBibleIdMax(bbName);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return max;
    }

    /***
     * Get bibleId count
     * @return count
     */
    @SuppressWarnings("JavaDoc")
    int GetBibleIdCount()
    {
        int count = 0;

        try
        {
            count = _dal.GetBibleIdCount();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return count;
    }

    /***
     * Get number of chapters in a book
     * @param bNumber
     * @return chapter count
     */
    @SuppressWarnings("JavaDoc")
    int GetBookChapterMax(final int bNumber)
    {
        int max = -1;

        try
        {
            max = _dal.GetBookChapterMax("k", bNumber);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return max;
    }

    /***
     * Does a book exist?
     * @param bNumber
     * @return true/false
     */
    @SuppressWarnings("JavaDoc")
    boolean IsBookExist(final int bNumber)
    {
        boolean status = false;

        try
        {
            status = _dal.IsBookExist("k", bNumber);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return status;
    }

    /***
     * Get plan id max
     * @return Plan id max
     */
    int GetPlanDescIdMax()
    {
        int max = 0;

        try
        {
            max = _dal.GetPlanDescIdMax();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        Integer[] ci = { 0, 0 };

        try
        {
            //cCount, vCount
            ci = _dal.GetBibleCiByBook(bNumber);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return ci;
    }

    /***
     * Delete a plan
     * @param planId    Plan Id
     */
    void DeletePlan(final int planId)
    {
        try
        {
            _dal.DeletePlan(planId);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Create a plan
     * @param pd    Plan description
     * @param strBookNumbers List of book numbers
     */
    void AddPlan(final PlanDescBO pd, final String strBookNumbers)
    {
        try
        {
            _dal.AddPlan(pd, strBookNumbers);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Get all plan descriptions
     * @return List of all plan descriptions
     */
    ArrayList<PlanDescBO> GetAllPlanDesc()
    {
        ArrayList<PlanDescBO> lst = new ArrayList<>();

        try
        {
            lst = _dal.GetAllPlanDesc();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return lst;
    }

    /***
     * Get a plan desc
     * @return Plan description
     */
    PlanDescBO GetPlanDesc(final int planId)
    {
        PlanDescBO pd = null;

        try
        {
            pd = _dal.GetPlanDesc(planId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return pd;
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
        ArrayList<PlanCalBO> lst = new ArrayList<>();

        try
        {
            lst = _dal.GetPlanCal(bbName, planId, pageNr);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return lst;
    }

    /***
     * Is a specific plan exist?
     * @param planRef
     * @return true/false
     */
    @SuppressWarnings("JavaDoc")
    boolean IsPlanDescExist(final String planRef)
    {
        boolean status = false;

        try
        {
            status = _dal.IsPlanDescExist(planRef);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return status;
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
        PlanCalBO pc = null;

        try
        {
            pc = _dal.GetPlanCalByDay(bbName, planId, dayNumber);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        int count = 0;

        try
        {
            count = _dal.GetPlanCalRowCount(bbName, planId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return count;
    }

    /***
     * Get current day number of a plan calendar (for today)
     * @param planId
     * @return day number (0 if not found)
     */
    @SuppressWarnings("JavaDoc")
    int GetCurrentDayNumberOfPlanCal(final int planId)
    {
        int dayNumber = 0;

        try
        {
            dayNumber = _dal.GetCurrentDayNumberOfPlanCal(planId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return dayNumber;
    }

    /***
     * Mark plan calendar of day
     * @param planId    Plan Id
     * @param dayNumber Day number
     * @param isRead    Is read
     */
    void MarkPlanCal(final int planId, final int dayNumber, final int isRead)
    {
        try
        {
            _dal.MarkPlanCal(planId, dayNumber, isRead);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        try
        {
            _dal.MarkAllAbovePlanCal(planId, dayNumber, isRead);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Get plan calendar progress status
     * @param planId    Plan Id
     * @return Progress status as html code
     */
    String GetPlanCalProgressStatus(final int planId)
    {
        String pgrStatus = "";

        try
        {
            final PlanDescBO pd = _dal.GetPlanDesc(planId);
            final int daysCount = pd.dayCount;
            final int daysRead = _dal.GetPlanCalDaysReadCount(planId);
            final int perc = (daysRead * 100) / daysCount;
            if (perc != 100)
            {
                final String sign;
                final int diffDays;
                final int currentDayNumber = _dal.GetCurrentDayNumberOfPlanCal(planId);
                if (currentDayNumber == 0)
                {
                    final String dtFormat = "yyyyMMdd";
                    final Calendar now = Calendar.getInstance();
                    final String dayNow = DateFormat.format(dtFormat, now).toString();
                    if (dayNow.compareTo(pd.startDt) < 0)
                    {
                        sign = "+";
                        diffDays = daysRead;
                    }
                    else
                    {
                        sign = "-";
                        diffDays = daysCount - daysRead;
                    }
                }
                else
                {
                    sign = (daysRead > currentDayNumber) ? "+" : (daysRead < currentDayNumber) ? "-" : "";
                    diffDays = Math.abs(daysRead - currentDayNumber);
                }
                final String verboseDays = diffDays > 1 ? _context.getString(R.string.planDaysSymbol) : _context.getString(R.string.planDaySymbol);
                final String verboseLate = sign.compareTo("+") == 0 ? PCommon.ConcaT("&nbsp;", _context.getString(R.string.planDayEarlySymbol))
                                         : sign.compareTo("-") == 0 ? PCommon.ConcaT("&nbsp;", _context.getString(R.string.planDayLateSymbol))
                                         : PCommon.ConcaT("&nbsp;", _context.getString(R.string.planDayLateSymbol));

                pgrStatus = PCommon.ConcaT("<blockquote>&nbsp;", perc, "%&nbsp;&nbsp;<small>(", daysRead, "/", daysCount,")</small><br>&nbsp;", diffDays, "&nbsp;", verboseDays, verboseLate, "</blockquote>");
            }
            else
            {
                final String planCompleted = _context.getString(R.string.planCompleted);
                pgrStatus = PCommon.ConcaT("<blockquote>&nbsp;", perc, "%&nbsp;&nbsp;<small>(", daysRead, "/", daysCount,")</small><br>&nbsp;", planCompleted, "</blockquote>");
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return pgrStatus;
    }

/*
    void SayVerse(final String bbName, final int bNumber, final int cNumber, final int vNumber)
    {
        try
        {
            final String msg = _dal.GetVerseTextOnly(bbName, bNumber, cNumber, vNumber);
            final Locale locale = GetLocale(bbName);
            if (locale == null)
            {
                //TODO NEXT: not supported locale
                PCommon.ShowToast(_context, "Language unavailable!", Toast.LENGTH_SHORT);
                return;
            }

            Say(locale, msg);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }
*/


    /**
     * Say chapter
     * @param bbName       Bible name
     * @param bNumber      Book number
     * @param cNumber      Chapter number
     * @param vNumberFrom  From Verse number
     */
    void Say(final String bbName, final int bNumber, final int cNumber, final int vNumberFrom)
    {
        //TODO TTS: STOP VIA UI IS NECESSARY BEFORE LISTEN CHAPTER
        try
        {
            final ThreadGroup threadGroup = new ThreadGroup(_context.getString(R.string.threadNfoGroup));
            final String threadName = PCommon.ConcaT(_context.getString(R.string.threadNfoPrefix), PCommon.TimeFuncShort());

            if (ttsManager != null)
            {
                ttsManager.ShutDown();

                ttsManager = null;
            }

            if (ttsThread != null)
            {
                ttsThread.interrupt();
            }

            ttsThread = new Thread(threadGroup, threadName)
            {
                @Override
                public void run()
                {
                    SayMain();
                }

                private void SayMain()
                {
                    try
                    {
                        final ArrayList<VerseBO> lstVerse = _dal.GetChapterFromPos(bbName, bNumber, cNumber, vNumberFrom);
                        final Locale locale = PCommon.GetLocale(_context, bbName);
                        if (locale == null)
                        {
                            //TODO NEXT: not supported language (DEBUG too)
                            PCommon.ShowToast(_context, "Language unavailable!", Toast.LENGTH_SHORT);
                        }
                        else
                        {
                            if (lstVerse != null)
                            {
                                if (lstVerse.size() > 0)
                                {
                                    ttsManager = new TtsManager(_context, locale);

                                    final boolean isReady = ttsManager.WaitForReady();
                                    if (!isReady)
                                    {
                                        throw new Exception("TTS not ready!");
                                    }

                                    for(final VerseBO verse : lstVerse)
                                    {
                                        ttsManager.SayAdd(verse.vText);
                                    }
                                }
                            }
                        }
                    }
                    catch(Exception ex)
                    {
                        if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
                    }
                }
            };
            ttsThread.setPriority(Thread.MIN_PRIORITY);
            ttsThread.start();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Shutdown TTS
     */
    void SayStop()
    {
        try
        {
            ttsManager.ShutDown();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            ttsManager = null;
        }
    }

    //</editor-fold>
}
