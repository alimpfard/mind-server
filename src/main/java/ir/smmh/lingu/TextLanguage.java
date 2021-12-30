package ir.smmh.lingu;

public class TextLanguage extends Language {

    private static TextLanguage singleton;

    public static TextLanguage singleton() {
        if (singleton == null) {
            singleton = new TextLanguage();
        }
        return singleton;
    }

    public TextLanguage() {
        super("Text Language", "txt", TokenizerMaker.singleton().maker.makeFrom("text-language"));
    }
}
