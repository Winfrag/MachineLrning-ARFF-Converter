/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arff_converter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Brendan Burns
 */
public class ARFF_Converter {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    try {
      //URL url = new URL("http://archive.ics.uci.edu/ml/machine-learning-databases/car/car.data");
      Scanner s = new Scanner(new File("C:/Users/Magpie/Downloads/WekaData/iristest.data"));
      s.useDelimiter("\n");
      StringBuilder sb=new StringBuilder();
      while (s.hasNext())
      {

      }
      // read from your scanner
    }
    catch(IOException ex) {
      System.err.println("IOException");
       // there was some connection problem, or the file did not exist on the server,
       // or your URL was not in the right format.
       // think about what to do now, and put it here.
       ex.printStackTrace(); // for now, simply output it.
    }

    System.out.println();
  }

}
