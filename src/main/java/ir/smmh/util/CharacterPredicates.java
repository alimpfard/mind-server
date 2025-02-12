package ir.smmh.util;

import java.util.function.Predicate;

public interface CharacterPredicates {
    Predicate<Character> VOWEL = c -> {
        switch (c) {
            case 'A':
            case 'E':
            case 'I':
            case 'O':
            case 'U':
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return true;
            default:
                return false;
        }
    };
}
