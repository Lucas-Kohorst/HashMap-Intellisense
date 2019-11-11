import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Given input intellisense suggestions will be generated. Words are read in
 * from unabridged dictionary text file. And stored in a HashMap HW 3 Base code
 * demo created by Prof. Floeser 3/30/18 Used in ISTE-222 for Hash Tables
 * @author Prof. Floeser, starter file
 * @author Lucas Kohorst
 */
public class StackIntellisense extends JFrame {
   private JTextField jtfInput;
   private JButton jbExit;
   private JButton jbClear;
   private JLabel jlStatus;

   private static final String file = "texts/UnabridgedDictionary.txt";
   private HashMap<String, HashMap> hashWords = null;
   private static HashIt hasher = null;
   private String word = "";

   public static void main(String[] args) {
      // Creating a new instance of hasher
      // which contains methods to create hashmap
      // and get values from it
      hasher = new HashIt(file);
      new StackIntellisense();
   } // end main

   /**
    * Create the GUI Assign the text field to a listener.
    */
   public StackIntellisense() {
      // Building the GUI
      createGUI();
      // Loading the file
      // and hashing it
      hashWords = hasher.loadFile();
      System.out.printf("Words loaded: %,d %n", hashWords.size());
   } // end Intellisense constructor

   /**
    * Created the interface for the application
    */
   public void createGUI() {
      setTitle("Possible Next Letters");

      JPanel jpMain = new JPanel(new GridLayout(0, 1));
      JPanel jpEntry = new JPanel();
      jpEntry.add(new JLabel("Enter letter: ", JLabel.CENTER));
      jtfInput = new JTextField(40);
      jpEntry.add(jtfInput);

      // Listens for user input to display the next 
      // possible letters
      jtfInput.addKeyListener(new KeyListener() {
         @Override
         public void keyTyped(KeyEvent e) { }

         @Override
         public void keyReleased(KeyEvent e) { }

         @Override
         public void keyPressed(KeyEvent e) {

            int code = e.getKeyCode();
            System.out.println("Code: " + code);

            if (code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z) {
               char input = (char) code;
               System.out.println("Valid character: " + input); // Print as a character

               word += String.valueOf(input);
               char[] lettersArray = word.toCharArray();

               ArrayList<String> letters = new ArrayList<String>();
               for (char letter : lettersArray) {
                  letters.add(String.valueOf(letter));
               }

               // Generates a list of possible letters
               ArrayList<String> possibleWords = hasher.searchMap(hashWords, letters);
               jlStatus.setText(possibleWords.toString());
            } else if (code == 8) {
               // If the code is of the backspace then go back 
               // a letter
               char[] lettersArray = word.toCharArray();
               word = "";
               ArrayList<String> letters = new ArrayList<String>();
               for (int i = 0; i < lettersArray.length - 1; i++) {
                  word += lettersArray[i];
                  letters.add(String.valueOf(lettersArray[i]));
               }
               ArrayList<String> possibleWords = hasher.searchMap(hashWords, letters);
               jlStatus.setText(possibleWords.toString());
            } else if (code == 1) {
               System.out.println("control a");
            } else {
               System.out.println("not a letter " + code); // Print as a number
            }
         }
      });

      jpMain.add(jpEntry);

      jlStatus = new JLabel("Possible Letters", JLabel.CENTER);
      jpMain.add(jlStatus);

      add(jpMain, BorderLayout.CENTER);

      JPanel jpButtons = new JPanel();
      jbExit = new JButton("Exit");
      jbClear = new JButton("Clear");
      jbExit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            System.exit(0);
         }
      });
      jbClear.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            ArrayList<String> possibleWords = hasher.searchMap(hashWords, new ArrayList<String>());
            System.out.println(possibleWords);
            jlStatus.setText(possibleWords.toString());
            jtfInput.setText("");
         }
      });

      jpButtons.add(jbExit);
      jpButtons.add(jbClear);
      add(jpButtons, BorderLayout.SOUTH);

      pack();
      setLocationRelativeTo(null);

      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
   } // end of createGUI

} // end of StackIntellisense