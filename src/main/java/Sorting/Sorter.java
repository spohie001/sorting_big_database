package Sorting;

import FileManagement.FileManager;
import Heap.MinHeap;
import Heap.MinHeapNode;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Sorter {
    ArrayList<String> sortingFiles = new ArrayList<>();
    Integer numOfSortingFiles;
    Integer numOfRecordsInBlock;
    Integer blockSize;
    Integer fileSize;
    Integer sizeOfRecordInFile;
    Integer numOfUsedBuffers = 0;
    FileManager fileManager;

    public Sorter( Integer numOfSortingFiles, Integer numOfRecordsInBlock, Integer blockSize, Integer fileSize, Integer sizeOfRecordInFile, FileManager fileManager){
        this.fileManager = fileManager;
        this.numOfRecordsInBlock = numOfRecordsInBlock;
        this.numOfSortingFiles = numOfSortingFiles;
        this.blockSize = blockSize;
        this.fileSize = fileSize;
        this.sizeOfRecordInFile = sizeOfRecordInFile;
        for(Integer i = 0; i < numOfSortingFiles; i++)
            sortingFiles.add(i + ".txt");
    }
    //returns number if not empty file or -1 if all empty
    private Integer FindNotEmpty(ArrayList <Integer> bytesToRead){
        for(int i = 0; i<numOfSortingFiles; i++){
            if(bytesToRead.get(i) > 0)
                return i;
        }
        return -1;
    }

    public void merge(Integer numBuffers, Integer blockSizeBuffer, Integer numOfRecordsInBuffer, Integer numOftapes) throws IOException{
        //ile jest taś mogólnie
        Integer tapes = numOftapes;
        //ile zostało taśm do przetworzenia
        Integer tapesLeft = numOftapes;
        //obsługujemy max 5 taśm
        Integer tapesNotEmpty =0;

        Integer fazysortowania = 1;
        Integer diskOp = 0;

        ArrayList <FileManager> fileManagers = new ArrayList<>();


        if(tapes < numBuffers)
            numOfUsedBuffers =tapes;
        else
            numOfUsedBuffers = numBuffers;
        //5

        //zmiana taśm do ktorychsie odwołujemy
        Integer firstTapeNum = 0;
        //TODO: całość trzeb azapętlić aż zostanie jedna taśma
        while(tapesLeft>1){
            fazysortowania++;
            //plus dodatkowy bufor wynikowy
            for (int i = 0; i < numOfUsedBuffers; i++) {
                Integer tapeNum = i+ firstTapeNum;
                String path = "src/main/java/Sorting/sortingFiles/" + tapeNum + ".txt";
                File f = new File(path);
                //czytamy tylko tyle plików ile pomieści jeden bufor
                fileManagers.add(new FileManager(path, (int) f.length(), blockSizeBuffer));
            }
            ArrayList<ArrayList<String>> buffers = new ArrayList<>();
            for (int i = 0; i < numOfUsedBuffers; i++) {
                buffers.add(new ArrayList<String>());
            }

            //ile zostało bajtów do przeczytani an aaśmie
            ArrayList<Integer> bytesToRead = new ArrayList<>();
            MinHeap heap = new MinHeap(numOfUsedBuffers);

            {
                Integer i;


                for (i = 0; i < numOfUsedBuffers; i++) {

                    Integer readrec = fileManagers.get(i).ReadBlock();
                    diskOp++;
                    Integer blockS = fileManagers.get(i).getFileSize() - readrec;
                    bytesToRead.add(blockS);
                    //dodaj do tego bufora ryle rekordów ile sie w nim zmieści

                    int n = readrec / sizeOfRecordInFile;
                    //do bufora dodaj tyle rekordów ile sie na nim zmieści
                    for (int j = 0; j < n; j++) {
                        buffers.get(i).add(fileManagers.get(i).NextRecord());
                    }

                    MinHeapNode node = new MinHeapNode(buffers.get(i).get(0), i);
                    heap.insert(node);
                    buffers.get(i).remove(0);
                    //tutaj zapisuje ile zostało na tasmie rekordow
                    bytesToRead.set(i, bytesToRead.get(i) - sizeOfRecordInFile);
                }
            }
            heap.minHeap();
            tapesNotEmpty = numOfUsedBuffers;

//taśma wynikowa!!

            File myObj = new File("src/main/java/Sorting/sortingFiles/" + tapes + ".txt");

            tapes++;
            tapesLeft++;
            String block = new String();
            while((heap.getIndex() > 0)){
                //1.
                //zamiast mieć bufor wynikowy to jak tworze string wynikowy

                //Integer size = 0;

                //weź najmniejszy i usuń z kopca
                MinHeapNode record = heap.remove();
                //weź jego plik
                Integer fileNum = record.getFileNum();
                Integer bNum = fileNum - firstTapeNum;

                //2.
                Integer shortRecord = 9;
                if (record.getValue().length() == shortRecord)
                    block += record.getValue() + "  ";
                else
                    block += record.getValue() + " ";
                //dodaliśmy kolejny rekord do bloku który kiedys zapiszemy n ataśmie

                //3.
                if(block.length() == blockSizeBuffer || heap.getIndex() == 0){
                    OutputStreamWriter streamWriter = new OutputStreamWriter(new FileOutputStream(myObj.getPath(), true));
                    diskOp++;
                    streamWriter.write(block);
                    streamWriter.close();//?
                    block = "";
                }


                /*4.*/
                //jeśli bufor nie jest pusty
                if(buffers.get(bNum).size()>0){
                    MinHeapNode node = new MinHeapNode(buffers.get(bNum).get(0), fileNum);
                    heap.insert(node);
                    buffers.get(bNum).remove(0);
                }
                else{
                    if(bytesToRead.get(fileNum) > 0){

                        Integer readrec = fileManagers.get(fileNum).ReadBlock();
                        diskOp++;
                        Integer blockS = bytesToRead.get(fileNum) - readrec;
                        //bytesToRead.add(blockS);


                        bytesToRead.set(fileNum, blockS);
                        //dodaj do tego bufora ryle rekordów ile sie w nim zmieści


                        int n = readrec/sizeOfRecordInFile;
                        //do bufora dodaj tyle rekordów ile sie na nim zmieści
                        for (int j = 0; j < n; j++) {
                            buffers.get(bNum).add(fileManagers.get(fileNum).NextRecord());
                        }

                        MinHeapNode node = new MinHeapNode(buffers.get(bNum).get(0), fileNum);
                        heap.insert(node);
                        buffers.get(bNum).remove(0);
                        //tutaj zapisuje ile zostało na tasmie rekordow

                    }
                    //jeśli pusta taśma to nie dodawaj elementu do kopca

                }
                heap.minHeap();
                /**/

            }
            tapesLeft -= numOfUsedBuffers;

            //zmiana taśm do ktorychsie odwołujemy
            firstTapeNum += numOfUsedBuffers;

            //jesli mamy mniej taśm niż buforów
            if(tapesLeft < numOfUsedBuffers)
                numOfUsedBuffers = tapesLeft;

        }

        System.out.println("\nLiczba faz sortowania = " + fazysortowania); //
        System.out.println("Liczba operacji dyskowych = " + diskOp);

    }

/**
    public void mergeFiles(Integer numBuffers, Integer blockSizeBuffer, Integer numOfRecordsInBuffer, Integer numOftapes) throws IOException {

        //ile jest taś mogólnie
        Integer tapes = numOftapes;
        //ile zostało taśm do przetworzenia
        Integer tapesLeft = numOftapes;
        //obsługujemy max 5 taśm
        Integer tapesNotEmpty =0;

        ArrayList <FileManager> fileManagers = new ArrayList<>();
        numOfUsedBuffers = numBuffers;
        //5

        //plus dodatkowy bufor wynikowy
        for(int i = 0; i <= numOfUsedBuffers; i++){
            String path = "src/main/java/Sorting/sortingFiles/" + i + ".txt";
            File f = new File(path);
            //czytamy tylko tyle plików ile pomieści jeden bufor
            fileManagers.add(new FileManager(path, (int) f.length(), blockSizeBuffer));
        }
        ArrayList<ArrayList<String>> buffers = new ArrayList<>();
        for(int i =0; i<numBuffers; i++){
            buffers.add(new ArrayList<String>());
        }
        //OSTATNI JEST WYNIKOWY!!


        //ile zostało bajtów do przeczytani an aaśmie
        ArrayList <Integer> bytesToRead = new ArrayList<>();
        MinHeap heap = new MinHeap(numOfUsedBuffers);
        Integer i;



        for(i = 0; i < numOfUsedBuffers; i++){
            bytesToRead.add(fileManagers.get(i).ReadBlock());
            //dodaj do tego bufora ryle rekordów ile sie w nim zmieści

            for(int j =0; j<numOfRecordsInBuffer; j++) {
                buffers.get(i).add(fileManagers.get(i).NextRecord());
            }

            MinHeapNode node = new MinHeapNode(buffers.get(i).get(0), i);
            heap.insert(node);

            //tutaj zapisuje ile zostało na tasmie rekordow
            bytesToRead.set(i, bytesToRead.get(i) - sizeOfRecordInFile);
        }
        heap.minHeap();

        String block = new String();
        Integer size = 0;
        while(heap.getIndex() > 0){

            //weź najmniejszy i usuń z kopca
            MinHeapNode record = heap.remove();
            //weź jego plik
            Integer fileNum = record.getFileNum();

            Integer shortRecord = 9;
            if(record.getValue().length() == shortRecord)
                block += record.getValue() + "  ";
            else
                block += record.getValue() + " ";

            //TODO: ZAPISUJEMY GDZY WSZTYTSTKIE TASMY Z KTORYCH CZYTALISMY SA PUSTE!!!
            //if(block.length() == blockSizeBuffer || heap.getIndex() == 0) {
              if(wszystkie taśmy puste){

                // trzeb azapisać na nowej taśmie
                tapes++;
                File myObj = new File("src/main/java/Sorting/sortingFiles/"+tapes+".txt");
                tapesLeft++;

                if (myObj.createNewFile()) {
                } else {
                }

                FileManager newFM = new FileManager("src/main/java/Sorting/sortingFiles/"+tapes+".txt", size, blockSizeBuffer);

                newFM.writeToFile(block);
                //Systeo//utFM.print();
                size += block.length();
                block = "";
            }

            //jeśli bufor nie jest pusty
            if(buffers.size()>0){
                buffers.get(i).remove(0);
                MinHeapNode node = new MinHeapNode(buffers.get(i).get(0), fileNum);
                heap.insert(node);
            }

            //if file not empty
            if(bytesToRead.get(fileNum) > 0){
                MinHeapNode node = new MinHeapNode(fileManagers.get(fileNum).NextRecord(), fileNum);
                heap.insert(node);
                bytesToRead.set(fileNum, bytesToRead.get(fileNum) - sizeOfRecordInFile);
            }
            else{
                //find file that not empty
                Integer notEmpty = FindNotEmpty(bytesToRead);
                if(notEmpty != -1){
                    MinHeapNode node = new MinHeapNode(fileManagers.get(notEmpty).NextRecord(), notEmpty);
                    heap.insert(node);
                    bytesToRead.set(notEmpty, bytesToRead.get(notEmpty) - sizeOfRecordInFile);
                }
                //all empty
            }
            heap.minHeap();
        }
    }
    /**/
    public Integer sortRecordsIntoTapes() throws IOException {
        ArrayList<String> records = new ArrayList<>();
        Integer fileNum = 0; //number of file we will write to
        Integer finish = 0; // we read whole file
        Integer continueSorting = 1; // we didn't reed whole file
        Integer bytesToRead = fileManager.ReadBlock(); //the size of block in bytes
        //as long as we can read full block and we have buffers to store the data
        while(bytesToRead == blockSize && fileNum < numOfSortingFiles) {
            records.clear();
            //FileWriter writer = new FileWriter("src/main/java/Sorting/sortingFiles/" + fileNum + ".txt");

            //store all records in an array, sort it
            for (int i = 0; i < numOfRecordsInBlock; i++)
                records.add(fileManager.NextRecord());
            Collections.sort(records);

            String block = new String();
            FileManager tapeFM = new FileManager("src/main/java/Sorting/sortingFiles/" + fileNum + ".txt", 0, blockSize);

            //write all records to destined file
            for(int j = 0; j < records.size(); j++) {
                String record =  records.get(j);

                Integer shortRecord = 9;
                if(record.length() == shortRecord)
                    block += record + "  ";
                else
                    block +=record + " ";
            }

            tapeFM.writeToFile(block);
            fileNum++; // next file
            bytesToRead = fileManager.ReadBlock(); // reading next block and memorising its length in bytes
        }

        //if the block is smaller than max size but npt empty and we bave free buffer for dara
        if(bytesToRead != 0 && fileNum < numOfSortingFiles){
            records.clear();

            //read every record and store it in destined file
            for (int i = 0; i < bytesToRead/sizeOfRecordInFile; i++)
                records.add(fileManager.NextRecord());
            Collections.sort(records);


            String block = new String();
            FileManager tapeFM = new FileManager("src/main/java/Sorting/sortingFiles/" + fileNum + ".txt", 0, blockSize);
            for(int j = 0; j < records.size(); j++){
                String record =  records.get(j);
                Integer shortRecord = 9;
                if(record.length() == shortRecord)
                    block += record + "  ";
                else
                    block += record + " ";

            }
            tapeFM.writeToFile(block);
            numOfUsedBuffers = fileNum + 1; //we used fileNum + 1 files
            return finish; //we read whole file
        }
        //if there still are bytes to read but we used all buffers
        else if(bytesToRead != 0 && fileNum == numOfSortingFiles){
            numOfUsedBuffers = fileNum; //we used fileNum files
            return continueSorting; //we didn't read whole file
        }
        numOfUsedBuffers = fileNum; //we used fileNum files
        return finish; //we read whole file
    }
}
