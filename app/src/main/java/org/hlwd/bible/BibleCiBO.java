
package org.hlwd.bible;

public class BibleCiBO
{
    protected int ciId;

    protected int bNumber;
    protected int cNumber;
    protected int vCount;

    protected BibleCiBO()
    {
        this.ciId = -1;
        this.bNumber = -1;
        this.cNumber = -1;
        this.vCount = -1;
    }

    protected BibleCiBO(final int id, final int bNumber, final int cNumber, final int vCount)
    {
        this.ciId = id;
        this.bNumber = bNumber;
        this.cNumber = cNumber;
        this.vCount = cNumber;
    }
}
