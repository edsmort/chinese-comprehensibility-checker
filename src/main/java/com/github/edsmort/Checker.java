package com.github.edsmort;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import picocli.CommandLine;
import picocli.CommandLine.*;

public class Checker implements Runnable {
  static List<String> chineseSentences;
  static List<String> userKnownWords;

  @Parameters(index = "0", description = "The subtitle file to be analysed") // Add support for multiple subtitle files
  private File subtitleFile;

  @Option(names = {"-w", "--word-list"}, description = "User provided list of known words to compare against")
  private File userKnownWordsFile = null;

  @Override
  public void run(){
    SubtitleComparator subComp = new SubtitleComparator();
    Map<String,String> subtitleWords = subComp.findWords(chineseSentences);
    String textBreakdown = formatHSKBreakdown(subComp.getHSKCounts());
    System.out.println(textBreakdown);
    if (userKnownWords != null) {
      Map<String, String> unknownWords = subComp.compareKnownWordsToSubtitle(userKnownWords, subtitleWords);
    }
  }

  public List<String> readFile(File file) {
    List<String> dataList = new ArrayList<>();
    try (Scanner myReader = new Scanner(file)) {
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        if (containsHanScript(data)) {
          dataList.add(data);
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return dataList;
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new Checker()).execute(args);
    System.exit(exitCode);
  }

  private String formatHSKBreakdown(Map<String, Integer> breakdown) {
    String textBreakdown = "HSK Breakdown - \n";
    for (int i = 1; i < 10; i++) {
      double percentage = (double)breakdown.get(String.valueOf(i))/(double)breakdown.get("total")*100;
      textBreakdown += "HSK" + String.valueOf(i) + ": " + String.valueOf(percentage) + "%\n";
    }
    double unknownPercentage = (double)breakdown.get("unknown")/(double)breakdown.get("total")*100;
    textBreakdown += "Unknown: " + String.valueOf(unknownPercentage) + "%";
    return textBreakdown; 
  }

  private boolean containsHanScript(String s) {
    return s.codePoints().anyMatch(
      codepoint ->
      Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
  }

}

