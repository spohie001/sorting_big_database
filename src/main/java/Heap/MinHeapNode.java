package Heap;

public class MinHeapNode {
    String value;
    Integer fileNum;

    public MinHeapNode(String value, Integer fileNum){
        this.value = value;
        this.fileNum = fileNum;
    }

    public Integer getFileNum() {
        return fileNum;
    }

    public String getValue() {
        return value;
    }
}
