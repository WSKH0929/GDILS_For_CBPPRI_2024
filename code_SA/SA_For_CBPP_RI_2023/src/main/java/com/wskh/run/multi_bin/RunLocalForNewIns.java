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
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RunLocalForNewIns {

    public static void main(String[] args) throws Exception {
        String dataDir = "../../instances/new";
        String resDir = "res/new";
        new File(resDir).mkdirs();
        new File(resDir + "/res").mkdirs();
        boolean[] rotateEnables = new boolean[]{false, true};

        File[] files = new File(dataDir).listFiles();

        if (files == null) {
            throw new RuntimeException("files is null");
        }

        FileOutputStream fileOutputStream = new FileOutputStream(resDir + "/Res-MultiBin-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".csv");
        fileOutputStream.write("Instance, n0, n,R, A, RotateEnable, UB, UB0Time, incTime, totalTime\n".getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            File file = files[i];
            for (boolean rotateEnable : rotateEnables) {

                Instance instance = ReadUtil.readInstanceForMultiBin3(file.getAbsolutePath(), rotateEnable);

                String instanceName = file.getName().replace(".txt", "") + "-" + (rotateEnable ? "R" : "F");

                System.out.println("-----------------------------------------------------------------------------");
                System.out.println(instanceName + " : " + instance.r + " , " + new Date());

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

                Solution solution = new Kevin_Solver(instance).solve(929L, 600000L);

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
                            + instance.r + ","
                            + instance.binS + ","
                            + (rotateEnable ? "R" : "F") + ","
                            + "\n";
                    fileOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
                }
            }

        }
        fileOutputStream.close();
    }
}
