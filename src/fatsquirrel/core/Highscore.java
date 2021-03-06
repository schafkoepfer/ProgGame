package fatsquirrel.core;

import fatsquirrel.core.Entities.Entity;

import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.HashMap;

public class Highscore {

    private static Map<String, ArrayList<Integer>> scores = new HashMap();
    private static ArrayList<Entity> botList = new ArrayList<>();

    public Highscore(){
        for(String botName : BoardConfig.getBotNames())
            scores.put(botName, new ArrayList());
    }

    public Highscore calculateHighscore(ArrayList<Entity> entityList, int round) {
        botList = entityList;
        String[] botNames = BoardConfig.getBotNames();
        ArrayList<Integer> tempList;
        for(int i = 0; i<entityList.size(); i++){
            tempList = (scores.get(botNames[i]));
            tempList.add(entityList.get(i).getEnergy());
            scores.put(botNames[i],tempList);
        }
        return this;
    }

    public void saveHighscores(){
        try{
            File fileOne=new File("./src/fatsquirrel/Output/highscores.map");
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);

            oos.writeObject(this.getMap());
            oos.flush();
            oos.close();
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loadHighscores(){
        try{
            File toRead=new File("./src/fatsquirrel/Output/fixhighscores.map");
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            HashMap<String,ArrayList<Integer>> mapInFile=(HashMap<String,ArrayList<Integer>>)ois.readObject();

            ois.close();
            fis.close();

            for(Map.Entry<String,ArrayList<Integer>> m :mapInFile.entrySet()){
                scores.put(m.getKey(),m.getValue());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Map getMap(){
        return scores;
    }

    public String toString(){

        String[] botNames = BoardConfig.getBotNames();
        ArrayList<Integer> tempList;
        for(int i = 0; i<botList.size(); i++){
            tempList = (scores.get(botNames[i]));
            Collections.sort(tempList);
            scores.put(botNames[i],tempList);
        }

        return "Highscores: " + scores.toString();
    }
}
