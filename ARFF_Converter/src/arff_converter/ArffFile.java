/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arff_converter;
import java.util.*;
import java.text.*;

/**
 *
 * @author Magpie
 */
public class ArffFile {

  public enum Datatype{
    NUMERIC, INT, REAL, NOMINAL, STRING, DATE, UNKNOWN
  }
  private class Attribute{
    String name;
    Datatype type;
    String dateFormat;
    ArrayList<String> nominalValueNames;
    int numNominalValues;

    Attribute(String n, Datatype dt){
      name=n;
      type=dt;
      nominalValueNames=new ArrayList<>();
    }
    Attribute(String n, Datatype dt, String[] nominals){
      this(n,dt);
      numNominalValues=nominals.length;
      nominalValueNames.addAll(Arrays.asList(nominals));
    }

    private String getDatatypeString(){
      switch(type){
        case NUMERIC: return "NUMERIC";
        case INT:     return "INTEGER";
        case REAL:    return "REAL";
        case NOMINAL: StringBuilder s=new StringBuilder();
                      s.append('{');
                      int n=nominalValueNames.size();
                      for(int i=0;i<n;i++){
                        s.append(nominalValueNames.get(i));
                        if(i<n-1)s.append(',');
                      }
                      s.append('}');
                      return s.toString();
        case STRING:  return "STRING";
        case DATE:    return "DATE "+dateFormat;
      }
      System.err.println("Unknown Datatype");
      return null;
    }
  }

  // Fields
  private String comments="";
  private String title="Random Data Picked Up Off the Floor";
  private Attribute[] attributes;
  private int numAttributes;
  private ArrayList<Object[]> data=new ArrayList<>();
  private String inputDataDelimiter=",";
  private String inputQuoteDelimiter="\"";

  // constructors
  public ArffFile(){}
  public ArffFile(String title, String inputfilename){
    DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
    Date date = new Date();
    comments=" File created by \"<insert awesome name for this ARFF Converter program>\" "
            +"\n on "+dateFormat.format(date)+" from file: "+inputfilename;
    this.title=title;
  }

  public void setInputDelimiter(String d){ // , by default
    inputDataDelimiter=d;
  }
  public void setInputQuoteDelimiter(String d){ // " by default
    inputQuoteDelimiter=d;
  }

  // output()
  public String output(){

    analyzeNominals();

    StringBuilder sb=new StringBuilder();
    sb.append(outputHeader());
    sb.append(outputData());
    return sb.toString();
  }

  // outputHeader()
  public String outputHeader(){
    StringBuilder sb=new StringBuilder();
    Scanner s=new Scanner(comments);
    s.useDelimiter("[\r\n]");
    while(s.hasNext()){
      sb.append('%');
      sb.append(s.next());
      sb.append(System.lineSeparator());
    }
    sb.append('%');
    sb.append(System.lineSeparator());
    sb.append("@RELATION ");
    sb.append(title);
    sb.append(System.lineSeparator());
    for(int i=0;i<numAttributes;i++){
      sb.append(System.lineSeparator());
      sb.append("@ATTRIBUTE ");
      sb.append('\'');
      sb.append(attributes[i].name);
      sb.append("' ");
      sb.append(attributes[i].getDatatypeString());
    }
    sb.append(System.lineSeparator());
    sb.append(System.lineSeparator());
    sb.append("@DATA ");
    sb.append(System.lineSeparator());
    return sb.toString();
  }

  // outputData()
  public String outputData(){
    StringBuilder sb=new StringBuilder();
    for (Object[] ob : data) {
      for (int j = 0; j<numAttributes; j++) {
          sb.append(ob[j]);
        if(j<numAttributes-1)sb.append(',');
      }
      sb.append(System.lineSeparator());
    }
    return sb.toString();
  }

  // addInstance()
  public void addInstance(String instanceString){
    Scanner sc=new Scanner(instanceString);
    sc.useDelimiter(inputDataDelimiter);
    Object[] ob=new Object[numAttributes];
    for(int i=0;i<numAttributes;i++){
      switch(attributes[i].type){
        case NUMERIC:
        case REAL:    if(sc.hasNextDouble())
                        ob[i]=sc.nextDouble();
                      else{
                        ob[i]="?";
                        String s=sc.next();
                        System.err.println("Data type mismatch, <"+s+"> should be a double");
                      }
                      break;
        case INT:     if(sc.hasNextInt())
                        ob[i]=sc.nextInt();
                      else{
                        ob[i]="?";
                        String s=sc.next();
                        System.err.println("Data type mismatch, <"+s+"> should be an integer");
                      }
                      break;
        case STRING:
        case NOMINAL:
        case DATE:
        case UNKNOWN: if(sc.hasNext(inputQuoteDelimiter+".*")){ // Takes care of strings in single or double quotes,
                        sc.useDelimiter(inputQuoteDelimiter);   // adds them back in with single quotes,
                        sc.next(); //eat the ,"                 // maybe this is wrong (the single quotes)
                        ob[i]="'"+sc.next()+"'";
                        sc.useDelimiter(inputDataDelimiter);
                      } else
                        ob[i]=sc.next();
                      break;
      }
    }
    data.add(ob);
  }

  private void analyzeNominals(){
    for(int a=0;a<numAttributes;a++){
      if(attributes[a].type==Datatype.NOMINAL){
        for(Object[] inst: data){
          String s=(String)inst[a];
          if(s.matches("\\d+")){//integer
            s="class-"+s;
            inst[a]=s;
          }
          if(s.compareTo("?")!=0){
            if(attributes[a].nominalValueNames.indexOf(s)<0)
              attributes[a].nominalValueNames.add(s);
          }
        }
      }
    }
  }

  // addComment()
  public void addComment(String c){
    comments+=System.lineSeparator()+" "+c;
  }

  // setTitle()
  public void setTitle(String t)
    {title=t;}

  //
  public void addAttribute(String name){
    Attribute[] temp=new Attribute[numAttributes+1];
    if (numAttributes>0)
      System.arraycopy(attributes, 0, temp, 0, numAttributes);
    temp[numAttributes]=new Attribute(name,Datatype.UNKNOWN);
    numAttributes++;
    attributes=temp;
  }
  public void addAttribute(String name, Datatype dt){
    Attribute[] temp=new Attribute[numAttributes+1];
    if (numAttributes>0)
      System.arraycopy(attributes, 0, temp, 0, numAttributes);
    temp[numAttributes]=new Attribute(name,dt);
    numAttributes++;
    attributes=temp;
  }
  public void addDateAttribute(String name, String dateFormat){
    Attribute[] temp=new Attribute[numAttributes+1];
    if (numAttributes>0)
      System.arraycopy(attributes, 0, temp, 0, numAttributes);
    temp[numAttributes]=new Attribute(name,Datatype.DATE);
    temp[numAttributes].dateFormat=dateFormat;
    numAttributes++;
    attributes=temp;
  }
  public void addNominalAttribute(String name, String[] nominalNames){
    Attribute[] temp=new Attribute[numAttributes+1];
    if (numAttributes>0)
      System.arraycopy(attributes, 0, temp, 0, numAttributes);
    temp[numAttributes]=new Attribute(name,Datatype.NOMINAL,nominalNames);
    numAttributes++;
    attributes=temp;
  }
}
