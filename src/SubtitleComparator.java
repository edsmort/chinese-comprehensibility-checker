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
      String[] textBlocks = line.split(" "); //blocks of text in a line are sometimes separated with a space, we can assume that no single word will be created using characters either side of a space
      for (String textBlock : textBlocks) {
        char[] chineseChars = textBlock.toCharArray();
        while (chineseChars.length > 0) {
          for (int i = textBlocks.length - 1; i <= 0; i--) {
            char[] checkChars = Arrays.copyOfRange(chineseChars, 0, i);
            String wordCandidate = String.valueOf(checkChars);
            if (chineseWordsWithHSK.containsKey(wordCandidate)) {
              subtitleWords.put(wordCandidate,chineseWordsWithHSK.get(wordCandidate));
              chineseChars = Arrays.copyOfRange(chineseChars, i, chineseChars.length);
              break;
            } else if (i == 1) {
              subtitleWords.put(wordCandidate,"unknown");
            } else {
              continue;
            }
          }
        }
      }
    }
    return subtitleWords;
  }
}
