/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arff_converter;
import java.util.Scanner;

/**
 *
 * @author Magpie
 */
public class ArffFile {

  abstract class Datum{

  }

  class Numeric extends Datum{

  }
  class Real extends Datum{

  }
  class Int extends Datum{

  }
  class Str extends Datum{

  }
  class Date extends Datum{

  }

  class Instance{
    Object[] fields;
  }

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
      System.arraycopy(nominals,0,nominalValueNames,0,numNominalValues);
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
  private String comments;
  private String title;
  private Attribute[] attributes;
  private int numAttributes;
  private Instance[] data;
  private int numInstances;

  // constructors
  public ArffFile(){
    comments="";
    title="Random Data Picked Up Off the Floor";
  }
  public ArffFile(String title){
    comments="";
    this.title=title;
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
    sb.append(System.lineSeparator());
    sb.append("@RELATION ");
    sb.append(title);
    sb.append(System.lineSeparator());
    for(int i=0;i<numAttributes;i++){
      sb.append("@ATTRIBUTE ");
      sb.append(attributes[i].name);
      sb.append(' ');
      sb.append(attributes[i].getDatatypeName());
    }
    sb.append(System.lineSeparator());
    return sb.toString();
  }

  // outputData()
  public String outputData(){
    StringBuilder sb=new StringBuilder();
    sb.append(System.lineSeparator());
    sb.append("@DATA ");
    sb.append(System.lineSeparator());
    for(int i=0;i<numInstances;i++){
      for(int j=0;j<numAttributes;j++){
        sb.append(this.data[i].fields[j]);
        if(j<numAttributes-1)sb.append(',');
      }
      sb.append(System.lineSeparator());
    }
    return sb.toString();
  }

  // addComment()
  public void addComment(String c){
    comments+=System.lineSeparator()+c;
  }

  // setTitle()
  public void setTitle(String t)
    {title=t;}

  //
  public void addRealAttribute(String name){
    Attribute[] temp=new Attribute[numAttributes+1];
    if (numAttributes>0)
      System.arraycopy(attributes, 0, temp, 0, numAttributes);
    temp[numAttributes]=new Attribute(name,Datatype.REAL);
    numAttributes++;
    attributes=temp;
  }
  public void addIntegerAttribute(String name){
    Attribute[] temp=new Attribute[numAttributes+1];
    if (numAttributes>0)
      System.arraycopy(attributes, 0, temp, 0, numAttributes);
    temp[numAttributes]=new Attribute(name,Datatype.INT);
    numAttributes++;
    attributes=temp;
  }
  public void addStringAttribute(String name){
    Attribute[] temp=new Attribute[numAttributes+1];
    if (numAttributes>0)
      System.arraycopy(attributes, 0, temp, 0, numAttributes);
    temp[numAttributes]=new Attribute(name,Datatype.STRING);
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
    temp[numAttributes]=new Attribute(name,Datatype.REAL,nominalNames);
    numAttributes++;
    attributes=temp;
  }
}
