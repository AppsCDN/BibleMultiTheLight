
package org.hlwd.bible;

class ShortSectionBO
{
    int from_id;
    int blockId;
    String content;

    ShortSectionBO(final int blockId, final String content, final int from_id)
    {
        this.from_id = from_id;
        this.blockId = blockId;
        this.content = content;
    }
}

//TODO NEXT: swap blockId lors d'un déplacement
//TODO NEXT: remove item lors d'une suppression
