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
    NUMERIC, INT, REAL, NOMINAL, STRING, DATE
  }
  private class Attribute{
    String name;
    Datatype type;
    String dateFormat;
    String[] nominalValueNames;
    int numNominalValues;

    Attribute(String n, Datatype dt){
      name=n;
      type=dt;
    }
    Attribute(String n, Datatype dt, String[] nominals){
      this(n,dt);
      numNominalValues=nominals.length;
      nominalValueNames=nominals;
    }

    private String getDatatypeName(){
      switch(type){
        case NUMERIC: return "NUMERIC";
        case INT:     return "INTEGER";
        case REAL:    return "REAL";
        case NOMINAL: StringBuilder s=new StringBuilder();
                      s.append('{');
                      for(int i=0;i<numNominalValues;i++){
                        s.append(nominalValueNames[i]);
                        if(i<numNominalValues-1)s.append(',');
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
  private int numInstances;
  private String inputDataDelimiter="[\\s,]+";

  // constructors
  public ArffFile(){}
  public ArffFile(String title, String inputfilename){
    DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
    Date date = new Date();
    comments=" File created by \"<insert awesome name for this ARFF Converter program>\" "
            +"\n on "+dateFormat.format(date)+" from file: "+inputfilename;
    this.title=title;
  }

  public void setInputDelimiter(String d){
    inputDataDelimiter=d;
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
      sb.append(attributes[i].name);
      sb.append(' ');
      sb.append(attributes[i].getDatatypeName());
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
    for(int i=0;i<data.size();i++){
      for(int j=0;j<numAttributes;j++){
        sb.append(this.data.get(i)[j]);
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
        case REAL:    ob[i]=sc.nextDouble();
                      break;
        case INT:     ob[i]=sc.nextInt();
                      break;
        case STRING:
        case NOMINAL:
        case DATE:    if(sc.hasNext("\".*")){    // may need some work not sure if will handle getting rid of the first "
                        sc.useDelimiter("\"");
                        ob[i]="'"+sc.next()+"'";
                        sc.useDelimiter(inputDataDelimiter);
                      } else if(sc.hasNext("'.*")){
                        sc.useDelimiter("'");
                        ob[i]="'"+sc.next()+"'";
                        sc.useDelimiter(inputDataDelimiter);
                      } else
                        ob[i]=sc.next();
                      break;
      }
    }
    data.add(ob);
  }

  // outputInstance()
  //public String outputInstance(String inputInstance){
  //  StringBuilder sb=new StringBuilder();


  //  return sb.toString();
  //}

  // addComment()
  public void addComment(String c){
    comments+=System.lineSeparator()+" "+c;
  }

  // setTitle()
  public void setTitle(String t)
    {title=t;}

  //
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
