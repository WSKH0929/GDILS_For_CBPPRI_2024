package com.wskh.utils;

import com.wskh.classes.Instance;
import com.wskh.classes.PlaceItem;
import com.wskh.classes.Solution;

import java.util.List;

public class CheckUtil {
    public static void checkSolution(Solution solution, Instance instance) {
        // 检查是否缺少/多余物品
        boolean[] used = new boolean[instance.n];
        int c = 0;
        for (List<PlaceItem> placeItemList : solution.placeItemLists) {
            for (PlaceItem placeItem : placeItemList) {
                if (!used[placeItem.id]) {
                    used[placeItem.id] = true;
                    c++;
                } else {
                    throw new RuntimeException("存在多余的物品");
                }
            }
        }
        if (c < instance.n) {
            throw new RuntimeException("缺少物品");
        }
        // 检查是否有超出箱子的情况
        for (List<PlaceItem> placeItemList : solution.placeItemLists) {
            for (PlaceItem placeItem : placeItemList) {
                if (CommonUtil.judgePointType(placeItem.x, placeItem.y, instance.r2) > 0
                        || CommonUtil.judgePointType(placeItem.x2, placeItem.y, instance.r2) > 0
                        || CommonUtil.judgePointType(placeItem.x, placeItem.y2, instance.r2) > 0
                        || CommonUtil.judgePointType(placeItem.x2, placeItem.y2, instance.r2) > 0) {
                    WriteUtil.drawSolutionForSingleBin(placeItemList, instance.r, "src/main/resources", "超出容器");
                    System.out.println(placeItemList.size());
                    throw new RuntimeException("超出容器");
                }
            }
        }
        // 检查是否有重叠
        for (List<PlaceItem> placeItemList : solution.placeItemLists) {
            for (int i = 0; i < placeItemList.size(); i++) {
                for (int j = i + 1; j < placeItemList.size(); j++) {
                    if (CommonUtil.isOverlap(placeItemList.get(i), placeItemList.get(j))) {
                        WriteUtil.drawSolutionForSingleBin(placeItemList, instance.r, "src/main/resources", "重叠");
                        throw new RuntimeException();
                    }
                }
            }
        }
    }
}
