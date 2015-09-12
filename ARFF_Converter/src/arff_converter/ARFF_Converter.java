/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arff_converter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ARFF_Converter {

  public static void main(String[] args) {
    String filename="";
    Scanner scan=null;
    File file=null;
    while(scan==null){
      filename=getString("Enter filename to convert: ");
      if(filename.charAt(0)==' ')
        System.exit(0);
      file=new File(filename);
      try{
        scan=new Scanner(file);
      }catch(FileNotFoundException e){
        System.out.println("File not found");
      }
    }


    //URL url = new URL("http://archive.ics.uci.edu/ml/machine-learning-databases/car/car.data");
    String title=filename.replaceFirst(".*\\\\+","").replaceFirst("\\.\\w{1,5}$","");

    String path=filename.replaceFirst(title+"\\.\\w{1,5}$","");
    String outfile=getString("Enter filename to save as (will be .arff) (enter for default) ");
    if(outfile.compareTo(" ")==0)
      outfile=path+title+".arff";
    else
      outfile=path+outfile+".arff";
    System.out.println(outfile);

    ArffFile arf=new ArffFile(title,filename);
    if(answerYorN("Do you want to remove any instances with ? or missing data? (Y or N) ",true))
      arf.removeMissingDataLines();
    while(true){
      String s=getString("Enter a comment (blank to skip): ");
      if(s.charAt(0)==' ')
        break;
      arf.addComment(s);
    }

    // find out the delimiter
    // assumptions:
    //    - the same delimiter is used in the whole file.
    //    - each line is either a new instance or
    //        optionally the first line is the attribute names.
    //    - there is only one delimiter (outside of "" or '').
    //    - if there are any " then they are for quoting strings.
    //    - if there are not any " and there are ' then they are for quoting strings.
    //    - if there is any whitespace besides linebreak,
    //        then any whitespace is a delimiter !!!unless there is ", "!!!
    //    - the possible delimiters are , ; whitespace.
    //    - if there are no delimiters then the whole line is one attribute.
    //    - if there is both , and ; then the delimiter is both.
    //    - if there are two ,, or ;; then that is a missing data item, will become a ?.
    //    - if there is multiple whitespace together, it gets skipped (//s+).

    // attribute names in first line? assumptions:
    //    - if any token in the first line is a number then it is data ("no attribute names").
    //    - better ask for sure if it looks like attribute names.


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
      if(!answerYorN("\n\nIs this first line the Attribute names? ",true))
        hasNames=false;
      else if(answerYorN("\n\nDo you want to change any Attribute names? ",false))
        changeNames=true;
    }

    if(!hasNames || changeNames){
      while(true){
        if(!hasNames){
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
    }


    while(true){
      System.out.println("\nGuessing Attribute types is not implemented. Get ready to enter types.");
      System.out.println("Sample lines of file:");
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
      if(answerYorN("\nHappy? (Enter to convert file) ",true))
        break;
    }

    for(int i=0;i<numFields;i++)
      arf.addAttribute(attNames[i],type[i]);

    try{
      scan=new Scanner(file);
    }catch(FileNotFoundException e){}
    if(hasNames)
      scan.nextLine();

    while(scan.hasNext()){
      arf.addInstance(scan.nextLine());
    }

    while(true){
    try{
        BufferedWriter br = new BufferedWriter(new FileWriter(outfile));
        br.write(arf.output());
        br.close();
        break;
      }catch(IOException e){
        System.out.println("Can't write file, Enter path to save: ");
        outfile=getString();
      }
    }
    System.out.println("Output written to \""+outfile+"\"");
  }

  static int count(String s, char c){
    int i=0, total=0;
    while((i=s.indexOf(c, i))>0){
      i++;
      total++;
    }
    return total;
  }
	static String getString(String p){
    System.out.print(p);
    return getString();
  }
	static char getChar(){
		return getString().charAt(0);
	}
	static String getString(){
    String s="";
    try{
    	s=new BufferedReader(new InputStreamReader(System.in)).readLine();
    }catch(IOException e){
      System.err.println("IOException");
      System.exit(1);
    }
    if(s.equals(""))
      return " ";
    return s;
	}
  static boolean answerYorN(String p, boolean YisDefault){
    while(true){
      System.out.print(p);
      char c=getChar();
      if(c=='y' || c=='Y')
        return true;
      if(c=='n' || c=='N')
        return false;
      if(c==' ')
        return YisDefault;
    }
  }
}
