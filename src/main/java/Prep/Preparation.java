package Prep;

import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Preparation {
    String symbols = "ACEFGHJKLMNPRSTUWYZ123456789";
    Integer maxIndex;
    String recordFilePath;
    String beginningsFilePath;
    Integer numOfRecords;
    Integer fileSize;

    public Preparation(int maxIndex, String recordFilePath, String beginningsFilePath, Integer numOfRecords){
        this.maxIndex = maxIndex;
        this.recordFilePath = recordFilePath;
        this.beginningsFilePath = beginningsFilePath;
        this.numOfRecords = numOfRecords;
    }

    String DrawBeginning() {
        Random rn = new Random();
        Integer index = ThreadLocalRandom.current().nextInt(0, maxIndex - 1);
        String begin = "";
        try {
            File Obj = new File(beginningsFilePath);
            Scanner reader = new Scanner(Obj);
            for(int i = 0; i <= index; i++)
                begin = reader.nextLine();

            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred.");
            e.printStackTrace();

        }return begin;
    }

    String DrawEnding(){
        String end = "";
        Integer numOfSymbols = 7;
        Integer index;

        for(int i = 0; i < numOfSymbols; i++){
            index = ThreadLocalRandom.current().nextInt(0, symbols.length());
            end += symbols.charAt(index);
        }
        return end;
    }

    String CreateLicensePlate(){
        return DrawBeginning() + DrawEnding();
    }

    public Integer CreateRecords() throws IOException {
        Integer shortRecord = 9;
        FileOutputStream FileOS = new FileOutputStream(recordFilePath);
        DataOutputStream DataOS = new DataOutputStream(FileOS);
        Integer totalBytes;
        try {
            for (int i = 0; i < numOfRecords; i++) {
                String lp = CreateLicensePlate();
                if(lp.length() == shortRecord)
                    DataOS.writeBytes(lp + "  ");
                else
                    DataOS.writeBytes(lp + " ");
            }
            totalBytes = DataOS.size();
            DataOS.close();
            fileSize = totalBytes;
            return totalBytes;
        } catch (IOException e) {
            System.out.println("An error has occurred.");
            e.printStackTrace();
            return -1;
        }
    }
    public void CreateSortingFiles(Integer n ){
        for(int i = 0; i<n; i++) {
            try {
                File myObj = new File("src/main/java/Sorting/sortingFiles/"+i+".txt");
                if (myObj.createNewFile()) {
                } else {
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }
    public void clear(Integer n){
        for(int i = 0; i<n; i++) {
            try {
                Files.deleteIfExists(Paths.get("src/main/java/Sorting/sortingFiles/"+i+".txt"));
            }
            catch (NoSuchFileException e) {
            }
            catch (DirectoryNotEmptyException e) {
            }
            catch (IOException e) {
            }

        }
    }
}
