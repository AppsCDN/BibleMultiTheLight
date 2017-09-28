
package org.hlwd.bible;

class PlanCalBO
{
    int planId;
    int dayNumber;
    String dayDt;
    int isRead;
    int bNumberStart;
    int cNumberStart;
    int vNumberStart;
    int bNumberEnd;
    int cNumberEnd;
    int vNumberEnd;
    String bsNameStart;
    String bsNameEnd;

    PlanCalBO()
    {
        planId = -1;
        dayNumber = 0;
        dayDt = null;
        isRead = 0;
        bNumberStart = 0;
        cNumberStart = 0;
        vNumberStart = 0;
        bNumberEnd = 0;
        cNumberEnd = 0;
        vNumberEnd = 0;
        bsNameStart = "";
        bsNameEnd = "";
    }
}
