package computor_v1;

import javax.swing.plaf.BorderUIResource;
import java.util.*;

public class Parser {
    private static LinkedList<String> equation = new LinkedList<>();
    private static HashMap<Integer, LinkedList<Double>> numbers = new HashMap<>();
    private static String searchVariable = null;
    private static HashMap<Integer, Double> finalEquation = new HashMap<>();
    private static List<Double> decisionResult = new ArrayList<>();
    private static int polynomialDegree = 0;

    public LinkedList<String> getEquation() {
        return equation;
    }
    private void checkingTwoCharactersSideBySide(int i, String str) throws WrongEquationException {
        if (i + 1 < str.length() && ((str.charAt(i) == '-' || str.charAt(i) == '+') && (str.charAt(i + 1) == '-' || str.charAt(i + 1) == '+')))
            throw new WrongEquationException("Два подряд идущих символа (" + str.charAt(i) + str.charAt(i + 1) + ")");
    }
    private void equationElementProcessing(String str, String turn) throws WrongEquationException {
        for (int i = 1; i < str.length(); ++i) {
            int start = i - 1;
            checkingTwoCharactersSideBySide(start, str);
            while (i < str.length() && str.charAt(i) != '-' && str.charAt(i) != '+') {
                i++;
                checkingTwoCharactersSideBySide(i, str);
            }
            if (turn.equals("left")) {
                if (start == 0 && str.charAt(0) != '-')
                    if (str.charAt(0) != '+')
                        equation.add("+" + str.substring(start, i));
                    else
                        equation.add(str.substring(start, i));
                else {
                    if (str.charAt(start) == '-' && equation.contains("+" + str.substring(start + 1, i))) {
                        System.out.println("1");
                        equation.remove("+" + str.substring(start + 1, i));
                    } else if (str.charAt(start) == '+' && equation.contains("-" + str.substring(start + 1, i))) {
                        System.out.println("2");
                        equation.remove("-" + str.substring(start + 1, i));
                    } else {
                        equation.add(str.substring(start, i));
                    }
                }
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
//        System.out.println("firstVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        String result = String.valueOf(elem.charAt(0));
        int i = 1;
        while (i < elem.length() && elem.charAt(i) != '*')
            ++i;
        try {
            double checkVar = Double.parseDouble(elem.substring(1, i));
            result += String.valueOf(checkVar);
        } catch (NumberFormatException e) {
            return false;
        }
        int start = ++i;
        while (i < elem.length() && elem.charAt(i) != '^')
            ++i;
        if (i - start <= 0)
            return false;
        if (searchVariable == null) {
            searchVariable = elem.substring(start, i);
            if (searchVariable.equals(""))
                return false;
        }
        else
            if (!searchVariable.equals(elem.substring(start, i)))
                return false;
        try {
            if (i + 1 > elem.length())
                return false;
            int checkVar = Integer.parseInt(elem.substring(++i));
            if (checkVar < 0 || checkVar > 2)
                return false;
            if (numbers.containsKey(checkVar)) {
                LinkedList<Double> newList = numbers.get(checkVar);
                newList.add(Double.parseDouble(result));
                numbers.put(checkVar, newList);
            } else {
                String finalResult = result;
                numbers.put(checkVar, new LinkedList<>(){{add(Double.parseDouble(finalResult));}});
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    private Boolean fifthVersionFormElementEquation(String elem) { // 5
//        System.out.println("fifthVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        String result = String.valueOf(elem.charAt(0));
        if (numbers.containsKey(0)) {
            double num = 0;
            try {
                num = Double.parseDouble(elem.substring(1));
            } catch (NumberFormatException e) {
                return false;
            }
            LinkedList<Double> newList = numbers.get(0);
            newList.add(Double.parseDouble(result + num));
            numbers.put(0, newList);
        } else {
            double num = 0;
            try {
                num = Double.parseDouble(elem.substring(1));
            } catch (NumberFormatException e) {
                return false;
            }
            String finalResult = result + num;
            numbers.put(0, new LinkedList<>(){{add(Double.parseDouble(finalResult));}});
        }
        return true;
    }
    private Boolean secondVersionFormElementEquation(String elem) { // X
//        System.out.println("secondVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        String result = String.valueOf(elem.charAt(0));
        int i = 1;
        while (i < elem.length() && elem.charAt(i) >= '0' && elem.charAt(i) <= '9')
            ++i;
        if (i != 1)
            return false;
        if (searchVariable == null) {
            searchVariable = elem.substring(1);
            if (searchVariable.equals(""))
                return false;
        } else
            if (!searchVariable.equals(elem.substring(1)))
                return false;
        if (numbers.containsKey(1)) {
            LinkedList<Double> newList = numbers.get(1);
            newList.add(Double.parseDouble(result + 1));
            numbers.put(1, newList);
        } else {
            String finalResult = result + 1;
            numbers.put(1, new LinkedList<>(){{add(Double.parseDouble(finalResult));}});
        }
        return true;
    }
    private Boolean thirdVersionFormElementEquation(String elem) { // X*X*X...
//        System.out.println("thirdVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        String result = String.valueOf(elem.charAt(0));
        int counter = 0;
        int i = 1;
        while (i < elem.length() && elem.charAt(i) >= '0' && elem.charAt(i) <= '9')
            ++i;
        if (i != 1)
            return false;
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
            ++counter;
        }
        if (numbers.containsKey(1)) {
            LinkedList<Double> newList = numbers.get(1);
            newList.add(Double.parseDouble(result + counter));
            numbers.put(1, newList);
        } else {
            String finalResult = result + counter;
            numbers.put(1, new LinkedList<>(){{add(Double.parseDouble(finalResult));}});
        }
        return true;
    }
    private Boolean sixthVersionFormElementEquation(String elem) { // 28*X
//        System.out.println("sixthVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        String result = String.valueOf(elem.charAt(0));
        int counter = 0;
        int i = 1;
        StringBuilder num = new StringBuilder();
        while (i < elem.length() && elem.charAt(i) >= '0' && elem.charAt(i) <= '9') {
            num.append(elem.charAt(i));
            ++i;
            if (elem.charAt(i) == '.')
                num.append(elem.charAt(i++));
        }
        if (i != 1 && elem.charAt(i) != '*')
            return false;
        int start = ++i;
        if (searchVariable == null) {
            if (start > elem.length())
                return false;
            searchVariable = elem.substring(start);
            if (searchVariable.equals(""))
                return false;
        } else
            if (!searchVariable.equals(elem.substring(start)))
                return false;
        if (numbers.containsKey(1)) {
            LinkedList<Double> newList = numbers.get(1);
            newList.add(Double.parseDouble(result + Double.parseDouble(String.valueOf(num))));
            numbers.put(1, newList);
        } else {
            String finalResult = result + Double.parseDouble(String.valueOf(num));
            numbers.put(1, new LinkedList<>(){{add(Double.parseDouble(finalResult));}});
        }
        return true;
    }

    private Boolean fourthVersionFormElementEquation(String elem) { // X^P
//        System.out.println("fourthVersionStr: " + elem);
        if (elem.charAt(0) != '-' && elem.charAt(0) != '+')
            return false;
        String result = String.valueOf(elem.charAt(0));
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
            if (i + 1 > elem.length())
                return false;
            int checkVar = Integer.parseInt(elem.substring(++i));
            if (checkVar < 0 || checkVar > 2)
                return false;
            if (numbers.containsKey(checkVar)) {
                LinkedList<Double> newList = numbers.get(checkVar);
                newList.add(Double.parseDouble(result + 1));
                numbers.put(checkVar, newList);
            } else {
                String finalResult = result + 1;
                numbers.put(checkVar, new LinkedList<>(){{add(Double.parseDouble(finalResult));}});
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    private void printFinalEquation(int degree) {
        LinkedList<Double> elems = numbers.get(degree);
        double sum = 0;

        for (Double d : elems) {
            sum += d;
        }
        if (sum != 0) {
            finalEquation.put(degree, sum);
            if (degree > polynomialDegree)
                polynomialDegree = degree;
            if (sum > 0)
                System.out.print("+ " + sum + "*" + searchVariable + "^" + degree + " ");
            else
                System.out.print("- " + (sum * -1) + "*" + searchVariable + "^" + degree + " ");
        }
    }
    private void gettingResult(double disc) {
        System.out.print("Полученный дискриминант уравнения: " + disc);
        if (disc < 0) {
            System.out.println(" (вещественные корни отсутствуют, имеются только комплексные)");
            decisionResult.add(null);
        }
        else if (disc == 0) {
            System.out.println(" (имеется только один корень)");
            decisionResult.add(- finalEquation.get(1) / (2 * finalEquation.get(2)));
        } else if (disc > 0) {
            System.out.println(" (имеется два корня)");
            double discriminantRoot = Math.sqrt(disc);
            decisionResult.add((- finalEquation.get(1) + discriminantRoot) / (2 * finalEquation.get(2)));
            decisionResult.add((- finalEquation.get(1) - discriminantRoot) / (2 * finalEquation.get(2)));
        }
    }
    private void solutionFinalEquation() {
        double disc;

        if (finalEquation.containsKey(2) && finalEquation.containsKey(1) && finalEquation.containsKey(0)) {
            disc = Math.pow(finalEquation.get(1), 2) - 4 * finalEquation.get(2) * finalEquation.get(0);
            gettingResult(disc);
        } else if (finalEquation.containsKey(2) && finalEquation.containsKey(1)) {
            disc = Math.pow(finalEquation.get(1), 2);
            gettingResult(disc);
        } else if (finalEquation.containsKey(2) && finalEquation.containsKey(0)){
            disc = -4 * finalEquation.get(2) * finalEquation.get(0);
            gettingResult(disc);
        } else if (finalEquation.containsKey(2)) {
            decisionResult.add(0.0);
        } else if (finalEquation.containsKey(1) && finalEquation.containsKey(0)) {
            decisionResult.add((finalEquation.get(0) * -1) / finalEquation.get(1));
        } else if (finalEquation.containsKey(1)) {
            decisionResult.add(0.0);
        } else if (finalEquation.containsKey(0)) {
            decisionResult.add(0.0);
        }
        System.out.println("Полученные результаты:");
        int counterResult = 0;
        int counter = 1;
        for (Double res : decisionResult) {
            if (res != null)
                System.out.println(searchVariable + "(" + counter++ + ") = " + res);
            else
                ++counterResult;
        }
        if (counterResult == decisionResult.size())
            System.out.println("Результаты отсутствуют");
    }
    private void creatingFinalEquation() {
        LinkedList<Double> elems;
        double sum = 0;
        System.out.print("Преобразованное уравнение: ");
        if (numbers.containsKey(2))
            printFinalEquation(2);
        if (numbers.containsKey(1))
            printFinalEquation(1);
        if (numbers.containsKey(0))
            printFinalEquation(0);
        System.out.println("= 0");
        System.out.println("Полиномиальная степень: " + polynomialDegree);
    }
    private void checkingElementsOfEquationForm() throws WrongEquationException{
        for (String elem : equation) {
            if (!firstVersionFormElementEquation(elem)
                    && !secondVersionFormElementEquation(elem)
                    && !thirdVersionFormElementEquation(elem)
                    && !fourthVersionFormElementEquation(elem)
                    && !fifthVersionFormElementEquation(elem)
                    && !sixthVersionFormElementEquation(elem))
                throw new WrongEquationException("Элемент уравнения '" + elem + "' не подходит ни под одну из допустимых форм записи (X, X*X, X^P, A*X^P, где 0 <= P <= 2)");
        }
        if (equation.isEmpty()) {
            System.out.println("Искомое значение может быть любым числом" + searchVariable);
            System.exit(0);
        }
        creatingFinalEquation();
        solutionFinalEquation();
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
    }
    public List<Double> getDecisionResult() {
        return decisionResult;
    }
}
