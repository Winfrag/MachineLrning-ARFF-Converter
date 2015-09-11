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
      String filename=getString("Enter filename to convert: ");
      File file=new File(filename);
      Scanner scan = new Scanner(file);

      String title=filename.replaceFirst(".*\\\\+","").replaceFirst("\\.\\w{1,5}$","");
      String path=filename.replaceFirst(title+"\\.\\w{1,5}$","");
      ArffFile arf=new ArffFile(title,filename);
//      arf.addComment("Blah blah blah this is the good old iris data");
//      arf.addComment("Just checking if this works");
//      arf.addAttribute("num1",ArffFile.Datatype.REAL);
//      arf.addAttribute("num2",ArffFile.Datatype.REAL);
//      arf.addAttribute("num3",ArffFile.Datatype.REAL);
//      arf.addAttribute("num4",ArffFile.Datatype.REAL);
//      arf.addNominalAttribute("class",new String[]{"Iris-setosa","Iris-versicolor","Iris-virginica"});

      // find out the delimiter
      // assumptions:
      //    - the same delimiter is used in the whole file.
      //    - each line is either a new instance or
      //        optionally the first line is the attribute names.
      //    - there is only one delimiter (outside of "" or '').
      //    - if there are any " then they are for quoting strings.
      //    - if there are not any " and there are ' then they are for quoting strings.
      //    - if there is any whitespace besides linebreak,
      //        then any whitespace is a delimiter unless there is ", "
      //    - the possible delimiters are , ; whitespace.
      //    - if there are no delimiters then the whole line is one attribute.
      //    - if there is both , and ; then the delimiter is both.
      //    - if there are two ,, or ;; then that is a missing data item, will become a ?.
      //    - if there is multiple whitespace together, it gets skipped (//s+).

      // attribute names in first line? assumptions:
      //    - if any token in the first line is a number then it is data ("no attribute names").
      //    - if every token in the first line is a non-number then it is attribute names.

      // make integers into classes? assumptions:
      //    - first or last column
      //    - no NOMINAL attributes
      //    - not sure may have to specify
      String fl=scan.nextLine();

      String qDelimiter="\"";
      String dDelimiter="";

      if(count(fl,'"')%2==0)
        qDelimiter="\"";
      else if(count(fl,'\'')%2==0)
        qDelimiter="'";

      String mod=fl.replaceAll(qDelimiter+".*"+qDelimiter, ""); // don't test for delimiters within quotes
      if(mod.contains(", "))
        dDelimiter=", ";
      else if(mod.indexOf(' ')>=0 || mod.indexOf('\t')>=0)
        dDelimiter="\\s+";
      else{
        if(count(mod,',')>=0)
          dDelimiter+=",";
        if(count(mod,';')>=0)
          dDelimiter+=";";
        dDelimiter="["+dDelimiter+"]";
      }
      if(dDelimiter.compareTo("[]")==0){
        System.err.println("Can't find a delimiter in the data (no , ; or whitespace in first line");
        dDelimiter=",";
      }
      arf.setInputDelimiter(dDelimiter);
      arf.setInputQuoteDelimiter(qDelimiter);

      Scanner ts=new Scanner(fl);
      ts.useDelimiter(dDelimiter);
      boolean hasNames=true;
      int numFields=0;
      while(ts.hasNext()){
        if(ts.hasNextDouble() || ts.hasNextInt())
          hasNames=false;
        ts.next();
        numFields++;
      }

      String[] attNames=new String[numFields];
      ArffFile.Datatype[] type=new ArffFile.Datatype[numFields];

      boolean changeNames=false;

      if(hasNames){
        ts=new Scanner(fl);
        ts.useDelimiter(dDelimiter);
        int i=0;
        while(ts.hasNext()){
          attNames[i++]=(ts.next().replaceAll(qDelimiter,""));
        }
        for(i=0;i<numFields;i++)
          System.out.print(attNames[i]+", ");
        System.out.print("\n\nDo you want to change any Attribute names? (Y or N) ");
        char c=getChar();
        if(c=='Y' || c=='y')
          changeNames=true;
      }

      if(!hasNames || changeNames){
        while(true){
          if(!changeNames){
            System.out.print("No Attribute names found\nDo you want to enter them? (Y or N)"+
                 "\n(if N then \"attrib1\", \"attrib2\", etc.) ");
            char c=getChar();
            if(c=='n' || c=='N'){
              for(int i=0;i<numFields;i++)
                attNames[i]="attrib"+(i+1);
              break;
            }
          }
          System.out.println(fl+"\n\nEnter a name for ");
          for(int i=0;i<numFields;i++){
            System.out.print("field "+(i+1)+": ");
            attNames[i]=getString();
          }
          System.out.println();
          for(int i=0;i<numFields;i++){
            System.out.print(attNames[i]+", ");
          }
          System.out.print("Accept? (Y or N) ");
          char c=getChar();
          if(c=='y' || c=='Y' || c==' '){
            break;
          }

        }// end while

        if(!hasNames) // reset scanner to beginning of file
          scan=new Scanner(file);
      }

      while(true){
        System.out.println("\nGuessing Attribute types is not implemented. Get ready to enter types.");
        System.out.println("First two lines of file:");
        if(scan.hasNextLine()) System.out.println("\n"+scan.nextLine());
        if(scan.hasNextLine()) System.out.println(scan.nextLine());
        System.out.println("\n(r)eal, (i)nteger, (s)tring, (d)ate, (n)ominal");
        for(int i=0;i<numFields;i++){
          while(true){
            System.out.print("Attribute "+(i+1)+": ");
            switch(getChar()){
              case 'r':type[i]=ArffFile.Datatype.REAL; break;
              case 'i':type[i]=ArffFile.Datatype.INT; break;
              case 's':type[i]=ArffFile.Datatype.STRING; break;
              case 'd':type[i]=ArffFile.Datatype.DATE; break;
              case 'n':type[i]=ArffFile.Datatype.NOMINAL; break;
              default: System.out.println("\n(r)eal, (i)nteger, (s)tring, (d)ate, (n)ominal");
                continue;
            }
            break;
          }
        }
        System.out.println("\nHappy? (Enter to convert file) ");
        char c=getChar();
        if(c==' '||c=='y'||c=='Y')
          break;
      }

      for(int i=0;i<numFields;i++)
        arf.addAttribute(attNames[i],type[i]);

      scan=new Scanner(file);
      if(hasNames)
        scan.nextLine();

      while(scan.hasNext()){
        arf.addInstance(scan.nextLine());
      }

      BufferedWriter br = new BufferedWriter(new FileWriter(path+title+".arff"));
      br.write(arf.output());
      br.close();
      System.out.println("Output written to \""+path+title+".arff");
    }





    catch(IOException ex) {
      System.err.println("IOException");
       // there was some connection problem, or the file did not exist on the server,
       // or your URL was not in the right format.
       // think about what to do now, and put it here.
       ex.printStackTrace(); // for now, simply output it.
    }
  }

  static int count(String s, char c){
    int i=0, total=0;
    while((i=s.indexOf(c, i))>0){
      i++;
      total++;
    }
    return total;
  }
	static String getString(String p) throws IOException{
    System.out.print(p);
    return getString();
  }
	static String getString() throws IOException{
		String s=new BufferedReader(new InputStreamReader(System.in)).readLine();
    if(s.equals(""))
      return " ";
    return s;
	}
	static char getChar() throws IOException{
		return getString().charAt(0);
	}

}
