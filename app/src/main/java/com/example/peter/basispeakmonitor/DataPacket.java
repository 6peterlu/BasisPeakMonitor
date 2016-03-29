package com.example.peter.basispeakmonitor;

/**
 * Created by Peter on 3/29/2016.
 */
public class DataPacket {

    private DataArray heartrate;
    private DataArray calories;
    private DataArray steps;
    private DataArray skin_temp;
    private DataArray gsr;
    private String[] displayedData;

    public DataPacket(String raw){
        updatePacket(raw);
    }

    public void updatePacket(String raw){
        heartrate = new DataArray(getData("heartrate", raw));
        calories = new DataArray(getData("calories", raw));
        steps = new DataArray(getData("steps", raw));
        skin_temp = new DataArray(getData("skin_temp", raw));
        gsr = new DataArray(getData("gsr", raw));

        displayedData[0] = "last recorded heartrate: "+heartrate.getLastValue();
        displayedData[1] = "last recorded calories: "+calories.getLastValue();
        displayedData[2] = "last recorded steps: "+steps.getLastValue();
        displayedData[3] = "last recorded skin temp: "+skin_temp.getLastValue();
        displayedData[4] = "last recorded gsr: "+gsr.getLastValue();
        displayedData[5] = "alerts: none";
    }

    private static String[] getData(String metric, String raw){
        int index = raw.indexOf(metric);
        if(index==-1){
            System.out.println("Invalid metric.");
            return null;
        } else {
            int startBracket = raw.indexOf("[", index);
            int endBracket = raw.indexOf("]", index);
            String numbers = raw.substring(startBracket+1,endBracket);
            String[] parsed = numbers.split(",");
            if(metric.equals("gsr")){
                for(int i = 0; i<parsed.length; i++){
                    if(!parsed[i].equals("null")) {
                        parsed[i] = convertFromScientific(parsed[i]);
                    }
                }
            }
            return parsed;
        }
    }
    //format: "5.07e-05" -> "0.0000507"
    private static String convertFromScientific(String input){
        String output = "0.";
        int eLoc = input.indexOf('e');
        int powerTen = Integer.parseInt(input.substring(eLoc+1));
        for(int i = 1; i<-powerTen; i++){
            output = output+"0";
        }
        for(int i = 0; i<eLoc; i++){
            if(input.charAt(i)=='.') continue;
            output = output+input.charAt(i);
        }
        return output;
    }

    //Getters
    public DataArray getHeartrate(){
        return heartrate;
    }
    public DataArray getCalories(){
        return calories;
    }
    public DataArray getSteps(){
        return steps;
    }
    public DataArray getSkinTemp(){
        return skin_temp;
    }
    public DataArray getGsr(){
        return gsr;
    }
    public String[] getDisplayedData(){
        return displayedData;
    }
}
