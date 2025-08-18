package com.rspsi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author ReverendDread on 12/5/2019
 * https://www.rune-server.ee/members/reverenddread/
 * @project RSPSiSuite
 */
public class XTEAConverter {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter directory containing .txt files:");
        File dir = new File("/home/james/Downloads/xteas.json");
        if(!dir.isDirectory()){
            System.err.println("Entered path is not a directory!");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try(FileReader fr = new FileReader(dir)) {
                Map<Integer, int[]> map = gson.fromJson(fr, new TypeToken<Map<Integer, int[]>>(){}.getType());

                List<XTEA> xteas = map.entrySet().stream().map(entry -> new XTEA(entry.getKey(), entry.getValue())).collect(Collectors.toList());
                try (FileWriter fw = new FileWriter(new File("/home/james/Downloads/xteasconv.json"))){
                    gson.toJson(xteas, fw);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

        List<XTEA> xteas = new ArrayList<>();

        for(File file : dir.listFiles()){
            if(file.getName().endsWith(".txt")){
                try {
                    int regionId = Integer.parseInt(file.getName().replace(".txt", "").trim());
                    int[] keys = new int[4];
                    List<String> lines = Files.readAllLines(file.toPath()).stream().filter(str -> str != null && !str.isEmpty()).limit(4).collect(Collectors.toList());
                    for(int index = 0;index<lines.size();index++){
                        keys[index] = Integer.parseInt(lines.get(index));
                    }
                    xteas.add(new XTEA(regionId, keys));
                } catch(Exception ex){
                    System.out.println("Failed to parse " + file.getName());
                }
            }


        }

        File jsonOut = new File(dir, "xteas.json");

        try(FileWriter fw = new FileWriter(jsonOut)){
            new GsonBuilder().setPrettyPrinting().create().toJson(xteas, fw);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static class XTEA {
        private final int region;
        private final int[] keys;

        public XTEA(int region, int[] keys) {
            this.region = region;
            this.keys = keys;
        }
    }

}
