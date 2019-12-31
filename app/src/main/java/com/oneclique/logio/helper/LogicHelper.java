package com.oneclique.logio.helper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LogicHelper {
    /**
     * This method is used to randomize the number without duplicates
     * @param from lower bound of number to be randomize (ex. 91)
     * @param to higher bound of number to be randomize (ex. 100)
     * @return list of randomized number from the given range
     */
    public static List<Integer> randomNumbers(int from, int to){

        List<Integer> tmpHolder = new ArrayList<>(to-from);
        List<Integer> numbers = new ArrayList<>(to-from);
        Random rand = new Random();

        for(int i = from; i <= to; i++) {
            tmpHolder.add(i);
        }

        while(tmpHolder.size() > 0) {
            int index = rand.nextInt(tmpHolder.size());
            numbers.add(tmpHolder.remove(index));
        }

        return numbers;
    }

    /**
     * This method is used to re-build one line string choices to List of choices
     * @param choices (ex. "C21<br/>C22<br/>C23<br/>C24<br/>...Cnn<br></br>")
     * @return List of choices
     */
    public static List<String> choiceReBuilder(String choices){
        return Arrays.asList(choices.split("<br/>"));
    }

    /**
     * This method is used to build List of choices to one line string of choices.
     * @param listChoices List of choices
     * @return one line string of choices
     */
    public static String choiceBuilder(List<String> listChoices){
        StringBuilder choices = new StringBuilder();
        for (String choice :
                listChoices) {
            choices.append(choice).append("<br/>");
        }
        return choices.toString();
    }
}
