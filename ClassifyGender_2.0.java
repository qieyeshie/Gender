package com.ldz;

import java.io.*;
import java.util.*;

public class ClassifyGender {

    static HashMap<String, Integer> name_map_f = new HashMap<>();
    static HashMap<String, Integer> name_map_m = new HashMap<>();
    static List<String> n_f_trainDataList = new ArrayList<>();
    static List<String> n_f_testDataList = new ArrayList<>();
    static List<String> n_m_trainDataList = new ArrayList<>();
    static List<String> n_m_testDataList = new ArrayList<>();
    static List<String> names_falemale = new ArrayList<>();
    static List<String> names_male = new ArrayList<>();

    //朴素贝叶斯算法
    static double base_f() {
        double mean_f = n_f_trainDataList.size() * 1.0 / (n_f_trainDataList.size() + n_m_trainDataList.size());
        double base_f = Math.log(mean_f);
        for (String k : name_map_f.keySet()) {
            double f = name_map_f.get(k);
            double frequency_falemale = f  / n_f_trainDataList.size();
            base_f += (Math.log(1.0 - frequency_falemale));
        }
        return base_f;
    }

    static double base_m() {
        double mean_m = n_m_trainDataList.size() * 1.0 / (n_f_trainDataList.size() + n_m_trainDataList.size());
        double base_m = Math.log(mean_m);
        for (String k : name_map_m.keySet()) {
            double f = name_map_m.get(k);
            double frequency_male = f  / n_m_trainDataList.size();
            base_m += (Math.log(1.0 - frequency_male));
        }
        return base_m;
    }

    static double GetLogProb_f(String string) {
        //拉普拉斯平滑参数
        double alpha = 1.0;
        //女性名字词频
        double freq_f = 0;
        //捕抓空值异常
        try {
            freq_f = name_map_f.get(string);
        } catch (NullPointerException nullPointerException) {

        } finally {

        }
        //拉普拉斯平滑计算
        double freq_smooth_f = (freq_f * 1.0 + alpha) / (names_falemale.size() + name_map_f.size() * alpha);
        //返回平滑处理后的结果
        return Math.log(freq_smooth_f) - Math.log(1 - freq_smooth_f);
    }

    static double GetLogProb_m(String string) {
        double alpha = 2.0; //最高0.848
        //男性单个字的词频
        double freq_m = 0;
        //捕抓空值异常
        try {
            freq_m = name_map_m.get(string);
        } catch (NullPointerException nullPointerException) {

        } finally {

        }
        double freq_smooth_m = (freq_m * 1.0 + alpha) / (names_male.size() + name_map_m.size() * alpha);
        return Math.log(freq_smooth_m) - Math.log(1 - freq_smooth_m);
    }

    //朴素贝叶斯算法
    static boolean ComputerLogProb(String string) {
        double logprob_f = base_f();
        double logprob_m = base_m();
        String[] splitt = string.split("");
        for (String k : splitt) {
            logprob_f += GetLogProb_f(k);
            logprob_m += GetLogProb_m(k);
        }
        //返回结果（true表示为女名，false为男名）
        return (logprob_f > logprob_m);
    }


    static int Getgender(String string) {
        int g = 0;
        if (ComputerLogProb(string) == true) {
            g = 0;
        } else {
            g = 1;
        }
        return (g);
    }


    public static void main(String[] args) throws IOException {

        //读取数据
        DataInputStream file = new DataInputStream(new FileInputStream(new File("E:\\mahout\\day3\\data\\name_data.txt")));
        BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
        HashMap<String, String> tokenMap = new HashMap<>();
        List<String> t_name = new ArrayList<>();
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] lineArr = line.split(",");
            tokenMap.put(lineArr[1], lineArr[2]);
            t_name.add(lineArr[1]);
        }

        HashMap<String, String> trainDataMap = new HashMap<>();
        HashMap<String, String> testDataMap = new HashMap<>();

        //70precent做训练集
        List<Map.Entry<String , String>> lists = new ArrayList<>(tokenMap.entrySet());
        int trainNum = (int) (0.7f * tokenMap.size());
        for (Map.Entry<String , String > set1 : lists.subList(0, trainNum)) {
            trainDataMap.put(set1.getKey(), set1.getValue());
        }
        for (Map.Entry<String , String > set2 : lists.subList(trainNum, tokenMap.size())) {
            testDataMap.put(set2.getKey(), set2.getValue());
        }

        //分别读取男女名字
        for (Map.Entry<String , String> map : trainDataMap.entrySet()) {
            if (map.getValue().equals("0")) {
                n_f_trainDataList.add(map.getKey());
            }else {
                n_m_trainDataList.add(map.getKey());
            }
        }

        for (Map.Entry<String , String> map : testDataMap.entrySet()) {
            if (map.getValue().equals("0")) {
                n_f_testDataList.add(map.getKey());
            }else {
                n_m_testDataList.add(map.getKey());
            }
        }


        //女性名字分词得到names_falemale
        for (String name1 : n_f_trainDataList) {
            String[] surname1 = name1.split("");
            for (String c1 : surname1) {
                names_falemale.add(c1);
            }
        }
        //统计女性名字分词后的每个文字的数量
        for (int i = 0; i < names_falemale.size(); i++) {
            String ch1 = names_falemale.get(i);
            if (!name_map_f.containsKey(ch1)) {
                name_map_f.put(ch1, 1);
            } else {
                int val1 = name_map_f.get(ch1);
                val1++;
                name_map_f.put(ch1, val1);
            }
        }

        //男性分词的得到names_male
        for (String name2 : n_m_trainDataList) {
            String[] surname2 = name2.split("");
            for (String c2 : surname2) {
                names_male.add(c2);
            }
        }
        //统计男性名字分词后的每个文字的数量
        for (int i = 0; i < names_male.size(); i++) {
            String ch2 = names_male.get(i);
            if (!name_map_m.containsKey(ch2)) {
                name_map_m.put(ch2, 1);
            } else {
                int val2 = name_map_m.get(ch2);
                val2++;
                name_map_m.put(ch2, val2);
            }
        }

        //试一试
        /*String name = "卢本伟";
        String out = null;
        if (Getgender(name) == 1) {
            out = "牛逼";
        }else {
            out = "女孩";
        }
        System.out.printf("%S%S\n", name , out);*/

        //测试结果
        int correctNum = 0;
        for (String k : testDataMap.keySet()) {
            if (Integer.parseInt(testDataMap.get(k)) == (Getgender(k))) {
                ++correctNum;
            }
        }
        //正确率
        System.out.printf("correctNum = %d correct precent = %f \n", correctNum, correctNum * 1.0 / (testDataMap.size()));
    }

}








