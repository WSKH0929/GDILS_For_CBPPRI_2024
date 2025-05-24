package com.wskh.solvers.multi_bin;

import com.wskh.classes.*;
import com.wskh.utils.CommonUtil;

import java.util.*;

public class Kevin_Solver {

    Instance instance;

    public Kevin_Solver(Instance instance) {
        this.instance = instance;
    }

    Solution bestSolution;
    Random random;
    long timeLimit;
    long startTime;

    private boolean isTimeLimit() {
        return (System.currentTimeMillis() - startTime) >= timeLimit;
    }

    private boolean[][] getANewBin() {
        int W = CommonUtil.ceilToInt(2 * instance.r);
        boolean[][] bin = new boolean[W][W];
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < W; y++) {
                double d1 = Math.sqrt(Math.pow(x + 1 - instance.r, 2) + Math.pow(y + 1 - instance.r, 2));
                double d2 = Math.sqrt(Math.pow(x - instance.r, 2) + Math.pow(y + 1 - instance.r, 2));
                double d3 = Math.sqrt(Math.pow(x + 1 - instance.r, 2) + Math.pow(y - instance.r, 2));
                double d4 = Math.sqrt(Math.pow(x - instance.r, 2) + Math.pow(y - instance.r, 2));
                if (d1 > instance.r || d2 > instance.r || d3 > instance.r || d4 > instance.r) {
                    bin[x][y] = true;
                }
            }
        }
        return bin;
    }

    private boolean packInBin(boolean[][] bin, Item item, List<PlaceItem> placeItemList) {
        // 不旋转
        PlaceItem placeItem = new PlaceItem(item.id, 0, 0, item.w, item.h, item.s);
        for (int i = 0; i < bin.length; i++) {
            for (int j = 0; j < bin[i].length; j++) {
                if (bestScore != null && isTimeLimit()) throw new TimeLimitException();
                if (!bin[i][j]) {
                    boolean canPack = true;
                    for (int k = i; k <= CommonUtil.floorToInt(i + placeItem.w) && canPack; k++) {
                        for (int u = j; u <= CommonUtil.floorToInt(j + placeItem.h); u++) {
                            if (bin[k][u]) {
                                canPack = false;
                                break;
                            }
                        }
                    }
                    if (canPack) {
                        for (int k = i; k <= CommonUtil.floorToInt(i + placeItem.w); k++) {
                            for (int u = j; u <= CommonUtil.floorToInt(j + placeItem.h); u++) {
                                bin[k][u] = true;
                            }
                        }
                        placeItem.x = i - instance.r;
                        placeItem.y = j - instance.r;

                        int m0 = (int) (placeItem.x + instance.r);
                        int u0 = (int) (placeItem.y + instance.r);
                        if (m0 != i) throw new RuntimeException();
                        if (u0 != j) throw new RuntimeException();

                        placeItemList.add(placeItem);
                        return true;
                    }
                }
            }
        }
        // 旋转
        if (instance.rotateEnable) {
            placeItem.w = item.h;
            placeItem.h = item.w;
            for (int i = 0; i < bin.length; i++) {
                for (int j = 0; j < bin[i].length; j++) {
                    if (!bin[i][j]) {
                        boolean canPack = true;
                        for (int k = i; k <= CommonUtil.floorToInt(i + placeItem.w) && canPack; k++) {
                            for (int u = j; u <= CommonUtil.floorToInt(j + placeItem.h); u++) {
                                if (bin[k][u]) {
                                    canPack = false;
                                    break;
                                }
                            }
                        }
                        if (canPack) {
                            for (int k = i; k <= CommonUtil.floorToInt(i + placeItem.w); k++) {
                                for (int u = j; u <= CommonUtil.floorToInt(j + placeItem.h); u++) {
                                    bin[k][u] = true;
                                }
                            }
                            placeItem.x = i - instance.r;
                            placeItem.y = j - instance.r;
                            placeItemList.add(placeItem);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private double computeScore(List<List<PlaceItem>> lists) {
        double score = -lists.size();
        double max = -100;
        double min = 100;
        for (List<PlaceItem> list : lists) {
            double sumS = 0;
            for (PlaceItem placeItem : list) sumS += placeItem.s;
            double density = sumS / instance.binS;
            max = Math.max(max, density);
            min = Math.min(min, density);
        }
        return score + max - min;
    }

    private void PGS(List<Item> itemList, List<boolean[][]> binList, List<List<PlaceItem>> placeItemListList) {
        for (Item item : itemList) {
            boolean packed = false;
            for (int b = 0; b < binList.size(); b++) {
                boolean[][] bin = binList.get(b);
                packed = packInBin(bin, item, placeItemListList.get(b));
                if (packed) break;
            }
            if (!packed) {
                boolean[][] newBin = getANewBin();
                List<PlaceItem> newPlaceItemList = new ArrayList<>();
                binList.add(newBin);
                placeItemListList.add(newPlaceItemList);
                packed = packInBin(newBin, item, newPlaceItemList);
                if (!packed) throw new RuntimeException("item cannot be packed");
            }
        }
        for (List<PlaceItem> placeItemList : placeItemListList) {
            for (PlaceItem placeItem : placeItemList) {
                placeItem.x2 = placeItem.x + placeItem.w;
                placeItem.y2 = placeItem.y + placeItem.h;
            }
        }
    }

    Double bestScore;

    public Solution solve(Long seed, long timeLimit) {
        startTime = System.currentTimeMillis();

        bestSolution = new Solution();
        random = seed == null ? new Random() : new Random(seed);
        this.timeLimit = timeLimit;

        startTime = System.currentTimeMillis();

        // FGS
        List<Item> unpackedItemList = new ArrayList<>(Arrays.asList(instance.items));
        unpackedItemList.sort((o1, o2) -> -Double.compare(o1.s, o2.s));

        List<boolean[][]> bestBinList = new ArrayList<>();
        List<List<PlaceItem>> bestPlaceItemListList = new ArrayList<>();
        bestBinList.add(getANewBin());
        bestPlaceItemListList.add(new ArrayList<>());
        PGS(unpackedItemList, bestBinList, bestPlaceItemListList);

        // 模拟退火
        // 计算初始解的适应度
        bestScore = computeScore(bestPlaceItemListList);
        System.out.println("初始解: " + bestPlaceItemListList.size() + " , " + bestScore);
        bestSolution.incTime = (System.currentTimeMillis() - startTime) / 1000d;
        bestSolution.ub0Time = bestSolution.incTime;

        // 开始迭代
        double T = 10d;
        double minT = 0.3;
        int CQP_EpochCnt = 2;
        int maxEpochForEachT = CommonUtil.ceilToInt(0.05 * Math.pow(instance.items.length, 4d / 3));

        List<boolean[][]> currentBinList = new ArrayList<>();
        for (boolean[][] bin : bestBinList) {
            boolean[][] currentBin = new boolean[bin.length][bin[0].length];
            for (int j = 0; j < bin.length; j++) {
                System.arraycopy(bin[j], 0, currentBin[j], 0, bin[j].length);
            }
            currentBinList.add(currentBin);
        }
        List<List<PlaceItem>> currentPlaceItemListList = new ArrayList<>();
        for (List<PlaceItem> placeItemList : bestPlaceItemListList) {
            currentPlaceItemListList.add(new ArrayList<>(placeItemList));
        }
        double currentScore = bestScore;

        try {
            while (!isTimeLimit()) {
                // 温度循环
                int acceptNum = 0;
                for (int i = 0; i < maxEpochForEachT; i++) {
                    // CQP
                    List<boolean[][]> tempBinList = new ArrayList<>();
                    for (boolean[][] bin : currentBinList) {
                        boolean[][] tempBin = new boolean[bin.length][bin[0].length];
                        for (int j = 0; j < bin.length; j++) {
                            System.arraycopy(bin[j], 0, tempBin[j], 0, bin[j].length);
                        }
                        tempBinList.add(tempBin);
                    }
                    List<List<PlaceItem>> tempPlaceItemListList = new ArrayList<>();
                    for (List<PlaceItem> placeItemList : currentPlaceItemListList) {
                        tempPlaceItemListList.add(new ArrayList<>(placeItemList));
                    }
                    for (int c = 0; c < CQP_EpochCnt; c++) {
                        int randomB = random.nextInt(tempPlaceItemListList.size());
                        List<PlaceItem> placeItemListB = tempPlaceItemListList.get(randomB);
                        boolean[][] tempBinB = tempBinList.get(randomB);
                        unpackedItemList = new ArrayList<>();
                        switch (random.nextInt(4)) {
                            case 0:
                                // 右上
                                for (int k = placeItemListB.size() - 1; k >= 0; k--) {
                                    PlaceItem placeItem = placeItemListB.get(k);
                                    if ((placeItem.x > 0 && placeItem.y > 0) || (placeItem.x2 > 0 && placeItem.y > 0) || (placeItem.x > 0 && placeItem.y2 > 0) || (placeItem.x2 > 0 && placeItem.y2 > 0)) {
                                        placeItemListB.remove(k);
                                        unpackedItemList.add(instance.items[placeItem.id]);
                                        int m0 = (int) (placeItem.x + instance.r);
                                        int u0 = (int) (placeItem.y + instance.r);
                                        for (int m = m0; m <= CommonUtil.floorToInt(m0 + placeItem.w); m++) {
                                            for (int u = u0; u <= CommonUtil.floorToInt(u0 + placeItem.h); u++) {
                                                tempBinB[m][u] = false;
                                            }
                                        }
                                    }
                                }
                                break;
                            case 1:
                                // 左上
                                for (int k = placeItemListB.size() - 1; k >= 0; k--) {
                                    PlaceItem placeItem = placeItemListB.get(k);
                                    if ((placeItem.x < 0 && placeItem.y > 0) || (placeItem.x2 < 0 && placeItem.y > 0) || (placeItem.x < 0 && placeItem.y2 > 0) || (placeItem.x2 < 0 && placeItem.y2 > 0)) {
                                        placeItemListB.remove(k);
                                        unpackedItemList.add(instance.items[placeItem.id]);
                                        int m0 = (int) (placeItem.x + instance.r);
                                        int u0 = (int) (placeItem.y + instance.r);
                                        for (int m = m0; m <= CommonUtil.floorToInt(m0 + placeItem.w); m++) {
                                            for (int u = u0; u <= CommonUtil.floorToInt(u0 + placeItem.h); u++) {
                                                tempBinB[m][u] = false;
                                            }
                                        }
                                    }
                                }
                                break;
                            case 2:
                                // 左下
                                for (int k = placeItemListB.size() - 1; k >= 0; k--) {
                                    PlaceItem placeItem = placeItemListB.get(k);
                                    if ((placeItem.x < 0 && placeItem.y < 0) || (placeItem.x2 < 0 && placeItem.y < 0) || (placeItem.x < 0 && placeItem.y2 < 0) || (placeItem.x2 < 0 && placeItem.y2 < 0)) {
                                        placeItemListB.remove(k);
                                        unpackedItemList.add(instance.items[placeItem.id]);
                                        int m0 = (int) (placeItem.x + instance.r);
                                        int u0 = (int) (placeItem.y + instance.r);
                                        for (int m = m0; m <= CommonUtil.floorToInt(m0 + placeItem.w); m++) {
                                            for (int u = u0; u <= CommonUtil.floorToInt(u0 + placeItem.h); u++) {
                                                tempBinB[m][u] = false;
                                            }
                                        }
                                    }
                                }
                                break;
                            case 3:
                                // 右下
                                for (int k = placeItemListB.size() - 1; k >= 0; k--) {
                                    PlaceItem placeItem = placeItemListB.get(k);
                                    if ((placeItem.x > 0 && placeItem.y < 0) || (placeItem.x2 > 0 && placeItem.y < 0) || (placeItem.x > 0 && placeItem.y2 < 0) || (placeItem.x2 > 0 && placeItem.y2 < 0)) {
                                        placeItemListB.remove(k);
                                        unpackedItemList.add(instance.items[placeItem.id]);
                                        int m0 = (int) (placeItem.x + instance.r);
                                        int u0 = (int) (placeItem.y + instance.r);
                                        for (int m = m0; m <= CommonUtil.floorToInt(m0 + placeItem.w); m++) {
                                            for (int u = u0; u <= CommonUtil.floorToInt(u0 + placeItem.h); u++) {
                                                tempBinB[m][u] = false;
                                            }
                                        }
                                    }
                                }
                                break;
                            default:
                                throw new RuntimeException();
                        }
                        unpackedItemList.sort((o1, o2) -> -Double.compare(o1.s, o2.s));
                        PGS(unpackedItemList, tempBinList, tempPlaceItemListList);
                        for (int b = tempBinList.size() - 1; b >= 0; b--) {
                            if (tempPlaceItemListList.get(b).isEmpty()) {
                                tempPlaceItemListList.remove(b);
                                tempBinList.remove(b);
                            }
                        }
                    }
                    // 更新最优解
                    double tempScore = computeScore(tempPlaceItemListList);
                    if (tempScore > bestScore) {
                        if (tempPlaceItemListList.size() < bestPlaceItemListList.size()) {
                            bestSolution.incTime = (System.currentTimeMillis() - startTime) / 1000d;
                        }
                        bestScore = tempScore;
                        bestPlaceItemListList = tempPlaceItemListList;
                    }
                    // 更新当前解
                    double delta = tempScore - currentScore;
                    if (delta > 0 || random.nextDouble() < Math.exp(delta / T)) {
                        currentPlaceItemListList = tempPlaceItemListList;
                        currentBinList = tempBinList;
                        currentScore = tempScore;
                        acceptNum++;
                    }
                }
                // 更新温度
                double rate = acceptNum / (double) maxEpochForEachT;
                if (rate > 0.96) {
                    T *= 0.5;
                } else if (rate > 0.15) {
                    T *= 0.95;
                } else {
                    T *= 0.8;
                }
                T = Math.max(minT, T);
            }
        } catch (TimeLimitException e) {

        }

        // 填充信息，返回结果
        System.out.println("最终解: " + bestPlaceItemListList.size() + " , " + bestScore);
        bestSolution.placeItemLists = bestPlaceItemListList;
        bestSolution.UB = bestPlaceItemListList.size();
        bestSolution.totalTime = (System.currentTimeMillis() - startTime) / 1000d;
        return bestSolution;
    }
}
