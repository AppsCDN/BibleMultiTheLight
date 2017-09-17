
package org.hlwd.bible;

public class VerseBO
{
    protected int id;

    protected String bbName;
    protected String bName;
    protected String bsName;

    protected int bNumber;
    protected int cNumber;
    protected int vNumber;

    protected String vText;

    protected int mark;

    protected VerseBO()
    {
        this.id = -1;
        this.bbName = "";
        this.bName =  "";
        this.bsName = "";
        this.bNumber = -1;
        this.cNumber = -1;
        this.vNumber = -1;
        this.vText = "";
        this.mark = 0;
    }

    protected VerseBO(final int id, final String bbName, final String bName, final String bsName, final int bNumber, final int cNumber, final int vNumber, final String vText)
    {
        this.id = id;
        this.bbName = bbName;
        this.bName =  bName;
        this.bsName = bsName;
        this.bNumber = bNumber;
        this.cNumber = cNumber;
        this.vNumber = cNumber;
        this.vText = vText;

        this.mark = 0;
    }
}
