package Language.Syntax.Functions;

import Language.Syntax.Interpreter;

import java.util.List;
import java.util.Scanner;

public class ReadLine implements LoxCallable {
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Scanner sc = new Scanner(System.in);
        System.out.print("> ");
        return sc.nextLine();
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public String toString(){ return "<native fn>"; }
}
