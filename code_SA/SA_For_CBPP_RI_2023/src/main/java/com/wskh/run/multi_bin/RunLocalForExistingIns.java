package com.wskh.run.multi_bin;

import com.wskh.classes.Instance;
import com.wskh.classes.Item;
import com.wskh.classes.PlaceItem;
import com.wskh.classes.Solution;
import com.wskh.solvers.multi_bin.Kevin_Solver;
import com.wskh.utils.CheckUtil;
import com.wskh.utils.ReadUtil;
import com.wskh.utils.WriteUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RunLocalForExistingIns {
    private static String convertRho(String rho) {
        if (rho.equals("1/3")) {
            return "0";
        }
        if (rho.equals("1/2")) {
            return "1";
        }
        if (rho.equals("2/3")) {
            return "2";
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String dataDir = "../../instances/existing";
        String resDir = "res/existing";
        new File(resDir).mkdirs();
        new File(resDir + "/res").mkdirs();
        String[] rhos = new String[]{"1/3", "1/2", "2/3"};
        boolean[] rotateEnables = new boolean[]{false, true};

        File[] files = new File(dataDir).listFiles();

        if (files == null) {
            throw new RuntimeException("files is null");
        }

        Arrays.sort(files, (o1, o2) -> {
            int c = Integer.compare(o1.getName().length(), o2.getName().length());
            if (c != 0) {
                return c;
            }
            if (o1.getName().contains("s")) {
                if (!o2.getName().contains("s")) {
                    return -1;
                }
            } else {
                if (o2.getName().contains("s")) {
                    return 1;
                }
            }
            return o1.compareTo(o2);
        });

        FileOutputStream fileOutputStream = new FileOutputStream(resDir + "/Res-MultiBin-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".csv");
        fileOutputStream.write("Instance, n0, n, Rho,R, A, RotateEnable, UB, UB0Time, incTime, totalTime\n".getBytes(StandardCharsets.UTF_8));
        for (File file : files) {
            for (String rho : rhos) {
                for (boolean rotateEnable : rotateEnables) {
                    if (rotateEnable && file.getName().contains("s")) {
                        continue;
                    }

                    System.gc();

                    Instance instance = ReadUtil.readInstanceForMultiBin(file.getAbsolutePath(), rho, 1, rotateEnable);
                    String instanceName = file.getName().replace(".txt", "") + "-" + convertRho(rho) + "-" + instance.n0 + "-" + (rotateEnable ? "R" : "F");

                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.println(instanceName + " : " + instance.r);

                    // 放大
                    int bei = 10;
                    for (Item item : instance.items) {
                        item.w *= bei;
                        item.h *= bei;
                        item.s *= (bei * bei);
                    }
                    instance.r *= bei;
                    instance.r2 = Math.pow(instance.r, 2);
                    instance.binS = Math.PI * instance.r2;

                    Solution solution = null;
                    try {
                        solution = new Kevin_Solver(instance).solve(929L, 600000L);
                    } catch (RuntimeException e) {
                        if (e.getMessage() != null && e.getMessage().equals("item cannot be packed")) {
                            System.out.println("item cannot be packed");
                        } else {
                            throw e;
                        }
                    }

                    // 还原实例大小
                    for (Item item : instance.items) {
                        item.w /= bei;
                        item.h /= bei;
                        item.s /= (bei * bei);
                    }
                    instance.r /= bei;
                    instance.r2 = Math.pow(instance.r, 2);
                    instance.binS = Math.PI * instance.r2;

                    if (solution != null) {

                        for (List<PlaceItem> placeItemList : solution.placeItemLists) {
                            for (PlaceItem placeItem : placeItemList) {
                                placeItem.w /= bei;
                                placeItem.h /= bei;
                                placeItem.s /= (bei * bei);
                                placeItem.x /= bei;
                                placeItem.y /= bei;
                                placeItem.x2 /= bei;
                                placeItem.y2 /= bei;
                            }
                        }

                        CheckUtil.checkSolution(solution, instance);
                        String line = file.getName().replace(".txt", "") + ","
                                + instance.n0 + ","
                                + instance.n + ","
                                + convertRho(rho) + ","
                                + instance.r + ","
                                + instance.binS + ","
                                + (rotateEnable ? "R" : "F") + ","
                                + solution.UB + ","
                                + solution.ub0Time + ","
                                + solution.incTime + ","
                                + solution.totalTime + ","
                                + "\n";
                        fileOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
                        new File(resDir + "/images/" + instanceName).mkdirs();

                        WriteUtil.writeSolutionForMultiBin(solution, instance, resDir + "/res", instanceName);
                        WriteUtil.drawSolutionForMultiBin(solution, instance.r, resDir + "/images/" + instanceName);
                    } else {
                        String line = file.getName().replace(".txt", "") + ","
                                + instance.n0 + ","
                                + instance.n + ","
                                + convertRho(rho) + ","
                                + instance.r + ","
                                + instance.binS + ","
                                + (rotateEnable ? "R" : "F") + ","
                                + "\n";
                        fileOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
                    }
                }

            }
        }
        fileOutputStream.close();
    }
}
