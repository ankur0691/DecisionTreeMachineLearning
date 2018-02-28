/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ankur
 */
public class inspect {
    
    public static double calculateEntropy(HashMap<String,Integer> labelmap,int count){
        double entropy = 0.0D;
        for(Entry<String,Integer> label : labelmap.entrySet()){
                entropy +=  (-1.0D * (((((double)label.getValue())/count) * Math.log((((double)label.getValue())/count)))) / Math.log(2));                
        }
        return entropy;
    }
    
    public static double calculateError(HashMap<String,Integer> labelmap, int count){
        int maximum = 0;
        for(Entry<String,Integer> label : labelmap.entrySet()){
             maximum = Math.max(maximum,label.getValue());
        }
        return (double)(count-maximum)/count;
    }
    
    public static void main(String args[]) {
        BufferedReader br = null;
        HashMap<String,Integer> labelmap = new HashMap<>();
        int flag = 0;
        int count = 0;
        String [] features;
        try {
            br = new BufferedReader(new FileReader(args[0]));
            String eachLine = "";
            while((eachLine = br.readLine())!=null){
                String[] entry = eachLine.split(",");
                if(flag==0){
                    flag=1;
                    features = entry;
                }else{
                    labelmap.put(entry[entry.length - 1],labelmap.getOrDefault(entry[entry.length-1],0)+1);
                    count++;
                }
            }
            PrintWriter out = new PrintWriter(new FileWriter(args[1]));
            out.println("entropy: " + calculateEntropy(labelmap,count));
            out.println("error: " + calculateError(labelmap,count));
            out.flush();
            out.close();
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(inspect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(inspect.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
