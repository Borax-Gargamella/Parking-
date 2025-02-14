package com.contest.parking.data.model;

public class Range  {
    public long start;
    public long end;

    public Range(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public boolean overlaps(Range other) {
        return start <= other.end && other.start <= end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
