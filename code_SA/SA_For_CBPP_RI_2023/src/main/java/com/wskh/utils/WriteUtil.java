package com.wskh.utils;

import com.wskh.classes.Instance;
import com.wskh.classes.PlaceItem;
import com.wskh.classes.Solution;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WriteUtil {

    public static void drawSolutionForMultiBin(Solution solution, double r, String path) {
        for (int i = 0; i < solution.placeItemLists.size(); i++) {
            List<PlaceItem> placeItemList = solution.placeItemLists.get(i);
            double[] xs = new double[placeItemList.size()];
            double[] ys = new double[placeItemList.size()];
            double[] ws = new double[placeItemList.size()];
            double[] hs = new double[placeItemList.size()];
            int[] idArr = new int[placeItemList.size()];
            for (int j = 0; j < placeItemList.size(); j++) {
                PlaceItem placeItem = placeItemList.get(j);
                xs[j] = placeItem.x;
                ys[j] = placeItem.y;
                ws[j] = placeItem.w;
                hs[j] = placeItem.h;
                idArr[j] = j + 1;
            }
            drawSolutionForSingleBin(idArr, xs, ys, ws, hs, r, path, "Bin-" + (i + 1));
        }
    }

    public static void drawSolutionForSingleBin(List<PlaceItem> placeItemList, double r, String path, String fileName) {
        double[] xs = new double[placeItemList.size()];
        double[] ys = new double[placeItemList.size()];
        double[] ws = new double[placeItemList.size()];
        double[] hs = new double[placeItemList.size()];
        int[] idArr = new int[placeItemList.size()];
        for (int i = 0; i < placeItemList.size(); i++) {
            PlaceItem placeItem = placeItemList.get(i);
            xs[i] = placeItem.x;
            ys[i] = placeItem.y;
            ws[i] = placeItem.w;
            hs[i] = placeItem.h;
            idArr[i] = i + 1;
        }
        drawSolutionForSingleBin(idArr, xs, ys, ws, hs, r, path, fileName);
    }

    public static void drawSolutionForSingleBin(int[] idArr, double[] xArr, double[] yArr, double[] wArr, double[] hArr, double r, String path, String fileName) {

        int paintR = 1000;

        //// 绘制原材料
        double ratio = (double) paintR / r;

        BufferedImage bufferedImage = new BufferedImage(2 * paintR + 40, 2 * paintR + 40,
                BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setStroke(new BasicStroke(8));
        // 绘制图片矩形背景
        g2.setColor(Color.white);
        g2.fillRect(0, 0, 2 * paintR + 40, 2 * paintR + 40);
        // 绘制圆形箱子
        g2.setColor(new Color(191, 191, 191));
        g2.fillArc(20, 20, 2 * paintR, 2 * paintR, 0, 360);
        g2.setColor(Color.black);
        g2.drawArc(20, 20, 2 * paintR, 2 * paintR, 0, 360);
//         设置透明度为0.5
//        Composite composite = g2.getComposite();
//        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
//        g2.setComposite(alphaComposite);
        //// 绘制零件
        for (int i = 0; i < xArr.length; i++) {
            double x = xArr[i];
            double y = yArr[i];
            double w = wArr[i];
            double h = hArr[i];

            int newX = (int) (x * ratio + paintR) + 20;
            int newY = (int) (2 * paintR - (y * ratio + paintR) - (h * ratio)) + 20;
            int newW = (int) (w * ratio);
            int newH = (int) (h * ratio);

            g2.setColor(Color.white);
            g2.fillRect(newX, newY, newW, newH);
            g2.setColor(Color.black);
            g2.drawRect(newX, newY, newW, newH);

            //设置字体颜色
            if (idArr != null) {
                g2.setColor(Color.BLACK);
                int fontSize = 36;
                g2.setFont(new Font("Times New Roman", Font.BOLD, fontSize));

                // 获取字体尺寸
                FontMetrics fontMetrics = g2.getFontMetrics();
                int fontWidth = fontMetrics.stringWidth(String.valueOf(idArr[i] + 1));
                int fontHeight = fontMetrics.getHeight();

                //向图片上写名字
                g2.drawString(String.valueOf(idArr[i]), newX + (newW - fontWidth) / 2, newY + (newH - fontHeight) / 2 + fontMetrics.getAscent());
            }
        }

        //// 结果输出
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            ImageIO.write(bufferedImage, "PNG", new FileOutputStream(path + "/" + fileName + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeSolutionForMultiBin(Solution solution, Instance instance, String path, String instanceName) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + instanceName + ".txt");
            fileOutputStream.write(("Instance: " + instanceName.split("-")[0] + "\n").getBytes(StandardCharsets.UTF_8));
            fileOutputStream.write(("Bin radius: " + instance.r + "\n").getBytes(StandardCharsets.UTF_8));
            if (instance.rho != null) {
                fileOutputStream.write(("Rho: " + instance.rho + "\n").getBytes(StandardCharsets.UTF_8));
            }
            fileOutputStream.write(("Rotate enable: " + instance.rotateEnable + "\n").getBytes(StandardCharsets.UTF_8));
            fileOutputStream.write(("Bin num: " + solution.placeItemLists.size() + "\n").getBytes(StandardCharsets.UTF_8));
            fileOutputStream.write(("Bin id,X coordinate of lower left corner,Y coordinate of lower left corner,Item's width,Item's height" + "\n").getBytes(StandardCharsets.UTF_8));
            for (int binId = 0; binId < solution.placeItemLists.size(); binId++) {
                for (PlaceItem placeItem : solution.placeItemLists.get(binId)) {
                    fileOutputStream.write(((binId + 1) + "," + placeItem.x + "," + placeItem.y + "," + placeItem.w + "," + placeItem.h + "\n").getBytes(StandardCharsets.UTF_8));
                }
            }
            fileOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
