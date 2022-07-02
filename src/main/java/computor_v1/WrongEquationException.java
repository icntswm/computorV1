package computor_v1;

public class WrongEquationException extends Exception{
    public WrongEquationException(String error) {
        super("Ошибка: " + error);
    }
}
