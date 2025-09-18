package com.example.backend.common;

public class HangulUtils {

    private static final char[] CHOSUNG_LIST = {
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    public static String getChosung(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder chosung = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= '가' && c <= '힣') {
                int chosungIndex = (c - '가') / (21 * 28);
                chosung.append(CHOSUNG_LIST[chosungIndex]);
            } else {
                chosung.append(c);
            }
        }
        return chosung.toString();
    }
    public static boolean isMixedMatch(String fullText, String mixedKeyword) {
        if (fullText == null || mixedKeyword == null || fullText.length() < mixedKeyword.length()) {
            return false;
        }

        for (int i = 0; i < mixedKeyword.length(); i++) {
            char keywordChar = mixedKeyword.charAt(i);
            char fullTextChar = fullText.charAt(i);

            if (isChosung(keywordChar)) { // 검색어가 초성일 경우 (예: 'ㅇ')
                // 후보 문자의 초성과 일치하는지 확인
                if (getChosung(String.valueOf(fullTextChar)).charAt(0) != keywordChar) {
                    return false;
                }
            } else {
                if (fullTextChar != keywordChar) {
                    return false;
                }
            }
        }
        return true;
    }

    // 주어진 문자가 초성인지 확인하는 private 헬퍼 메서드
    private static boolean isChosung(char c) {
        for (char chosung : CHOSUNG_LIST) {
            if (c == chosung) {
                return true;
            }
        }
        return false;
    }
}
