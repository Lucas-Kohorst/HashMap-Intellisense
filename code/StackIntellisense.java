import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Given input intellisense suggestions will be generated. Words are read in
 * from unabridged dictionary text file. And stored in a HashMap HW 3 Base code
 * demo created by Prof. Floeser 3/30/18 Used in ISTE-222 for Hash Tables
 * @author Prof. Floeser, starter file
 * @author Lucas Kohorst
 */
public class StackIntellisense extends JFrame implements Serializable {
   private JTextField jtfInput;
   private JButton jbExit;
   private JLabel jlStatus;

   private final String INPUTFILE = "texts/UnabridgedDictionary.txt";
   private HashMap<String, HashMap> hashWords = null;
   // Creating the words HashMap
   HashMap<String, HashMap> words = null;

   public static void main(String[] args) {
      new StackIntellisense();
   } // end main

   /**
    * Create the GUI Assign the text field to a listener.
    */
   public StackIntellisense() {
      setTitle("Key listener/Hash tables starter code");

      JPanel jpMain = new JPanel(new GridLayout(0, 1));
      JPanel jpEntry = new JPanel();
      jpEntry.add(new JLabel("Enter word: ", JLabel.CENTER));
      jtfInput = new JTextField(20);
      jpEntry.add(jtfInput);

      jtfInput.addKeyListener(new KeyActions()); // <<<< register key events enabled

      jpMain.add(jpEntry);

      jlStatus = new JLabel("Status goes here", JLabel.CENTER);
      jpMain.add(jlStatus);

      add(jpMain, BorderLayout.CENTER);

      JPanel jpButtons = new JPanel();
      jbExit = new JButton("Exit");
      jbExit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            System.exit(0);
         }
      });

      jpButtons.add(jbExit);
      add(jpButtons, BorderLayout.SOUTH);

      pack();
      setLocationRelativeTo(null);

      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);

      // Since the HashMap takes such a long time
      // to generate from the UnabridgedDictionary
      // try to read it in from a stored object file
      // useful for running this program multiple times
      try {
			FileInputStream fis = new FileInputStream("words.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			words = (HashMap<String, HashMap>) ois.readObject();
         ois.close();
         System.out.println("Words are loaded");
      } 
      // If object was not stored create it
      // this will take some time
      catch (Exception e) {
         System.out.println("Word Object could not be found");
         hashWords = loadFile(new File(INPUTFILE));
         System.out.printf("Words loaded: %,d %n", hashWords.size());
         // Storing as an object file for later use
         try {
            FileOutputStream fos = new FileOutputStream("words.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hashWords);
            oos.close();
         } catch (IOException ioe) {
            System.err.println("Could Not write object to file: " + ioe);
         }
      }
   } // end KeyDetection constructor

   /**
    * Generate a nested HashMap of the letters in a word
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
    * Load a HashMap with a character key, and as a value, another HashMap of data
    * The HashMap could be defined as <Character, HashMap> but working with String
    * is so much easier.
    */
   private HashMap<String, HashMap> loadFile(File theFile) {
      // Reading in the file
      try (BufferedReader br = new BufferedReader(new FileReader(theFile))) {
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
    * Handler for pressing a key. This was added as an inner class to share
    * variables, but doesn't have to be inner. NOTE: Most of this code is for
    * debugging and demo. You will probably remove most of it.
    */
   class KeyActions extends KeyAdapter // KeyListener needs all 3 methods or extends KeyAdapter
   {
      // Alternative methods you could use. Each works slightly different.
      // KeyPressed is processed first, so we will override that method.
      // public void keyTyped( KeyEvent e )
      // public void keyReleased( KeyEvent e )
      public void keyPressed(KeyEvent e) {
         // Wondering what ActionKey would detect, CAPS-LOCK, maybe others
         // System.out.println("ActionKey = " + e.isActionKey() );
         // Get a number
         int code = e.getKeyCode();

         if (code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z) {
            char input = (char) code;
            System.out.println("Valid character: " + input); // Print as a character
            // Getting Intellisense suggestions
            // checkInput(String.valueOf(input), hashWords);
         } else
            System.out.println("not a letter " + code); // Print as a number
         // Google for "Ascii table" to see what these numbers (code) map to the keys you
         // pressed

         // jlStatus.setText("Maybe a word: " + new Date(e.getWhen()));
         // long whenPressed = e.getWhen(); //
         // System.out.println("KeyInfo: "+ e );
         // System.out.println("Time: " + whenPressed + " = " + new Date( whenPressed )
         // );
      }

   } // end KeyActions inner class

} // end KeyDetection outer class