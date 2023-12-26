import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
  static List<String> chineseSentences = new ArrayList<>();

  public static void main(String[] args) throws FileNotFoundException {
    if (args.length == 1) { //TODO: possible extension to allow multiple file path arguments
      File input = new File(args[0]);
      Scanner myReader = new Scanner(input);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        if (containsHanScript(data)) {
          chineseSentences.add(data);
        }
      }
      SubtitleComparator subComp = new SubtitleComparator(chineseSentences);
    } else {
      System.out.println("Usage: hsk-grader [file-path]");
    }
  }

  public static boolean containsHanScript(String s) {
    return s.codePoints().anyMatch(
      codepoint ->
      Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
  }
}

