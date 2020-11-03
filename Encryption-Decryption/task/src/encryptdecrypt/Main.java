package encryptdecrypt;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        CommandArguments arguments = null;
        try {
            arguments = getCommandArguments(args);
        } catch (IOException e) {
            System.out.println("Error in processing arguments. Terminating");
            System.exit(0);
        }
        Algorithm algorithm = Algorithm.create(arguments.algorithm, arguments.key);
        final String output = algorithm.apply(arguments.data, arguments.mode);
        arguments.out.print(output);
        arguments.out.close();
    }

    private static CommandArguments getCommandArguments(String[] args) throws IOException {
        CommandArguments commandArguments = new CommandArguments();
        for (int i = 0; i < args.length; i += 2) {
            if ("-mode".equalsIgnoreCase(args[i])) {
                commandArguments.mode = args[i + 1];
            } else if ("-data".equalsIgnoreCase(args[i])) {
                commandArguments.data = args[i + 1];
            } else if ("-key".equalsIgnoreCase(args[i])) {
                commandArguments.key = Integer.parseInt(args[i + 1]);
            } else if ("-in".equalsIgnoreCase(args[i])) {
                if ("".equals(commandArguments.data)) {
                    String input = Files.readString(Paths.get(args[i + 1]));
                    System.out.println("input = " + input);
                    commandArguments.data = input;
                }
            } else if ("-out".equalsIgnoreCase(args[i])) {
                commandArguments.out = new PrintWriter(args[i + 1]);
            } else if ("-alg".equalsIgnoreCase(args[i])) {
                commandArguments.algorithm = args[i + 1];
            }
        }
        return commandArguments;
    }

    private static class CommandArguments {
        private String mode = "enc";
        private int key = 0;
        private String data = "";
        private PrintWriter out = new PrintWriter(System.out);
        private String algorithm = "shift";
    }

    public static abstract class Algorithm {
        private final int key;

        protected Algorithm(int key) {
            this.key = key;
        }

        public static Algorithm create(String type, int key) {
            if ("shift".equalsIgnoreCase(type)) {
                return new ShiftAlgorithm(key);
            } else if ("unicode".equalsIgnoreCase(type)) {
                return new UnicodeAlgorithm(key);
            } else {
                return null;
            }
        }

        public String apply(String input, String mode) {
            StringBuilder sb = new StringBuilder(input.length());
            final char[] chars = input.toCharArray();
            for (char c :
                    chars) {
                final char modified;
                if ("enc".equalsIgnoreCase(mode)) {
                    modified = encrypt(c);
                } else {
                    modified = decrypt(c);
                }
                sb.append(modified);
            }
            return sb.toString();
        }

        protected abstract char encrypt(char c);

        protected abstract char decrypt(char c);

        public final int getKey() {
            return key;
        }
    }

    public static class ShiftAlgorithm extends Algorithm {
        private static final int ALPHABET_SIZE = 26;

        protected ShiftAlgorithm(int key) {
            super(key);
        }

        @Override
        protected char encrypt(char c) {
            if (isAlphabet(c)) {
                final char min;
                if (isLowerAlphabet(c)) {
                    min = 'a';
                } else {
                    min = 'A';
                }
                return (char) ((c - min + getKey()) % ALPHABET_SIZE + min);
            } else {
                return c;
            }
        }

        private boolean isAlphabet(char c) {
            return isLowerAlphabet(c) || isUpperAlphabet(c);
        }

        private boolean isUpperAlphabet(char c) {
            return c >= 'A' && c <= 'Z';
        }

        private boolean isLowerAlphabet(char c) {
            return c >= 'a' && c <= 'z';
        }

        @Override
        protected char decrypt(char c) {
            if (isAlphabet(c)) {
                final char min;
                final char max;
                if (isLowerAlphabet(c)) {
                    min = 'a';
                    max = 'z';
                } else {
                    min = 'A';
                    max = 'Z';
                }
                int i = c - min - getKey();
                if (i >= 0) {
                    return (char) (min + i);
                } else {
                    return (char) (max + i + 1);
                }
            } else {
                return c;
            }
        }
    }

    public static class UnicodeAlgorithm extends Algorithm {

        protected UnicodeAlgorithm(int key) {
            super(key);
        }

        @Override
        protected char encrypt(char inputChar) {
            if (inputChar == Character.MIN_VALUE) {
                return inputChar;
            }
            final int size = Character.MAX_VALUE;
            return (char) ((inputChar - 1 + getKey()) % size + 1);
        }

        @Override
        protected char decrypt(char inputChar) {
            if (inputChar == Character.MIN_VALUE) {
                return inputChar;
            }
            int i = inputChar - getKey();
            if (i > 0) {
                return (char) i;
            } else {
                return (char) (Character.MAX_VALUE - i);
            }
        }
    }
}
