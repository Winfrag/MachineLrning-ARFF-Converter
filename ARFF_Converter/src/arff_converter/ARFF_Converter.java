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
      StringBuilder sb=new StringBuilder();

      ArffFile arf=new ArffFile("Iris","iris.data");
      arf.addComment("Blah blah blah this is the good old iris data");
      arf.addComment("Just checking if this works");
      arf.addAttribute("num1",ArffFile.Datatype.REAL);
      arf.addAttribute("num2",ArffFile.Datatype.REAL);
      arf.addAttribute("num3",ArffFile.Datatype.REAL);
      arf.addAttribute("num4",ArffFile.Datatype.REAL);
      arf.addNominalAttribute("class",new String[]{"Iris-setosa","Iris-versicolor","Iris-virginica"});

      while(s.hasNext()){
        arf.addInstance(s.nextLine());
      }

      System.out.print(arf.outputHeader());
      System.out.print(arf.outputData());

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
