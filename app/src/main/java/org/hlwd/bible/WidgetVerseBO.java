
package org.hlwd.bible;

public class WidgetVerseBO
{
    protected int id;
    protected String vRef;
    protected String vText;
    protected String bbName;
    protected int bNumber;
    protected int cNumber;
    protected int vNumber;
    protected int mark;

    protected WidgetVerseBO()
    {
        this.id = -1;
        this.vRef = "";
        this.vText = "";
        this.bbName = "k";
        this.bNumber = -1;
        this.cNumber = -1;
        this.vNumber = -1;
        this.mark = 0;
    }

    protected WidgetVerseBO(final int id, final String vRef, final String vText, final String bbName, final int bNumber, final int cNumber, final int vNumber, final int mark)
    {
        this.id = id;
        this.vRef = vRef;
        this.vText = vText;
        this.bbName = bbName;
        this.bNumber = bNumber;
        this.cNumber = cNumber;
        this.vNumber = vNumber;
        this.mark = mark;
    }
}

