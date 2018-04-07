
package org.hlwd.bible;

class SectionBO
{
    int id;
    String before;
    String content;
    String after;
    int blockId;
    int blockSubId;
    String blockRef;

    SectionBO()
    {
        this.id = -1;
        this.before = null;
        this.content = null;
        this.after = null;
        this.blockId = -1;
        this.blockSubId = -1;
        this.blockRef = null;
    }
}
