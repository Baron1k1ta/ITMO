package info.kgeorgiy.ja.baronov.walk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Walk {
    private static final int HASH_SUM_CONST_SHA_256 = 8;
    private static final int BUFFER_CAPACITY = 2048;
    private static final int NUM = 16;
    private static final String ALGORITHM = "SHA-256";
    private static final String ZERO_HASH = "0".repeat(NUM);
    private static final String LINE_SEPARATOR = System.lineSeparator();


    private static String calculateHash(Path path) {
        try {
            MessageDigest hashBuilder = MessageDigest.getInstance(ALGORITHM);
            byte[] buffer = new byte[BUFFER_CAPACITY];

            try (InputStream stream = Files.newInputStream(path)) {
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    hashBuilder.update(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                System.err.println("Error: incorrect stream read " + e.getMessage());
                return ZERO_HASH;
            }

            byte[] hash = hashBuilder.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < HASH_SUM_CONST_SHA_256; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error: incorrect algorithm " + e.getMessage());
            return ZERO_HASH;
        }
    }


    private static void walk(String input, String output) {
        try {
            Path inputPath = Path.of(input);
            Path outputPath = Path.of(output);

            if (outputPath.getParent() != null) {
                try {
                    Files.createDirectories(outputPath.getParent());
                } catch (IOException e) {
                    System.err.println("ERROR: IOException can't create folder for output file: " + e.getMessage());
                    return;
                }
            }
            try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
                try(BufferedWriter writer = Files.newBufferedWriter(outputPath)){
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {
                            Path filePath = Path.of(line);
                            String hash = calculateHash(filePath);
                            writer.write(String.format("%s %s" + LINE_SEPARATOR, hash, line));
                        } catch (InvalidPathException | IOException e) {
                            writer.write(String.format("%s %s" + LINE_SEPARATOR, ZERO_HASH, line));
                        }
                    }
                }catch (IOException e) {
                    System.err.println("Error: I/O exception occurred in writer. " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("Error: I/O exception occurred in reader. " + e.getMessage());
            }
        } catch (InvalidPathException e) {
            System.err.println("Error: invalid path names. " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Error: path names are null. " + e.getMessage());
        }
    }

    private static boolean argumentsCheck(String[] args){
        if (args == null) {
            System.err.println("Error: arguments array is null");
            return false;
        }else if (args.length != 2) {
            System.err.println("Error: required exactly 2 arguments");
            return false;
        }else if(args[0] == null|| args[1] == null){
            System.err.println("Error: arguments array is null or empty");
            return false;
        }
        return true;
    }
    public static void main(String[] args){
        if (argumentsCheck(args)){
            walk(args[0], args[1]);
        }
    }
}




