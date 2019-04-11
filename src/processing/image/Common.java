package processing.image;

class Common {

    static int arithmeticOperation(int a, int b, char operator) {
        if (operator == '+') return a + b;
        else if (operator == '-') return a - b;
        else if (operator == '*') return a * b;
        else if (operator == '/') return a / b;
        else throw new java.lang.Error("No such operation");
    }
}
