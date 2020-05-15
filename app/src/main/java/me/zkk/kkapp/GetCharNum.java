package me.zkk.kkapp;
import java.util.HashMap;

import me.zkk.kkapp.ExampleService2;

public class GetCharNum implements ExampleService2{
    @Override
    public HashMap<Character, Integer> getCharNum(String str) {
        char[] charArr = str.toCharArray();
        HashMap<Character, Integer> charHash = new HashMap<>();
        for (int i = 0; i < charArr.length; ++i) {
            if(charHash.containsKey(charArr[i])) {
                charHash.put(charArr[i], charHash.get(charArr[i]) + 1);
            } else {
                charHash.put(charArr[i], 1);
            }
        }
        return charHash;
    }
}
