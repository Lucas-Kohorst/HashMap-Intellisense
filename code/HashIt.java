import java.util.*;
import java.io.*;

/**
 * Loads in a txt files of words
 * and returns the hashmap of the letters
 * @author Lucas Kohorst
 */
public class HashIt {
   private HashMap<String, HashMap> words = null;
   private File file;

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

      map2.forEach((key, value) -> map1.merge(key, value,
            (map1Value, map2Value) -> map1Value == map2Value ? map1Value : mergeMaps(map1Value, map2Value)));
      return map1;
   } // end of mergeMaps

   /**
    * Generate a nested HashMap of the letters in a word
    * 
    * @param word, the word to add to the HashMap
    * @return the generated HashMap
    */
   public HashMap<String, HashMap> stackMap(String word) {
      Stack<String> st = new Stack<String>();
      HashMap<String, HashMap> map = new HashMap<>();
      HashMap<String, HashMap> tempMap = new HashMap<String, HashMap>();

      char[] letters = word.toCharArray();
      for (char letter : letters) {
         st.push(String.valueOf(letter));
      }
      tempMap.put(st.pop(), null);

      int size = st.size();

      // If the word is a single letter
      if (size <= 0) {
         return tempMap;
      } else {
         for (int i = 0; i < size - 1; i++) {
            HashMap<String, HashMap> localTempMap = new HashMap<String, HashMap>();
            localTempMap.putAll(tempMap);
            tempMap.clear();
            tempMap.put(st.pop(), localTempMap);
         }
         map.put(st.pop(), tempMap);
         return map;
      }
   } // end of stackMap

}