package packJdbc;

import java.util.Scanner;

public class ScannerWork {
    Scanner scanner = new Scanner(System.in);

    public char wybierzChar() {
        System.out.println();
        System.out.println("Podaj znak:");
        char znak = scanner.next().charAt(0);
        return  znak;
    }

    public String getString() {
        System.out.println();
        System.out.println("Podaj tekst:");
        String name = "";
        return name = scanner.next();
    }

    public int getInt() {
        System.out.println();
        System.out.println("Podaj liczbę int:");
        int liczba = 0;
        return liczba = scanner.nextInt();
    }

    public double getDouble() {
        System.out.println();
        System.out.println("Podaj liczbę double:");
        double liczbad = 0.0;
        return liczbad = scanner.nextDouble();
    }

    public boolean getBoolean() {
        System.out.println();
        System.out.println("Podaj boolean:");
        String tekstBoolean = scanner.next();
        boolean flag = false;
        flag = Boolean.parseBoolean(tekstBoolean);
        return flag;
    }
}
