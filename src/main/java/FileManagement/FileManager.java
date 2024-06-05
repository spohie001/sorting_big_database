package FileManagement;

import java.io.*;

public class FileManager {
    Integer blockSize;
    Integer offsetRead = 0;
    Integer offsetWrite = 0;
    Integer nextToRetRecordIdx = 0;
    String filePath;
    Integer fileSize;
    FileInputStream file;
    InputStreamReader streamReader;
    RandomAccessFile randomAccessFile;
    char[] inBuffer;

    public FileManager( String filePath, Integer  fileSize, Integer blockSize ) throws FileNotFoundException {
        this.filePath = filePath;
        this.blockSize = blockSize;
        this.fileSize = fileSize;
        this.file = new FileInputStream(filePath);
        this.streamReader = new InputStreamReader(file);
        this.randomAccessFile = new RandomAccessFile(filePath, "rw");
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public Integer ReadBlock() throws IOException {
        if(fileSize >= blockSize) {
            randomAccessFile.seek(offsetRead);
            inBuffer = new char[blockSize];
            streamReader.read(inBuffer, 0, blockSize);
            offsetRead += blockSize;
            fileSize -= blockSize;
            return blockSize;
        }
        else{
            randomAccessFile.seek(offsetRead);
            inBuffer = new char[fileSize];
            streamReader.read(inBuffer, 0, fileSize);
            return fileSize;
        }
    }

    public String NextRecord(){
        String record = new String(); //

        if(nextToRetRecordIdx >= inBuffer.length - 1)
            nextToRetRecordIdx = 0;

        char token = inBuffer[nextToRetRecordIdx];

        while(token != ' ' && nextToRetRecordIdx != inBuffer.length - 1){
            record += token;
            nextToRetRecordIdx++;
            token = inBuffer[nextToRetRecordIdx];
        }
        while(token == ' '&& nextToRetRecordIdx != inBuffer.length - 1) {
            nextToRetRecordIdx++;
            token = inBuffer[nextToRetRecordIdx];
        }

        return record;
    }

    public void writeToFile( String buffer){
        try {
            OutputStreamWriter streamWriter = new OutputStreamWriter(new FileOutputStream(filePath, true));
            streamWriter.write(buffer);
            offsetWrite += buffer.length();
            fileSize += buffer.length();
            streamWriter.close();
        } catch (IOException e) {
            System.out.println("An error has occurred.");
            e.printStackTrace();
        }
    }
}
