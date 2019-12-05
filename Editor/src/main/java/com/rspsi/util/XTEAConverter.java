package com.rspsi.util;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author ReverendDread on 12/5/2019
 * https://www.rune-server.ee/members/reverenddread/
 * @project RSPSiSuite
 */
public class XTEAConverter {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter directory containing .txt files:");
        File dir = new File("F:\\Conspiracyx\\Cock Server\\conspiracyx\\data\\map\\archiveKeys\\unpacked");
        if(!dir.isDirectory()){
            System.err.println("Entered path is not a directory!");
            System.exit(0);
        }

        List<XTEA> xteas = new ArrayList<>();

        for(File file : dir.listFiles()){
            if(file.getName().endsWith(".txt")){
                try {
                    int regionId = Integer.parseInt(file.getName().replace(".txt", "").trim());
                    int[] keys = new int[4];
                    List<String> lines = Files.readAllLines(file.toPath());
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
