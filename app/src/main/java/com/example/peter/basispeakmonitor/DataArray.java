package com.example.peter.basispeakmonitor;


/**
 * Created by Peter on 3/22/2016.
 * Edited by Ashley on 3/22/2016 !!!! TESTING ONE TWO THREE PLZ
 */
public class DataArray {
    private float[] rawData;
    private float[] truncatedData;
    private float[] sparseData;
    private String[] labels;
    //Hi ashley
    //Hello Peter

    public DataArray(String[] input){
        rawData = new float[input.length];
        for(int i = 0; i <input.length; i++){
            if(input[i].equals("null")) {
                rawData[i] = (float) -1;
            } else {
                rawData[i] = Float.parseFloat(input[i]);
            }
        }
        createTruncData();
    }
    private void createTruncData(){
        int nullAtEnd=0;
        for(int i = rawData.length-1; i>=0; i--){
            if(rawData[i]==-1) nullAtEnd++;
        }
        truncatedData = new float[rawData.length-nullAtEnd];
        for(int i = 0; i<truncatedData.length; i++){
            truncatedData[i] = rawData[i];
        }
    }
    public void createSparseData(int sparsity){
        sparseData = new float[truncatedData.length/sparsity+1];
        int labelSparsity = 75;
        labels = new String[truncatedData.length/sparsity+1];
        for(int i = 0; i<sparseData.length; i++){
            sparseData[i] = truncatedData[i*sparsity];
            if(i%3==0){
                labels[i]= ""+i/3;
            } else {
                labels[i] = "";
            }

        }
    }
    public float[] getRawData(){
        if(rawData != null){
            return rawData;
        }
        return null;
    }

    public float[] getTruncData(){
        if(truncatedData != null){
            return truncatedData;
        }
        return null;
    }
    public float[] getSparseData(){
        return sparseData;
    }
    public String[] getLabels(){
        return labels;
    }
    public float getLastValue(){
        if(truncatedData.length>0){
            return truncatedData[truncatedData.length-1];
        }
        return -1;
    }

}
