
package org.hlwd.bible;

public class CacheTabBO
{
    protected int tabNumber;
    protected String tabType;
    protected String tabTitle;
    protected String fullQuery;
    protected int scrollPosY;
    protected String bbName;
    protected boolean isBook;
    protected boolean isChapter;
    protected boolean isVerse;
    protected int bNumber;
    protected int cNumber;
    protected int vNumber;
    protected String trad;

    protected CacheTabBO()
    {
        this.tabNumber = -1;
        this.tabType = "S";         //F: FAV, S:SEARCH
        this.tabTitle = "..";
        this.fullQuery = "";
        this.scrollPosY = 0;
        this.bbName = "k";
        this.isBook = false;
        this.isChapter = false;
        this.isVerse = false;
        this.bNumber = 0;
        this.cNumber = 0;
        this.vNumber = 0;
        this.trad = this.bbName;
    }

    protected CacheTabBO(final int tabNumber, final String tabType, final String tabTitle, final String fullQuery, final int scrollPosY,
                         final String bbName, final boolean isBook, final boolean isChapter, final boolean isVerse,
                         final int bNumber, final int cNumber, final int vNumber, final String trad)
    {
        this.tabNumber = tabNumber;
        this.tabType = tabType;
        this.tabTitle = tabTitle;
        this.fullQuery = fullQuery;
        this.scrollPosY = scrollPosY;
        this.bbName = bbName;
        this.isBook = isBook;
        this.isChapter = isChapter;
        this.isVerse = isVerse;
        this.bNumber = bNumber;
        this.cNumber = cNumber;
        this.vNumber = vNumber;
        this.trad = trad;
    }
}
