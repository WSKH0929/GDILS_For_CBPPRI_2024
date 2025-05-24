package com.wskh.utils;

import com.wskh.classes.Instance;
import com.wskh.classes.Item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadUtil {

    public static Instance readInstanceForMultiBin(String path, String rho, int copyNum, boolean rotateEnable) {
        Instance instance = new Instance();
        instance.rho = rho;
        instance.rotateEnable = rotateEnable;
        List<Item> itemList = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(" ");
                if (split.length == 4) {
                    int num = Integer.parseInt(split[0]);
                    instance.n0 = num * copyNum;
                    itemList = new ArrayList<>(num);
                    if ("1/3".equals(rho)) {
                        instance.r = Double.parseDouble(split[1]);
                    } else if ("1/2".equals(rho)) {
                        instance.r = Double.parseDouble(split[2]);
                    } else if ("2/3".equals(rho)) {
                        instance.r = Double.parseDouble(split[3]);
                    } else {
                        throw new RuntimeException();
                    }
                    instance.r2 = instance.r * instance.r;
                    instance.binS = Math.PI * instance.r2;
                } else {
                    assert itemList != null;
                    Item item = new Item(0, Double.parseDouble(split[0]), Double.parseDouble(split[1]));
                    for (int i = 0; i < copyNum; i++) {
                        itemList.add(item.copy());
                    }
                }
            }
            assert itemList != null;
            // 计算容器半径
            itemList = itemList.stream().filter(item -> CommonUtil.compareDouble(item.w * item.w + item.h * item.h, 2 * instance.r * 2 * instance.r) <= 0).collect(Collectors.toList());
            instance.items = new Item[itemList.size()];
            for (int j = 0; j < instance.items.length; j++) {
                instance.items[j] = itemList.get(j);
                instance.items[j].id = j;
            }
            instance.n = itemList.size();
            bufferedReader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    public static Instance readInstanceForMultiBin3(String path, boolean rotateEnable) {
        Instance instance = new Instance();
        instance.rotateEnable = rotateEnable;
        List<Item> itemList = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(" ");
                if (itemList == null) {
                    itemList = new ArrayList<>();
                    instance.r = Double.parseDouble(line);
                } else {
                    Item item = new Item(0, Double.parseDouble(split[0]), Double.parseDouble(split[1]));
                    itemList.add(item);
                }
            }
            instance.r2 = instance.r * instance.r;
            instance.binS = Math.PI * instance.r2;
            instance.n0 = itemList.size();
            // 去除放不下的物品
            itemList = itemList.stream().filter(item -> (CommonUtil.compareDouble(item.w * item.w + item.h * item.h, 2* instance.r * 2 * instance.r) <= 0 && item.w > Parameter.EPS)).collect(Collectors.toList());
            instance.items = new Item[itemList.size()];
            for (int j = 0; j < instance.items.length; j++) {
                instance.items[j] = itemList.get(j);
                instance.items[j].id = j;
            }
            instance.n = itemList.size();
            bufferedReader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

}