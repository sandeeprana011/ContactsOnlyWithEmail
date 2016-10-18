package com.rana.contactswithemail.structure;

/**
 * Created by sandeeprana on 18/10/16.
 * License is only applicable to individuals and non-profits
 * and that any for-profit company must
 * purchase a different license, and create
 * a second commercial license of your
 * choosing for companies
 */
public class CallLogDetail {
    long totalTime;
    int howManyTimesCalled;

    public CallLogDetail(long totalTime, int howManyTimesCalled) {
        this.totalTime = totalTime;
        this.howManyTimesCalled = howManyTimesCalled;
    }

    public int getHowManyTimesCalled() {
        return howManyTimesCalled;
    }

    public void setHowManyTimesCalled(int howManyTimesCalled) {
        this.howManyTimesCalled = howManyTimesCalled;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
}
