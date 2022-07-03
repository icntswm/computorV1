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
    private void checkingTwoCharactersSideBySide(int i, String str) throws WrongEquationException {
        if (i + 1 < str.length() && ((str.charAt(i) == '-' || str.charAt(i) == '+') && (str.charAt(i + 1) == '-' || str.charAt(i + 1) == '+')))
            throw new WrongEquationException("Два подряд идущих символа (" + str.charAt(i) + str.charAt(i + 1) + ")");
    }
    private void equationElementProcessing(String str, String turn) throws WrongEquationException {
        System.out.println("STRING: " + str);
        for (int i = 1; i < str.length(); ++i) {
            int start = i - 1;
            checkingTwoCharactersSideBySide(start, str);
            while (i < str.length() && str.charAt(i) != '-' && str.charAt(i) != '+') {
                i++;
                checkingTwoCharactersSideBySide(i, str);
            }
            System.out.println("----> " + str.substring(start, i));
            if (turn.equals("left")) {
                if (start == 0 && str.charAt(0) != '-')
                    if (str.charAt(0) != '+')
                        equation.add("+" + str.substring(start, i));
                    else
                        equation.add(str.substring(start, i));
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
    private Boolean firstVersionFormElementEquation(String elem) { // A*X^P
        System.out.println("firstVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        int i = 1;
        while (i < elem.length() && elem.charAt(i) != '*')
            ++i;
        try {
            double checkVar = Double.parseDouble(elem.substring(1, i));
        } catch (NumberFormatException e) {
            return false;
        }
        int start = ++i;
        while (i < elem.length() && elem.charAt(i) != '^')
            ++i;
        if (searchVariable == null) {
            searchVariable = elem.substring(start, i);
            if (searchVariable.equals(""))
                return false;
        }
        else
            if (!searchVariable.equals(elem.substring(start, i)))
                return false;
        try {
            int checkVar = Integer.parseInt(elem.substring(++i));
            if (checkVar < 0)
                return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    private Boolean secondVersionFormElementEquation(String elem) { // X
        System.out.println("secondVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        if (searchVariable == null) {
            searchVariable = elem.substring(1);
            if (searchVariable.equals(""))
                return false;
        } else
            if (!searchVariable.equals(elem.substring(1)))
                return false;
        return true;
    }
    private Boolean thirdVersionFormElementEquation(String elem) { // X*X*X...
        System.out.println("thirdVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        int i = 1;
        int start = 1;
        while (i < elem.length()) {
            while (i < elem.length() && elem.charAt(i) != '*')
                ++i;
            if (searchVariable == null) {
                searchVariable = elem.substring(start, i);
                if (searchVariable.equals(""))
                    return false;
            } else
                if (!searchVariable.equals(elem.substring(start, i)))
                    return false;
            start = ++i;
        }
        return true;
    }

    private Boolean fourthVersionFormElementEquation(String elem) { // X^P
        System.out.println("fourthVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        int i = 1;
        while (i < elem.length() && elem.charAt(i) != '^')
            ++i;
        if (searchVariable == null) {
            searchVariable = elem.substring(1, i);
            if (searchVariable.equals(""))
                return false;
        }
        else
            if (!searchVariable.equals(elem.substring(1, i)))
                return false;
        try {
            int checkVar = Integer.parseInt(elem.substring(++i));
            if (checkVar < 0)
                return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    private void checkingElementsOfEquationForm() throws WrongEquationException{
        for (String elem : equation) {
            if (!firstVersionFormElementEquation(elem)
                    && !secondVersionFormElementEquation(elem)
                    && !thirdVersionFormElementEquation(elem)
                    && !fourthVersionFormElementEquation(elem))
                throw new WrongEquationException("Элемент уравнения '" + elem + "' не подходит ни под одну из допустимых форм записи (X, X*X, X^P, A*X^P)");
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
        }
        System.out.println(equation);

    }
}
