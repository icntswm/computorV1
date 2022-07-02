package computor_v1;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        if (args.length == 0) {
            System.err.println("Ошибка: Отсутствует уравнение");
            System.exit(1);
        }
        parser.parsingStart(args[0].split("="));
    }
}




/*
Examples:

5 * X^0 + 4 * X^1 - 9.3 * X^2 = 1 * X^0
5 * X^0 + 4 * X^1 = 4 * X^0
8 * X^0 - 6 * X^1 + 0 * X^2 - 5.6 * X^3 = 3 * X^0
42∗X^0 = 42∗X^0


"-5 * X^0 + 4 * X^1 +5 * X^0 - 9.3 * X^2 + 2 * X^3 - 1 * X^0= - 1 * X^0 +2 * X^3"
*/