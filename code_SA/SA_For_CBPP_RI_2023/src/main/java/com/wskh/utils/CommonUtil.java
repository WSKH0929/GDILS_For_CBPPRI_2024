package com.wskh.utils;

import com.wskh.classes.PlaceItem;

public class CommonUtil {

    public static int ceilToInt(double x) {
        return (int) Math.ceil(x - Parameter.EPS);
    }

    public static int floorToInt(double x) {
        return (int) Math.floor(x + Parameter.EPS);
    }

    public static int compareDouble(double d1, double d2) {
        if (d1 - Parameter.EPS > d2) {
            return 1;
        }
        if (d2 - Parameter.EPS > d1) {
            return -1;
        }
        return 0;
    }

    // 判断一个点和圆的关系，0：正好在圆上，1：在圆外，-1：在圆内
    public static int judgePointType(double x, double y, double r2) {
        return CommonUtil.compareDouble(x * x + y * y, r2);
    }

    public static boolean isOverlap(PlaceItem a, PlaceItem b) {
        return !(a.x2 - Parameter.EPS < b.x) && !(b.x2 - Parameter.EPS < a.x) && !(a.y2 - Parameter.EPS < b.y) && !(b.y2 - Parameter.EPS < a.y);
    }

}