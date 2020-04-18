package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;

abstract class Algorithm {
    abstract protected String encrypt(String data, int step);

    abstract protected String decrypt(String data, int step);
}

class ShiftAlgorithm extends Algorithm {
    @Override
    protected String encrypt(String data, int key) {
        StringBuilder encodedString = new StringBuilder();
        char encodedChar;
        for (char c : data.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                encodedChar = (char) ((c - 'a' + key) / ('z' - 'a' + 1));
            } else if (c >= 'A' && c <= 'Z') {
                encodedChar = (char) ((c - 'A' + key) / ('Z' - 'A' + 1));
            } else {
                encodedChar = c;
            }
            encodedString.append(encodedChar);
        }
        return encodedString.toString();
    }

    @Override
    protected String decrypt(String data, int key) {
        StringBuilder dedcodedString = new StringBuilder();
        int orderChar;
        for (char c : data.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                orderChar = c - 'a' - key;
            } else if (c >= 'A' && c <= 'Z') {
                orderChar = c - 'A' - key;
            } else {
                orderChar = c;
            }

            if (orderChar < 0) {
                orderChar += ('z' - 'a' + 1) * (int) (Math.round(Math.abs(orderChar) / 26.0) + 1);
            }
            dedcodedString.append((char) (orderChar + 'a'));
        }
        return dedcodedString.toString();
    }
}

class UnicodeAlgorithm extends Algorithm {
    @Override
    protected String encrypt(String data, int key) {
        StringBuilder encodedString = new StringBuilder();
        for (char c: data.toCharArray()) {
            encodedString.append((char) (c + key));
        }
        return encodedString.toString();
    }

    @Override
    protected String decrypt(String data, int key) {
        StringBuilder decodedString = new StringBuilder();
        for (char c: data.toCharArray()) {
            decodedString.append((char) (c - key));
        }
        return decodedString.toString();
    }
}

class AlgorithmFactory {
    Algorithm initiateAlgorithm(String type) {
        switch (type) {
            case "shift":
                return new ShiftAlgorithm();
            case "unicode":
                return new UnicodeAlgorithm();
            default:
                return null;
        }
    }

    Algorithm orderAlgorithm(String type) {
        Algorithm algo = initiateAlgorithm(type);
        if (algo == null) {
            System.out.println("Sorry, we are not able to initiate this algorithm");
            return null;
        }
        return algo;
    }
}

public class DecryptEncrypt {
    public static String readFileAsString(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void main(String[] args) {
        int key = 0;
        String mode = "enc";
        String data = "";
        String in = null, out = null;
        String algo = "shift";

        for (String arg: args) {
            int index = Arrays.asList(args).indexOf(arg);
            switch (arg) {
                case "-key":
                    key = Integer.parseInt(args[index + 1]);
                    break;
                case "-mode":
                    mode = args[index + 1];
                    break;
                case "-data":
                    data = args[index + 1];
                    break;
                case "-in":
                    in = args[index + 1];
                    break;
                case "-out":
                    out = args[index + 1];
                    break;
                case "-algo":
                    algo = args[index + 1];
                    break;
            }
        }

        AlgorithmFactory algoFactory = new AlgorithmFactory();
        Algorithm selectedAlgo = algoFactory.orderAlgorithm(algo);

        String result;

        if (data.equals("") && in != null) {
            try {
                data = readFileAsString(in);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        if (mode.equals("dec")) {
            result = selectedAlgo.decrypt(data, key);
        } else {
            result = selectedAlgo.encrypt(data, key);
        }

        if (out == null) {
            System.out.println(result);
        } else {
            File file = new File(out);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(result);
            } catch (IOException e) {
                System.out.println("An exception occur" + e.getMessage());
            }
        }
    }
}