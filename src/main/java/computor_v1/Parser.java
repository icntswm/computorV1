package computor_v1;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Parser {
    private static Set<String> equation = new LinkedHashSet<>();
    private static Set<Double> numbersFromEquation = new LinkedHashSet<>();
    private static String searchVariable = null;

    public Set<String> getEquation() {
        return equation;
    }
    private void equationElementProcessing(String str, String turn) {
        for (int i = 1; i < str.length(); ++i) {
            int start = i - 1;
            while (i < str.length() && str.charAt(i) != '-' && str.charAt(i) != '+')
                i++;
            if (turn.equals("left")) {
                if (start == 0 && str.charAt(0) != '-')
                    equation.add("+" + str.substring(start, i));
                else
                    if (str.charAt(start) == '-' && equation.contains("+" + str.substring(start + 1, i)))
                        equation.remove("+" + str.substring(start + 1, i));
                    else if (str.charAt(start) == '+' && equation.contains("-" + str.substring(start + 1, i)))
                        equation.remove("-" + str.substring(start + 1, i));
                    else
                        equation.add(str.substring(start, i));
            } else if (turn.equals("right")) {
                if (str.charAt(start) != '-' && str.charAt(start) != '+')
                    if (equation.contains("+" + str.substring(start, i)))
                        equation.remove("+" + str.substring(start, i));
                    else
                        equation.add("-" + str.substring(start, i));
                else if (str.charAt(start) == '+')
                    if (equation.remove(str.substring(start, i)))
                        equation.remove(str.substring(start, i));
                    else
                        equation.add("-" + str.substring(start + 1, i));
                else if (str.charAt(start) == '-') {
                    if (equation.contains(str.substring(start, i)))
                        equation.remove(str.substring(start, i));
                    else
                        equation.add("+" + str.substring(start + 1, i));
                }
            }
        }
    }
    private String removingSpacesFromEquation(String str) {
        String[] woSpace = str.split(" ");
        StringBuilder newStr = new StringBuilder("");
        for (String o : woSpace) {
            newStr.append(o);
        }
        return newStr.toString();
    }
    //checkingForAMatchingCharacter
    private void checkingElementsOfEquationForm() throws WrongEquationException, NumberFormatException {
  //      checkingForAMatchingCharacter(equation.iterator().next());
        for (String elem : equation) {
            if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
                throw new WrongEquationException("Неправильный символ перед элементом уравнения '" + elem + "' (поддерживаются '-' и '+')");
            String number = null;
            int i = 1;
            System.out.println("elem: " + elem);
            for (; i < elem.length() && elem.charAt(i) != '*'; i++);
            number = elem.substring(1, i++);
            int start = i;
            if (number == null)
                throw new WrongEquationException("Несоответствие форме: a * x^p");
            else
                numbersFromEquation.add(Double.parseDouble(number));
            for (; i < elem.length() && elem.charAt(i) != '^'; i++);
            if (searchVariable == null)
                searchVariable = elem.substring(start, i);
            else
                if (!searchVariable.equals(elem.substring(start, i)))
                    throw new WrongEquationException("Несоответствие имен искомых переменных");
            String coeff = elem.substring(i + 1);
            if (!coeff.equals("1") && !coeff.equals("2") && !coeff.equals("0"))
                throw new WrongEquationException("Степень числа можнет быть только целым числом в диапазоне от 0 до 2 включительно");
        }
    }
    public void parsingStart(String[] strArg) {

        try {
            if (strArg.length != 2)
                throw new WrongEquationException("Уравнение должно иметь вид: A = B (Недопустимо: A, A = B = C и тп.)");
            equationElementProcessing(removingSpacesFromEquation(strArg[0].trim()), "left");
            equationElementProcessing(removingSpacesFromEquation(strArg[1].trim()), "right");
            checkingElementsOfEquationForm();
        } catch (WrongEquationException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("Ошибка: В уравнении есть число, в котором имеются буквы");
            System.exit(1);
        }
        System.out.println(equation);

    }
}
