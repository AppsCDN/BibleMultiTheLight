
package org.hlwd.bible;

class BibleRefBO
{
    int id;
    String bbName;
    int bNumber;
    String bName;
    String bsName;

    BibleRefBO()
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
