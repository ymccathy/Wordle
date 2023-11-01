import java.awt.Color;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.NullCipher;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.awt.event.KeyEvent;
import java.io.*;

public class GameLogic {

   // Name of file containing all the possible "secret words"
   private static final String SECRET_WORDS_FILENAME = "secrets.txt";

   // Name of file containing all the valid guess words
   private static final String VALID_GUESSES_FILENAME = "valids.txt";

   // Use for generating random numbers!
   private static final Random rand = new Random();

   // Dimensions of the game grid in the game window
   public static final int MAX_ROWS = 6;
   public static final int MAX_COLS = 5;

   // Character codes for the enter and backspace key press
   public static final char ENTER_KEY = KeyEvent.VK_ENTER;
   public static final char BACKSPACE_KEY = KeyEvent.VK_BACK_SPACE;

   // The null character value (used to represent an "empty" value for a spot on
   // the game grid)
   public static final char NULL_CHAR = 0;

   // Various Color Values
   private static final Color CORRECT_COLOR = new Color(53, 209, 42); // (Green)
   private static final Color WRONG_PLACE_COLOR = new Color(235, 216, 52); // (Yellow)
   private static final Color WRONG_COLOR = Color.DARK_GRAY; // (Dark Gray [obviously])
   private static final Color DEFAULT_KEY_COLOR = new Color(160, 163, 168); // (Light Gray)

   // A preset, hard-coded secret word to be use when the resepective debug is
   // enabled
   private static final char[] DEBUG_PRESET_SECRET = {'S', 'L', 'E', 'E', 'K'};

   // ...Feel free to add more **FINAL** variables of your own!

   // ****************** NON-FINAL GLOBAL VARIABLES ******************
   // Array storing all valid guesses read out of the respective file
   private static String[] validGuesses;

   // The current row/col where the user left off typing
   private static int currentRow, currentCol;

   // *******************************************************************

   // This function gets called ONCE when the game is very first launched
   // before the user has the opportunity to do anything.
   //
   // Should perform any initialization that needs to happen at the start of the
   // game,and return the randomly chosen "secret word" as a char array
   //
   // If either of the valid guess or secret words files cannot be read, or are
   // missing the word count in the first line, this function returns null.

   public static char[] initializeGame() {
      // Read in the secret words file and store each word in an array
         String[] secretWords = null;
  
         try {
            Scanner scanner = new Scanner(new File(SECRET_WORDS_FILENAME));
            int numWords = scanner.nextInt() - 1;
            Scanner scannervalid = new Scanner(new File(VALID_GUESSES_FILENAME));
            int numValidWords = scannervalid.nextInt() - 1;
  
          secretWords = new String[numWords];
          for (int i = 0; i < numWords; i++) {
              secretWords[i] = scanner.next();
          }
  
          validGuesses = new String[numValidWords];
          for (int a = 0; a < numValidWords; a++) {
              validGuesses[a] = scannervalid.next();
          }
  
          scanner.close();
          scannervalid.close();
         } 
      
         catch (FileNotFoundException e) {
          System.err.println("Error: secrets.txt file not found");
          return null;
         } 
      
         catch (InputMismatchException e) {
          System.err.println("Error: Invalid format in secrets.txt");
          return null;
         }
  
         // Select a random word from the array and return it as a char array
         String secretWord = secretWords[rand.nextInt(secretWords.length)];
         if (JWordleLauncher.DEBUG_USE_PRESET_SECRET){
            return DEBUG_PRESET_SECRET;
         }
         else if (!JWordleLauncher.DEBUG_USE_PRESET_SECRET){
            return BreakString(secretWord.toUpperCase());
         }
         else{
            return null;
         }
      }


      // This function gets called everytime the user types a valid key on the
      // keyboard (alphabetic character, enter, or backspace) or clicks one of the
      // keys on the graphical keyboard interface.
      // The key pressed is passed in as a char value.
      public static void reactToKey(char key) {
         if (key == BACKSPACE_KEY) {
            currentCol -= 1;
            if (currentCol < 0) {
               currentCol = 0;
            }
            GameGUI.setGridChar(currentRow, currentCol, NULL_CHAR);
         } 
         else if (key == ENTER_KEY) {
            char[] checkChar = GameGUI.getSecretWordArr();
            char[] storeInput = new char[MAX_COLS];
            int greenNum = 0;

            StoreInput(storeInput);

            if (currentCol != MAX_COLS || !validWordCheck(storeInput)) {
               GameGUI.wiggle(currentRow);
            }
            else {
               greenNum = greenCheck(storeInput, checkChar, greenNum);
               yellowCheck(storeInput, checkChar);
               grayCheck(storeInput, checkChar);

               currentRow += 1;
               currentCol = 0;
               EndGame(greenNum);
            }
         }

         else {
            if (currentCol < MAX_COLS){
               GameGUI.setGridChar(currentRow, currentCol, key);
               currentCol += 1;
               System.out.println("keyPressed called! key (int value) = '" + ((int) key) + "'");
            }
         }
      }


      // Stores user input from the game grid into a character array,
      // loops through each grid cell in the current row up to the current column,
      // retrieves the character in the cell using GameGUI.getGridChar()
      // and return the userInput array with the stored input.
      public static char[] StoreInput(char []userInput){
      for (int i = 0; i < currentCol; i++) {
         char guess = GameGUI.getGridChar(currentRow, i);
         userInput[i] = guess;
      }
      return userInput;
      }

      // This function checks if the user's input is a valid guess by comparing it with
      // the array of valid guesses. It converts the input to uppercase and returns true if
      // it matches any of the valid guesses, otherwise false.
      public static boolean validWordCheck(char[] userInput){
         if (JWordleLauncher.DEBUG_ALL_GUESSES_VALID){
            return true;
         }
         else{
            String checker = String.valueOf(userInput).toUpperCase();
            for (int i = 0; i < validGuesses.length; i++) {
               if (checker.equals(validGuesses[i].toUpperCase())) {
               return true;
               }
            }
            return false;
            }
      }

      // This function checks if the game has ended by checking if the current row is greater than or equal to MAX_ROWS.
      // If yes, the game is over and the "gameOver" function is called with the parameter "didPlayerWin" change to false.
      // If the player has guessed the correct word (greenNum equals MAX_COLS), the "didPlayerWin" variable is true
      // and the "gameOver" function is called with the parameter "didPlayerWin" set to true.
      public static void EndGame(int greenNum){
         boolean didPlayerWin = false;
         if (currentRow >= MAX_ROWS) {
            GameGUI.gameOver(didPlayerWin);
         } 
         if (greenNum == MAX_COLS){
            didPlayerWin = true;
            GameGUI.gameOver(didPlayerWin);
         }
      }

      // This function takes a String input and breaks it down into a char array
      // where each element of the array is a character from the input string.
      public static char[] BreakString(String word) {
         char[] newChar = new char[word.length()];

         for (int i = 0; i < word.length(); i++) {
            
            newChar[i] = word.charAt(i);
         }
      return newChar;
      }

      // This function greenCheck() compares the user's guess with the answer array to determine which characters are correct. 
      // If a character in the user's guess is also in the answer array and located in the correct position, greenCheck() increases by 1. 
      // It returns the final green letter count, which equals 5 if the user has correctly guessed the word.
      // To prevent a redundancy in future checks, if a character in the user's guess is found to be correct, it is set to '0' in the input array.
      // If a character in the user's guess is correctly identified, the corresponding cell in the answer grid is colored green, 
      // and the letter key is colored green as well.
      public static int greenCheck(char[] input, char[] secretWord, int greenNum) {
      for (int i = 0; i < input.length; i++) {

         if (input[i] == secretWord[i]) {

            GameGUI.setGridColor(currentRow, i, CORRECT_COLOR);
            GameGUI.setKeyColor(input[i], CORRECT_COLOR);
            secretWord[i] = '0';
            greenNum++;
         }
      }
      return greenNum;
      }

      // This function yellowCheck() checks if a character is inthe wrong position in a modified string array. 
      // It uses a nested for-loop to compare the user's guess and answer arrays and sets the letter's grid to yellow 
      // if it's in the answer array but not in the correct position. 
      // If the letter wasn't previously colored green, the key will be colored yellow.
      public static void yellowCheck(char[] input, char[] secretWord) {
      for (int i = 0; i < input.length; i++) {

         for (int j = 0; j < secretWord.length; j++) {

            if (input[i] == secretWord[j] && i != j && GameGUI.getGridColor(currentRow,i)!=CORRECT_COLOR ) {

               GameGUI.setGridColor(currentRow, i, WRONG_PLACE_COLOR);
               secretWord[j] = '0';

               if (GameGUI.getKeyColor(input[i]) != CORRECT_COLOR && GameGUI.getKeyColor(input[i]) != WRONG_PLACE_COLOR) {
                  
                  GameGUI.setKeyColor(input[i], WRONG_PLACE_COLOR);
               }
            }
         }
      }
      }

      // The function deals with the "leftover" array by greenCheck and yellowCheck. 
      // It colors the key and grid of a letter dark gray if it is not correct and not in the wrong place, 
      // but only if they haven't been colored green or yellow before.
      public static void grayCheck(char[] input, char[] secretWord) {
      for (int i = 0; i < secretWord.length; i++) {

         if (input[i] != secretWord[i] && GameGUI.getGridColor(currentRow,i)!=CORRECT_COLOR && GameGUI.getGridColor(currentRow, i) != WRONG_PLACE_COLOR) {

            GameGUI.setGridColor(currentRow, i, WRONG_COLOR);

            if (GameGUI.getKeyColor(input[i]) != CORRECT_COLOR && GameGUI.getKeyColor(input[i]) != WRONG_PLACE_COLOR) {

               GameGUI.setKeyColor(input[i], WRONG_COLOR);
            }
         }
         }
   }
}

