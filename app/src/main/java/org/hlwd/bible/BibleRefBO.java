
package org.hlwd.bible;

public class BibleRefBO
{
    protected int id;

    protected String bbName;
    protected int bNumber;
    protected String bName;
    protected String bsName;

    protected BibleRefBO()
    {
        this.bbName = null;
        this.bNumber = -1;
        this.bName =  null;
        this.bsName = null;
    }

    protected BibleRefBO(final String bbName, final int bNumber, final String bName, final String bsName)
    {
        this.bbName = bbName;
        this.bNumber = bNumber;
        this.bName =  bName;
        this.bsName = bsName;
    }
}
