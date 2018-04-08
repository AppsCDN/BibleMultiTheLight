
package org.hlwd.bible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchFragment extends Fragment
{
    private Context _context = null;
    private SCommon _s = null;
    @SuppressLint("StaticFieldLeak")
    private static View v;
    private static FRAGMENT_TYPE fragmentType;
    private Button tvBookTitle;
    private TextView btnBack;
    private TextView btnForward;

    private SimpleCursorAdapter cursorAdapter;
    private MatrixCursor matrixCursor;
    private String searchFullQuery;
    private int searchFullQueryLimit = 3;
    private SearchView searchView = null;
    @SuppressLint("StaticFieldLeak")
    private static RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;

    private boolean isBook = false,  isChapter = false,  isVerse = false;
    private int     bNumber = 0,     cNumber = 0,        vNumber = 0,       scrollPosY = 0;
    private String  bbName = null, tabTitle = null, trad = null;

    private int planId = -1, planDayNumber = -1, planPageNumber = -1;

    enum FRAGMENT_TYPE
    {
        FAV_TYPE,
        SEARCH_TYPE,
        ARTICLE_TYPE,
        PLAN_TYPE
    }

    public SearchFragment()
    {
        fragmentType = FRAGMENT_TYPE.SEARCH_TYPE;
    }

    @SuppressLint("ValidFragment")
    public SearchFragment(final FRAGMENT_TYPE type)
    {
        fragmentType = type;
    }

    static int GetScrollPosY()
    {
        final LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (lm == null)
            return 0;

        return lm.findFirstVisibleItemPosition();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle bundle)
    {
        try
        {
            super.onCreateView(inflater, container, bundle);

            CheckLocalInstance();

            v = inflater.inflate(R.layout.fragment_search, container, false);
            setHasOptionsMenu(true);

            tvBookTitle = (Button) v.findViewById(R.id.tvBookTitle);
            btnBack = (TextView) v.findViewById(R.id.btnBack);
            btnForward = (TextView) v.findViewById(R.id.btnForward);
            btnBack.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    SetLocalBibleName();

                    if (fragmentType == FRAGMENT_TYPE.SEARCH_TYPE)
                    {
                        if (isBook && isChapter)
                        {
                            btnForward.setEnabled(true);

                            if (cNumber > 1)
                            {
                                scrollPosY = 0;
                                cNumber--;
                                searchFullQuery = PCommon.ConcaT(bNumber, " ", cNumber);
                                ShowChapter(bbName, bNumber, cNumber);
                            }
                            else
                            {
                                if (bNumber > 1)
                                {
                                    final int bNumberTry = bNumber - 1;
                                    final int cNumberTry = _s.GetBookChapterMax(bNumberTry);
                                    if (IsChapterExist(bbName, bNumberTry, cNumberTry))
                                    {
                                        scrollPosY = 0;
                                        bNumber = bNumberTry;
                                        cNumber = cNumberTry;
                                        searchFullQuery = PCommon.ConcaT(bNumber, " ", cNumber);
                                        ShowChapter(bbName, bNumber, cNumber);
                                    }
                                }
                                else
                                {
                                    btnBack.setEnabled(false);
                                }
                            }
                        }
                    }
                    else if (fragmentType == FRAGMENT_TYPE.PLAN_TYPE)
                    {
                        btnForward.setEnabled(true);
                        ShowPlan(-1);
                    }
                }
            });
            btnForward.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    SetLocalBibleName();

                    if (fragmentType == FRAGMENT_TYPE.SEARCH_TYPE)
                    {
                        if (isBook && isChapter)
                        {
                            btnBack.setEnabled(true);

                            int cNumberTry = cNumber + 1;
                            if (IsChapterExist(bbName, bNumber, cNumberTry))
                            {
                                scrollPosY = 0;
                                cNumber = cNumberTry;
                                searchFullQuery = PCommon.ConcaT(bNumber, " ", cNumber);
                                ShowChapter(bbName, bNumber, cNumber);
                            }
                            else
                            {
                                final int bNumberTry = bNumber + 1;
                                cNumberTry = 1;
                                if (IsChapterExist(bbName, bNumberTry, cNumberTry))
                                {
                                    scrollPosY = 0;
                                    bNumber = bNumberTry;
                                    cNumber = cNumberTry;
                                    searchFullQuery = PCommon.ConcaT(bNumber, " ", cNumber);
                                    ShowChapter(bbName, bNumber, cNumber);
                                }
                                else
                                {
                                    btnForward.setEnabled(false);
                                }
                            }
                        }
                    }
                    else if (fragmentType == FRAGMENT_TYPE.PLAN_TYPE)
                    {
                        btnBack.setEnabled(true);
                        ShowPlan(1);
                    }
                }
            });
            //TODO FAB: D-PAD LEFT TO DETECT
            //v.onKeyUp(KeyEvent.KEYCODE_DPAD_LEFT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT){
            //    int a = 5;
            //});

            if (fragmentType == FRAGMENT_TYPE.SEARCH_TYPE)
            {
                //Search book autocomplete
                matrixCursor = new MatrixCursor(new String[]{"_id", "text"});
                String[] from = {"text"};
                int[] to = {android.R.id.text1};                                                                         //android.R.ciId.text1,  R.ciId.listBnameEntry

                cursorAdapter = new SimpleCursorAdapter(_context, R.layout.book_entry, matrixCursor, from, to, SimpleCursorAdapter.NO_SELECTION); //android.R.layout.select_dialog_item,      R.layout.book_entry)
                cursorAdapter.setFilterQueryProvider(new FilterQueryProvider()
                {
                    @Override
                    public Cursor runQuery(CharSequence query)
                    {
                        if (PCommon._isDebugVersion) System.out.println("RunQuery: " + query);

                        return GetCursor(query);
                    }
                });
            }

            ShowBookTitle(false);   //TODO: to change with resume
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            if (PCommon._isDebugVersion) System.out.println("SearchFragment: OnCreateView, tab:" + MainActivity.Tab.GetCurrentTabPosition());
        }

        return v;
    }

    @Override
    public void onResume()
    {
        try
        {
            super.onResume();

            CheckLocalInstance();
            SetLocalBibleName();

            if (fragmentType == FRAGMENT_TYPE.FAV_TYPE)
            {
                searchFullQueryLimit = 0;

                tabTitle = _context.getString(R.string.favHeader);
                searchFullQuery = null;
                scrollPosY = 0;
                isBook = false;
                isChapter = false;
                isVerse = false;
                bNumber = 0;
                cNumber = 0;
                vNumber = 0;
                trad = bbName;

                final CacheTabBO t = _s.GetCurrentCacheTab();
                if (t != null)
                {
                    searchFullQuery = t.fullQuery;
                    scrollPosY = t.scrollPosY;
                }

                // Set objects
                SetTabTitle(tabTitle);
                CreateRecyclerView();

                final int favOrder = GetFavOrder();
                recyclerViewAdapter = new BibleAdapter(getContext(), bbName, searchFullQuery, favOrder, null);
                if (WhenTabIsEmptyOrNull(true)) return;
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.scrollToPosition(scrollPosY);

                return;
            }
            else if (fragmentType == FRAGMENT_TYPE.PLAN_TYPE)
            {
                searchFullQueryLimit = 3;

                final CacheTabBO t = _s.GetCurrentCacheTab();
                if (t == null)
                {
                    WhenTabIsEmptyOrNull(false);
                    return;
                }

                tabTitle = t.tabTitle;
                searchFullQuery = t.fullQuery;
                scrollPosY = t.scrollPosY;
                bbName = t.bbName;
                isBook = t.isBook;
                isChapter = t.isChapter;
                isVerse = t.isVerse;
                bNumber = t.bNumber;
                cNumber = t.cNumber;
                vNumber = t.vNumber;
                trad = t.trad;

                @SuppressWarnings("UnusedAssignment") String title = null;
                final String[] cols = searchFullQuery.split("\\s");
                if (cols.length == 3)
                {
                    planId = Integer.parseInt(cols[0]);
                    planDayNumber = Integer.parseInt(cols[1]);
                    planPageNumber = Integer.parseInt(cols[2]);
                    PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.PLAN_ID, planId);
                    PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.PLAN_PAGE, planPageNumber);
                    final PlanDescBO pd = _s.GetPlanDesc(planId);
                    if (pd != null)
                    {
                        final PlanCalBO pc = _s.GetPlanCalByDay(bbName, planId, planDayNumber);
                        if (pc != null)
                        {
                            title = PCommon.ConcaT(getString(R.string.planCalTitleDay), " ", pc.dayNumber, "/", pd.dayCount);

                            //Book title
                            ShowBookTitle(true);
                            tvBookTitle.setText(title);
                        }
                    }
                    else
                    {
                        t.tabType = "S";
                        _s.SaveCacheTab(t);

                        ShowBookTitle(false);
                    }
                }

                // Set objects
                SetTabTitle(tabTitle);

                SearchBible(true);

                return;
            }
            else if (fragmentType == FRAGMENT_TYPE.ARTICLE_TYPE)
            {
                searchFullQueryLimit = 0;

                tabTitle = getString(R.string.tabTitleDefault);
                searchFullQuery = null;
                scrollPosY = 0;
                isBook = false;
                isChapter = false;
                isVerse = false;
                bNumber = 0;
                cNumber = 0;
                vNumber = 0;
                trad = bbName;

                final CacheTabBO t = _s.GetCurrentCacheTab();
                if (t != null)
                {
                    tabTitle = t.tabTitle;
                    searchFullQuery = t.fullQuery;
                    scrollPosY = t.scrollPosY;
                }

                // Set objects
                SetTabTitle(tabTitle);
                CreateRecyclerView();

                //Show article
                final String artHtml = GetArticle(t);
                recyclerViewAdapter = new BibleArticleAdapter(getContext(), bbName, artHtml);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.scrollToPosition(scrollPosY);

                return;
            }

            //Search type
            searchFullQueryLimit = 3;

            // Get tab info
            final CacheTabBO t = _s.GetCurrentCacheTab();
            if (t == null)
            {
                WhenTabIsEmptyOrNull(false);
                return;
            }

            //tabNumber is critical => function
            tabTitle = t.tabTitle;
            searchFullQuery = t.fullQuery;
            scrollPosY = t.scrollPosY;
            bbName = t.bbName;
            isBook = t.isBook;
            isChapter = t.isChapter;
            isVerse = t.isVerse;
            bNumber = t.bNumber;
            cNumber = t.cNumber;
            vNumber = t.vNumber;
            trad = t.trad;

            // Set objects
            SetTabTitle(tabTitle);

            if (isBook && isChapter)
            {
                SearchBible(false);
            }
            else
            {
                SearchBible(true);  //TODO if empty ???
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
        finally
        {
            if (PCommon._isDebugVersion) System.out.println("SearchFragment: OnResume, tab:" + MainActivity.Tab.GetCurrentTabPosition());
        }
    }

    private boolean WhenTabIsEmptyOrNull(final boolean shouldCheckRecycler)
    {
        try
        {
            if (!shouldCheckRecycler)
            {
                CreateRecyclerView();
            }
            else
            {
                if (recyclerViewAdapter != null && recyclerViewAdapter.getItemCount() > 0)
                {
                    return false;
                }
            }

            final String content = getString(R.string.tabEmpty);
            recyclerViewAdapter = new BibleArticleAdapter(getContext(), bbName, content);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.scrollToPosition(0);

            return true;
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return false;
    }

    /***
     * Get article
     * @param t     CacheTabBO
     */
    @SuppressWarnings("JavaDoc")
    private String GetArticle(final CacheTabBO t)
    {
        String artHtml = "";

        try
        {
            //Set objects
            final int artId = PCommon.GetResId(_context, PCommon.ConcaT(t.fullQuery, "_CONTENT"));
            final String artTitle = getString(PCommon.GetResId(_context, t.fullQuery));
            final String ha = PCommon.ConcaT("<br><H>", artTitle, "</H>");
            artHtml = getString(artId);

            int i = -1;
            int pos;

            //Parse <R></R>
            String[] arrRef = artHtml.split("<R>");
            if (arrRef.length > 0)
            {
                String strVerses;
                for (String strRef : arrRef)
                {
                    i++;

                    if (strRef != null)
                    {
                        pos = strRef.indexOf("</R>");
                        if (pos >= 0)
                        {
                            pos = pos + 4;
                            strRef = strRef.substring(0, pos);
                            strRef = PCommon.ConcaT("<R>", strRef);
                            arrRef[ i ] = strRef;
                            String[] ref = strRef.replaceFirst("<R>", "").replaceFirst("</R>", "").split("\\s");
                            if (ref.length == 4) {
                                strVerses = _s.GetVersesHtml(bbName, Integer.parseInt(ref[0]), Integer.parseInt(ref[1]), Integer.parseInt(ref[2]), Integer.parseInt(ref[3]));
                            }
                            else
                            {   //5
                                strVerses = _s.GetVersesHtml(ref[0], Integer.parseInt(ref[1]), Integer.parseInt(ref[2]), Integer.parseInt(ref[3]),  Integer.parseInt(ref[4]));
                            }
                            //noinspection UnusedAssignment
                            ref = null;
                            artHtml = artHtml.replaceFirst(strRef, strVerses);
                        }
                    }
                }
                //noinspection UnusedAssignment
                strVerses = null;
            }

            //Parse <HB></HB>
            i = -1;
            //noinspection UnusedAssignment
            arrRef = null;
            arrRef = artHtml.split("<HB>");
            if (arrRef.length > 0)
            {
                for (String strRef : arrRef)
                {
                    i++;

                    if (strRef != null)
                    {
                        pos = strRef.indexOf("</HB>");
                        if (pos >= 0)
                        {
                            pos = pos + 5;
                            strRef = strRef.substring(0, pos);
                            strRef = PCommon.ConcaT("<HB>", strRef);
                            arrRef[ i ] = strRef;
                            String[] ref = strRef.replaceFirst("<HB>", "").replaceFirst("</HB>", "").split("\\s");
                            artHtml = artHtml.replaceFirst(strRef,
                                    PCommon.ConcaT("<HS>", _s.GetBookRef( bbName, Integer.parseInt(ref[0]) ).bName, "</HS>"));
                            //noinspection UnusedAssignment
                            ref = null;
                        }
                    }
                }
            }
            //noinspection UnusedAssignment
            arrRef = null;

            //Parse <T></T>,  <H></H>,  <HA/>
            artHtml = artHtml
                    .replaceFirst("<HA/>", ha)
                    .replaceAll("<HS>", "<br><span><u>")
                    .replaceAll("</HS>", "</u></span>")
                    .replaceAll("<H>", "<h1><u>")
                    .replaceAll("</H>", "</u></h1>");
            //End Parse
            //TODO FAB: see spannableString(builder) for titles without too much space after.
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return artHtml;
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        final MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_search, menu);

        if (fragmentType == FRAGMENT_TYPE.ARTICLE_TYPE)
        {
            menu.findItem(R.id.mnu_open_result).setVisible(false);
            menu.findItem(R.id.mnu_copy_result_to_clipboard).setVisible(false);
            menu.findItem(R.id.mnu_share_result).setVisible(false);

            menu.findItem(R.id.mnu_open_verse).setVisible(false);
            menu.findItem(R.id.mnu_copy_verse_to_clipboard).setVisible(false);
            menu.findItem(R.id.mnu_share_verse).setVisible(false);

            menu.findItem(R.id.mnu_fav).setVisible(false);
        }
        else if (fragmentType == FRAGMENT_TYPE.FAV_TYPE)
        {
            menu.findItem(R.id.mnu_open_result).setVisible(false);
            menu.findItem(R.id.mnu_copy_result_to_clipboard).setVisible(false);
            menu.findItem(R.id.mnu_share_result).setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item)
    {
        try
        {
            final int itemId = item.getItemId();
            final int bibleId = Integer.parseInt(PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_ID, "0"));
            final int position = Integer.parseInt(PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.VIEW_POSITION, "0"));
            final VerseBO verse = _s.GetVerse(bibleId);
            if (verse == null) return false;
            final String fullquery = PCommon.ConcaT(verse.bNumber, " ", verse.cNumber);

            final AlertDialog builder = new AlertDialog.Builder(getContext()).create();
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View vllLanguages = inflater.inflate(R.layout.fragment_languages_multi, (ViewGroup) getActivity().findViewById(R.id.llLanguages));

            if ( itemId == R.id.mnu_open_chapter || itemId == R.id.mnu_open_result || itemId == R.id.mnu_open_verse || itemId == R.id.mnu_copy_verse_to_clipboard) {
                SetLocalBibleName();
            }

            switch (itemId)
            {
                case R.id.mnu_open_verse:
                {
                    //MainActivity.Slide(true);   //TODO FAB: to remove, uncomment block here under
                    return true;

                    /*
                    final String msg = PCommon.ConcaT(getString(R.string.mnuOpenVerse), "");
                    PCommon.SelectBibleLanguageMulti(builder, getContext(), vllLanguages, msg, "", true, false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            final String bbname = PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, verse.bbName);
                            if (bbname.equals("")) return;
                            final String fullquery = PCommon.ConcaT(verse.bNumber, " ", verse.cNumber, " ", verse.vNumber);
                            final String tbbName = PCommon.GetPrefTradBibleName(getContext(), true);
                            MainActivity.Tab.AddTab(getContext(), tbbName, verse.bNumber, verse.cNumber, fullquery, verse.vNumber);
                        }
                    });
                    builder.show();

                    return true;
                    */
                }
                case R.id.mnu_open_chapter:
                {
                    final String msg = PCommon.ConcaT(getString(R.string.mnuOpenChapter), "");
                    PCommon.SelectBibleLanguageMulti(builder, getContext(), vllLanguages, msg, "", true, false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            final String bbname = PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, verse.bbName);
                            if (bbname.equals("")) return;
                            final String tbbName = PCommon.GetPrefTradBibleName(getContext(), true);
                            MainActivity.Tab.AddTab(getContext(), tbbName, verse.bNumber, verse.cNumber, fullquery, verse.vNumber);
                        }
                    });
                    builder.show();

                    return true;
                }
                case R.id.mnu_open_result:
                {
                    final int tabIdFrom = tabNumber();
                    if (tabIdFrom < 0)
                        return false;

                    final String msg = PCommon.ConcaT(getString(R.string.mnuOpenResult), "");
                    PCommon.SelectBibleLanguageMulti(builder, getContext(), vllLanguages, msg, "", true, false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            final String bbname = PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, verse.bbName);
                            if (bbname.equals("")) return;
                            final String tbbName = PCommon.GetPrefTradBibleName(getContext(), true);

                            if (isVerse)
                            {
                                final String fullquery = PCommon.ConcaT(verse.bNumber, " ", verse.cNumber, " ", verse.vNumber);
                                MainActivity.Tab.AddTab(getContext(), tbbName, verse.bNumber, verse.cNumber, fullquery, verse.vNumber);
                            }
                            else if (isChapter)
                            {
                                MainActivity.Tab.AddTab(getContext(), tbbName, verse.bNumber, verse.cNumber, fullquery, verse.vNumber);
                            }
                            else
                            {
                                MainActivity.Tab.AddTab(getContext(), tabIdFrom, tbbName, verse.vNumber);
                            }
                        }
                    });
                    builder.show();

                    return true;
                }

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                case R.id.mnu_copy_verse_to_clipboard:
                {
                    final String msg = PCommon.ConcaT(getString(R.string.mnuCopyVerseToClipboard), "");
                    PCommon.SelectBibleLanguageMulti(builder, getContext(), vllLanguages, msg, "", true, false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            final String bbname = PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, verse.bbName);
                            if (bbname.equals("")) return;
                            final String tbbName = PCommon.GetPrefTradBibleName(getContext(), true);
                            final String text = _s.GetVerseText(tbbName, verse.bNumber, verse.cNumber, verse.vNumber);
                            PCommon.CopyTextToClipboard(_context, "", text);
                        }
                    });
                    builder.show();

                    return true;
                }
                case R.id.mnu_copy_chapter_to_clipboard:
                {
                    final String msg = PCommon.ConcaT(getString(R.string.mnuCopyChapterToClipboard), "");
                    PCommon.SelectBibleLanguageMulti(builder, getContext(), vllLanguages, msg, "", true, false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            final String bbname = PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, verse.bbName);
                            if (bbname.equals("")) return;
                            final String tbbName = PCommon.GetPrefTradBibleName(getContext(), true);
                            final String text = _s.GetChapterText(tbbName, verse.bNumber, verse.cNumber);
                            PCommon.CopyTextToClipboard(_context, "", text);
                        }
                    });
                    builder.show();

                    return true;
                }
                case R.id.mnu_copy_result_to_clipboard:
                {
                    final int tabIdFrom = tabNumber();
                    if (tabIdFrom < 0)
                        return false;

                    final String msg = PCommon.ConcaT(getString(R.string.mnuCopyResultToClipboard), "");
                    PCommon.SelectBibleLanguageMulti(builder, getContext(), vllLanguages, msg, "", true, false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            final String bbname = PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, verse.bbName);
                            if (bbname.equals("")) return;
                            final String tbbName = PCommon.GetPrefTradBibleName(getContext(), true);
                            final String text;

                            if (isVerse)
                            {
                                text = _s.GetVerseText(tbbName, verse.bNumber, verse.cNumber, verse.vNumber);
                                PCommon.CopyTextToClipboard(_context, "", text);
                            }
                            else if (isChapter)
                            {
                                text = _s.GetChapterText(tbbName, verse.bNumber, verse.cNumber);
                                PCommon.CopyTextToClipboard(_context, "", text);
                            }
                            else
                            {
                                final int tabIdTo = MainActivity.Tab.GetTabCount();
                                if (tabIdTo < 0) return;
                                text = _s.GetResultText(tabIdFrom, tabIdTo, tbbName);
                                PCommon.CopyTextToClipboard(_context, "", text);
                            }
                        }
                    });
                    builder.show();

                    return true;
                }

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                case R.id.mnu_share_verse:
                {
                    final String msg = PCommon.ConcaT(getString(R.string.mnuShareVerse), "");
                    PCommon.SelectBibleLanguageMulti(builder, getContext(), vllLanguages, msg, "", true, false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            final String bbname = PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, verse.bbName);
                            if (bbname.equals("")) return;
                            final String tbbName = PCommon.GetPrefTradBibleName(getContext(), true);
                            final String text = _s.GetVerseText(tbbName, verse.bNumber, verse.cNumber, verse.vNumber);

                            PCommon.ShareText(getContext(), text);
                        }
                    });
                    builder.show();

                    return true;
                }
                case R.id.mnu_share_chapter:
                {
                    final String msg = PCommon.ConcaT(getString(R.string.mnuShareChapter), "");
                    PCommon.SelectBibleLanguageMulti(builder, getContext(), vllLanguages, msg, "", true, false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            final String bbname = PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, verse.bbName);
                            if (bbname.equals("")) return;
                            final String tbbName = PCommon.GetPrefTradBibleName(getContext(), true);
                            final String text = _s.GetChapterText(tbbName, verse.bNumber, verse.cNumber);

                            PCommon.ShareText(getContext(), text);
                        }
                    });
                    builder.show();

                    return true;
                }
                case R.id.mnu_share_result:
                {
                    final String msg = PCommon.ConcaT(getString(R.string.mnuShareResult), "");
                    PCommon.SelectBibleLanguageMulti(builder, getContext(), vllLanguages, msg, "", true, false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            final String bbname = PCommon.GetPref(getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, verse.bbName);
                            if (bbname.equals("")) return;
                            final String tbbName = PCommon.GetPrefTradBibleName(getContext(), true);

                            final int tabIdFrom = tabNumber();
                            if (tabIdFrom < 0)
                                return;

                            if (isVerse)
                            {
                                final String text = _s.GetVerseText(tbbName, verse.bNumber, verse.cNumber, verse.vNumber);
                                PCommon.ShareText(getContext(), text);
                            }
                            else if (isChapter)
                            {
                                final String text = _s.GetChapterText(tbbName, verse.bNumber, verse.cNumber);
                                PCommon.ShareText(getContext(), text);
                            }
                            else
                            {
                                final int tabIdTo = MainActivity.Tab.GetTabCount();
                                if (tabIdTo < 0) return;
                                final String text = _s.GetResultText(tabIdFrom, tabIdTo, tbbName);
                                PCommon.ShareText(getContext(), text);
                            }
                        }
                    });
                    builder.show();

                    return true;
                }
                case R.id.mnu_add_note:
                {
                    //TODO: mark is hardcoded! final int markType = 1;
                    final String changeDt = PCommon.NowYYYYMMDD();
                    final NoteBO noteBO = new NoteBO(verse.bNumber, verse.cNumber, verse.vNumber, changeDt, 1);
                    _s.SaveNote(noteBO);

                    UpdateViewMark(position, 1);

                    return true;
                }
                case R.id.mnu_add_reading:
                {
                    //TODO: warning hardcoded
                    final String changeDt = PCommon.NowYYYYMMDD();
                    final NoteBO noteBO = new NoteBO(verse.bNumber, verse.cNumber, verse.vNumber, changeDt, 2);
                    _s.SaveNote(noteBO);

                    UpdateViewMark(position, 2);

                    return true;
                }
                case R.id.mnu_remove_note:
                {
                    _s.DeleteNote(verse.bNumber, verse.cNumber, verse.vNumber);

                    UpdateViewMark(position, 0);

                    return true;
                }
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater)
    {
        try
        {
            super.onCreateOptionsMenu(menu, menuInflater);

            SetLocalBibleName();

            if (fragmentType == FRAGMENT_TYPE.SEARCH_TYPE)
            {
                menuInflater.inflate(R.menu.menu_search, menu);

                final MenuItem mnu_search_bible_name_item = menu.findItem(R.id.mnu_search_bible_name);
                final String bbNameLanguage = (bbName.compareToIgnoreCase("k") == 0) ? "EN" : (bbName.compareToIgnoreCase("d") == 0) ? "IT" : (bbName.compareToIgnoreCase("v") == 0) ? "ES" : "FR";
                mnu_search_bible_name_item.setTitle(bbNameLanguage);
            }
            else if (fragmentType == FRAGMENT_TYPE.PLAN_TYPE)
            {
                menuInflater.inflate(R.menu.menu_plan, menu);
            }
            else if (fragmentType == FRAGMENT_TYPE.FAV_TYPE)
            {
                menuInflater.inflate(R.menu.menu_fav, menu);
            }
            else
            {
                final int INSTALL_STATUS = _s.GetInstallStatus(_context);
                if (INSTALL_STATUS == 4) menuInflater.inflate(R.menu.menu_art, menu);

                //No search
                return;
            }

            final MenuItem mnu_search_item = menu.findItem(R.id.mnu_search);
            searchView = (SearchView) mnu_search_item.getActionView();
            searchView.setLongClickable(true);
            searchView.setIconifiedByDefault(true);
            searchView.setQueryHint((fragmentType == FRAGMENT_TYPE.SEARCH_TYPE) ? getString(R.string.searchBibleHint) : getString(R.string.searchFavHint));

            if (fragmentType == FRAGMENT_TYPE.SEARCH_TYPE)
            {
                searchView.setSuggestionsAdapter(cursorAdapter);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
                {
                    @Override
                    public boolean onQueryTextSubmit(String query)
                    {
                        if (PCommon._isDebugVersion) System.out.println("TextSubmit: " + query);

                        //Save
                        searchFullQuery = query;
                        trad = bbName;

                        SearchBible(false);

                        mnu_search_item.collapseActionView();

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query)
                    {
                        if (PCommon._isDebugVersion) System.out.println("TextChange: " + query);

                        return false;
                    }
                });
                searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener()
                {
                    @Override
                    public boolean onSuggestionSelect(int position)
                    {
                        return false;
                    }

                    @Override
                    public boolean onSuggestionClick(int position)
                    {
                        if (PCommon._isDebugVersion) System.out.println("Suggestion Click: pos=" + position);

                        //Get selected book
                        @SuppressWarnings("unused") final int bookId = matrixCursor.getInt(0);
                        final String bookName = matrixCursor.getString(1);

                        if (PCommon._isDebugVersion) System.out.println("Book (" + bookId + ") => " + bookName);

                        //Change SearchView query
                        searchView.setQuery(bookName, false);

                        return true;
                    }
                });

                final AutoCompleteTextView searchViewAutoComplete = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
                searchViewAutoComplete.setThreshold(1);
            }
            else
            {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
                {
                    @Override
                    public boolean onQueryTextSubmit(String query)
                    {
                        if (PCommon._isDebugVersion) System.out.println("TextSubmit: " + query);

                        //Save
                        searchFullQuery = query;

                        SearchBible(false);
                        mnu_search_item.collapseActionView();

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query)
                    {
                        if (PCommon._isDebugVersion) System.out.println("TextChange: " + query);

                        return false;
                    }
                });
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        final int id = item.getItemId();
        switch (id)
        {
            case R.id.mnu_search_bible_name:
            {
                SetLocalBibleName();

                bbName = RollBookName(bbName);

                final String bbNameLanguage = (bbName.compareToIgnoreCase("k") == 0) ? "EN" : (bbName.compareToIgnoreCase("d") == 0) ? "IT" : (bbName.compareToIgnoreCase("v") == 0) ? "ES" : "FR";
                item.setTitle(bbNameLanguage);

                return true;
            }
            case R.id.mnu_fav_sort:
            {
                ShowFavOrder();

                return true;
            }
            case R.id.mnu_fav_clear_filter:
            {
                //Clear Fav filter
                searchFullQuery = "";
                final int orderBy = GetFavOrder();

                SaveTab();

                recyclerViewAdapter = new BibleAdapter(getContext(), bbName, searchFullQuery, orderBy, null);
                if (WhenTabIsEmptyOrNull(true)) return true;
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.scrollToPosition(scrollPosY);

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Check local instance (to copy reader all activities that use it)
     */
    private void CheckLocalInstance()
    {
        if (_context == null) _context = getActivity().getApplicationContext();

        CheckLocalInstance(_context);
    }

    /***
     * Check local instance (to copy reader all activities that use it)
     */
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

    private void CreateRecyclerView()
    {
        SetLayoutManager();

        recyclerViewAdapter = new BibleAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void SetLayoutManager()
    {
        recyclerView = (RecyclerView) v.findViewById(R.id.card_recycler_view);

        final RecyclerView.LayoutManager layoutManager;
        if (trad == null)
        {
            layoutManager = new LinearLayoutManager(_context);
        }
        else
        {
            final int ic = trad.length();
            final int dc = this.GetDynamicColumnCount(ic);
            layoutManager = (dc <= 1) ? new LinearLayoutManager(_context) : new GridLayoutManager(_context, dc);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);             //TODO FAB: fixedsize ??

        registerForContextMenu(recyclerView);
    }

    private Cursor GetCursor(CharSequence query)
    {
        MatrixCursor cursor = null;

        try
        {
            if (query == null) return null;

            final ArrayList<BibleRefBO> lstRef = _s.GetListBookByName(bbName, query.toString());
            cursor = new MatrixCursor(new String[]{"_id", "text"});

            if (lstRef.size() > 0)
            {
                String bookId;
                String bookName;

                for (final BibleRefBO r : lstRef)
                {
                    bookId = Integer.toString(r.bNumber);
                    bookName = r.bName;

                    cursor.addRow(new String[]{bookId, bookName});
                }
            }

            matrixCursor = cursor;
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return cursor;
    }

    private boolean IsChapterExist(final String bbName, final int bNumber, final int cNumber)
    {
        final ArrayList<VerseBO> lstVerse = _s.GetVerse(bbName, bNumber, cNumber, 1);

        return !(lstVerse == null || lstVerse.size() == 0);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean IsVerseExist(final String bbName, final int bNumber, final int cNumber, final int vNumber)
    {
        final ArrayList<VerseBO> lstVerse = _s.GetVerse(bbName, bNumber, cNumber, vNumber);

        return !(lstVerse == null || lstVerse.size() == 0);
    }

    /***
     * Update view at position and show/hide the mark
     * @param position  Position
     * @param markType  Mark type
     */
    private void UpdateViewMark(final int position, final int markType)
    {
        try
        {
            if (fragmentType == FRAGMENT_TYPE.FAV_TYPE)
            {
                SearchBible(false);
                return;
            }

            ((BibleAdapter) recyclerViewAdapter).lstVerse.get(position).mark = markType;
            recyclerViewAdapter.notifyItemChanged(position);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    private void ShowChapter(final String bbName, final int bNumber, final int cNumber)
    {
        try
        {
            //Try
            if (!IsChapterExist(bbName, bNumber, cNumber)) return;

            //Book title
            ShowBookTitle(true);

            final BibleRefBO ref = _s.GetBookRef(bbName, bNumber);
            tvBookTitle.setText(ref.bName);

            final String title = PCommon.ConcaT(ref.bsName, cNumber);
            SetTabTitle(title);
            SaveTab();

            //Get chapter
            recyclerViewAdapter = new BibleAdapter(_context, trad, bNumber, cNumber);
            if (WhenTabIsEmptyOrNull(true)) return;
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.scrollToPosition(scrollPosY);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    /***
     * Show plan
     * @param planDayMove   Move number of days in plan calendar
     */
    private void ShowPlan(final int planDayMove)
    {
        try
        {
            //Check
            final PlanDescBO pd = _s.GetPlanDesc(planId);
            if (pd == null) return;

            final int newPlanDayNumber = planDayNumber + planDayMove;
            if (newPlanDayNumber > pd.dayCount)
            {
                final Button btnForward = (Button) v.findViewById(R.id.btnForward);
                btnForward.setEnabled(false);
                return;
            }
            if (newPlanDayNumber < 1)
            {
                final Button btnBack = (Button) v.findViewById(R.id.btnBack);
                btnBack.setEnabled(false);
                return;
            }

            //Save
            final CacheTabBO t = _s.GetCacheTab(tabNumber());
            if (t == null) return;
            t.fullQuery = PCommon.ConcaT(planId, " ", newPlanDayNumber, " ", planPageNumber);
            t.scrollPosY = 0;
            _s.SaveCacheTab(t);

            final PlanCalBO pc =_s.GetPlanCalByDay(t.bbName, planId, newPlanDayNumber);
            final int bNumberStart = pc.bNumberStart, cNumberStart = pc.cNumberStart, vNumberStart = pc.vNumberStart;
            final int bNumberEnd = pc.bNumberEnd, cNumberEnd = pc.cNumberEnd, vNumberEnd = pc.vNumberEnd;
            final boolean copy =_s.CopyCacheSearchForOtherBible(t.tabNumber, t.trad, bNumberStart, cNumberStart, vNumberStart, bNumberEnd, cNumberEnd, vNumberEnd);
            if (!copy) return;

            onResume();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    private void ShowVerse(final String bbName, final int bNumber, final int cNumber, final int vNumber)
    {
        try
        {
            //Try
            if (!IsVerseExist(bbName, bNumber, cNumber, vNumber)) return;

            //Book title
            ShowBookTitle(false);

            final BibleRefBO ref = _s.GetBookRef(bbName, bNumber);
            tvBookTitle.setText(ref.bName);

            final String title = PCommon.ConcaT(ref.bsName, cNumber, " ", vNumber);
            SetTabTitle(title);
            SaveTab();

            //Get verse
            recyclerViewAdapter = new BibleAdapter(_context, trad, bNumber, cNumber, vNumber);
            if (WhenTabIsEmptyOrNull(true)) return;
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.scrollToPosition(0);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    private void ShowVerses(final String bbName, final int bNumber, final int cNumber, final int vNumber, final int vNumberTo)
    {
        try
        {
            //Try
            if (!IsVerseExist(bbName, bNumber, cNumber, vNumber)) return;

            //Book title
            ShowBookTitle(false);

            final BibleRefBO ref = _s.GetBookRef(bbName, bNumber);
            tvBookTitle.setText(ref.bName);

            final String title = PCommon.ConcaT(ref.bsName, cNumber, " ", vNumber, "-", vNumberTo);
            SetTabTitle(title);
            SaveTab();

            //Get verses
            recyclerViewAdapter = new BibleAdapter(_context, trad, bNumber, cNumber, vNumber, vNumberTo);
            if (WhenTabIsEmptyOrNull(true)) return;
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.scrollToPosition(0);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    private void ShowBookTitle(boolean show)
    {
        tvBookTitle.setVisibility(show ? View.VISIBLE : View.GONE);
        btnBack.setVisibility(show ? View.VISIBLE : View.GONE);
        btnForward.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void SaveRef(final boolean isBook, final boolean isChapter, final boolean isVerse, final int bNumber, final int cNumber, final int vNumber)
    {
        this.isBook = isBook;
        this.isChapter = isChapter;
        this.isVerse = isVerse;
        this.bNumber = bNumber;
        this.cNumber = cNumber;
        this.vNumber = vNumber;
    }

    private int tabNumber()
    {
        return MainActivity.Tab.GetCurrentTabPosition();
    }

    private void SaveTab()
    {
        final String tabType = (fragmentType == FRAGMENT_TYPE.SEARCH_TYPE) ? "S" : (fragmentType == FRAGMENT_TYPE.PLAN_TYPE) ? "P" : (fragmentType == FRAGMENT_TYPE.FAV_TYPE) ? "F" : "A";
        if (trad == null || trad.equals("")) trad = bbName;
        final CacheTabBO cacheTab = new CacheTabBO(tabNumber(), tabType, tabTitle, searchFullQuery, scrollPosY, bbName, isBook, isChapter, isVerse, bNumber, cNumber, vNumber, trad);
        _s.SaveCacheTab(cacheTab);
        SetLayoutManager();
    }

    /***
     * Set local bible name
     */
    private void SetLocalBibleName()
    {
        if (bbName == null) bbName = PCommon.GetPrefBibleName(_context);
    }

    private String RollBookName(final String bbName)
    {
        try
        {
            final int INSTALL_STATUS = _s.GetInstallStatus(_context);
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
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return "k";
    }

    /***
     * Get Fav Order
     * @return 0..
     */
    private int GetFavOrder()
    {
        return Integer.parseInt(PCommon.GetPref(_context, IProject.APP_PREF_KEY.FAV_ORDER, "0"));
    }

    /***
     * Get dynamic column count
     * @param ic Info count to display
     * @return Preferred number of columns to use to display the info
     */
    private int GetDynamicColumnCount(final int ic)
    {
        final int dc;

        switch (ic)
        {
            case 1:
            {
                dc = Integer.parseInt(PCommon.GetPref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_1, "1"));
                break;
            }
            case 2:
            {
                dc = Integer.parseInt(PCommon.GetPref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_2, "1"));
                break;
            }
            case 3:
            {
                dc = Integer.parseInt(PCommon.GetPref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_3, "1"));
                break;
            }
            case 4:
            {
                dc = Integer.parseInt(PCommon.GetPref(_context, IProject.APP_PREF_KEY.LAYOUT_DYNAMIC_4, "1"));
                break;
            }
            default:
            {
                dc = 1;
                break;
            }
        }

        return dc;
    }

    private void ShowFavOrder()
    {
        try
        {
            final AlertDialog builder = new AlertDialog.Builder(getContext()).create();
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_fav_order, (ViewGroup) getActivity().findViewById(R.id.llFavOrderBy));

            class InnerClass
            {
                private void OnDismiss(View view)
                {
                    try
                    {
                        final int orderBy = Integer.parseInt(view.getTag().toString());
                        PCommon.SavePrefInt(_context, IProject.APP_PREF_KEY.FAV_ORDER, orderBy);
                        builder.dismiss();

                        recyclerViewAdapter = new BibleAdapter(getContext(), bbName, searchFullQuery, orderBy, null);
                        if (WhenTabIsEmptyOrNull(true)) return;
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.scrollToPosition(scrollPosY);
                    }
                    catch (Exception ex)
                    {
                        if (PCommon._isDebugVersion) PCommon.LogR(view.getContext(), ex);
                    }
                }
            }
            final InnerClass innerClass = new InnerClass();

            final Button btn0 = (Button) view.findViewById(R.id.btnFavOrder0);
            btn0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    innerClass.OnDismiss(view);
                }
            });
            final Button btn1 = (Button) view.findViewById(R.id.btnFavOrder1);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    innerClass.OnDismiss(view);
                }
            });
            final Button btn2 = (Button) view.findViewById(R.id.btnFavOrder2);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    innerClass.OnDismiss(view);
                }
            });

            builder.setCancelable(true);
            builder.setView(view);
            builder.setTitle(R.string.favOrderTitle);
            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getContext(), ex);
        }
    }

    private void SetTabTitle(final String title)
    {
        tabTitle = title;
        MainActivity.Tab.SetCurrentTabTitle(title);
    }

    private void SearchBible(final boolean useCache)
    {
        try
        {
            if (fragmentType == FRAGMENT_TYPE.FAV_TYPE)
            {
                /* if (searchFullQuery != null)
                {
                    if (searchFullQuery.isEmpty()) searchFullQuery = null;
                }
                */

                SaveTab();

                final int orderBy = GetFavOrder();
                recyclerViewAdapter = new BibleAdapter(getContext(), bbName, searchFullQuery, orderBy, null);
                if (WhenTabIsEmptyOrNull(true)) return;
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.scrollToPosition(scrollPosY);

                return;
                //TODO: this is a menu_divider. After I have to create all SearchNotes methods or add a param to searchbible to convert the queries
            }
            else if (fragmentType == FRAGMENT_TYPE.ARTICLE_TYPE)
            {
                //No recycler
                return;
            }

            if (useCache)
            {
                recyclerViewAdapter = new BibleAdapter(_context, tabNumber());
                final int itemCount = recyclerViewAdapter.getItemCount();
                if (itemCount > 0)
                {
                    SetLayoutManager();

                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.scrollToPosition(scrollPosY);

                    return;
                }
            }

            SetLocalBibleName();

            @SuppressWarnings("UnusedAssignment") boolean isBook = false,  isChapter = false,  isVerse = false;
            @SuppressWarnings("UnusedAssignment") int     bNumber = 0,     cNumber = 0,        vNumber = 0;
            @SuppressWarnings("UnusedAssignment") int wCount = 0;
            final String[] words = searchFullQuery.split("\\s");
            final String patternDigit = "\\d+";

            wCount = words.length;

            //--------------------------------------------------------------------------------------
            ShowBookTitle(false);

            if (wCount == 0) return;
            //...so minimum is one

            class InnerClass
            {
                public final String MergeWords(final int fromWordPos, final String[] words)
                {
                    final int toWordPos = words.length - 1;

                    String mergedString = "";
                    for(int i=fromWordPos; i <= toWordPos; i++)
                    {
                        if (i != fromWordPos)
                            mergedString = PCommon.ConcaT(mergedString, " ", words[ i ]);
                        else
                            mergedString = PCommon.ConcaT(mergedString, words[ i ]);
                    }

                    return mergedString;
                }
            }

            //Try to get bName and convert it to bNumber
            bNumber = words[ 0 ].matches(patternDigit) ? Integer.parseInt(words[ 0 ]) : _s.GetBookNumberByName( bbName, words[ 0 ] );

            if (wCount == 4)
            {
                if ((words[0].matches(patternDigit) && words[1].matches(patternDigit) && words[2].matches(patternDigit) && words[3].matches(patternDigit)))
                {
                    isBook = true;
                    isChapter = false;
                    isVerse = false;

                    if (bNumber <= 0) bNumber = Integer.parseInt(words[0]);     //TODO ?  one param less
                    cNumber = Integer.parseInt(words[1]);
                    vNumber = Integer.parseInt(words[2]);
                    final int vNumberTo = Integer.parseInt(words[3]);

                    //noinspection ConstantConditions,ConstantConditions,ConstantConditions
                    SaveRef(isBook, isChapter, isVerse, bNumber, cNumber, vNumber);
                    ShowVerses(bbName, bNumber, cNumber, vNumber, vNumberTo);

                    return;
                }
            }

            if (wCount == 3)
            {
                //<bNumber> <cNumber> <vNumber>
                if ((words[0].matches(patternDigit) && words[1].matches(patternDigit) && words[2].matches(patternDigit)) ||
                        (bNumber > 0 && words[1].matches(patternDigit) && words[2].matches(patternDigit)))
                {
                    isBook = true;
                    isChapter = true;
                    isVerse = true;

                    if (bNumber <= 0) bNumber = Integer.parseInt(words[0]);
                    cNumber = Integer.parseInt(words[1]);
                    vNumber = Integer.parseInt(words[2]);

                    //noinspection ConstantConditions,ConstantConditions,ConstantConditions
                    SaveRef(isBook, isChapter, isVerse, bNumber, cNumber, vNumber);
                    ShowVerse(bbName, bNumber, cNumber, vNumber);

                    return;
                }
            }

            if (wCount >= 3)
            {
                //<bNumber> <cNumber> <expr>...
                if ((words[0].matches(patternDigit) && words[1].matches(patternDigit)) ||
                        (bNumber > 0 && words[1].matches(patternDigit)))
                {
                    isBook = true;
                    isChapter = false;
                    isVerse = false;

                    if (bNumber <= 0) bNumber = Integer.parseInt(words[0]);
                    cNumber = Integer.parseInt(words[1]);
                    vNumber = 0;

                    //noinspection ConstantConditions,ConstantConditions,ConstantConditions
                    SaveRef(isBook, isChapter, isVerse, bNumber, cNumber, vNumber);

                    final InnerClass innerClass = new InnerClass();
                    final String searchExpr = innerClass.MergeWords(2, words);

                    if (PCommon._isDebugVersion) System.out.println("SearchExpr: " + searchExpr);

                    SetTabTitle(searchExpr);
                    SaveTab();

                    recyclerViewAdapter = new BibleAdapter(_context, bbName, bNumber, cNumber, searchExpr);
                    if (WhenTabIsEmptyOrNull(true)) return;
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.scrollToPosition(scrollPosY);

                    return;
                }
            }

            if (wCount == 2)
            {
                //<bNumber> <cNumber>
                if (( words[ 0 ].matches(patternDigit) && words[ 1 ].matches(patternDigit) ) ||
                    (bNumber > 0 && words[ 1 ].matches(patternDigit) ))
                {
                    isBook = true;
                    isChapter = true;
                    isVerse = false;

                    if (bNumber <= 0) bNumber = Integer.parseInt(words[ 0 ]);
                    cNumber = Integer.parseInt(words[ 1 ]);
                    vNumber = 0;

                    //noinspection ConstantConditions,ConstantConditions,ConstantConditions
                    SaveRef(isBook, isChapter, isVerse, bNumber, cNumber, vNumber);
                    ShowChapter(bbName, bNumber, cNumber);

                    return;
                }
            }

            if (wCount >= 2)
            {
                //<bNumber> <expr>...
                if ((words[0].matches(patternDigit)) ||
                        (bNumber > 0 ))
                {
                    isBook = true;
                    isChapter = false;
                    isVerse = false;

                    if (bNumber <= 0) bNumber = Integer.parseInt(words[0]);
                    cNumber = 0;
                    vNumber = 0;

                    //noinspection ConstantConditions,ConstantConditions,ConstantConditions
                    SaveRef(isBook, isChapter, isVerse, bNumber, cNumber, vNumber);

                    final InnerClass innerClass = new InnerClass();
                    final String searchExpr = innerClass.MergeWords(1, words);

                    if (PCommon._isDebugVersion) System.out.println("SearchExpr: " + searchExpr);

                    SetTabTitle(searchExpr);
                    SaveTab();

                    recyclerViewAdapter = new BibleAdapter(_context, bbName, bNumber, searchExpr);
                    if (WhenTabIsEmptyOrNull(true)) return;
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.scrollToPosition(scrollPosY);

                    return;
                }
            }

            if (wCount == 1)
            {
                //<bNumber>
                if ((words[0].matches(patternDigit)) ||
                        (bNumber > 0 ))
                {
                    isBook = true;
                    isChapter = true;
                    isVerse = false;

                    if (bNumber <= 0) bNumber = Integer.parseInt(words[0]);
                    cNumber = 1;
                    vNumber = 0;

                    //noinspection ConstantConditions,ConstantConditions,ConstantConditions
                    SaveRef(isBook, isChapter, isVerse, bNumber, cNumber, vNumber);
                    ShowChapter(bbName, bNumber, cNumber);

                    return;
                }
            }

            //Finally <expr>...
            if (searchFullQuery.length() >= searchFullQueryLimit)
            {
                isBook = true;
                isChapter = false;
                isVerse = false;

                bNumber = 0;
                cNumber = 0;
                vNumber = 0;

                //noinspection ConstantConditions,ConstantConditions,ConstantConditions
                SaveRef(isBook, isChapter, isVerse, bNumber, cNumber, vNumber);
                SetTabTitle(searchFullQuery);
                SaveTab();

                recyclerViewAdapter = new BibleAdapter(_context, bbName, searchFullQuery);
                if (WhenTabIsEmptyOrNull(true)) return;
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.scrollToPosition(scrollPosY);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }
}
