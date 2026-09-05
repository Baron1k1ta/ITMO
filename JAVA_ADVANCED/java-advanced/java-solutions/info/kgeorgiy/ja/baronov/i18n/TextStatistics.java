package info.kgeorgiy.ja.baronov.i18n;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.*;
import java.util.*;

public class TextStatistics {
    private static final String separator = System.lineSeparator();

    private static final String dotSeparator = "." + separator;
    private static final String dotSeparatorTab = dotSeparator + "\t";


    /**
     * Entry point of the TextStatistics application.
     * <p>
     * The program expects exactly four command-line arguments:
     * <ol>
     *     <li>Locale tag of the input text.</li>
     *     <li>Locale tag for generating report.</li>
     *     <li>Path to the input file containing the text.</li>
     *     <li>Path to the output file where the localized report will be saved.</li>
     * </ol>
     * <p>
     * The application analyzes the input text.
     *
     */
    public static void main(String[] args) {
        if(args == null || args.length != 4 || args[0] == null || args[1] == null || args[2] == null || args[3] == null){
            System.err.println("incorrect input");
            return;
        }
        try{

            Locale textLocale = Locale.forLanguageTag(args[0]);
            Locale outputLocale = Locale.forLanguageTag(args[1]);
            Path inputPath = Path.of(args[2]);
            Path outputPath = Path.of(args[3]);


            getStatistics(textLocale, outputLocale, inputPath, outputPath);

        }catch(IllformedLocaleException e){
            System.err.println("incorrect locale arguments " + e.getMessage());
        }
    }

    record Statistics<T, V>(
            int amount,
            int amountOfDifferent,
            T min,
            T max,
            String minLength,
            String maxLength,
            V average
    ) {
        public Statistics(int amount,
                          int amountOfDifferent,
                          T min,
                          T max,
                          V average) {
            this(amount,
                    amountOfDifferent,
                    min,
                    max,
                    null,
                    null,
                    average);
        }
    }

    private static void getStatistics(Locale textLocale, Locale outputLocale, Path inputPath, Path outputPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)){
            String text = Files.readString(inputPath);

            NumberFormat numberFormat = NumberFormat.getNumberInstance(outputLocale);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(outputLocale);
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, outputLocale);

            ResourceBundle bundle = ResourceBundle.getBundle("info.kgeorgiy.ja.baronov.i18n.bundle", outputLocale);

            final Statistics<String, Double> sentences = getLetterStatistics(textLocale, text, false);
            final Statistics<String, Double> words = getLetterStatistics(textLocale, text, true);
            final Statistics<Double, Double> numbers = getNumberStatistics(text, NumberFormat.getNumberInstance(textLocale));
            final Statistics<Double, Double> money = getNumberStatistics(text, NumberFormat.getCurrencyInstance(textLocale));
            final Statistics<Date, Date> dates = getDateStatistics(text, textLocale);


            writeHeaderStats(bundle, sentences.amount, words.amount, numbers.amount, money.amount, dates.amount,
                    numberFormat, inputPath, outputLocale, writer);
            writeSentencesStats(bundle, sentences, numberFormat, outputLocale, writer);
            writeWordsStats(bundle, words, numberFormat, outputLocale, writer);
            writeNumberStats(bundle, numbers, numberFormat, writer, outputLocale);
            writeMoneyStats(bundle, money, numberFormat, currencyFormat, writer);
            writeDateStats(bundle, dates, numberFormat, dateFormat, writer);

        } catch (IOException e) {
            System.err.println("Error while reading the file: " + e.getMessage());
        }

    }

    private static String changeSeparator(double number, Locale locale){
        if (locale.getLanguage().equals("ru")) {
            return String.valueOf(number).replace('.', ',');
        }
        return String.valueOf(number);
    }

    private static Statistics<Date, Date> getDateStatistics(String text, Locale locale) {

        List<DateFormat> formats = List.of(
                DateFormat.getDateInstance(DateFormat.SHORT,  locale),
                DateFormat.getDateInstance(DateFormat.MEDIUM, locale),
                DateFormat.getDateInstance(DateFormat.LONG,   locale),
                DateFormat.getDateInstance(DateFormat.FULL,   locale)
        );

        final List<Date> dates = new ArrayList<>();
        final TreeSet<Date> uniqueDates = new TreeSet<>();


        ParsePosition parsePosition = new ParsePosition(0);
        while (parsePosition.getIndex() < text.length()) {
            boolean parsed = false;
            for (DateFormat df : formats) {
                int start = parsePosition.getIndex();
                Date value = df.parse(text, parsePosition);
                if (value != null) {
                    dates.add(value);
                    uniqueDates.add(value);
                    parsed = true;
                    break;
                } else {
                    parsePosition.setIndex(start);
                }
            }

            if (!parsed) {
                parsePosition.setIndex(parsePosition.getIndex() + 1);
            }
        }

        return new Statistics<>(
                dates.size(),
                uniqueDates.size(),
                uniqueDates.isEmpty() ? null : uniqueDates.first(),
                uniqueDates.isEmpty() ? null : uniqueDates.last(),
                new Date ((long) dates.stream()
                        .mapToLong(Date::getTime)
                        .average()
                        .orElse(0L))
        );
    }



    private static Statistics<Double, Double> getNumberStatistics(String text, NumberFormat format) {
        final ParsePosition parsePosition = new ParsePosition(0);
        final List<Double> numbers = new ArrayList<>();
        final TreeSet<Double> uniqueNumbers = new TreeSet<>(Comparator.comparingDouble(Double::doubleValue));

        while (parsePosition.getIndex() < text.length()) {
            final Number value = format.parse(text, parsePosition);
            if (value == null) {
                int index = parsePosition.getIndex() + 1;
                parsePosition.setIndex(index);
                continue;
            }
            final double result = value.doubleValue();
            numbers.add(result);
            uniqueNumbers.add(result);

        }

        return new Statistics<>(
                numbers.size(),
                uniqueNumbers.size(),
                uniqueNumbers.isEmpty() ? 0.0d : uniqueNumbers.first(),
                uniqueNumbers.isEmpty() ? 0.0d : uniqueNumbers.last(),
                BigDecimal.valueOf(
                        numbers.stream()
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(0.0d)
                ).setScale(3, RoundingMode.HALF_UP).doubleValue()
        );

    }

    private static Statistics<String, Double> getLetterStatistics(
            Locale textLocale,
            String text,
            boolean isWord
    ) {

        List<String> letters;

        if (isWord){
            List<String> rawTokens = getTokens(text, BreakIterator.getWordInstance(textLocale));
            letters = rawTokens.stream()
                    .filter(s -> s.codePoints().anyMatch(Character::isLetter))
                    .toList();
        }else{
            letters = getTokens(text, BreakIterator.getSentenceInstance(textLocale));
        }

        TreeSet<String> unique = new TreeSet<>(Collator.getInstance(textLocale));
        unique.addAll(letters);

        return new Statistics<>(
                letters.size(),
                unique.size(),
                unique.isEmpty() ? "" : unique.first(),
                unique.isEmpty() ? "" : unique.last(),
                letters.stream().min(Comparator.comparingInt(String::length)).orElse(""),
                letters.stream().max(Comparator.comparingInt(String::length)).orElse(""),
                BigDecimal.valueOf(
                        letters.stream().mapToInt(String::length).average().orElse(0.0d)
                ).setScale(3, RoundingMode.HALF_UP).doubleValue()
        );
    }

    private static List<String> getTokens(String fileString, BreakIterator breakIterator) {
        List<String> tokens = new ArrayList<>();
        breakIterator.setText(fileString);

        int start = breakIterator.first();
        for (int end = breakIterator.next(); end != BreakIterator.DONE; start = end, end = breakIterator.next()) {
            String token = fileString.substring(start, end).replaceAll("\\s+", " ").trim();
            tokens.add(token);
        }

        return tokens;
    }


    private static void writeHeaderStats(ResourceBundle bundle, int sentences, int words, int numbers, int money, int dates, NumberFormat numberFormat, Path inputPath, Locale outputLocale, BufferedWriter writer){
        StringBuilder report = new StringBuilder();
        report.append(bundle.getString("file")).append(" \"").append(inputPath.getFileName()).append("\"");
        if (outputLocale.getLanguage().equals("ru")) {
            report.append(".");
        }
        report.append(separator);
        report.append(bundle.getString("summary")).append(separator).append("\t");
        report.append(bundle.getString("sentences")).append(" ").append(numberFormat.format(sentences)).append(dotSeparatorTab);
        report.append(bundle.getString("words")).append(" ").append(numberFormat.format(words)).append(dotSeparatorTab);
        report.append(bundle.getString("numbers")).append(" ").append(numberFormat.format(numbers)).append(dotSeparatorTab);
        report.append(bundle.getString("sums")).append(" ").append(numberFormat.format(money)).append(dotSeparatorTab);
        report.append(bundle.getString("dates")).append(" ").append(numberFormat.format(dates)).append(dotSeparator);

        try {
            writer.write(report.toString());
        } catch (IOException e) {
            System.err.println("Error while writing sentence stats" + e.getMessage());
        }
    }

    private static void writeSentencesStats(ResourceBundle bundle, Statistics<String, Double> sentences, NumberFormat numberFormat, Locale outputLocale, BufferedWriter writer){
        StringBuilder report = new StringBuilder();
        report.append(bundle.getString("sentences_stat")).append(separator).append("\t");
        report.append(bundle.getString("sentences")).append(" ").append(numberFormat.format(sentences.amount)).append(" (").append(numberFormat.format(sentences.amountOfDifferent))
                .append(" ").append(bundle.getString("different")).append(")").append(dotSeparator);
        if (sentences.amount != 0){
            report.append("\t").append(bundle.getString("minimum_sentence")).append(" \"").append(sentences.min).append("\"").append(dotSeparatorTab);
            report.append(bundle.getString("maximum_sentence")).append(" \"").append(sentences.max).append("\"").append(dotSeparatorTab);
            report.append(bundle.getString("minimum_sentence_length")).append(" ").append(sentences.minLength.length()).append(" (\"").append(sentences.minLength).append("\")").append(dotSeparatorTab);
            report.append(bundle.getString("maximum_sentence_length")).append(" ").append(sentences.maxLength.length()).append(" (\"").append(sentences.maxLength).append("\")").append(dotSeparatorTab);
            report.append(bundle.getString("average_sentence_length")).append(" ").append(changeSeparator(sentences.average, outputLocale)).append(dotSeparator);
        }
        try {
            writer.write(report.toString());
        } catch (IOException e) {
            System.err.println("Error while writing sentence stats" + e.getMessage());
        }
    }


    private static void writeWordsStats(ResourceBundle bundle, Statistics<String, Double> words, NumberFormat numberFormat, Locale outputLocale, BufferedWriter writer){
        StringBuilder report = new StringBuilder();
        report.append(bundle.getString("words_stat")).append(separator).append("\t");
        report.append(bundle.getString("words")).append(" ").append(numberFormat.format(words.amount)).append(" (").append(numberFormat.format(words.amountOfDifferent)).append(" ").append(bundle.getString("different")).append(")").append(dotSeparator);
        if (words.amount != 0){
            report.append("\t").append(bundle.getString("minimum_word")).append(" \"").append(words.min).append("\"").append(dotSeparatorTab);
            report.append(bundle.getString("maximum_word")).append(" \"").append(words.max).append("\"").append(dotSeparatorTab);
            report.append(bundle.getString("minimum_word_length")).append(" ").append(words.minLength.length()).append(" (\"").append(words.minLength).append("\")").append(dotSeparatorTab);
            report.append(bundle.getString("maximum_word_length")).append(" ").append(words.maxLength.length()).append(" (\"").append(words.maxLength).append("\")").append(dotSeparatorTab);
            report.append(bundle.getString("average_word_length")).append(" ").append(changeSeparator(words.average, outputLocale)).append(dotSeparator);
        }
        try {
            writer.write(report.toString());
        } catch (IOException e) {
            System.err.println("Error while writing sentence stats" + e.getMessage());
        }
    }

    private static void writeNumberStats(ResourceBundle bundle, Statistics<Double, Double> numbers, NumberFormat numberFormat, BufferedWriter writer, Locale outputLocale){
        StringBuilder report = new StringBuilder();
        report.append(bundle.getString("numbers_stat")).append(separator).append("\t");
        report.append(bundle.getString("numbers")).append(" ").append(numberFormat.format(numbers.amount)).append(" (").append(numberFormat.format(numbers.amountOfDifferent)).append(" ").append(bundle.getString("different")).append(")").append(dotSeparator);
        if (numbers.amount != 0){
            report.append("\t").append(bundle.getString("minimum_number")).append(" ").append(changeSeparator(numbers.min, outputLocale)).append(dotSeparatorTab);
            report.append(bundle.getString("maximum_number")).append(" ").append(changeSeparator(numbers.max, outputLocale)).append(dotSeparatorTab);
            report.append(bundle.getString("average_number")).append(" ").append(changeSeparator(numbers.average, outputLocale)).append(dotSeparator);
        }
        try {
            writer.write(report.toString());
        } catch (IOException e) {
            System.err.println("Error while writing sentence stats" + e.getMessage());
        }
    }

    private static void writeMoneyStats(ResourceBundle bundle, Statistics<Double, Double> money, NumberFormat numberFormat, NumberFormat currencyFormat,BufferedWriter writer){
        StringBuilder report = new StringBuilder();
        report.append(bundle.getString("sum_stat")).append(separator).append("\t");
        report.append(bundle.getString("sums")).append(" ").append(numberFormat.format(money.amount)).append(" (").append(numberFormat.format(money.amountOfDifferent)).append(" ").append(bundle.getString("different")).append(")").append(dotSeparator);
        if (money.amount != 0){
            report.append("\t").append(bundle.getString("minimum_sum")).append(" ").append(currencyFormat.format(money.min)).append(dotSeparatorTab);
            report.append(bundle.getString("maximum_sum")).append(" ").append(currencyFormat.format(money.max)).append(dotSeparatorTab);
            report.append(bundle.getString("average_sum")).append(" ").append(currencyFormat.format(money.average)).append(dotSeparator);
        }
        try {
            writer.write(report.toString());
        } catch (IOException e) {
            System.err.println("Error while writing sentence stats" + e.getMessage());
        }
    }

    private static void writeDateStats(ResourceBundle bundle, Statistics<Date, Date> dates, NumberFormat numberFormat, DateFormat dateFormat,BufferedWriter writer){
        StringBuilder report = new StringBuilder();
        report.append(bundle.getString("dates_stat")).append(separator).append("\t");
        report.append(bundle.getString("dates")).append(" ").append(numberFormat.format(dates.amount)).append(" (").append(numberFormat.format(dates.amountOfDifferent)).append(" ").append(bundle.getString("different")).append(").");
        if (dates.amount != 0){
            report.append(separator).append("\t").append(bundle.getString("minimum_date")).append(" ").append(dates.min == null ? "—" : dateFormat.format(dates.min)).append(dotSeparatorTab);
            report.append(bundle.getString("maximum_date")).append(" ").append(dates.max == null ? "—" : dateFormat.format(dates.max)).append(dotSeparatorTab);
            report.append(bundle.getString("average_date")).append(" ").append(dateFormat.format(dates.average)).append(dotSeparator);
        }
        try {
            writer.write(report.toString());
        } catch (IOException e) {
            System.err.println("Error while writing sentence stats" + e.getMessage());
        }
    }

}


