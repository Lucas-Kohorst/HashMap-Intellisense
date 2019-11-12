import java.util.*;
import java.io.*;

/**
 * Loads in a txt files of words and returns the hashmap of the letters
 * 1ISTE-222 Computational Problem Solving in the Information Domain
 * III Hashmap–Homework–Programming
 * @author Lucas Kohorst
 */
public class HashIt {
   private HashMap<String, HashMap> words = null;
   private File file;

   /**
    * Constructor that takes in a string 
    * coverts it to a file
    * @param file
    */
   public HashIt(String file) {
      this.file = new File(file);
   }

   /**
    * Load a HashMap with a character key, and as a value, another HashMap of data
    * The HashMap could be defined as <Character, HashMap> but working with String
    * is so much easier.
    */
   public HashMap<String, HashMap> loadFile() {
      // Reading in the file
      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
         String word;
         words = new HashMap<String, HashMap>();
         // Reading all the lines of the file
         while ((word = br.readLine()) != null) {
            // Gets a HashMap of the current word
            // and then merges the current hashmap
            // with the new one
            mergeMaps(words, stackMap(word));
         }
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }
      return words;
   } // end of loadFile

   /**
    * Merge two HashMaps with duplicate keys
    * @see https://howtodoinjava.com/java/collections/hashmap/merge-two-hashmaps/
    * @param map1, HashMap to merge into
    * @param map2, HashMap to merge from
    * @return merged HashMap
    */
   public HashMap<String, HashMap> mergeMaps(HashMap<String, HashMap> map1, HashMap<String, HashMap> map2) {
      // Checking if a HashMap contains only the key
      // Only need to check map2 because map1 is all
      // of the words and will never have a null key
      // e.g. {e=null}
      Object[] nullArray = map2.values().toArray();
      // If there are no values in the HashMap
      // than it is null
      if (nullArray[0] == null) {
         // Getting the value of the only key
         ArrayList<String> keys = new ArrayList(map2.keySet());
         String key = keys.get(0);
         map1.put(key, null);
         return map1;
      }

      // Iterates through each element in each map
      // compares map1 and map2 if they are not equal
      // runs this function again
      map2.forEach((key, value) -> map1.merge(key, value,
            (map1Value, map2Value) -> map1Value == map2Value ? map1Value : mergeMaps(map1Value, map2Value)));
      return map1;
   } // end of mergeMaps

   /**
    * Generate a nested HashMap of the letters in a word
    * @param word, the word to add to the HashMap
    * @return the generated HashMap
    */
   public HashMap<String, HashMap> stackMap(String word) {
      Stack<String> st = new Stack<String>();
      HashMap<String, HashMap> map = new HashMap<>();
      HashMap<String, HashMap> tempMap = new HashMap<String, HashMap>();

      // Adding the current word to an array
      char[] letters = word.toCharArray();
      for (char letter : letters) {
         // Adding all letters onto a stack
         st.push(String.valueOf(letter));
      }
      // Putting the last letter into the temp map
      // tempMap = {lastLetter=null}
      tempMap.put(st.pop(), null);
      
      int size = st.size();

      // If the word is a single letter
      if (size <= 0) {
         return tempMap;
      } else {
         // Iterate over the size of the stack
         for (int i = 0; i < size - 1; i++) {
            HashMap<String, HashMap> localTempMap = new HashMap<String, HashMap>();
            // Add everything from the temp map to a local one
            localTempMap.putAll(tempMap);
            // Clear the temp map so the next letter's 
            // hashmap can take its place
            tempMap.clear();
            // Add the previous tempmap as the next value
            tempMap.put(st.pop(), localTempMap);
         }
         // Put the tempmap as the map with the last letter
         map.put(st.pop(), tempMap);
         return map;
      }
   } // end of stackMap

   /**
    * Searches the map with the given input 
    * to return the next possible letters
    * @param map, hashmap to search
    * @param words, letters to search by
    * @return next possible letters
    */
   public ArrayList<String> searchMap(HashMap<String, HashMap> map, ArrayList<String> words) {
      ArrayList<String> possibleWords = new ArrayList<String>();
      try {
         HashMap<String, HashMap> baseMap = getNestedMap(map, words);
         Set<String> keySet = baseMap.keySet();
         for (String key : keySet) {
            possibleWords.add(key);
         }
      } catch (Exception e) { }
      return possibleWords;
   } // end of searchMap

   /**
    * Helper function for searchMap
    * gets the hashmap that is on level
    * with the input
    * @param map, the map to get next map of
    * @param words, the letters to get map by
    * @return, a map level with the inputted letters
    */
   public HashMap<String, HashMap> getNestedMap(HashMap<String, HashMap> map, ArrayList<String> words) {
      if (words.size() == 0) {
         return map;
      }
      HashMap<String, HashMap> nestedMap = map.get(words.get(0).toLowerCase());
      words.remove(0);
      return getNestedMap(nestedMap, words);
   } // end of getNestedMap

} // end of HashIt