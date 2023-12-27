import java.io.*;
import java.util.*;
import java.net.URL;

public class SubtitleComparator {
  private List<String> subtitleLines;
  private Hashtable<String, String> chineseHSKReferenceDict;
  private Hashtable<String, String> wordsInSubtitles;
  private Hashtable<String, Integer> HSKCounts;
  
  public SubtitleComparator(List<String> userSubtitles) throws FileNotFoundException {
    subtitleLines = userSubtitles; 
    chineseHSKReferenceDict = populateChineseHSKReferenceDict();
    HSKCounts = initHSKCounts();
    wordsInSubtitles = findWords(subtitleLines);
  }

  private Hashtable<String, Integer> initHSKCounts() {
    Hashtable<String, Integer> counts = new Hashtable<String, Integer>();
    for (int i = 1; i < 10; i++) {
      counts.put(String.valueOf(i), 0);
    }
    counts.put("unknown", 0);
    counts.put("total", 0);
    return counts;
  }

  private Hashtable<String, String> populateChineseHSKReferenceDict() throws FileNotFoundException {
    Hashtable<String, String> wordsHSK = new Hashtable<String, String>();
    InputStream inputStream = getClass().getResourceAsStream("/dict_hsk.csv");
    try (Scanner myReader = new Scanner(inputStream)) {
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        String[] splitData = data.split(",");
        wordsHSK.put(splitData[0],splitData[1]);
      }
    }
    return wordsHSK;
  }

  public Hashtable<String, String> findWords(List<String> subtitles) {
    Hashtable<String, String> subtitleWords = new Hashtable<String, String>();
    for (String line : subtitles) {
      String[] textBlocks = line.split(" "); //blocks of text in a line are sometimes separated with a space, we can assume that no single word will be created using characters either side of a space
      for (String textBlock : textBlocks) {
        char[] chineseChars = textBlock.toCharArray();
        while (chineseChars.length > 0) {
          for (int i = chineseChars.length; i > 0; i--) {
            char[] checkChars = Arrays.copyOfRange(chineseChars, 0, i);
            String wordCandidate = String.valueOf(checkChars);
            if (chineseHSKReferenceDict.containsKey(wordCandidate)) {
              String HSKLevel = chineseHSKReferenceDict.get(wordCandidate);
              subtitleWords.put(wordCandidate,HSKLevel);
              HSKCounts.put(HSKLevel, HSKCounts.get(HSKLevel) +1);
              HSKCounts.put("total", HSKCounts.get("total") + 1);
              chineseChars = Arrays.copyOfRange(chineseChars, i, chineseChars.length);
                break;
              } else if (i == 1) {
                subtitleWords.put(wordCandidate,"unknown");
              HSKCounts.put("unknown",HSKCounts.get("unknown") + 1);
              HSKCounts.put("total", HSKCounts.get("total") + 1);
              chineseChars = new char[0];
              break;
            } else {
              continue;
            }
          }
        }
      }
    }
    return subtitleWords;
  }

  public Hashtable<String, Integer> getHSKCounts() {
    return HSKCounts;
  }
}
