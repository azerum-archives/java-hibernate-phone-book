package phonebook.view;

import org.springframework.lang.Nullable;
import java.util.*;

public class Console {
    private final Scanner scanner;

    public Console() {
        scanner = new Scanner(System.in);
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void pressEnterToContinue() {
        System.out.print("Press Enter to continue > ");
        scanner.nextLine();

        System.out.println();
    }

    @Nullable
    public <TValue> TValue select(
        @Nullable String nullLabel,
        List<TValue> values,
        String prompt
    ) {
        LinkedHashMap<String, TValue> labelsToValues = new LinkedHashMap<>();

        for (TValue v : values) {
            labelsToValues.put(v.toString(), v);
        }

        return select(nullLabel, labelsToValues, prompt);
    }

    @Nullable
    public <TValue> TValue select(
        @Nullable String nullLabel,
        LinkedHashMap<String, TValue> labelsToValues,
        String prompt
    ) {
        List<String> labels = labelsToValues.keySet().stream().toList();

        if (nullLabel != null) {
            System.out.println("0. " + nullLabel);
        }

        list(labels, 1);

        int choice = readIntBetween(0, labels.size(), prompt);
        System.out.println();

        if (choice == 0) {
            return null;
        }

        String chosenLabel = labels.get(choice - 1);
        return labelsToValues.get(chosenLabel);
    }

    public <T> void list(List<T> items, int startIndex) {
        for (int i = 0; i < items.size(); ++i) {
            System.out.println((startIndex + i) + ". " + items.get(i));
        }

        System.out.println();
    }

    public int readIntBetween(int min, int max, String prompt) {
        int i;

        while (true) {
            System.out.print(prompt);

            try {
                i = Integer.parseInt(scanner.nextLine());

                if (i >= min && i <= max) {
                    break;
                }
            }
            catch (NumberFormatException ignored) {}
        }

        return i;
    }
}
