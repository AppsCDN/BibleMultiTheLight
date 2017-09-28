
package org.hlwd.bible;

class PlanDescBO
{
    int planId;

    String planRef;
    String startDt;
    String endDt;
    int bCount;
    int cCount;
    int vCount;
    int vDayCount;
    int dayCount;

    PlanDescBO()
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
