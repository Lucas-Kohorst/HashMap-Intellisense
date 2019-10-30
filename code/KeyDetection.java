import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Given input intellisense suggestions will be
 * generated. 
 * Words are read in from unabridged dictionary text
 * file. And stored in a HashMap
 * HW 3
 * Base code demo created by
 * Prof. Floeser 3/30/18 
 * Used in ISTE-222 for Hash Tables
 * @author Lucas Kohorst
 */
public class KeyDetection extends JFrame {
   private JTextField jtfInput;
   private JButton jbExit;
   private JLabel jlStatus;
   
   private final String INPUTFILE = "texts/small_words.txt";
   private HashMap<String, HashMap> hashWords = null;

   public static void main(String[] args) {
      new KeyDetection();
   } // end main

   /**
    * Create the GUI Assign the text field to a listener.
    */
   public KeyDetection() {
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

      hashWords = loadFile(new File(INPUTFILE));
      System.out.println(hashWords);
      System.out.printf("Words loaded: %,d %n", hashWords.size());

   } // end KeyDetection constructor

   /**
    * Takes the word section and generates a HashMap for it
    * @param section, the word section to generate
    * @return the HashMap of words
    */
   private HashMap<String, HashMap> generateHashMap(ArrayList<String> chars, HashMap sectionList, int index) {
      HashMap generatedHashMap;
      // Return HashMap if there are no more chars
      // to bt "put"
      if (chars.size() == 0) {
         generatedHashMap = sectionList;
      } else {
         // Check if the key already exists
         // if so nest the HashMap
         String charValue = chars.get(index);
         if (sectionList.containsKey(charValue)) {
            // Getting the nested chars hashmap
            HashMap<String, HashMap> nestedHashMap = generateNestedSectionHashMap(chars, sectionList);
            sectionList.put(charValue, nestedHashMap);
            // Clearing the chars arraylist
            chars.clear();
            generateHashMap(chars, sectionList, 0);
         }
         // Otherwise put the key into the main hash table
         else {
            // Check there is still more arraylist to go through
            sectionList.put(chars.get(index), new HashMap<>());
            chars.remove(index);
            generateHashMap(chars, sectionList, index++);
         }
      }

      return sectionList;

   } // end of generateHashMap

   /**
    * Takes the nested word section and generates a HashMap for it
    * @param section, the word section to generate
    * @return the nested HashMap of words
    */
   private HashMap<String, HashMap> generateNestedSectionHashMap(ArrayList<String> chars, HashMap sectionList) {
      // Getting HashMap from key that
      // is already placed
      HashMap oldHashMap = (HashMap) sectionList.get(chars.get(0));
      // The hashmap to return
      HashMap nestedHashMap = new HashMap<>();
      // temp hashmap for holding values to be nested
      HashMap tempHashMap = null;
      // Building the nested hashmaps from the end first
      ArrayList<String> reversedChars = chars;
      Collections.reverse(reversedChars);
      for (String elm : reversedChars) {
         // Putting values into local hashmap
         HashMap localHashMap = new HashMap<>();
         // Linking the current hashmap with the
         // previous one
         localHashMap.put(elm, tempHashMap);
         tempHashMap = localHashMap;
      }
      return tempHashMap;
   } // end of generateNestedSectionHashMap

   /**
    * For DEBUGGING purposes only 
    * log all of the words currently stored 
    * by printing out their hashmaps
    * @param words, hashmap of stored words
    */
   public void printWords(HashMap<String, HashMap> words) {
      ArrayList<Object> valuesArray = new ArrayList<Object>();
      for (String key : words.keySet()) {
         if (words.get(key) != null) {
            valuesArray.add(words.get(key));
         }
      }
      System.out.println("[LOG]: " + valuesArray.toString());
   } // end of printWords

   /**
    * Check if input is a word
    * @return list of possible words
    */
   public ArrayList<String> checkInput(String input, HashMap<String, HashMap> map) {
      ArrayList<String> possibleWords = new ArrayList<String>();
      possibleWords.addAll(map.keySet());

      // !! While (Do While?) loop to traverse through the hashmap down the inputted word and all possibilities after that
      // possibly want to put values into tree map 

      System.out.println(possibleWords.toString());
      return possibleWords;
   } // end of checkInput

   /**
    * Load a HashMap with a character key, and as a value, another HashMap of data
    * The HashMap could be defined as <Character,HashMap> but working with String
    * is so much easier.
    */
   private HashMap<String, HashMap> loadFile(File theFile) {
      // Creating the words HashMap
      HashMap<String, HashMap> words = null;

      // Reading in the file
      try (BufferedReader br = new BufferedReader(new FileReader(theFile))) {
         String line;
         words = new HashMap<String, HashMap>();
         // Reading all the lines of the file
         while ((line = br.readLine()) != null) {
            // Splitting line to a char array
            String charArray[] = line.split("(?!^)");
            String keyChar = charArray[0];
            // Converting array to arraylist
            ArrayList<String> chars = new ArrayList<String>(Arrays.asList(charArray));
            // Removing the first char before generating map
            // because it is already used as the key
            chars.remove(0);
            // Generating HashMap from arraylist
            HashMap<String, HashMap> generatedTable = generateHashMap(chars, words, 0);
            printWords(words);
            words.put(charArray[0], generatedTable);
         }
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }

      return words;
   }

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
            checkInput(String.valueOf(input), hashWords);
         }
         else
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