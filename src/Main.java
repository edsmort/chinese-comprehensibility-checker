import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Main {
  static List<String> chineseSentences = new ArrayList<>();

  public static void main(String[] args) throws FileNotFoundException {
    if (args.length == 1) { //TODO: possible extension to allow multiple file path arguments
      File input = new File(args[0]);
      try (Scanner myReader = new Scanner(input)) {
        while (myReader.hasNextLine()) {
          String data = myReader.nextLine();
          if (containsHanScript(data)) {
            chineseSentences.add(data);
          }
        }
      }
      SubtitleComparator subComp = new SubtitleComparator(chineseSentences);
      String textBreakdown = formatHSKBreakdown(subComp.getHSKCounts());
      System.out.println(textBreakdown);
    } else {
      System.out.println("Usage: hsk-grader [file-path]");
    }
  }

  private static String formatHSKBreakdown(Hashtable<String, Integer> breakdown) {
    String textBreakdown = "HSK Breakdown - \n";
    double total = 0;
    for (int i = 1; i < 10; i++) {
      double percentage = (double)breakdown.get(String.valueOf(i))/(double)breakdown.get("total")*100;
      textBreakdown += "HSK" + String.valueOf(i) + ": " + String.valueOf(percentage) + "%\n";
      total += percentage;
    }
    double unknownPercentage = (double)breakdown.get("unknown")/(double)breakdown.get("total")*100;
    total += unknownPercentage;
    System.out.println(String.valueOf(total));
    textBreakdown += "Unknown: " + String.valueOf(unknownPercentage) + "%";
    return textBreakdown; 
  }

  private static boolean containsHanScript(String s) {
    return s.codePoints().anyMatch(
      codepoint ->
      Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
  }
}

