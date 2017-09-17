
package org.hlwd.bible;

//bNumber INTEGER, cNumber INTEGER, vNumber INTEGER, changeDt TEXT, mark INTEGER, note TEXT,
public class NoteBO
{
    protected int bNumber;
    protected int cNumber;
    protected int vNumber;
    protected String changeDt;
    protected int mark;
    protected String note = "";     //DEFAULT

    protected NoteBO(final int bNumber, final int cNumber, final int vNumber, final String changeDt, final int mark)
    {
        this.bNumber = bNumber;
        this.cNumber = cNumber;
        this.vNumber = vNumber;
        this.changeDt = changeDt;
        this.mark = mark;
    }
}
