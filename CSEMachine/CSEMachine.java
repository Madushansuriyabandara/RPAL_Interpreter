package CSEMachine;

import Symbols.*;
import java.util.ArrayList;

// CSEMachine class is for executing the CSEMachine
public class CSEMachine {
    private ArrayList<Symbol> control;
    private ArrayList<Symbol> stack;
    private ArrayList<E> environment;

    public CSEMachine(ArrayList<Symbol> control, ArrayList<Symbol> stack, ArrayList<E> environment) {
        this.setControl(control);
        this.setStack(stack);
        this.setEnvironment(environment);
    }  
    
    public void setControl(ArrayList<Symbol> control) {
        this.control = control;
    }
    
    public void setStack(ArrayList<Symbol> stack) {
        this.stack = stack;
    }
    
    public void setEnvironment(ArrayList<E> environment) {
        this.environment = environment;
    }
    
    // executes the CSEMachine
    public void execute() {
        E currentEnvironment = this.environment.get(0);
        int j = 1;
        // Loop until the control stack is empty
        while (!control.isEmpty()) {
            // printControl();
            // printStack();
            // printEnvironment();
            Symbol currentSymbol = control.get(control.size()-1);
            control.remove(control.size()-1);
            // if the current symbol is an identifier
            if (currentSymbol instanceof Id) {
                this.stack.add(0, currentEnvironment.lookup((Id) currentSymbol));
            }
            // if the current symbol is a lambda
            else if (currentSymbol instanceof Lambda) {
                Lambda lambda = (Lambda) currentSymbol;
                lambda.setEnvironment(currentEnvironment.getIndex());
                this.stack.add(0, lambda);
            }
            // if the current symbol is a gamma
            else if (currentSymbol instanceof Gamma) {
                Symbol nextSymbol = this.stack.get(0);
                this.stack.remove(0);
                // if the next symbol is a lambda
                if (nextSymbol instanceof Lambda) {
                    Lambda lambda = (Lambda) nextSymbol;
                    E e = new E(j++);
                    if (lambda.identifiers.size() == 1) {
                        e.values.put(lambda.identifiers.get(0), this.stack.get(0));
                        this.stack.remove(0);
                    }
                    else {
                        Tup tup = (Tup) this.stack.get(0);
                        this.stack.remove(0);
                        int i = 0;
                        for (Id id: lambda.identifiers) {
                            e.values.put(id, tup.symbols.get(i++));
                        }
                    }
                    for (E environment: this.environment) {
                        if (environment.getIndex() == lambda.getEnvironment()) {
                            e.setParent(environment);
                        }
                    }        
                    currentEnvironment = e;
                    this.control.add(e);
                    this.control.add(lambda.getDelta());
                    this.stack.add(0, e);
                    this.environment.add(e);
                }
                // if the next symbol is a Tup
                else if (nextSymbol instanceof Tup) {
                    Tup tup = (Tup) nextSymbol;
                    int i = Integer.parseInt(this.stack.get(0).getData());
                    this.stack.remove(0);
                    this.stack.add(0, tup.symbols.get(i-1));
                }
                // if the next symbol is a Ystar
                else if (nextSymbol instanceof Ystar) {
                    Lambda lambda = (Lambda) this.stack.get(0);
                    this.stack.remove(0);
                    Eta eta = new Eta();
                    eta.setIndex(lambda.getIndex());
                    eta.setEnvironment(lambda.getEnvironment());
                    eta.setIdentifier(lambda.identifiers.get(0));
                    eta.setLambda(lambda);
                    this.stack.add(0, eta);
                }
                // if the next symbol is an Eta
                else if (nextSymbol instanceof Eta) {
                    Eta eta = (Eta) nextSymbol;
                    Lambda lambda = eta.getLambda();
                    this.control.add(new Gamma());
                    this.control.add(new Gamma());
                    this.stack.add(0, eta);
                    this.stack.add(0, lambda);
                }
                // any other case
                else {
                    // if the next symbol data is "Print"
                    if ("Print".equals(nextSymbol.getData())) {}
                    // if the next symbol data is "Stem"
                    else if ("Stem".equals(nextSymbol.getData())) {
                        Symbol s = this.stack.get(0);
                        this.stack.remove(0);
                        s.setData(s.getData().substring(0, 1));
                        this.stack.add(0, s);
                    }
                    // if the next symbol data is "Stern"
                    else if ("Stern".equals(nextSymbol.getData())) {
                        Symbol s = this.stack.get(0);
                        this.stack.remove(0);
                        s.setData(s.getData().substring(1));
                        this.stack.add(0, s);
                    }
                    // if the next symbol data is "Conc"                    
                    else if ("Conc".equals(nextSymbol.getData())) {
                        Symbol s1 = this.stack.get(0);
                        Symbol s2 = this.stack.get(1);
                        this.stack.remove(0);
                        this.stack.remove(0);
                        s1.setData(s1.getData() + s2.getData());
                        this.stack.add(0, s1);                                          
                    }
                    // if the next symbol data is "Order"
                    else if ("Order".equals(nextSymbol.getData())) {
                        Tup tup = (Tup) this.stack.get(0);
                        this.stack.remove(0);
                        Int n = new Int(Integer.toString(tup.symbols.size()));
                        this.stack.add(0, n);
                    }
                    // if the next symbol data is "Null"
                    else if ("Null".equals(nextSymbol.getData())) {}
                    // if the next symbol data is "ItoS"
                    else if ("Itos".equals(nextSymbol.getData())) {}
                    // if the next symbol data is "Isinteger"
                    else if ("Isinteger".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Int) {
                            this.stack.add(0, new Bool("true"));
                        }
                        else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    }
                    // if the next symbol data is "Isstring"
                    else if ("Isstring".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Str) {
                            this.stack.add(0, new Bool("true"));
                        }
                        else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);                        
                    }
                    // if the next symbol data is "Istuple"
                    else if ("Istuple".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Tup) {
                            this.stack.add(0, new Bool("true"));
                        }
                        else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    }
                    // if the next symbol data is "Isdummy"
                    else if ("Isdummy".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Dummy) {
                            this.stack.add(0, new Bool("true"));
                        }
                        else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    }
                    // if the next symbol data is "Istruthvalue"
                    else if ("Istruthvalue".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Bool) {
                            this.stack.add(0, new Bool("true"));
                        }
                        else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    }
                    // if the next symbol data is "Isfunction"
                    else if ("Isfunction".equals(nextSymbol.getData())) {
                        if (this.stack.get(0) instanceof Lambda) {
                            this.stack.add(0, new Bool("true"));
                        }
                        else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    }
                }
            }
            // if the current symbol is an E
            else if (currentSymbol instanceof E) {     
                this.stack.remove(1);
                this.environment.get(((E) currentSymbol).getIndex()).setIsRemoved(true);
                int y = this.environment.size();
                while (y > 0) {
                    if (!this.environment.get(y-1).getIsRemoved()) {
                        currentEnvironment = this.environment.get(y-1);
                        break;
                    }
                    else {
                        y--;
                    }
                }
            }
            // if the current symbol is a Rator
            else if (currentSymbol instanceof Rator) {
                if (currentSymbol instanceof Uop) {
                    Symbol rator = currentSymbol;
                    Symbol rand = this.stack.get(0);
                    this.stack.remove(0);
                    stack.add(0, this.applyUnaryOperation(rator, rand));
                }
                if (currentSymbol instanceof Bop) {
                    Symbol rator = currentSymbol;
                    Symbol rand1 = this.stack.get(0);
                    Symbol rand2 = this.stack.get(1);
                    this.stack.remove(0);
                    this.stack.remove(0);
                    this.stack.add(0, this.applyBinaryOperation(rator, rand1, rand2));
                }
            }
            // if the current symbol is a Beta
            else if (currentSymbol instanceof Beta) {
                if (Boolean.parseBoolean(this.stack.get(0).getData())) {
                    this.control.remove(control.size()-1); 
                }
                else {
                    this.control.remove(control.size()-2); 
                }
                this.stack.remove(0);
            }
            // if the current symbol is a Tau
            else if (currentSymbol instanceof Tau) {
                Tau tau = (Tau) currentSymbol;
                Tup tup = new Tup();
                for (int i = 0; i < tau.getN(); i++) {
                    tup.symbols.add(this.stack.get(0));
                    this.stack.remove(0);
                }
                this.stack.add(0, tup);
            }
            // if the current symbol is a Delta
            else if (currentSymbol instanceof Delta) {
                this.control.addAll(((Delta) currentSymbol).symbols);
            }
            // if the current symbol is a B
            else if (currentSymbol instanceof B) {
                this.control.addAll(((B) currentSymbol).symbols);
            }
            // any other case
            else {
                this.stack.add(0, currentSymbol);
            }
        }   
    }
    
    // printControl method prints the control stack
    public void printControl() {
        System.out.print("Control: ");
        for (Symbol symbol: this.control) {
            System.out.print(symbol.getData());
            if (symbol instanceof Lambda) {
                System.out.print(((Lambda) symbol).getIndex());
            }
            else if (symbol instanceof Delta) {
                System.out.print(((Delta) symbol).getIndex());
            }
            else if (symbol instanceof E) {
                System.out.print(((E) symbol).getIndex());
            }
            else if (symbol instanceof Eta) {
                System.out.print(((Eta) symbol).getIndex());
            }
            System.out.print(",");
        }
        System.out.println();
    }
    
    // printStack method prints the stack
    public void printStack() {
        System.out.print("Stack: ");
        for (Symbol symbol: this.stack) {
            System.out.print(symbol.getData());
            if (symbol instanceof Lambda) {
                System.out.print(((Lambda) symbol).getIndex());
            }
            else if (symbol instanceof Delta) {
                System.out.print(((Delta) symbol).getIndex());
            }
            else if (symbol instanceof E) {
                System.out.print(((E) symbol).getIndex());
            }
            else if (symbol instanceof Eta) {
                System.out.print(((Eta) symbol).getIndex());
            }
            System.out.print(",");
        }
        System.out.println();
    }
    
    // printEnvironment method prints the environment
    public void printEnvironment() {
        for (Symbol symbol: this.environment) {
            System.out.print("e"+((E) symbol).getIndex()+ " --> ");
            if (((E) symbol).getIndex()!=0) {
                System.out.println("e"+((E) symbol).getParent().getIndex());
            }
            else {
                System.out.println();
            }
        }
    }
    
    // applyUnaryOperation method applies unary operations such as neg and not
    public Symbol applyUnaryOperation(Symbol rator, Symbol rand) {
        if ("neg".equals(rator.getData())) {
            int val = Integer.parseInt(rand.getData());
            return new Int(Integer.toString(-1*val));
        }
        else if ("not".equals(rator.getData())) {
            boolean val = Boolean.parseBoolean(rand.getData());
            return new Bool(Boolean.toString(!val));
        }
        else {
            return new Err();
        }
    }
    
    // applyBinaryOperation method applies binary operations such as +, -, *, /, **, &, or, eq, ne, ls, le, gr, ge, aug
    public Symbol applyBinaryOperation(Symbol rator, Symbol rand1, Symbol rand2) {
        if ("+".equals(rator.getData())) {
            int val1 = Integer.parseInt(rand1.getData());
            int val2 = Integer.parseInt(rand2.getData());
            return new Int(Integer.toString(val1+val2));
        }
        else if ("-".equals(rator.getData())) {
            int val1 = Integer.parseInt(rand1.getData());
            int val2 = Integer.parseInt(rand2.getData());
            return new Int(Integer.toString(val1-val2));
        }
        else if ("*".equals(rator.getData())) {
            int val1 = Integer.parseInt(rand1.getData());
            int val2 = Integer.parseInt(rand2.getData());
            return new Int(Integer.toString(val1*val2));
        }
        else if ("/".equals(rator.getData())) {
            int val1 = Integer.parseInt(rand1.getData());
            int val2 = Integer.parseInt(rand2.getData());
            return new Int(Integer.toString(val1/val2));
        }
        else if ("**".equals(rator.getData())) {
            int val1 = Integer.parseInt(rand1.getData());
            int val2 = Integer.parseInt(rand2.getData());
            return new Int(Integer.toString((int) Math.pow(val1, val2)));
        }
        else if ("&".equals(rator.getData())) {            
            boolean val1 = Boolean.parseBoolean(rand1.getData());
            boolean val2 = Boolean.parseBoolean(rand2.getData());
            return new Bool(Boolean.toString(val1 && val2));
        }
        else if ("or".equals(rator.getData())) {            
            boolean val1 = Boolean.parseBoolean(rand1.getData());
            boolean val2 = Boolean.parseBoolean(rand2.getData());
            return new Bool(Boolean.toString(val1 || val2));
        }
        else if ("eq".equals(rator.getData())) {            
            String val1 = rand1.getData();
            String val2 = rand2.getData();
            return new Bool(Boolean.toString(val1.equals(val2)));
        }
        else if ("ne".equals(rator.getData())) {            
            String val1 = rand1.getData();
            String val2 = rand2.getData();
            return new Bool(Boolean.toString(!val1.equals(val2)));
        }
        else if ("ls".equals(rator.getData())) {            
            int val1 = Integer.parseInt(rand1.getData());
            int val2 = Integer.parseInt(rand2.getData());
            return new Bool(Boolean.toString(val1 < val2));
        }
        else if ("le".equals(rator.getData())) {            
            int val1 = Integer.parseInt(rand1.getData());
            String s1=rand2.getData();
            int val2 = Integer.parseInt(s1);
            return new Bool(Boolean.toString(val1 <= val2));
        }
        else if ("gr".equals(rator.getData())) {            
            int val1 = Integer.parseInt(rand1.getData());
            int val2 = Integer.parseInt(rand2.getData());
            return new Bool(Boolean.toString(val1 > val2));
        }
        else if ("ge".equals(rator.getData())) {            
            int val1 = Integer.parseInt(rand1.getData());
            int val2 = Integer.parseInt(rand2.getData());
            return new Bool(Boolean.toString(val1 >= val2));
        }
        else if ("aug".equals(rator.getData())) {  
            if (rand2 instanceof Tup) {
                ((Tup) rand1).symbols.addAll(((Tup) rand2).symbols);
            }
            else {
                ((Tup) rand1).symbols.add(rand2);
            }
            return rand1;
        }
        else {
            return new Err();
        }
    }
    
    // getTupleValue method returns the value of a tuple
    public String getTupleValue(Tup tup) {
        String temp = "(";
        for (Symbol symbol: tup.symbols) {
            if (symbol instanceof Tup) {
                temp = temp + this.getTupleValue((Tup) symbol) + ", ";
            }
            else {
                temp = temp + symbol.getData() + ", ";
            }            
        }
        temp = temp.substring(0, temp.length()-2) + ")";
        return temp;
    }
    
    // getAnswer method returns the answer of the CSEMachine
    public String getAnswer() {
        this.execute();
        if (stack.get(0) instanceof Tup) {
            return this.getTupleValue((Tup) stack.get(0));
        }
        return stack.get(0).getData();
    }
}