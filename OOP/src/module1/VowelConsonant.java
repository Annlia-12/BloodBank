package module1;

import java.util.Scanner;

public class VowelConsonant {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a character: ");
        char ch = sc.next().toLowerCase().charAt(0);

        switch (ch) {
            case 'a': case 'e': case 'i': case 'o': case 'u':
                System.out.println("It's a vowel.");
                break;
            default:
                if (Character.isLetter(ch))
                    System.out.println("It's a consonant.");
                else
                    System.out.println("Not an alphabet.");
        }
    }
}

