
package org.hlwd.bible;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    @SuppressLint("StaticFieldLeak")
    private static TabLayout tabLayout;
    private View llMain;
    private View slideViewMenu;
    private View slideViewMenuHandle;
    private View slideViewTab;
    private View slideViewTabHandleMain;
    private View slideViewTabHandle;
    private boolean isUiTelevision = false;
    private static boolean isPlanSelectAlreadyWarned = false;
    private SCommon _s = null;

    @Override
    protected void onStart()
    {
        try
        {
            super.onStart();

            CheckLocalInstance(getApplicationContext());

            _s.DeleteAllLogs();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);

            CheckLocalInstance(getApplicationContext());

            if (PCommon._isDebugVersion) System.out.println("Main: onCreate");

            isUiTelevision = PCommon.IsUiTelevision(getApplicationContext());
            final int themeId = PCommon.GetPrefThemeId( getApplicationContext() );
            setTheme(themeId);
            setContentView(PCommon.SetUILayout(getApplicationContext(), R.layout.activity_main, R.layout.activity_main_tv));

            llMain = findViewById(isUiTelevision ? R.id.slideViewTab : R.id.llMain);
            slideViewMenu = (isUiTelevision) ? findViewById(R.id.slideViewMenu) : null;
            slideViewMenuHandle = (isUiTelevision) ? findViewById(R.id.mnuTvHandle) : null;
            slideViewTab = (isUiTelevision) ? findViewById(R.id.slideViewTab) : null;
            slideViewTabHandleMain = (isUiTelevision) ? findViewById(R.id.slideViewTabHandleMain) : null;
            slideViewTabHandle = (isUiTelevision) ? findViewById(R.id.slideViewTabHandle) : null;

            if (!isUiTelevision) {
                final Toolbar toolbar = findViewById(R.id.toolbar);
                if (toolbar != null) { setSupportActionBar(toolbar); }
            }
            else
            {
                slideViewMenuHandle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide( false );
                    }
                });
                slideViewTabHandle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide( true );
                    }
                });
                final View slideViewTabSearch = findViewById(R.id.slideViewTabSearch);
                slideViewTabSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchTv(v.getContext(), true);
                    }
                });
                final View slideViewTabFilter = findViewById(R.id.slideViewTabFilter);
                slideViewTabFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchTv(v.getContext(), false);
                    }
                });
                final View mnuTvArticles = findViewById(R.id.mnuTvArticles);
                mnuTvArticles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide(false);
                        PCommon.ShowArticles(v.getContext());
                    }
                });
                final View mnuTvBooks = findViewById(R.id.mnuTvBooks);
                mnuTvBooks.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide(false);
                        ShowBooks(v.getContext());
                    }
                });
                final View mnuTvFav = findViewById(R.id.mnuTvFav);
                mnuTvFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide(false);
                        ShowFav();
                    }
                });
                final View mnuTvReadings = findViewById(R.id.mnuTvReadings);
                mnuTvReadings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide(false);
                        ShowFav("2");
                    }
                });
                final View mnuTvHelp = findViewById(R.id.mnuTvHelp);
                mnuTvHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide(false);
                        PCommon.ShowArticle(v.getContext(),"ART_APP_HELP");
                    }
                });
                final View mnuTvPlans = findViewById(R.id.mnuTvPlans);
                mnuTvPlans.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide(false);
                        ShowPlans();
                    }
                });
                final View mnuTvPrbl = findViewById(R.id.mnuTvPrbl);
                mnuTvPrbl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide(false);
                        ShowPrbl();
                    }
                });
                final View mnuTvSettings = findViewById(R.id.mnuTvSettings);
                mnuTvSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Slide(false);
                        final Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
                final View mnuTvAbout = findViewById(R.id.mnuTvAbout);
                mnuTvAbout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowAbout(v.getContext());
                    }
                });
                final View mnuTvQuit = findViewById(R.id.mnuTvQuit);
                mnuTvQuit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PCommon.TryQuitApplication(v.getContext());
                    }
                });
            }

            tabLayout = findViewById(R.id.tabLayout);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
            {
                @Override
                public void onTabSelected(final TabLayout.Tab tab)
                {
                    try
                    {
                        final int tabId = tab.getPosition();
                        if (PCommon._isDebugVersion) System.out.println("TabSelected: " + tabId);

                        final CacheTabBO cacheTab = _s.GetCacheTab(tabId);
                        final SearchFragment.FRAGMENT_TYPE fragmentType;

                        final FragmentManager fm = getSupportFragmentManager();
                        final FragmentTransaction ft = fm.beginTransaction();

                        if (cacheTab == null)
                        {
                            fragmentType = SearchFragment.FRAGMENT_TYPE.SEARCH_TYPE;
                        }
                        else
                        {
                            if (cacheTab.tabType.compareTo("S") == 0)
                            {
                                fragmentType = SearchFragment.FRAGMENT_TYPE.SEARCH_TYPE;
                            }
                            else if (cacheTab.tabType.compareTo("F") == 0)
                            {
                                fragmentType = SearchFragment.FRAGMENT_TYPE.FAV_TYPE;
                            }
                            else if (cacheTab.tabType.compareTo("P") == 0)
                            {
                                fragmentType = SearchFragment.FRAGMENT_TYPE.PLAN_TYPE;
                            }
                            else
                            {
                                fragmentType = SearchFragment.FRAGMENT_TYPE.ARTICLE_TYPE;
                            }
                        }
                        final Fragment frag = new SearchFragment(fragmentType);
                        ft.replace(R.id.content_frame, frag, Integer.toString(tabId));
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();

                        if (isUiTelevision)
                        {
                            final int INSTALL_STATUS = PCommon.GetInstallStatus(getApplicationContext());
                            if (INSTALL_STATUS != 5)
                            {
                                final int perc = GetInstallStatusPerc();
                                if (perc < 10) return;
                                final String contentMsg = GetInstallStatusMsg();
                                PCommon.ShowToast(getApplicationContext(), contentMsg, Toast.LENGTH_SHORT);
                            }
                        }
                    }
                    catch(Exception ex)
                    {
                        if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
                    }
                    finally
                    {
                        Tab.LongPress(tabLayout.getContext());
                    }
                }

                @Override
                public void onTabUnselected(final TabLayout.Tab tab)
                {
                    try
                    {
                        if (tab == null)
                            return;

                        final int tabId = tab.getPosition();
                        if (tabId < 0)
                            return;

                        final FragmentManager fm = getSupportFragmentManager();
                        final Fragment frag = fm.findFragmentByTag(Integer.toString(tabId));
                        if (frag == null)
                            return;
                        if (!(frag instanceof SearchFragment))
                            return;

                        final int posY = SearchFragment.GetScrollPosY();
                        final CacheTabBO t = _s.GetCacheTab(tabId);
                        if (t == null)
                            return;

                        t.scrollPosY = posY;
                        _s.SaveCacheTab(t);
                    }
                    catch(Exception ex)
                    {
                        if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
                    }
                }

                @Override
                public void onTabReselected(final TabLayout.Tab tab) {
                }
            });

            tabLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    TabLayout.Tab tab;
                    String tabTitle;
                    final int tabCount = _s.GetCacheTabCount();
                    for(int i=0; i < tabCount; i++)
                    {
                        tab = tabLayout.newTab().setText(Integer.toString(i));
                        tabLayout.addTab(tab);

                        tabTitle = _s.GetCacheTabTitle(i);
                        if (tabTitle == null) tabTitle = getString(R.string.tabTitleDefault);
                        tab.setText(tabTitle);
                    }

                    final int restoreTabSelected = Integer.parseInt(PCommon.GetPref(getApplicationContext(), IProject.APP_PREF_KEY.TAB_SELECTED, "0"));
                    if (restoreTabSelected >= 0) {
                        if (tabLayout != null) {
                            if (tabCount > 0 && restoreTabSelected < tabCount) {
                                tabLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //noinspection EmptyCatchBlock
                                        try
                                        {
                                            //noinspection ConstantConditions
                                            tabLayout.getTabAt(restoreTabSelected).select();
                                        }
                                        catch(Exception ex) { }
                                    }
                                });
                            }
                        }
                    }

                    final int UPDATE_STATUS = Integer.parseInt(PCommon.GetPref(getApplicationContext(), IProject.APP_PREF_KEY.UPDATE_STATUS, "1"));
                    if (UPDATE_STATUS != 1)
                    {
                        PCommon.SavePrefInt(getApplicationContext(), IProject.APP_PREF_KEY.UPDATE_STATUS, 1);
                        PCommon.ShowArticle(getApplicationContext(),"ART_APP_LOG");
                    }
                }
            });
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        try
        {
            if (PCommon._isDebugVersion) System.out.println("Main: onPostResume");

            /* This code has been removed since version 3.7
            PCommon.SetSound(getApplicationContext(), true);
            */

            final String BIBLE_NAME = PCommon.GetPref(getApplicationContext(), IProject.APP_PREF_KEY.BIBLE_NAME, "");
            if (BIBLE_NAME.compareToIgnoreCase("k") != 0 && BIBLE_NAME.compareToIgnoreCase("l") != 0 && BIBLE_NAME.compareToIgnoreCase("d") != 0 && BIBLE_NAME.compareToIgnoreCase("v") != 0 && BIBLE_NAME.compareToIgnoreCase("a") != 0 )
            {
                //Forced temporary
                PCommon.SavePref(getApplicationContext(), IProject.APP_PREF_KEY.BIBLE_NAME, "k");

                final LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(PCommon.SetUILayout(getApplicationContext(), R.layout.fragment_languages, R.layout.fragment_languages_tv), (ViewGroup) findViewById(R.id.llLanguages));
                final String msg = getString(R.string.mnuLanguage);
                final String desc = "";
                final AlertDialog builder = new AlertDialog.Builder(MainActivity.this).create();
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface)
                    {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run() {
                                PCommon.ShowArticle(getApplicationContext(), "ART_APP_LOG");
                            }
                        }, 500);
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run() {
                                PCommon.ShowArticle(getApplicationContext(),"ART_APP_HELP");
                            }
                        }, 1000);

                        handler = new Handler();
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run() {
                                PCommon.ShowDialog(MainActivity.this, R.string.languageInstalling, R.string.installMsg, R.string.optionsDisabledInstalling);
                            }
                        }, 1500);
                    }
                });

                PCommon.SelectBibleLanguage(builder, getApplicationContext(), view, msg, desc, false, true);
                builder.show();
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        try
        {
            getMenuInflater().inflate(R.menu.menu_bible, menu);

            if (!isUiTelevision)
            {
                final int INSTALL_STATUS = PCommon.GetInstallStatus(getApplicationContext());
                if (INSTALL_STATUS != 5)
                {
                    menu.findItem(R.id.mnu_prbl).setEnabled(false);
                    menu.findItem(R.id.mnu_articles).setEnabled(false);
                    menu.findItem(R.id.mnu_plans).setEnabled(false);
                    menu.findItem(R.id.mnu_group_settings).setEnabled(false);
                    menu.findItem(R.id.mnu_edit).setEnabled(false);
                    menu.findItem(R.id.mnu_edit_select_from).setEnabled(false);
                    menu.findItem(R.id.mnu_edit_select_to).setEnabled(false);
                    menu.findItem(R.id.mnu_edit_move).setEnabled(false);
                    menu.findItem(R.id.mnu_edit_add).setEnabled(false);
                    menu.findItem(R.id.mnu_edit_update).setEnabled(false);
                    menu.findItem(R.id.mnu_edit_remove).setEnabled(false);

                    final String contentMsg = GetInstallStatusMsg();
                    final Snackbar snackbar = Snackbar.make(llMain, contentMsg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        try
        {
            final int id = item.getItemId();
            switch (id)
            {
                case R.id.mnu_language:

                    PCommon.SavePref(getApplicationContext(), IProject.APP_PREF_KEY.BIBLE_NAME, "");
                    onPostResume();
                    return true;

                case R.id.mnu_add_tab:

                    Tab.AddTab(getApplicationContext());
                    return true;

                case R.id.mnu_remove_tab:

                    Tab.RemoveCurrentTab(getApplicationContext());
                    return true;

                case R.id.mnu_show_fav:

                    ShowFav();
                    return true;

                case R.id.mnu_plan:

                    final int planId = Integer.parseInt(PCommon.GetPref(getApplicationContext(), IProject.APP_PREF_KEY.PLAN_ID, "-1"));
                    final int planPageNumber = Integer.parseInt(PCommon.GetPref(getApplicationContext(), IProject.APP_PREF_KEY.PLAN_PAGE, "-1"));
                    ShowPlan(planId, planPageNumber);
                    return true;

                case R.id.mnu_books:

                    ShowBooks(this);
                    return true;

                case R.id.mnu_plans:

                    ShowPlans();
                    return true;

                case R.id.mnu_reading:

                    ShowFav("2");
                    return true;

                case R.id.mnu_prbl:

                    ShowPrbl();
                    return true;

                case R.id.mnu_articles:

                    PCommon.ShowArticles(this);
                    return true;

                case R.id.mnu_group_settings:

                    final Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
                    startActivityForResult(intent, 1);
                    return true;

                case R.id.mnu_help:

                    PCommon.ShowArticle(this,"ART_APP_HELP");
                    return true;

                case R.id.mnu_about:

                    ShowAbout(this);
                    return true;

                case R.id.mnu_quit:

                    PCommon.TryQuitApplication(getApplicationContext());
                    return true;
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /* This code has been removed since version 3.7
        PCommon.SetSound(getApplicationContext(), false);
        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tabLayout != null)
        {
            tabLayout.removeAllTabs();
            tabLayout.removeAllViews();
            tabLayout = null;
        }

        _s = null;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    recreate();
                }
            }, 500);
        }
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

    /***
     * Get install status percentage
     * @return percentage
     */
    private int GetInstallStatusPerc()
    {
        final int pos = _s.GetBibleIdCount();

        return Math.round((pos * 100) / 155510);
    }

    /***
     * Get install status msg
     * @return Content msg
     */
    private String GetInstallStatusMsg()
    {
        final int perc = GetInstallStatusPerc();

        return PCommon.ConcaT(getString(R.string.languageInstalling), " (", perc == 0 ? 1 : perc, "%)");
    }

    /***
     * Show Fav (open Fav or goto it)
     */
    private void ShowFav()
    {
        try
        {
            if (tabLayout == null) return;

            final int tabCount = tabLayout.getTabCount();
            @SuppressWarnings("UnusedAssignment") boolean isFavShow = false;

            CacheTabBO cacheTabFav = _s.GetCacheTabFav();
            if (cacheTabFav == null)
            {
                //noinspection ConstantConditions
                isFavShow = false;

                cacheTabFav = new CacheTabBO();
                cacheTabFav.tabNumber = -1;
                cacheTabFav.tabType = "F";
                cacheTabFav.tabTitle = getString(R.string.favHeader);

                _s.SaveCacheTabFav(cacheTabFav);
            }
            else
            {
                isFavShow = (cacheTabFav.tabNumber >= 0);
            }

            isFavShow = !isFavShow;
            if (isFavShow)
            {
                //Show fav tab
                //############
                for (int i=tabCount-1; i >= 0; i--)
                {
                    _s.UpdateCacheId(i, i+1);
                }

                cacheTabFav.tabNumber = 0;
                _s.SaveCacheTabFav(cacheTabFav);

                final TabLayout.Tab tab = tabLayout.newTab().setText(R.string.favHeader);
                tabLayout.addTab(tab, 0);
            }
            Tab.FullScrollTab(getApplicationContext(), HorizontalScrollView.FOCUS_LEFT);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    private void ShowBooks(final Context context)
    {
        try
        {
            final int installStatus = PCommon.GetInstallStatus(context);
            if (installStatus < 1) return;

            final Typeface typeface = PCommon.GetTypeface(context);
            final int fontSize = PCommon.GetFontSize(context);
            final String bbnm = PCommon.GetPref(context, IProject.APP_PREF_KEY.BIBLE_NAME, "k");

            final AlertDialog builderBook = new AlertDialog.Builder(context).create();
            final LayoutInflater inflater = getLayoutInflater();

            final ScrollView sv = new ScrollView(context);
            sv.setLayoutParams(PCommon._layoutParamsMatchAndWrap);

            final ArrayList<BibleRefBO> lstRef =_s.GetListAllBookByName(bbnm);
            final LinearLayout llBooks = new LinearLayout(context);
            llBooks.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            llBooks.setOrientation(LinearLayout.VERTICAL);
            llBooks.setPadding(0, 15, 0, 15);

            final AlertDialog builderChapter = new AlertDialog.Builder(context).create();             //, R.style.DialogStyleKaki
            final View vwSvSelection = inflater.inflate(R.layout.fragment_selection_items, (ViewGroup) findViewById(R.id.svSelection));

            final AlertDialog builderLanguages = new AlertDialog.Builder(context).create();
            final View vllLanguages = inflater.inflate(PCommon.SetUILayout(context, R.layout.fragment_languages_multi, R.layout.fragment_languages_multi_tv), (ViewGroup) findViewById(R.id.llLanguages));

            int bNumber;
            String refText;
            String refNr;
            boolean isBookExist;
            int bNumberParam;
            boolean shouldWarn = false;

            for (BibleRefBO ref : lstRef)
            {
                bNumber = ref.bNumber;
                refNr = String.format(Locale.US, "%2d", bNumber);
                refText = PCommon.ConcaT(refNr, ": ", ref.bName, " (", ref.bsName, ")");

                final TextView tvBook = new TextView(context);
                tvBook.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvBook.setPadding(10, 15, 10, 15);
                tvBook.setText( refText );
                tvBook.setTag( bNumber );

                bNumberParam = (bNumber != 66) ? bNumber + 1 : 66;
                isBookExist = (installStatus == 5) || _s.IsBookExist(bNumberParam);
                if (isBookExist)
                {
                    tvBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            try
                            {
                                final int bNumber = (int) view.getTag();
                                if (PCommon._isDebugVersion) System.out.println(bNumber);

                                final int chapterMax = _s.GetBookChapterMax(bNumber);
                                if (chapterMax < 1)
                                {
                                    PCommon.ShowToast(view.getContext(), R.string.toastBookNotInstalled, Toast.LENGTH_SHORT);
                                    return;
                                }
                                final String[] titleArr = ((TextView)view).getText().toString().substring(3).split("\\(");
                                final String title = PCommon.ConcaT(getString(R.string.mnuBook), ": ", titleArr[0]);

                                PCommon.SelectItem(builderChapter, view.getContext(), vwSvSelection, title, R.string.tvChapter, "", true, chapterMax, false);
                                builderChapter.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        PCommon.SelectBibleLanguageMulti(builderLanguages, view.getContext(), vllLanguages, title, "", true, false);
                                        builderLanguages.setOnDismissListener(new DialogInterface.OnDismissListener()
                                        {
                                            @Override
                                            public void onDismiss(DialogInterface dialogInterface)
                                            {
                                                final String bbName = PCommon.GetPrefBibleName(context);
                                                final String bbname = PCommon.GetPref(view.getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, bbName);
                                                if (bbname.equals("")) return;
                                                final String tbbName = PCommon.GetPrefTradBibleName(view.getContext(), true);
                                                final int cNumber = Integer.parseInt(PCommon.GetPref(view.getContext(), IProject.APP_PREF_KEY.BOOK_CHAPTER_DIALOG, "1"));
                                                final String fullQuery = PCommon.ConcaT(bNumber, " ", cNumber);
                                                MainActivity.Tab.AddTab(view.getContext(), "S", tbbName, fullQuery);
                                            }
                                        });
                                        builderLanguages.show();
                                    }
                                });
                                builderChapter.show();
                            }
                            catch (Exception ex)
                            {
                                if (PCommon._isDebugVersion) PCommon.LogR(view.getContext(), ex);
                            }
                            finally
                            {
                                builderBook.dismiss();
                            }
                        }
                    });
                }
                else
                {
                    if (!shouldWarn) shouldWarn = true;
                    tvBook.setEnabled( false );
                }
                //TODO FAB: slow GetDrawable
                tvBook.setFocusable(true);
                tvBook.setBackground(PCommon.GetDrawable(context, R.drawable.focus_text));

                //Font
                if (typeface != null) { tvBook.setTypeface(typeface); }
                tvBook.setTextSize(fontSize);

                llBooks.addView(tvBook);
            }

            final Typeface tfTitle = Typeface.defaultFromStyle(Typeface.BOLD);
            final TextView tvNT = new TextView(context);
            tvNT.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            tvNT.setPadding(10, 20, 10, 20);
            tvNT.setGravity(Gravity.CENTER_HORIZONTAL);
            tvNT.setText( R.string.tvBookNT );
            tvNT.setTextSize(fontSize);
            tvNT.setTypeface( tfTitle );
            llBooks.addView(tvNT, 39);

            final TextView tvOT = new TextView(context);
            tvOT.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            tvOT.setPadding(10, 20, 10, 20);
            tvOT.setGravity(Gravity.CENTER_HORIZONTAL);
            tvOT.setText( R.string.tvBookOT );
            tvOT.setTextSize(fontSize);
            tvOT.setTypeface( tfTitle );
            llBooks.addView(tvOT, 0);

            if (shouldWarn)
            {
                final TextView tvWarn = new TextView(context);
                tvWarn.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvWarn.setPadding(10, 10, 10, 20);
                tvWarn.setGravity(Gravity.CENTER_HORIZONTAL);
                tvWarn.setText( R.string.tvBookInstall );
                tvWarn.setTextSize(fontSize);
                llBooks.addView(tvWarn, 0);
            }
            sv.addView(llBooks);

            builderBook.setTitle(R.string.mnuBooks);
            builderBook.setCancelable(true);
            builderBook.setView(sv);
            builderBook.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    private void ShowPrbl()
    {
        try
        {
            final AlertDialog builder = new AlertDialog.Builder(this).create();                     //R.style.DialogStyleKaki
            final ScrollView sv = new ScrollView(this);
            sv.setLayoutParams(PCommon._layoutParamsMatchAndWrap);

            final LinearLayout llPrbl = new LinearLayout(this);
            llPrbl.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            llPrbl.setOrientation(LinearLayout.VERTICAL);
            llPrbl.setPadding(0, 15, 0, 15);

            final Typeface typeface = PCommon.GetTypeface(this);
            final int fontSize = PCommon.GetFontSize(this);

            int resId;
            String[] prblValue;
            TextView tvPrbl;
            String text;

            final String bbName = PCommon.GetPref(getApplicationContext(), IProject.APP_PREF_KEY.BIBLE_NAME, "k");
            final AlertDialog builderLanguages = new AlertDialog.Builder(this).create();             //, R.style.DialogStyleKaki
            final LayoutInflater inflater = getLayoutInflater();
            final View vllLanguages = inflater.inflate(PCommon.SetUILayout(this, R.layout.fragment_languages_multi, R.layout.fragment_languages_multi_tv), (ViewGroup) findViewById(R.id.llLanguages));

            for (String prblRef : this.getResources().getStringArray(R.array.PRBL_ARRAY))
            {
                prblValue = prblRef.split("\\|");
                resId = PCommon.GetResId(this, prblValue[0]);
                text = PCommon.ConcaT(getString(R.string.bulletDefault), " ", getString(resId));

                tvPrbl = new TextView(this);
                tvPrbl.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvPrbl.setPadding(10, 15, 10, 15);
                tvPrbl.setText( text );
                tvPrbl.setTag( prblValue[1] );
                tvPrbl.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View view)
                    {
                        try
                        {
                            final String fullQuery = (String) view.getTag();
                            if (PCommon._isDebugVersion) System.out.println(fullQuery);

                            final String msg = ((TextView)view).getText().toString().substring(2);
                            PCommon.SelectBibleLanguageMulti(builderLanguages, view.getContext(), vllLanguages, msg, "", true, false);
                            builderLanguages.setOnDismissListener(new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface)
                                {
                                    final String bbname = PCommon.GetPref(view.getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, bbName);
                                    if (bbname.equals("")) return;
                                    final String tbbName = PCommon.GetPrefTradBibleName(view.getContext(), true);
                                    MainActivity.Tab.AddTab(view.getContext(), "S", tbbName, fullQuery);
                                }
                            });
                            builderLanguages.show();
                        }
                        catch (Exception ex)
                        {
                            if (PCommon._isDebugVersion) PCommon.LogR(view.getContext(), ex);
                        }
                        finally
                        {
                            builder.dismiss();
                        }
                    }
                });
                tvPrbl.setFocusable(true);
                tvPrbl.setBackground(PCommon.GetDrawable(getApplicationContext(), R.drawable.focus_text));

                //Font
                if (typeface != null) { tvPrbl.setTypeface(typeface); }
                tvPrbl.setTextSize(fontSize);

                llPrbl.addView(tvPrbl);
            }
            sv.addView(llPrbl);

            builder.setTitle(R.string.mnuPrbl);
            builder.setCancelable(true);
            builder.setView(sv);
            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    private TextView CreateTvTitle(final int titleId, final int fontSize)
    {
        try
        {
            final Typeface tfTitle = Typeface.defaultFromStyle(Typeface.BOLD);
            final TextView tvTitle = new TextView(this);
            tvTitle.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            tvTitle.setPadding(10, 30, 10, 20);
            tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            tvTitle.setText( titleId );
            tvTitle.setTextSize(fontSize);
            tvTitle.setTypeface( tfTitle );

            return tvTitle;
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }

        return null;
    }

    private void ShowPlans()
    {
        try
        {
            final AlertDialog builder = new AlertDialog.Builder(this).create();
            final ScrollView sv = new ScrollView(this);
            sv.setLayoutParams(PCommon._layoutParamsMatchAndWrap);

            final LinearLayout llPlans = new LinearLayout(this);
            llPlans.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            llPlans.setOrientation(LinearLayout.VERTICAL);
            llPlans.setPadding(0, 15, 0, 15);

            final Typeface typeface = PCommon.GetTypeface(this);
            final int fontSize = PCommon.GetFontSize(this);

            TextView tvPlan;

            int resId, idx;
            String[] cols;
            String text;

            idx = 0;
            for (String plan : this.getResources().getStringArray(R.array.PLAN_ARRAY))
            {
                cols = plan.split("\\|");
                final String planRef = cols[0];
                final boolean planExist = _s.IsPlanDescExist(planRef);

                resId = PCommon.GetResId(this, planRef);
                text = PCommon.ConcaT(getString(R.string.bulletDefault), " ", getString(resId));

                tvPlan = new TextView(this);
                tvPlan.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvPlan.setPadding(10, 15, 10, 15);
                tvPlan.setText( text );
                tvPlan.setTag( idx );
                tvPlan.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View view)
                    {
                        try
                        {
                            final int planIdx = Integer.parseInt(view.getTag().toString());
                            ShowPlan( true, planRef, planIdx, -1, -1);
                        }
                        catch (Exception ex)
                        {
                            if (PCommon._isDebugVersion) PCommon.LogR(view.getContext(), ex);
                        }
                        finally
                        {
                            builder.dismiss();
                        }
                    }
                });
                if (planExist) tvPlan.setEnabled(false);
                tvPlan.setFocusable(true);
                tvPlan.setBackground(PCommon.GetDrawable(getApplicationContext(), R.drawable.focus_text));

                //Font
                if (typeface != null) { tvPlan.setTypeface(typeface); }
                tvPlan.setTextSize(fontSize);

                llPlans.addView(tvPlan);
                idx++;
            }
            TextView tvTitle = CreateTvTitle(R.string.tvPlanThemes, fontSize);
            if (tvTitle != null) llPlans.addView(tvTitle, 0);
            tvTitle = CreateTvTitle(R.string.tvPlanBooks, fontSize);
            if (tvTitle != null) llPlans.addView(tvTitle, 10);

            //~~~

            final int planDescIdMax = _s.GetPlanDescIdMax();
            if (planDescIdMax <= 0)
            {
                if (!isPlanSelectAlreadyWarned)
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run() {
                            isPlanSelectAlreadyWarned = true;
                            PCommon.ShowDialog(MainActivity.this, R.string.planSelect, R.string.planSelectMsg);
                        }
                    }, 500);
                }
            }
            else
            {
                final GridLayout glYourPlans = new GridLayout(this);
                glYourPlans.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                glYourPlans.setColumnCount(1);
                llPlans.addView(glYourPlans, 0);

                ArrayList<PlanDescBO> lstPd = _s.GetAllPlanDesc();
                for (PlanDescBO pd : lstPd)
                {
                    idx = 0;
                    @SuppressWarnings("UnusedAssignment") String plan = null;

                    for (String planToFind : this.getResources().getStringArray(R.array.PLAN_ARRAY))
                    {
                        cols = planToFind.split("\\|");
                        if (cols[0].compareTo(pd.planRef) == 0)
                        {
                            final int planId = pd.planId;
                            plan = planToFind;

                            cols = plan.split("\\|");
                            final String planRef = cols[0];
                            resId = PCommon.GetResId(this, planRef);
                            text = PCommon.ConcaT(getString(R.string.bulletDefault), " ", getString(resId));

                            tvPlan = new TextView(this);
                            tvPlan.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                            tvPlan.setPadding(10, 10, 10, 0);
                            tvPlan.setText(text);
                            tvPlan.setTag(idx);
                            tvPlan.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(final View view)
                                {
                                    try
                                    {
                                        final int planIdx = Integer.parseInt(view.getTag().toString());
                                        if (PCommon._isDebugVersion) System.out.println(planIdx);
                                        ShowPlan(false, planRef, planIdx, planId, -1);
                                    }
                                    catch (Exception ex)
                                    {
                                        if (PCommon._isDebugVersion) PCommon.LogR(view.getContext(), ex);
                                    }
                                    finally
                                    {
                                        builder.dismiss();
                                    }
                                }
                            });
                            tvPlan.setFocusable(true);
                            tvPlan.setBackground(PCommon.GetDrawable(getApplicationContext(), R.drawable.focus_text));

                            text = _s.GetPlanCalProgressStatus(planId);
                            final TextView tvStatus = new TextView(this);
                            tvStatus.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                            tvStatus.setPadding(50, 10, 10, 0);
                            //noinspection deprecation
                            tvStatus.setText(Html.fromHtml(text));
                            tvStatus.setTag(idx);
                            tvStatus.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(final View view)
                                {
                                    try
                                    {
                                        final int planIdx = Integer.parseInt(view.getTag().toString());
                                        if (PCommon._isDebugVersion) System.out.println(planIdx);
                                        ShowPlan(false, planRef, planIdx, planId, -1);
                                    }
                                    catch (Exception ex)
                                    {
                                        if (PCommon._isDebugVersion) PCommon.LogR(view.getContext(), ex);
                                    }
                                    finally
                                    {
                                        builder.dismiss();
                                    }
                                }
                            });
                            tvStatus.setOnLongClickListener(new View.OnLongClickListener()
                            {
                                @Override
                                public boolean onLongClick(View view)
                                {
                                    ShowPlansMenu(builder, planId);
                                    return false;
                                }
                            });
                            //Font
                            if (typeface != null) { tvPlan.setTypeface(typeface); }
                            tvPlan.setTextSize(fontSize);

                            glYourPlans.addView(tvStatus, 0);
                            glYourPlans.addView(tvPlan, 0);
                        }
                        idx++;
                    }
                }
                if (lstPd.size() > 0) lstPd.clear();
            }
            tvTitle = CreateTvTitle(R.string.tvPlanYourPlans, fontSize);
            if (tvTitle != null) llPlans.addView(tvTitle, 0);

            //~~~
            sv.addView(llPlans);

            builder.setTitle(R.string.mnuPlansReading);
            builder.setCancelable(true);
            builder.setView(sv);
            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    /***
     * Show plans context menu
     * @param dlgPlans  Parent dialog
     * @param planId    Plan Id
     */
    private void ShowPlansMenu(final AlertDialog dlgPlans, final int planId)
    {
        try
        {
            final PlanDescBO pd = _s.GetPlanDesc(planId);
            if (pd == null) return;

            final Typeface typeface = PCommon.GetTypeface(this);
            final int fontSize = PCommon.GetFontSize(this);

            final LayoutInflater inflater = this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_plans_menu, (ViewGroup) this.findViewById(R.id.llPlansMenu));

            final AlertDialog builder = new AlertDialog.Builder(this).create();
            builder.setCancelable(true);
            builder.setTitle(R.string.mnuPlanReading);
            builder.setView(view);

            final int resId = PCommon.GetResId(getApplicationContext(), pd.planRef);
            final String planTitle = PCommon.ConcaT("<b>", getString(resId), " :</b>");
            final TextView tvPlanTitle = view.findViewById(R.id.tvPlanTitle);
            //noinspection deprecation
            tvPlanTitle.setText(Html.fromHtml(planTitle));
            if (typeface != null) { tvPlanTitle.setTypeface(typeface); }
            tvPlanTitle.setTextSize(fontSize);

            final Button btnDelete = view.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    btnDelete.setEnabled(false);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            _s.DeletePlan(planId);
                            builder.dismiss();
                            dlgPlans.dismiss();
                            ShowPlans();
                        }
                    }, 500);
                }
            });
            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    /***
     * Show plan
     * @param planId        Plan Id
     * @param pageNumber    Page number (should be >= 0 (0 = first page))
     */
    private void ShowPlan(final int planId, final int pageNumber)
    {
        try
        {
            final PlanDescBO pd = _s.GetPlanDesc(planId);
            final boolean isNewPlan = pd == null;
            if (isNewPlan) return;

            int idx = 0, planIdx = -1;
            String[] cols;

            for (String planToFind : this.getResources().getStringArray(R.array.PLAN_ARRAY))
            {
                cols = planToFind.split("\\|");
                if (cols[0].compareTo(pd.planRef) == 0)
                {
                    planIdx = idx;
                    break;
                }

                idx++;
            }
            if (planIdx < 0) return;

            //noinspection ConstantConditions
            ShowPlan(isNewPlan, pd.planRef, planIdx, planId, pageNumber);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    /***
     * Show plan
     * @param isNewPlan     True: new plan, False: Update
     * @param planRef       Plan reference
     * @param planIdx       Position in array of templates
     * @param planId        Plan Id
     * @param pageNumber    Page number (should be >= 0 (0 = first page), -1 = auto)
     */
    private void ShowPlan(final boolean isNewPlan, final String planRef, final int planIdx, final int planId, final int pageNumber)
    {
        try
        {
            final Typeface typeface = PCommon.GetTypeface(this);
            final int fontSize = PCommon.GetFontSize(this);

            //Check PageNumber
            final String bbname = PCommon.GetPrefBibleName(this);
            final int planCalRowCount = _s.GetPlanCalRowCount(bbname, planId);
            final int pageSize = 31;
            final int pageCount = ((planCalRowCount / pageSize) + 1);
            final int fpageNumber;
            if (pageNumber <= 0)
            {
                //todo find dayNumber
                fpageNumber = 0;
            }
            else if (pageNumber >= pageCount)
            {
                fpageNumber = pageCount - 1;
            }
            else
            {
                fpageNumber = pageNumber;
            }

            final String plan = getApplicationContext().getResources().getStringArray(R.array.PLAN_ARRAY)[planIdx];
            final String[] cols = plan.split("\\|");
            @SuppressWarnings("UnusedAssignment") int bCount = 0, cCount = 0, vCount = 0, bNumber;
            final PlanDescBO pd;
            final String dtFormat = "yyyyMMdd";

            if (isNewPlan)
            {
                Integer[] ciTot = { 0, 0 };
                @SuppressWarnings("UnusedAssignment") Integer[] ci = null;

                //for book: get nb chapters & nb verses
                final String[] books = cols[1].split(",");
                for (String bookNumber : books)
                {
                    bNumber = Integer.parseInt(bookNumber);
                    ci = _s.GetBibleCiByBook( bNumber );

                    ciTot[ 0 ] += ci[ 0 ];
                    ciTot[ 1 ] += ci[ 1 ];
                }
                bCount = books.length;
                cCount = ciTot[ 0 ];
                vCount = ciTot[ 1 ];

                pd = new PlanDescBO();
                pd.planId = _s.GetPlanDescIdMax() + 1;
                pd.planRef = planRef;
                pd.bCount = bCount;
                pd.cCount = cCount;
                pd.vCount = vCount;

                //noinspection UnusedAssignment
                ci = null;
                //noinspection UnusedAssignment
                ciTot = null;
            }
            else
            {
                pd = _s.GetPlanDesc(planId);
                if (pd == null) return;

                bCount = pd.bCount;
                cCount = pd.cCount;
                vCount = pd.vCount;
            }

            //Dialog
            final LayoutInflater inflater = this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_plan, (ViewGroup) this.findViewById(R.id.llPlan));

            final int planRefResId = PCommon.GetResId(getApplicationContext(), planRef);
            final String builderTitle = PCommon.ConcaT(getString(R.string.mnuPlan), ": ", getString(planRefResId));
            final AlertDialog builder = new AlertDialog.Builder(this).create();
            builder.setCancelable(true);
            builder.setTitle(builderTitle);
            builder.setView(view);

            final String strPlanDesc = PCommon.ConcaT(getResources().getString(R.string.planBookCount), ": ", bCount, "\n",
                    getResources().getString(R.string.planChapterCount), ": ", cCount, "\n",
                    getResources().getString(R.string.planVerseCount), ": ", vCount, "\n");
            final TextView tvPlanDesc = view.findViewById(R.id.tvPlanDesc);
            tvPlanDesc.setText(strPlanDesc);
            final Button btnGotoPlans = view.findViewById(R.id.btnGotoPlans);
            btnGotoPlans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    builder.dismiss();
                    ShowPlans();
                }
            });

            if (isNewPlan)
            {
                final int defaultVdayCount = 40;
                final int maxVerses = 2000;
                final int maxDays = 4444;
                final View glPlanCalMeasures = view.findViewById(R.id.glPlanCalMeasures);
                glPlanCalMeasures.setVisibility(View.VISIBLE);

                final NumberPicker npVerseCount = view.findViewById(R.id.npVerseCount);
                final NumberPicker npDayCount = view.findViewById(R.id.npDayCount);
                npVerseCount.setMinValue(7);
                npVerseCount.setMaxValue( pd.vCount < maxVerses ? pd.vCount : maxVerses );
                npVerseCount.setValue(defaultVdayCount);
                npVerseCount.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        pd.vDayCount = npVerseCount.getValue();
                        pd.dayCount = pd.vCount % pd.vDayCount == 0 ? pd.vCount / pd.vDayCount : (pd.vCount / pd.vDayCount) + 1;
                        npDayCount.setValue(pd.dayCount);
                    }
                });
                npDayCount.setMinValue(1);
                npDayCount.setMaxValue( pd.vCount < maxDays ? pd.vCount : maxDays );
                npDayCount.setValue((pd.vCount/defaultVdayCount) + 1);
                npDayCount.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        pd.dayCount = npDayCount.getValue();
                        pd.vDayCount = pd.vCount % pd.dayCount == 0 ? pd.vCount / pd.dayCount : (pd.vCount / pd.dayCount) + 1;
                        npVerseCount.setValue(pd.vDayCount);
                    }
                });

                final Calendar nowCal = Calendar.getInstance();
                pd.startDt = DateFormat.format(dtFormat, nowCal).toString();
                final Button btnPlanSetStartDt = view.findViewById(R.id.btnPlanSetStartDt);
                btnPlanSetStartDt.setText(pd.startDt);
                btnPlanSetStartDt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        final DatePickerDialog.OnDateSetListener setDateListener = new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                            {
                                final Calendar startDt = Calendar.getInstance();
                                startDt.set(year, month, dayOfMonth);
                                final String startDtStr = DateFormat.format(dtFormat, startDt).toString();
                                btnPlanSetStartDt.setText(startDtStr);
                                pd.startDt = startDtStr;
                            }
                        };
                        final DatePickerDialog dtFragment = new DatePickerDialog(v.getContext(), setDateListener, nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH), nowCal.get(Calendar.DAY_OF_MONTH));
                        dtFragment.show();
                    }
                });

                final Button btnPlanCreate = view.findViewById(R.id.btnPlanCreate);
                btnPlanCreate.setVisibility(View.VISIBLE);
                btnPlanCreate.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        btnPlanCreate.setEnabled(false);
                        btnGotoPlans.setEnabled(false);

                        pd.vDayCount = npVerseCount.getValue();
                        pd.dayCount =  pd.vCount % pd.vDayCount == 0 ? pd.vCount / pd.vDayCount : (pd.vCount / pd.vDayCount) + 1;

                        if (pd.dayCount > maxDays || pd.vDayCount > maxVerses)
                        {
                            final int msgId = pd.dayCount > maxDays ? R.string.seeMat24_32 : R.string.seeGen2_2;
                            PCommon.ShowToast(getApplicationContext(), msgId, Toast.LENGTH_LONG);
                            builder.dismiss();
                            ShowPlans();
                            return;
                        }

                        final Calendar cal = Calendar.getInstance();
                        cal.set(Integer.parseInt(pd.startDt.substring(0, 4)),
                                Integer.parseInt(pd.startDt.substring(4, 6)),
                                Integer.parseInt(pd.startDt.substring(6, 8)));
                        cal.add(Calendar.DAY_OF_MONTH, pd.dayCount);
                        pd.endDt = DateFormat.format(dtFormat, cal).toString();

                        final ProgressDialog pgr = new ProgressDialog(view.getContext());
                        pgr.setMessage(getString(R.string.planCreating));
                        pgr.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pgr.setIndeterminate(true);
                        pgr.show();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                pd.planId = _s.GetPlanDescIdMax() + 1;
                                _s.AddPlan(pd, cols[ 1 ]);

                                builder.dismiss();
                                ShowPlans();

                                pgr.dismiss();
                            }
                        }, 500);
                    }
                });
            }
            else
            {
                final Button btnDelete = view.findViewById(R.id.btnDelete);
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowPlansMenu(builder, planId);
                    }
                });
                final Button btnBack = view.findViewById(R.id.btnBack);
                btnBack.setVisibility(View.VISIBLE);
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        builder.dismiss();
                        ShowPlan(planId, fpageNumber - 1);
                    }
                });
                final Button btnForward = view.findViewById(R.id.btnForward);
                btnForward.setVisibility(View.VISIBLE);
                btnForward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        builder.dismiss();
                        ShowPlan(planId, fpageNumber + 1);
                    }
                });
                if (fpageNumber == 0) btnBack.setEnabled(false);
                if (fpageNumber == pageCount - 1) btnForward.setEnabled(false);

                final GridLayout glCal = view.findViewById(R.id.glCal);
                glCal.setVisibility(View.VISIBLE);

                final ArrayList<PlanCalBO> lstCal = _s.GetPlanCal(bbname, pd.planId, fpageNumber);
                TextView tvDay, tvUntil, tvTitleIsRead;
                CheckBox chkIsRead;
                String strDay, strUntil;

                tvDay = new TextView(this);
                tvDay.setLayoutParams(PCommon._layoutParamsWrap);
                tvDay.setPadding(10, 10, 10, 10);
                PCommon.SetTextAppareance(tvDay, this, R.style.TextAppearance_AppCompat_Subhead);
                //noinspection deprecation
                tvDay.setText( Html.fromHtml( PCommon.ConcaT("<b>", getString(R.string.planCalTitleDt).replaceFirst("\n", "<br><u>"), "</b>")));

                tvUntil = new TextView(this);
                tvUntil.setLayoutParams(PCommon._layoutParamsWrap);
                tvUntil.setPadding(10, 10, 10, 10);
                PCommon.SetTextAppareance(tvUntil, this, R.style.TextAppearance_AppCompat_Subhead);
                //noinspection deprecation
                tvUntil.setText( Html.fromHtml( PCommon.ConcaT("<b>", getString(R.string.planCalTitleUntil).replaceFirst("\n", "<br><u>"), "</b>")));

                tvTitleIsRead = new TextView(this);
                tvTitleIsRead.setLayoutParams(PCommon._layoutParamsWrap);
                tvTitleIsRead.setPadding(10, 10, 10, 10);
                PCommon.SetTextAppareance(tvTitleIsRead, this, R.style.TextAppearance_AppCompat_Subhead);
                //noinspection deprecation
                tvTitleIsRead.setText( Html.fromHtml( PCommon.ConcaT("<b>", getString(R.string.planCalTitleIsRead).replaceFirst("\n", "<br><u>"), "</b>")));

                glCal.addView(tvTitleIsRead);
                glCal.addView(tvDay);
                glCal.addView(tvUntil);

                final int nowDayNumber = _s.GetCurrentDayNumberOfPlanCal(planId);
                for (PlanCalBO pc : lstCal)
                {
                    chkIsRead = new CheckBox(this);
                    chkIsRead.setLayoutParams(PCommon._layoutParamsWrap);
                    chkIsRead.setPadding(10, 10, 10, 10);
                    chkIsRead.setEnabled(true);
                    chkIsRead.setTag(R.id.tv1, pc.planId);
                    chkIsRead.setTag(R.id.tv2, pc.dayNumber);
                    chkIsRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                            final int planId = (int) v.getTag(R.id.tv1);
                            final int dayNumber = (int) v.getTag(R.id.tv2);
                            final int isRead = isChecked ? 1 : 0;
                            _s.MarkPlanCal(planId, dayNumber, isRead);
                        }
                    });
                    chkIsRead.setChecked(pc.isRead == 1);

                    tvDay = new TextView(this);
                    tvDay.setLayoutParams(PCommon._layoutParamsWrap);
                    tvDay.setPadding(10, 10, 10, 10);
                    strDay = PCommon.ConcaT(pc.dayNumber, "\n", pc.dayDt);
                    tvDay.setText( strDay );
                    if (typeface != null) { tvDay.setTypeface(typeface); }
                    tvDay.setTextSize(fontSize);
                    if (pc.dayNumber == nowDayNumber)
                    {
                        final Typeface tfBold = Typeface.defaultFromStyle(Typeface.BOLD);
                        tvDay.setTypeface(tfBold);
                    }
                    tvDay.setTag(R.id.tv1, pc.planId);
                    tvDay.setTag(R.id.tv2, pc.dayNumber);
                    tvDay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            final int planId = (int) v.getTag(R.id.tv1);
                            final int dayNumber = (int) v.getTag(R.id.tv2);
                            ShowPlanMenu(builder, planId, dayNumber, fpageNumber);
                        }
                    });
                    tvDay.setFocusable(true);
                    tvDay.setBackground(PCommon.GetDrawable(getApplicationContext(), R.drawable.focus_text));

                    tvUntil = new TextView(this);
                    tvUntil.setLayoutParams(PCommon._layoutParamsWrap);
                    tvUntil.setPadding(10, 10, 10, 10);
                    strUntil = PCommon.ConcaT(pc.bsNameStart, " ", pc.cNumberStart, ".", pc.vNumberStart, "\n", pc.bsNameEnd, " ", pc.cNumberEnd, ".", pc.vNumberEnd);
                    tvUntil.setText( strUntil );
                    if (typeface != null) { tvUntil.setTypeface(typeface); }
                    tvUntil.setTextSize(fontSize);
                    tvUntil.setTag(R.id.tv1, pc.planId);
                    tvUntil.setTag(R.id.tv2, pc.dayNumber);
                    tvUntil.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            final int planId = (int) v.getTag(R.id.tv1);
                            final int dayNumber = (int) v.getTag(R.id.tv2);
                            ShowPlanMenu(builder, planId, dayNumber, fpageNumber);
                        }
                    });
                    tvUntil.setFocusable(true);
                    tvUntil.setBackground(PCommon.GetDrawable(getApplicationContext(), R.drawable.focus_text));

                    glCal.addView(chkIsRead);
                    glCal.addView(tvDay);
                    glCal.addView(tvUntil);
                }
                btnGotoPlans.requestFocus();
            }

            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    /***
     * Show plan context menu
     * @param dlgPlan       Parent dialog
     * @param planId        Plan Id
     * @param dayNumber     Day number
     * @param pageNumber    Page number
     */
    private void ShowPlanMenu(final AlertDialog dlgPlan, final int planId, final int dayNumber, final int pageNumber)
    {
        try
        {
            final PlanDescBO pd = _s.GetPlanDesc(planId);
            if (pd == null) return;

            final Typeface typeface = PCommon.GetTypeface(this);
            final int fontSize = PCommon.GetFontSize(this);

            final LayoutInflater inflater = this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_plan_menu, (ViewGroup) this.findViewById(R.id.llPlanMenu));

            final AlertDialog builder = new AlertDialog.Builder(this).create();
            builder.setCancelable(true);
            builder.setTitle(R.string.mnuPlanReading);
            builder.setView(view);

            final int resId = PCommon.GetResId(getApplicationContext(), pd.planRef);
            final String planTitle = PCommon.ConcaT("<b>", getString(resId), " :</b>");
            final TextView tvPlanTitle = view.findViewById(R.id.tvPlanTitle);
            //noinspection deprecation
            tvPlanTitle.setText(Html.fromHtml(planTitle));
            if (typeface != null) { tvPlanTitle.setTypeface(typeface); }
            tvPlanTitle.setTextSize(fontSize);

            final Button btnOpen = view.findViewById(R.id.btnOpen);
            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    final Handler handler = new Handler();
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            final String bbname = PCommon.GetPrefBibleName(getApplicationContext());
                            if (bbname == null) return;
                            final String fullQuery = PCommon.ConcaT(planId, " ", dayNumber, " ", pageNumber);

                            final AlertDialog builderLanguages = new AlertDialog.Builder(view.getContext()).create();             //, R.style.DialogStyleKaki
                            final LayoutInflater inflater = getLayoutInflater();
                            final View vllLanguages = inflater.inflate(PCommon.SetUILayout(view.getContext(), R.layout.fragment_languages_multi, R.layout.fragment_languages_multi_tv), (ViewGroup) findViewById(R.id.llLanguages));
                            final String msg = getString(R.string.mnuPlanReading);
                            PCommon.SelectBibleLanguageMulti(builderLanguages, view.getContext(), vllLanguages, msg, "", true, false);
                            builderLanguages.setOnDismissListener(new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface)
                                {
                                    final String bbnamed = PCommon.GetPref(view.getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, bbname);
                                    if (bbnamed.equals("")) return;
                                    final String tbbName = PCommon.GetPrefTradBibleName(view.getContext(), true);
                                    MainActivity.Tab.AddTab(getApplicationContext(), "P", tbbName, fullQuery);
                                    builder.dismiss();
                                    dlgPlan.dismiss();
                                }
                            });
                            builderLanguages.show();
                        }
                    });
                }
            });
            final Button btnMarkAllAboveAsRead = view.findViewById(R.id.btnMarkAllAboveAsRead);
            btnMarkAllAboveAsRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            _s.MarkAllAbovePlanCal(planId, dayNumber, 1);
                            builder.dismiss();
                            dlgPlan.dismiss();
                            ShowPlan(planId, pageNumber);
                        }
                    });
                }
            });
            final Button btnUnmarkAllAboveAsRead = view.findViewById(R.id.btnUnmarkAllAboveAsRead);
            btnUnmarkAllAboveAsRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            _s.MarkAllAbovePlanCal(planId, dayNumber, 0);
                            builder.dismiss();
                            dlgPlan.dismiss();
                            ShowPlan(planId, pageNumber);
                        }
                    });
                }
            });
            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    /***
     * @param markType 2:reading, 1:fav
     */
    private void ShowFav(@SuppressWarnings("SameParameterValue") final String markType)
    {
        try
        {
            final AlertDialog builder = new AlertDialog.Builder(this).create();                     //R.style.DialogStyleKaki
            final ScrollView sv = new ScrollView(this);
            sv.setLayoutParams(PCommon._layoutParamsMatchAndWrap);

            final LinearLayout llReading = new LinearLayout(this);
            llReading.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            llReading.setOrientation(LinearLayout.VERTICAL);

            final Typeface typeface = PCommon.GetTypeface(this);
            final int fontSize = PCommon.GetFontSize(this);

            final String bbName = PCommon.GetPref(getApplicationContext(), IProject.APP_PREF_KEY.BIBLE_NAME, "k");
            final int orderBy = 0;          //TODO: it's hardcoded => enum list
            final ArrayList<VerseBO> lstVerse =_s.SearchNotes(bbName, "", orderBy, markType);
            if (lstVerse.size() == 0)
            {
                PCommon.ShowToast(getApplicationContext(), R.string.toastEmpty, Toast.LENGTH_SHORT);
                return;
            }

            TextView tvReading;
            String fullQuery;
            String markText;

            final AlertDialog builderLanguages = new AlertDialog.Builder(this).create();             //, R.style.DialogStyleKaki
            final LayoutInflater inflater = getLayoutInflater();
            final View vllLanguages = inflater.inflate(PCommon.SetUILayout(this, R.layout.fragment_languages_multi, R.layout.fragment_languages_multi_tv), (ViewGroup) findViewById(R.id.llLanguages));

            for (VerseBO verse : lstVerse)
            {
                fullQuery = PCommon.ConcaT(verse.bNumber, " ", verse.cNumber, " ", verse.vNumber);
                markText = PCommon.ConcaT(getString(R.string.bulletDefault), " <b>", verse.bName, " ", verse.cNumber, ".", verse.vNumber, ":</b><br>", verse.vText);

                tvReading = new TextView(this);
                tvReading.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvReading.setPadding(10, 10, 10, 10);
                //noinspection deprecation
                tvReading.setText( Html.fromHtml(markText));
                tvReading.setTag( fullQuery );
                tvReading.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View view)
                    {
                        try
                        {
                            final String tag = (String) view.getTag();
                            if (PCommon._isDebugVersion) System.out.println(tag);
                            final String[] ref = tag.split(" ");
                            final int bNumber = Integer.parseInt(ref[0]);
                            final int cNumber = Integer.parseInt(ref[1]);
                            final int vNumber = Integer.parseInt(ref[2]);
                            final String fullQuery = PCommon.ConcaT(bNumber, " ", cNumber);

                            final String msg = PCommon.ConcaT(getString(R.string.mnuReading), "");
                            PCommon.SelectBibleLanguageMulti(builderLanguages, view.getContext(), vllLanguages, msg, "", true, false);
                            builderLanguages.setOnDismissListener(new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface)
                                {
                                    final String bbname = PCommon.GetPref(view.getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, bbName);
                                    if (bbname.equals("")) return;
                                    final String tbbName = PCommon.GetPrefTradBibleName(view.getContext(), true);
                                    MainActivity.Tab.AddTab(view.getContext(), tbbName, bNumber, cNumber, fullQuery, vNumber);
                                }
                            });
                            builderLanguages.show();
                         }
                        catch (Exception ex)
                        {
                            if (PCommon._isDebugVersion) PCommon.LogR(view.getContext(), ex);
                        }
                        finally
                        {
                            builder.dismiss();
                        }
                    }
                });
                tvReading.setFocusable(true);
                tvReading.setBackground(PCommon.GetDrawable(getApplicationContext(), R.drawable.focus_text));

                //Font
                if (typeface != null) { tvReading.setTypeface(typeface); }
                tvReading.setTextSize(fontSize);

                llReading.addView(tvReading);
            }
            sv.addView(llReading);

            builder.setTitle(markType.equalsIgnoreCase("2") ? R.string.mnuReading : R.string.mnuFav);
            builder.setCancelable(true);
            builder.setView(sv);
            builder.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    private void ShowAbout(final Context context)
    {
        try
        {
            final AlertDialog builder = new AlertDialog.Builder(context).create();                  //R.style.DialogStyleKaki
            builder.setTitle(R.string.mnuAbout);
            builder.setCancelable(true);

            final Typeface typeface = PCommon.GetTypeface(this);
            final int fontSize = PCommon.GetFontSize(this);

            final LinearLayout llMain = new LinearLayout(context);
            llMain.setOrientation(LinearLayout.VERTICAL);
            llMain.setLayoutParams(PCommon._layoutParamsWrap);

            final ScrollView sv = new ScrollView(context);
            sv.setSmoothScrollingEnabled(false);
            sv.setPadding(0,0,0, 10);

            final LinearLayout llSv = new LinearLayout(context);
            llSv.setOrientation(LinearLayout.VERTICAL);
            llSv.setLayoutParams(PCommon._layoutParamsMatch);
            llSv.setPadding(10, 10, 10, 10);
            llSv.setVerticalGravity(Gravity.CENTER_VERTICAL);
            llSv.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            sv.addView(llSv);

            final PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            final int dbVersion = _s.GetDbVersion();
            final String app = PCommon.ConcaT("Bible Multi\n", getString(R.string.appName));
            final String devName = PCommon.ConcaT("hot", "little", "white", "dog");
            final String devEmail = PCommon.ConcaT(devName, "@", "gm", "ail", ".", "co", "m");
            final String aboutDev = PCommon.ConcaT(app, "\n", pi.versionName, " (", dbVersion, ") - ", pi.versionCode, "\n");
            final String aboutContent =  PCommon.ConcaT(context.getString(R.string.aboutContactMe));

            //---
            final ImageView iv = new ImageView(context);
            iv.setMaxWidth(100);
            iv.setMaxHeight(100);
            iv.setImageResource(R.drawable.thelightlogo);
            iv.setAdjustViewBounds(true);
            llSv.addView(iv);

            //---
            final TextView tvDev = new TextView(context);
            tvDev.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            tvDev.setPadding(0, 5, 0, 0);
            tvDev.setText(aboutDev);
            tvDev.setGravity(Gravity.CENTER_HORIZONTAL);
            tvDev.setCursorVisible(true);
            if (typeface != null) { tvDev.setTypeface(typeface); }
            tvDev.setTextSize(fontSize);
            tvDev.setFocusable(false);
            llSv.addView(tvDev);

            //---
            final TextView tvContent = new TextView(context);
            tvContent.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            tvContent.setPadding(0, 5, 0, 5);
            tvContent.setText(aboutContent);
            tvContent.setGravity(Gravity.CENTER_HORIZONTAL);
            tvContent.setCursorVisible(true);
            if (typeface != null) { tvContent.setTypeface(typeface); }
            tvContent.setTextSize(fontSize);
            tvContent.setFocusable(true);
            tvContent.setBackground(PCommon.GetDrawable(context, R.drawable.focus_text));
            llSv.addView(tvContent);
            //---
            final TextView tv1 = new TextView(context);
            tv1.setFocusable(false);
            llSv.addView(tv1);

            //---
            final Button btnGitlab = new Button(context);
            btnGitlab.setLayoutParams(PCommon._layoutParamsWrap);
            btnGitlab.setText(R.string.btnGitlab);
            btnGitlab.setOnClickListener(new View.OnClickListener() {

                public void onClick(View vw)
                {
                    PCommon.OpenUrl(vw.getContext(), "https://gitlab.com/hotlittlewhitedog/BibleMultiTheLight/issues");
                }
            });
            btnGitlab.setFocusable(true);
            btnGitlab.setBackground(PCommon.GetDrawable(context, R.drawable.focus_button));
            llSv.addView(btnGitlab);

            //---
            final TextView tv2 = new TextView(context);
            tv2.setFocusable(false);
            llSv.addView(tv2);

            //---
            final Button btnEmail = new Button(context);
            btnEmail.setLayoutParams(PCommon._layoutParamsWrap);
            btnEmail.setText(R.string.sendEmail);
            btnEmail.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0)
                {
                    PCommon.SendEmail(context,
                            new String[]{ devEmail },
                            app,
                            "");
                }
            });
            btnEmail.setFocusable(true);
            btnEmail.setBackground(PCommon.GetDrawable(context, R.drawable.focus_button));
            llSv.addView(btnEmail);

            //---
            llMain.addView(sv);

            builder.setView(llMain);
            builder.show();
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }

    static class Tab
    {
        @SuppressLint("StaticFieldLeak")
        static SCommon _s = null;

        static void SetCurrentTabTitle(final String title)
        {
            //noinspection EmptyCatchBlock
            try
            {
                final int tabId = GetCurrentTabPosition();
                if (tabId < 0)
                    return;

                //noinspection ConstantConditions
                tabLayout.getTabAt(tabId).setText(title);
                PCommon.SavePref(tabLayout.getContext(), IProject.APP_PREF_KEY.TAB_SELECTED, Integer.toString(tabId));
            }
            catch(Exception ex) { }
        }

        static int GetCurrentTabPosition()
        {
            if (tabLayout == null)
                return -1;

            final int tabSelected = tabLayout.getSelectedTabPosition();
            if (tabSelected < 0)
                return -1;

            return tabSelected;
        }

        static void SelectTabByArtName(final String artName)
        {
            final int artNameTabId = _s.GetArticleTabId(artName);
            if (artNameTabId >= 0)
            {
                tabLayout.post(new Runnable()
                {
                    @Override
                    public void run() {
                        //noinspection EmptyCatchBlock
                        try
                        {
                            //noinspection ConstantConditions
                            tabLayout.getTabAt(artNameTabId).select();
                        }
                        catch(Exception ex) { }
                    }
                });
            }
        }

        static int GetTabCount()
        {
            if (tabLayout == null)
                return -1;

            return tabLayout.getTabCount();
        }

        /***
         * Add tab empty SEARCH_TYPE, FAV_TYPE
         * @param context
         */
        @SuppressWarnings("JavaDoc")
        static void AddTab(final Context context)
        {
            try
            {
                if (tabLayout == null)
                    return;

                CheckLocalInstance(context);

                final TabLayout.Tab tab = tabLayout.newTab().setText(R.string.tabTitleDefault);
                tabLayout.addTab(tab);
                FullScrollTab(context, HorizontalScrollView.FOCUS_RIGHT);
            }
            catch (Exception ex)
            {
                if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
            }
        }

        /***
         * Add tab for Open chapter SEARCH_TYPE
         * @param context
         * @param tbbName
         * @param bNumber
         * @param cNumber
         * @param fullQuery
         * @param vNumber
         */
        @SuppressWarnings("JavaDoc")
        static void AddTab(final Context context, final String tbbName, final int bNumber, final int cNumber, final String fullQuery, final int vNumber)
        {
            try
            {
                if (tabLayout == null)
                    return;

                CheckLocalInstance(context);

                final int tabNumber = tabLayout.getTabCount();
                final String bbname = tbbName.substring(0, 1);
                final int pos = (vNumber - 1) * tbbName.length();
                final CacheTabBO t = new CacheTabBO(tabNumber, "S", context.getString(R.string.tabTitleDefault), fullQuery, pos, bbname, true, true, false, bNumber, cNumber, 0, tbbName);
                _s.SaveCacheTab(t);

                final TabLayout.Tab tab = tabLayout.newTab().setText(R.string.tabTitleDefault);
                tabLayout.addTab(tab);
                FullScrollTab(context, HorizontalScrollView.FOCUS_RIGHT);
            }
            catch (Exception ex)
            {
                if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
            }
        }

        /***
         * Add tab for Open result PRBL | ARTICLE | PLAN | INTENT
         * @param context
         * @param cacheTabType
         * @param tbbName
         * @param fullQuery bNumber, cNumber, vNumberFrom, vNumberTo
         */
        @SuppressWarnings("JavaDoc")
        static void AddTab(final Context context, final String cacheTabType, final String tbbName, final String fullQuery)
        {
            try
            {
                if (tabLayout == null)
                    return;

                CheckLocalInstance(context);

                final String bbname = tbbName.substring(0, 1);
                final int tabNumber = tabLayout.getTabCount();
                final String tabTitle;
                final CacheTabBO t;
                if (cacheTabType.equalsIgnoreCase("A"))
                {
                    final String resString;
                    final int tabNameSize = Integer.parseInt(context.getString(R.string.tabSizeName));

                    if (fullQuery.startsWith("ART"))
                    {
                        final int resId = PCommon.GetResId(context, fullQuery);
                        resString = context.getString(resId);
                    }
                    else
                    {
                        final String myArtPrefix = context.getString(R.string.tabMyArtPrefix);
                        resString = PCommon.ConcaT(myArtPrefix, fullQuery);
                    }

                    tabTitle = (resString.length() <= tabNameSize) ? resString : fullQuery;
                    t = new CacheTabBO(tabNumber, cacheTabType, tabTitle, fullQuery, 0, bbname, true, false, false, 0, 0, 0, tbbName);
                    _s.SaveCacheTab(t);
                }
                else if (cacheTabType.equalsIgnoreCase("P"))
                {
                    final String[] cols = fullQuery.split("\\s");
                    if (cols.length != 3) return;
                    final int planId = Integer.parseInt(cols[0]);
                    final PlanDescBO pd =_s.GetPlanDesc(planId);
                    if (pd == null) return;
                    final int resId = PCommon.GetResId(context, pd.planRef);
                    tabTitle = context.getString(resId);
                    t = new CacheTabBO(tabNumber, cacheTabType, tabTitle, fullQuery, 0, bbname, true, false, false, 0, 0, 0, tbbName);
                    _s.SaveCacheTab(t);

                    final int planDayNumber = Integer.parseInt(cols[1]);
                    final PlanCalBO pc =_s.GetPlanCalByDay(bbname, planId, planDayNumber);
                    final int bNumberStart = pc.bNumberStart, cNumberStart = pc.cNumberStart, vNumberStart = pc.vNumberStart;
                    final int bNumberEnd = pc.bNumberEnd, cNumberEnd = pc.cNumberEnd, vNumberEnd = pc.vNumberEnd;
                    final int tabIdTo = MainActivity.Tab.GetTabCount();
                    final boolean copy =_s.CopyCacheSearchForOtherBible(tabIdTo, tbbName, bNumberStart, cNumberStart, vNumberStart, bNumberEnd, cNumberEnd, vNumberEnd);
                    if (!copy) return;
                }
                else
                {
                    tabTitle = context.getString(R.string.tabTitleDefault);
                    t = new CacheTabBO(tabNumber, cacheTabType, tabTitle, fullQuery, 0, bbname, true, false, false, 0, 0, 0, tbbName);
                    _s.SaveCacheTab(t);
                }
                final TabLayout.Tab tab = tabLayout.newTab().setText(tabTitle);
                tabLayout.addTab(tab);
                FullScrollTab(context, HorizontalScrollView.FOCUS_RIGHT);
            }
            catch (Exception ex)
            {
                if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
            }
        }

        /***
         * Add tab for Open result SEARCH_TYPE
         * @param context
         * @param tabNumberFrom
         * @param bbNameTo
         * @param vNumber
         */
        @SuppressWarnings("JavaDoc")
        static void AddTab(final Context context, final int tabNumberFrom, final String bbNameTo, final int vNumber)
        {
            try
            {
                if (tabLayout == null)
                    return;

                CheckLocalInstance(context);

                final int pos = (vNumber - 1) * bbNameTo.length();
                final int tabNumberTo = tabLayout.getTabCount();
                final CacheTabBO t = _s.GetCacheTab(tabNumberFrom);
                t.tabType = "S";
                t.tabNumber = tabNumberTo;
                t.bbName = bbNameTo.substring(0, 1);
                t.scrollPosY = pos;
                t.isBook = true;
                t.isChapter = false;
                t.isVerse = false;
                t.trad = bbNameTo;
                _s.SaveCacheTab(t);
                _s.CopyCacheSearchForOtherBible(tabNumberFrom, tabNumberTo, bbNameTo);

                final TabLayout.Tab tab = tabLayout.newTab().setText(R.string.tabTitleDefault);
                tabLayout.addTab(tab);
                FullScrollTab(context, HorizontalScrollView.FOCUS_RIGHT);
            }
            catch (Exception ex)
            {
                if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
            }
        }

        static void RemoveCurrentTab(final Context context)
        {
            try
            {
                if (tabLayout == null)
                    return;

                CheckLocalInstance(context);

                final int tabNumberToRemove = tabLayout.getSelectedTabPosition();

                Tab.RemoveTabAt(context, tabNumberToRemove);
            }
            catch (Exception ex)
            {
                if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
            }
        }

        static void RemoveTabAt(final Context context, final int tabNumberToRemove)
        {
            try
            {
                if (tabLayout == null)
                    return;

                CheckLocalInstance(context);

                final int tabCount = tabLayout.getTabCount();
                if (tabCount <= 1)
                    return;

                _s.DeleteCache(tabNumberToRemove);

                int toTabId;
                for(int fromTabId = tabNumberToRemove + 1; fromTabId < tabCount; fromTabId++)
                {
                    toTabId = fromTabId - 1;

                    _s.UpdateCacheId(fromTabId, toTabId);
                }

                //Finally
                tabLayout.removeTabAt(tabNumberToRemove);
            }
            catch (Exception ex)
            {
                if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
            }
        }

        private static void CheckLocalInstance(final Context context)
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

        private static void FullScrollTab(final Context context, final int direction)
        {
            try
            {
                tabLayout.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //noinspection EmptyCatchBlock
                        try
                        {
                            if (direction == HorizontalScrollView.FOCUS_RIGHT)
                            {
                                final int tabId = tabLayout.getTabCount() - 1;
                                //noinspection ConstantConditions
                                tabLayout.getTabAt(tabId).select();
                            }
                            else if (direction == HorizontalScrollView.FOCUS_LEFT)
                            {
                                //noinspection ConstantConditions
                                tabLayout.getTabAt(0).select();
                            }
                            tabLayout.fullScroll(direction);
                        }
                        catch(Exception ex) { }
                    }
                });
            }
            catch (Exception ex)
            {
                if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
            }
        }

        private static void LongPress(final Context context)
        {
            tabLayout.post(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        final View vw = tabLayout.getChildAt(0);
                        final int count = ((LinearLayout) vw).getChildCount();
                        for(int i = 0; i < count; i++)
                        {
                            final int index = i;
                            ((LinearLayout) vw).getChildAt( index ).setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(final View view)
                                {
                                    //noinspection EmptyCatchBlock
                                    try
                                    {
                                        Tab.RemoveTabAt(context, index);
                                        final int tabSelect = (index == 0) ? 0 : index - 1;
                                        //noinspection ConstantConditions
                                        tabLayout.getTabAt( tabSelect ).select();
                                    }
                                    catch(Exception ex) { }

                                    return true;
                                }
                            });
                        }
                    }
                    catch (Exception ex)
                    {
                        if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
                    }
                }
            });
        }
    }

    private void ReloadFavTabTv(final Context context, final String tbbName, final int bNumber, final int cNumber, final String fullQuery, @SuppressWarnings("SameParameterValue") final int vNumber)
    {
        try
        {
            if (tabLayout == null)
                return;

            CheckLocalInstance(context);

            final int tabNumber = 0;
            final String bbname = tbbName.substring(0, 1);
            final int pos = 0;
            final CacheTabBO t = new CacheTabBO(tabNumber, "F", context.getString(R.string.tabTitleDefault), fullQuery, pos, bbname, false, false, false, bNumber, cNumber, vNumber, tbbName);
            _s.SaveCacheTab(t);

            final FragmentManager fm = getSupportFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            final Fragment frag = new SearchFragment(SearchFragment.FRAGMENT_TYPE.FAV_TYPE);
            ft.replace(R.id.content_frame, frag, Integer.toString(tabNumber));
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    private void Slide(final boolean showMnu)
    {
        if (slideViewMenu == null) return;

        if (showMnu)
        {
            final int installStatus = PCommon.GetInstallStatus(getApplicationContext());
            if (installStatus != 5)
            {
                PCommon.ShowToast(getApplicationContext(), R.string.installQuit, Toast.LENGTH_SHORT);
                return;
            }
        }

        final int mnuTvVisibility = showMnu ? View.VISIBLE : View.GONE;
        final int tabVisibility = showMnu ? View.GONE : View.VISIBLE;

        slideViewMenu.setVisibility(mnuTvVisibility);
        slideViewTabHandleMain.setVisibility(tabVisibility);
        slideViewTab.setVisibility(tabVisibility);

        if (showMnu)
        {
            slideViewMenuHandle.requestFocus();
        }
        else
        {
            slideViewTabHandle.requestFocus();
        }
    }
/*
final TranslateAnimation animate = new TranslateAnimation(
        0,           // fromXDelta
        -1 * slideViewMenu.getWidth(),          // toXDelta
        0,           // fromYDelta
        0);            // toYDelta
animate.setDuration(500);
animate.setFillAfter(true);
slideViewMenu.startAnimation(animate);
*/

    /***
     * Search TV
     */
    private void SearchTv(final Context context, final boolean isSearchBible)
    {
        //TODO FAB: cancel not working
        try
        {
            final int searchFullQueryLimit = 3;
            final int installStatus = PCommon.GetInstallStatus(context);
            if (installStatus < 1) return;

            final String bbname = PCommon.GetPref(context, IProject.APP_PREF_KEY.BIBLE_NAME, "k");
            final AlertDialog builderText = new AlertDialog.Builder(context).create();
            final LayoutInflater inflater = getLayoutInflater();
            final View vw = inflater.inflate(R.layout.fragment_search_tv, (ViewGroup) findViewById(R.id.clSearch));
            final EditText etSearchText = vw.findViewById(R.id.etSearchText);
            final String searchTextHint = PCommon.ConcaT("<i>", getString(isSearchBible ? R.string.searchBibleHint : R.string.searchFavHint, "</i>"));
            etSearchText.setHint(Html.fromHtml(searchTextHint));
            final NumberPicker npSearchLanguage = vw.findViewById(R.id.npSearchLanguage);
            final View vwFavOrder = vw.findViewById(R.id.tvFavOrder);
            final ToggleButton btnOrder0 = vw.findViewById(R.id.btnOrder0);
            final ToggleButton btnOrder1 = vw.findViewById(R.id.btnOrder1);
            final ToggleButton btnOrder2 = vw.findViewById(R.id.btnOrder2);

            if (!isSearchBible)
            {
                npSearchLanguage.setVisibility(View.GONE);
                final int orderBy = Integer.valueOf(PCommon.GetPref(vw.getContext(), IProject.APP_PREF_KEY.FAV_ORDER, "1"));
                btnOrder0.setChecked(false);
                btnOrder1.setChecked(false);
                btnOrder2.setChecked(false);
                if (orderBy == 0) btnOrder0.setChecked(true);
                    else if (orderBy == 2) btnOrder2.setChecked(true);
                        else btnOrder1.setChecked(true);
                btnOrder0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int orderBy = Integer.parseInt(v.getTag().toString());
                        PCommon.SavePrefInt(v.getContext(), IProject.APP_PREF_KEY.FAV_ORDER, orderBy);
                        btnOrder1.setChecked(false);
                        btnOrder2.setChecked(false);
                    }
                });
                btnOrder1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int orderBy = Integer.parseInt(v.getTag().toString());
                        PCommon.SavePrefInt(v.getContext(), IProject.APP_PREF_KEY.FAV_ORDER, orderBy);
                        btnOrder0.setChecked(false);
                        btnOrder2.setChecked(false);
                    }
                });
                btnOrder2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int orderBy = Integer.parseInt(v.getTag().toString());
                        PCommon.SavePrefInt(v.getContext(), IProject.APP_PREF_KEY.FAV_ORDER, orderBy);
                        btnOrder0.setChecked(false);
                        btnOrder1.setChecked(false);
                    }
                });
            }
            else
            {
                vwFavOrder.setVisibility(View.INVISIBLE);
                btnOrder0.setVisibility(View.INVISIBLE);
                btnOrder1.setVisibility(View.INVISIBLE);
                btnOrder2.setVisibility(View.INVISIBLE);
            }

            final String[] npLanguageValues = new String[] { getString(R.string.languageEn), getString(R.string.languageEs), getString(R.string.languageFr), getString(R.string.languageIt), getString(R.string.languagePt) };
            npSearchLanguage.setDisplayedValues(npLanguageValues);
            npSearchLanguage.setMinValue(1);
            npSearchLanguage.setMaxValue(5);
            npSearchLanguage.setValue(bbname.equalsIgnoreCase("k") ? 1 : bbname.equalsIgnoreCase("v") ? 2 : bbname.equalsIgnoreCase("l") ? 3 : bbname.equalsIgnoreCase("d") ? 4 : 5);
            npSearchLanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etSearchText.requestFocus();
                }
            });
            final Button btnSearchContinue = vw.findViewById(R.id.btnSearchContinue);
            btnSearchContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etSearchText.setText(etSearchText.getText().toString().replaceAll("\n", ""));
                    if (etSearchText.getText().toString().length() < searchFullQueryLimit) {
                        PCommon.ShowToast(v.getContext(), R.string.toastEmpty3, Toast.LENGTH_SHORT);
                        return;
                    }
                    builderText.dismiss();
                }
            });
            final Button btnSearchClear = vw.findViewById(R.id.btnSearchClear);
            btnSearchClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    etSearchText.setText("");
                    etSearchText.requestFocus();
                    if (!isSearchBible)
                    {
                        builderText.dismiss();
                    }
                }
            });
            builderText.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    try
                    {
                        final String bbname = (npSearchLanguage.getValue() == 1) ? "k" : (npSearchLanguage.getValue() == 2) ? "v" : (npSearchLanguage.getValue() == 3) ? "l" :  (npSearchLanguage.getValue() == 4) ? "d" : "a";
                        PCommon.SavePref(etSearchText.getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, bbname);
                        SearchTvBook(context, etSearchText.getText().toString(), isSearchBible);
                    }
                    catch (Exception ex)
                    {
                        if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
                    }
                }
            });
            builderText.setTitle(R.string.mnuSearchAll);
            builderText.setCancelable(true);
            builderText.setView(vw);
            builderText.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

/* else
{

    final String bbname = PCommon.GetPref(view.getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, bbName);
    if (bbname.equals("")) return;
    //not used: final String tbbName = PCommon.GetPrefTradBibleName(view.getContext(), true);
    final int cNumber = Integer.parseInt(PCommon.GetPref(view.getContext(), IProject.APP_PREF_KEY.BOOK_CHAPTER_DIALOG, "0"));
    final String fullQuery = PCommon.ConcaT(bNumber,
            cNumber != 0 ? PCommon.ConcaT( " ", cNumber) : "",
            searchText != null ? PCommon.ConcaT(" ", searchText) : "");
    MainActivity.Tab.AddTab(view.getContext(), bbname, bNumber, cNumber, fullQuery, 1);

    //redo MainActivity.ShowHideFavClick() in Tab
    //Set fullQuery in db, save Order in PREF and switch to tab. Here under is a test, please remove the code when done.
    final int tabCount = MainActivity.Tab.GetTabCount();
    @SuppressWarnings("UnusedAssignment") boolean isFavShow = false;

    CacheTabBO cacheTabFav = _s.GetCacheTabFav();
    if (cacheTabFav == null)
    {
        isFavShow = false;

        cacheTabFav = new CacheTabBO();
        cacheTabFav.tabNumber = -1;
        cacheTabFav.tabType = "F";
        cacheTabFav.tabTitle = getString(R.string.favHeader);

        _s.SaveCacheTabFav(cacheTabFav);
    }
    else
    {
        isFavShow = true;
    }

    isFavShow = !isFavShow;
    if (isFavShow)
    {
        //Show fav tab
        //############
        for (int i=tabCount-1; i >= 0; i--)
        {
            _s.UpdateCacheId(i, i+1);
        }

        cacheTabFav.tabNumber = 0;
        _s.SaveCacheTabFav(cacheTabFav);

        final TabLayout.Tab tab = tabLayout.newTab().setText(R.string.favHeader);
        tabLayout.addTab(tab, 0);
        MainActivity.Tab.FullScrollTab(getApplicationContext(), HorizontalScrollView.FOCUS_LEFT);
    }
    else
    {
        //Remove fav tab
        //##############
        MainActivity.Tab.RemoveTabFav(getApplicationContext());
    }
} */

    private void SearchTvBook(final Context context, final String searchText, final boolean isSearchBible)
    {
        try
        {
            final int installStatus = PCommon.GetInstallStatus(context);
            if (installStatus < 1) return;

            if (!isSearchBible)
            {
                final String bbName = PCommon.GetPrefBibleName(context);
                final String bbname = PCommon.GetPref(context, IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, bbName);
                if (bbname.equals("")) return;
                ShowFav();
                ReloadFavTabTv(context, bbname, 0, 0, searchText, 0);
                return;
            }

            final Typeface typeface = PCommon.GetTypeface(context);
            final int fontSize = PCommon.GetFontSize(context);
            final String bbnm = PCommon.GetPref(context, IProject.APP_PREF_KEY.BIBLE_NAME, "k");

            final AlertDialog builderBook = new AlertDialog.Builder(context).create();
            final LayoutInflater inflater = getLayoutInflater();

            final ScrollView sv = new ScrollView(context);
            sv.setLayoutParams(PCommon._layoutParamsMatchAndWrap);

            final ArrayList<BibleRefBO> lstRef =_s.GetListAllBookByName(bbnm);
            final LinearLayout llBooks = new LinearLayout(context);
            llBooks.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            llBooks.setOrientation(LinearLayout.VERTICAL);
            llBooks.setPadding(0, 15, 0, 15);

            final AlertDialog builderChapter = new AlertDialog.Builder(context).create();
            final View vwSvSelection = inflater.inflate(R.layout.fragment_selection_items, (ViewGroup) findViewById(R.id.svSelection));

            int bNumber;
            String refText;
            String refNr;
            boolean isBookExist;
            int bNumberParam;
            boolean shouldWarn = false;

            for (BibleRefBO ref : lstRef)
            {
                bNumber = ref.bNumber;
                refNr = String.format(Locale.US, "%2d", bNumber);
                refText = PCommon.ConcaT(refNr, ": ", ref.bName, " (", ref.bsName, ")");

                final TextView tvBook = new TextView(context);
                tvBook.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvBook.setPadding(10, 15, 10, 15);
                tvBook.setText( refText );
                tvBook.setTag( bNumber );

                bNumberParam = (bNumber != 66) ? bNumber + 1 : 66;
                isBookExist = (installStatus == 5) || _s.IsBookExist(bNumberParam);
                if (isBookExist)
                {
                    tvBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            try
                            {
                                final int bNumber = (int) view.getTag();
                                final int chapterMax = _s.GetBookChapterMax(bNumber);
                                if (chapterMax < 1)
                                {
                                    PCommon.ShowToast(view.getContext(), R.string.toastBookNotInstalled, Toast.LENGTH_SHORT);
                                    return;
                                }
                                final String[] titleArr = ((TextView)view).getText().toString().substring(3).split("\\(");
                                final String title = PCommon.ConcaT(getString(R.string.mnuBook), ": ", titleArr[0]);

                                PCommon.SelectItem(builderChapter, view.getContext(), vwSvSelection, title, R.string.tvChapter, "", true, chapterMax, true);
                                builderChapter.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        final String bbName = PCommon.GetPrefBibleName(context);
                                        final String bbname = PCommon.GetPref(view.getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, bbName);
                                        if (bbname.equals("")) return;
                                        //not used: final String tbbName = PCommon.GetPrefTradBibleName(view.getContext(), true);
                                        final int cNumber = Integer.parseInt(PCommon.GetPref(view.getContext(), IProject.APP_PREF_KEY.BOOK_CHAPTER_DIALOG, "0"));
                                        final String fullQuery = PCommon.ConcaT(bNumber,
                                                cNumber != 0 ? PCommon.ConcaT( " ", cNumber) : "",
                                                searchText != null ? PCommon.ConcaT(" ", searchText) : "");
                                        //noinspection ConstantConditions
                                        if (isSearchBible)
                                        {
                                            MainActivity.Tab.AddTab(view.getContext(), bbname, bNumber, cNumber, fullQuery, 1);
                                        }
                                        else
                                        {
                                            ShowFav();
                                            ReloadFavTabTv(view.getContext(), bbname, bNumber, cNumber, fullQuery, 0);
                                        }
                                    }
                                });
                                builderChapter.show();
                            }
                            catch (Exception ex)
                            {
                                if (PCommon._isDebugVersion) PCommon.LogR(view.getContext(), ex);
                            }
                            finally
                            {
                                builderBook.dismiss();
                            }
                        }
                    });
                }
                else
                {
                    if (!shouldWarn) shouldWarn = true;
                    tvBook.setEnabled( false );
                }
                //TODO FAB: slow GetDrawable
                tvBook.setFocusable(true);
                tvBook.setBackground(PCommon.GetDrawable(context, R.drawable.focus_text));

                //Font
                if (typeface != null) { tvBook.setTypeface(typeface); }
                tvBook.setTextSize(fontSize);

                llBooks.addView(tvBook);
            }

            final Typeface tfTitle = Typeface.defaultFromStyle(Typeface.BOLD);
            final TextView tvNT = new TextView(context);
            tvNT.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            tvNT.setPadding(10, 20, 10, 20);
            tvNT.setGravity(Gravity.CENTER_HORIZONTAL);
            tvNT.setText( R.string.tvBookNT );
            tvNT.setTextSize(fontSize);
            tvNT.setTypeface( tfTitle );
            llBooks.addView(tvNT, 39);

            final TextView tvOT = new TextView(context);
            tvOT.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            tvOT.setPadding(10, 20, 10, 20);
            tvOT.setGravity(Gravity.CENTER_HORIZONTAL);
            tvOT.setText( R.string.tvBookOT );
            tvOT.setTextSize(fontSize);
            tvOT.setTypeface( tfTitle );
            llBooks.addView(tvOT, 0);

            final String refNr0 = String.format(Locale.US, "%2d", 0);
            final String refText0 = PCommon.ConcaT(refNr0, ": ", context.getString(R.string.itemAll));
            final TextView tvALL = new TextView(context);
            tvALL.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
            tvALL.setPadding(10, 20, 10, 20);
            tvALL.setText(refText0);
            if (typeface != null) { tvALL.setTypeface(typeface); }
            tvALL.setTextSize(fontSize);
            //TODO FAB: slow GetDrawable
            tvALL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    final String bbName = PCommon.GetPrefBibleName(context);
                    final String bbname = PCommon.GetPref(v.getContext(), IProject.APP_PREF_KEY.BIBLE_NAME_DIALOG, bbName);
                    if (bbname.equals("")) return;
                    //noinspection ConstantConditions
                    if (isSearchBible)
                    {
                        MainActivity.Tab.AddTab(v.getContext(), "S", bbname, searchText);
                    }
                    else
                    {
                        ShowFav();
                        ReloadFavTabTv(v.getContext(), bbname, 0, 0, searchText, 0);
                    }
                    builderBook.dismiss();
                }
            });
            tvALL.setFocusable(true);
            tvALL.setBackground(PCommon.GetDrawable(context, R.drawable.focus_text));
            llBooks.addView(tvALL, 0);

            if (shouldWarn)
            {
                final TextView tvWarn = new TextView(context);
                tvWarn.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvWarn.setPadding(10, 10, 10, 20);
                tvWarn.setGravity(Gravity.CENTER_HORIZONTAL);
                tvWarn.setText( R.string.tvBookInstall );
                tvWarn.setTextSize(fontSize);
                llBooks.addView(tvWarn, 0);
            }
            sv.addView(llBooks);

            builderBook.setTitle(R.string.mnuBooks);
            builderBook.setCancelable(true);
            builderBook.setView(sv);
            builderBook.show();
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }
}
