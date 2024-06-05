import FileManagement.FileManager;
import FileManagement.FilePrinter;
import Prep.Preparation;
import Sorting.Sorter;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        FileOutputStream clear = new FileOutputStream("src/main/java/Sorting/output.txt"); //quick cleaning output file

        String beginningsPath = "src/main/java/Prep/begin.txt";
        String recordsPath = "src/main/java/Prep/records.txt";
        Integer maxIndex = 244; //number of possible beginnings

        Integer buffers = 3;
        Integer numOfRecordsInBuffer = 2;
        Integer numOfRecordsOnTape = buffers*numOfRecordsInBuffer;
        Integer sizeOfRecordInFile = 11;
        Integer blockSize = numOfRecordsOnTape * sizeOfRecordInFile;
        Integer numOfRecordsInFile = 12;
        Preparation prep = new Preparation(maxIndex, recordsPath, beginningsPath, numOfRecordsInFile);

        Integer fileSize = prep.CreateRecords();
        Integer numOfBuffers = (int) Math.ceil(fileSize / blockSize);
        prep.CreateSortingFiles(numOfBuffers);

        FileManager fm = new FileManager(recordsPath, fileSize, blockSize);
        Sorter s = new Sorter(numOfBuffers, numOfRecordsOnTape,blockSize, fileSize, sizeOfRecordInFile, fm);

        FilePrinter printer = new FilePrinter();


        System.out.println("przed sortowaniem:");
        printer.print(recordsPath, fileSize, blockSize);
        //fm.print();
        s.sortRecordsIntoTapes();
        s.merge(buffers, numOfRecordsInBuffer*sizeOfRecordInFile,
                numOfRecordsInBuffer, numOfBuffers);
        FileManager out = new FileManager("src/main/java/Sorting/sortingFiles/2.txt", fileSize, blockSize);
        System.out.println("\n\npo sortowaniu:");
        printer.print("src/main/java/Sorting/sortingFiles/2.txt", fileSize, blockSize);
        //out.print();

    }
}
