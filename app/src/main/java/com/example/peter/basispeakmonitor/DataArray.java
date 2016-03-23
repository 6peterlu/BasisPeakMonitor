package com.example.peter.basispeakmonitor;

import java.util.ArrayList;

/**
 * Created by Peter on 3/22/2016.
 * Edited by Ashley on 3/22/2016 !!!! TESTING ONE TWO THREE PLZ
 */
public class DataArray {
    private ArrayList<Double> rawData;
    private ArrayList<Double> truncatedData;
    //Hi ashley
    //Hello Peter

    public DataArray(String[] input){
        rawData = new ArrayList<>();
        truncatedData = new ArrayList<>();

        for(int i = 0; i < input.length; i++){
            if(input[i].equals("null")) rawData.add((double) -1);
            else{
                rawData.add(Double.parseDouble(input[i]));
                truncatedData.add(Double.parseDouble(input[i]));
            }
        }
    }

    public ArrayList<Double> getRawData(){
        if(rawData != null){
            return rawData;
        }
        return null;
    }

    public ArrayList<Double> getTruncData(){
        if(truncatedData != null){
            return truncatedData;
        }
        return null;
    }

    public double getLastValue(){
        if(!truncatedData.isEmpty()){
            return truncatedData.get(truncatedData.size() -1);
        }
        return -1;
    }

    public double average(ArrayList<Double> input){
        if(!input.isEmpty()){
          double sum = 0;
          for (int i = 0; i < input.size(); i++){
              sum += input.get(i);
          }
          return sum/input.size();
        }
        return -1;
    }
}
