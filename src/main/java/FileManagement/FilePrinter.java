package FileManagement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class FilePrinter {

    public FilePrinter(){    }

    public void print(String path, Integer fileSize, Integer blockSize) throws IOException {
        Integer offsetRead = 0;
        FileInputStream file = new FileInputStream(path);
        InputStreamReader streamReader = new InputStreamReader(file);
        RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
        char[] inBuffer;

        while(fileSize >= blockSize) {
            randomAccessFile.seek(offsetRead);
            inBuffer = new char[blockSize];
            streamReader.read(inBuffer, 0, blockSize);
            offsetRead += blockSize;
            fileSize -= blockSize;
            System.out.print(inBuffer);
        }
            randomAccessFile.seek(offsetRead);
            inBuffer = new char[fileSize];
            streamReader.read(inBuffer, 0, fileSize);
            System.out.print(inBuffer);
            randomAccessFile.close();
        }

}
