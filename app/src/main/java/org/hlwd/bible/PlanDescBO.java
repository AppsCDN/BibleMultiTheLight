
package org.hlwd.bible;

public class PlanDescBO
{
    protected int planId;

    protected String planRef;
    protected String startDt;
    protected String endDt;
    protected int bCount;
    protected int cCount;
    protected int vCount;
    protected int vDayCount;
    protected int dayCount;

    protected PlanDescBO()
    {
        planId = -1;
        planRef = null;
        startDt = null;
        endDt = null;
        bCount = 0;
        cCount = 0;
        vCount = 0;
        vDayCount = 0;
        dayCount = 0;
    }
}
