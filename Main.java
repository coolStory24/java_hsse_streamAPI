import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {
  private static class CaesarCipher {

    private static List<Integer> readCharCodesFromFile(String path) throws FileNotFoundException {

      List<Integer> charCodeList = new ArrayList<>();

      try (var fileReader = new FileReader(path)) {
        int charCode;

        while ((charCode = fileReader.read()) != -1) {
          charCodeList.add(charCode);
        }

        return charCodeList;
      } catch (IOException e) {
        throw new FileNotFoundException(String.format("File with path \"%s\" not found", path));
      }
    }

    private static List<Integer> encrypt(List<Integer> charCodeListToEncrypt, int encryptionKey) {
      return charCodeListToEncrypt.stream()
          .map(
              charCode -> {
                // numbers
                if (charCode >= 48 && charCode <= 57) {
                  return ((charCode + encryptionKey) - 48) % 10 + 48;
                }

                // upper case
                if (charCode >= 65 && charCode <= 90) {
                  return ((charCode + encryptionKey) - 65) % 26 + 65;
                }

                // lower case
                if (charCode >= 97 && charCode <= 122) {
                  return ((charCode + encryptionKey) - 97) % 26 + 97;
                }

                return charCode;
              })
          .toList();
    }

    private static List<Character> charCodeListToCharList(List<Integer> charCodeList) {
      return charCodeList.stream().map(code -> (char) code.intValue()).toList();
    }

    private static void writeToFile(List<Character> charList, String path)
        throws FileNotFoundException {
      try (FileWriter writer = new FileWriter(path)) {

        for (char character : charList) {
          writer.write(character);
        }

        writer.flush();
      } catch (IOException e) {
        throw new FileNotFoundException(String.format("File with path \"%s\" not found", path));
      }
    }

    public static void runEncryption(String inputPath, String outputPath, int key)
        throws FileNotFoundException {
      var chars = CaesarCipher.readCharCodesFromFile(inputPath);
      var encrypted = CaesarCipher.encrypt(chars, key);
      var encryptedChars = CaesarCipher.charCodeListToCharList(encrypted);

      CaesarCipher.writeToFile(encryptedChars, outputPath);
    }
  }

  private static final Logger logger = Logger.getLogger(Main.class.getSimpleName());
  private static final int NUMBER_OF_ARGUMENTS = 3;
  private static final String INPUT_PATH = "input/";
  private static final String OUTPUT_PATH = "output/";

  public static void main(String[] args) {
    if (args.length < NUMBER_OF_ARGUMENTS) {
      logger.warning(NUMBER_OF_ARGUMENTS + " arguments must be provided");
      return;
    }

    String inputFileName = args[0];
    int encryptionKey;
    String outputFileName = args[2];

    try {
      encryptionKey = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      logger.warning("Invalid format of encryption key (must be numeric)");
      return;
    }

    var inputPath = INPUT_PATH + inputFileName;
    var outputPath = OUTPUT_PATH + outputFileName;
    try {
      CaesarCipher.runEncryption(inputPath, outputPath, encryptionKey);
      logger.info("Successfully encrypted");
    } catch (FileNotFoundException e) {
      logger.warning(e.getMessage());
    }
  }
}
