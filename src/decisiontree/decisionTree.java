/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package decisiontree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author ankur
 */
public class decisionTree {
    class Feature {
        public String name;
        public HashMap<String,Integer> valueMap;
        public String encoding[];
        int count=0;
        public Feature(String name){
            this.name = name;
            this.encoding = new String[2];
            this.valueMap = new HashMap<>();
        }
    
    }
    
    class TreeNode{
        public String featureName;
        public int featureIndex;
        public TreeNode child[];
        public boolean endLeaf;
        public String label;
        public double entropy = 0;
        public TreeNode(String label){
            child = new TreeNode[2];
            child[0] = null; 
            child[1] = null;
            this.label = label;
            this.endLeaf = false;
        }
        
        public TreeNode(String label, boolean endLeaf){
            child[0] = null;
            child[1] = null;
            this.label = label;
            this.endLeaf = false;
        }
        public void setEntropy(int positives, int negatives){
            this.entropy = -1.0 * ( ((negatives/(negatives+positives)) * Math.log(negatives/(negatives+positives))) + ( (positives/(negatives+positives)) * Math.log(positives/(negatives+positives)))) / Math.log(2);
        }
    } 

    
    public Feature feature[];
    public List<String> binaryRep = new ArrayList<>();
    public String classEncoding[] = new String[2];
    public HashMap<String,Integer> classMap = new HashMap<>();
    int classCount = 0;
    public TreeNode treeRoot;
    String regex = "";
    int totalCount = 0;
    public List<Integer> Split = new ArrayList<>();
    HashSet<Integer> hset = new HashSet<>();
    public decisionTree(String input, int maxDepth){
        readInput(input);
        treeRoot = buildTree(maxDepth+1,0,regex,hset);
    }
    
    public TreeNode buildTree(int maxDepth, int currentLevel, String cRegex, HashSet<Integer> hset){
        if(currentLevel > maxDepth || currentLevel > feature.length){
            return null;
        }
        int positives=0;
        int negatives=0;
        String pattern1 = cRegex.substring(0, cRegex.length()-1) + "0";
        String pattern2 = cRegex.substring(0, cRegex.length()-1) + "1";
        for(String s: binaryRep){
            if(Pattern.matches(pattern1,s))
                positives++;
            if(Pattern.matches(pattern2,s))
                negatives++;
        }
        TreeNode root = new TreeNode(positives>negatives?classEncoding[0]:classEncoding[1]);
        //System.out.print(" " + root.label + ": ");
        double rootEntropy = setEntropy(positives,negatives);
        
        
        int SplitIndex = -1;
        double MaxGain = 0.0;
        for(int i=0;i<feature.length;i++){
            if(hset.contains(i)){
                double currentGain = 0.0D;
                currentGain = rootEntropy - calculateGain(i,cRegex);
                //System.out.println(i + "------" + currentGain);
                if(currentGain > MaxGain){
                    MaxGain = currentGain;
                    SplitIndex = i;
                }
            }
        }
        
        System.out.println("[" + positives + " " + classEncoding[0] + "/" + negatives + " " + classEncoding[1] + "]");
        
        if(SplitIndex!=-1 && (currentLevel + 1) != maxDepth && (currentLevel) != feature.length){
            root.featureName = feature[SplitIndex].name;
            root.featureIndex = SplitIndex;
            hset.remove(SplitIndex);
            for(int i=0;i<currentLevel+1;i++)
                System.out.print("|");
            System.out.print(root.featureName + " = " + feature[SplitIndex].encoding[0]);
            root.child[0] = buildTree(maxDepth,currentLevel+1, cRegex.substring(0,SplitIndex) + "0" + cRegex.substring(SplitIndex+1,cRegex.length()),hset);
            for(int i=0;i<currentLevel+1;i++)
                System.out.print("|");
            System.out.print(root.featureName + " = " + feature[SplitIndex].encoding[1]);
            root.child[1] = buildTree(maxDepth,currentLevel+1, cRegex.substring(0,SplitIndex) + "1" + cRegex.substring(SplitIndex+1,cRegex.length()),hset);
            hset.add(SplitIndex);
        } else{
            root.endLeaf = true;
    }
        return root;
    }
    
    public double calculateGain(int featureIndex, String cRegex){
        int positives1 = 0;
        int negatives1 = 0;
        int positives2 = 0;
        int negatives2 = 0;
        String pattern3 = cRegex.substring(0,featureIndex)+ "0" + cRegex.substring(featureIndex+1,cRegex.length()-1) + "0";
        String pattern4 = cRegex.substring(0,featureIndex)+ "0" + cRegex.substring(featureIndex+1,cRegex.length()-1) + "1";
        String pattern5 = cRegex.substring(0,featureIndex)+ "1" + cRegex.substring(featureIndex+1,cRegex.length()-1) + "0";
        String pattern6 = cRegex.substring(0,featureIndex)+ "1" + cRegex.substring(featureIndex+1,cRegex.length()-1) + "1";
        for(String s: binaryRep){
            if(Pattern.matches(pattern3,s))
                positives1++;
            if(Pattern.matches(pattern4,s))
                negatives1++;
            if(Pattern.matches(pattern5,s))
                positives2++;
            if(Pattern.matches(pattern6,s))
                negatives2++;
        }
        double fraction1 =  ((double)(negatives1+positives1))/(negatives1+positives1+negatives2+positives2);
        double fraction2 =  ((double)(negatives2+positives2))/(negatives1+positives1+negatives2+positives2);
        return ((double)fraction1 * setEntropy(positives1,negatives1))  + ((double)fraction2 * setEntropy(positives2,negatives2));
    }
    
    public double setEntropy(int positives, int negatives){
        double product1 = 0.0D;
        double product2 = 0.0D;
        if(negatives!=0)
                product1 = (((double)negatives/(negatives+positives)) * Math.log((double)negatives/(negatives+positives)));
        if(positives!=0)
                product2 = (((double)positives/(negatives+positives)) * Math.log((double)positives/(negatives+positives)));
            return -1.0D * ( product1 + product2) / Math.log(2);
        }
    
    public void readInput(String input){
        BufferedReader br  = null;
            int flag = 0;
        try {
            br = new BufferedReader(new FileReader(input));
            String eachLine = "";
            while((eachLine = br.readLine())!=null){
                String[] entry = eachLine.split(",");
                //initalize all the features
                if(flag==0){
                    flag=1;
                    feature = new Feature[entry.length-1];
                    for(int i=0;i<entry.length-1;i++){
                        feature[i] = new Feature(entry[i]);
                    }
                    for(int i=0;i<entry.length;i++){
                        regex = regex + ".";
                    }
                }else{
                    for(int i=0;i<entry.length-1;i++){
                        if(!feature[i].valueMap.containsKey(entry[i])){
                            feature[i].valueMap.put(entry[i],(feature[i].count));
                            feature[i].encoding[feature[i].count] = entry[i];
                            feature[i].count++;
                        }
                    }
                    if(!classMap.containsKey(entry[entry.length-1])){
                        classMap.put(entry[entry.length-1],classCount);
                        classEncoding[classCount] = entry[entry.length-1];
                        classCount++;
                    }
                    totalCount++;
                }
            }
            /*for(Feature f: feature){
                int i = 0;
                for(String key: f.valueMap.keySet()){
                    f.encoding[i++] = key;
                }
            }*/
            br.close();
            br = new BufferedReader(new FileReader(input));
            eachLine = br.readLine();
            while((eachLine = br.readLine())!=null){
                String[] entry = eachLine.split(",");
                StringBuilder sbuild = new StringBuilder();
                //initalize all the features
                for(int i=0;i<entry.length-1;i++){
                        sbuild.append(feature[i].valueMap.get(entry[i]));
                        totalCount++;
                    }
                sbuild.append(classMap.get(entry[entry.length-1]));
                binaryRep.add(sbuild.toString());
                }
            br.close();
            for(int i=0;i<feature.length;i++)
                hset.add(i);
            } catch (FileNotFoundException ex) {
            Logger.getLogger(decisionTree.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(decisionTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(binaryRep);
    }
    
    public double calculateTrainError(String input, String output){
        BufferedReader br  = null;
        int totalCount = 0;
        int negatives = 0;
        try {
            br = new BufferedReader(new FileReader(input));
            String eachLine = br.readLine();
            PrintWriter out = new PrintWriter(new FileWriter(output));
            while((eachLine = br.readLine())!=null){
                TreeNode root = treeRoot;
                TreeNode temp = treeRoot;
                String[] entry = eachLine.split(",");
                while(root!=null){
                    temp = root;
                    root = root.child[feature[root.featureIndex].valueMap.get(entry[root.featureIndex])];
                } 
                out.println(temp.label); 
                
                //System.out.println(temp.label);
                if(!entry[entry.length-1].equals(temp.label))
                    negatives++;
                totalCount++;
                
            }
            out.flush();
            out.close();
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(decisionTree.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(decisionTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (double)negatives/totalCount;
    }
    
    public static void main(String args[]){
        decisionTree dt = new decisionTree(args[0],Integer.parseInt(args[2]));
        //calculateEntropy 
        
        double trainError = dt.calculateTrainError(args[0],args[3]);
        double testError = dt.calculateTrainError(args[1],args[4]);
        try {
            PrintWriter out = new PrintWriter(new FileWriter(args[5]));
            out.println("error(train): " +  trainError);
            out.println("error(test): " + testError);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(decisionTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
