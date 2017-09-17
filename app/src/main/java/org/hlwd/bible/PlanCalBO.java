
package org.hlwd.bible;

public class PlanCalBO
{
    protected int planId;
    protected int dayNumber;
    protected String dayDt;
    protected int isRead;
    protected int bNumberStart;
    protected int cNumberStart;
    protected int vNumberStart;
    protected int bNumberEnd;
    protected int cNumberEnd;
    protected int vNumberEnd;
    protected String bsNameStart;
    protected String bsNameEnd;

    protected PlanCalBO()
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
