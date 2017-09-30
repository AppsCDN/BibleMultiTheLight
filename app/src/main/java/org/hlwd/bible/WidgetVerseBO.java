
package org.hlwd.bible;

class WidgetVerseBO
{
    final int id;
    final String vRef;
    final String vText;
    final String bbName;
    final int bNumber;
    final int cNumber;
    final int vNumber;
    final int mark;

    WidgetVerseBO(final int id, final String vRef, final String vText, final String bbName, final int bNumber, final int cNumber, final int vNumber, final int mark)
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

