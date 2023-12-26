import java.io.*;
import java.util.*;
import java.net.URL;

public class SubtitleComparator {
  private List<String> subtitleLines;
  private Hashtable<String, String> chineseWordsWithHSK;
  private Hashtable<String, String> wordsInSubtitles;
  
  public SubtitleComparator(List<String> userSubtitles) throws FileNotFoundException {
    subtitleLines = userSubtitles; 
    chineseWordsWithHSK = populateChineseWordsWithHSK();
    wordsInSubtitles = findWords(subtitleLines);
  }

  private Hashtable<String, String> populateChineseWordsWithHSK() throws FileNotFoundException {
    Hashtable<String, String> wordsHSK = new Hashtable<String, String>();
    URL url = getClass().getResource("dict_hsk.csv");
    File input = new File(url.getPath());
    try (Scanner myReader = new Scanner(input)) {
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        String[] splitData = data.split(",");
        wordsHSK.put(splitData[0],splitData[1]);
      }
    }
    System.out.println(wordsHSK.toString());
    return wordsHSK;
  }

  public Hashtable<String, String> findWords(List<String> subtitles) {
    Hashtable<String, String> subtitleWords = new Hashtable<String, String>();
    for (String line : subtitles) {
      for (char ch : line.toCharArray()) {
        String chineseChar = String.valueOf(ch);
        if (chineseWordsWithHSK.containsKey(chineseChar)) {
          subtitleWords.put(chineseChar,chineseWordsWithHSK.get(chineseChar));
        } 
        else {
          subtitleWords.put(chineseChar,"unknown");
        }
      }
    }
    return subtitleWords;
  }
}
