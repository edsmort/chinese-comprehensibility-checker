package com.github.edsmort;

import java.io.*;
import java.util.*;

public class SubtitleComparator {
  private Map<String, String> chineseHSKReferenceDict;
  private Map<String, Integer> HSKCounts;
  
  public SubtitleComparator() {
    chineseHSKReferenceDict = populateChineseHSKReferenceDict();
    HSKCounts = initHSKCounts();
  }

  private Map<String, Integer> initHSKCounts() {
    Map<String, Integer> counts = new HashMap<String, Integer>();
    for (int i = 1; i < 10; i++) {
      counts.put(String.valueOf(i), 0);
    }
    counts.put("unknown", 0);
    counts.put("total", 0);
    return counts;
  }

  private Map<String, String> populateChineseHSKReferenceDict() {
    Map<String, String> wordsHSK = new HashMap<>();
    InputStream inputStream = getClass().getResourceAsStream("/dict_hsk.csv");
    try (Scanner myReader = new Scanner(inputStream)) {
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        String[] splitData = data.split(",");
        wordsHSK.put(splitData[0],splitData[1]);
      }
    } catch (Exception e) {
      System.out.println("dict_hsk.csv file not in src/main/resources");
      System.err.println(e);
    }
    return wordsHSK;
  }

  public Map<String, String> compareKnownWordsToSubtitle(List<String> knownWords, Map<String, String> subtitleWords) {
    for (String known : knownWords) {
      if (subtitleWords.get(known) != null) subtitleWords.remove(known);
    }
    return subtitleWords;
  }

  //TODO: refactor this and separate out some logic
  public Map<String, String> findWords(List<String> subtitles) {
    Map<String, String> subtitleWords = new HashMap<>();
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

  public Map<String, Integer> getHSKCounts() {
    return HSKCounts;
  }
}
