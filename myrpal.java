import CSEMachine.Runner;

// myrpal class is the main class
public class myrpal {
    public static void main(String[] args) {  
        String filename;
        boolean isPrintAST = false;
        
        // if there is only the filename
        if (args.length == 1) {
            filename = args[0];
            System.out.println(Runner.Run(filename,isPrintAST));    
        }
        // if there is the -ast switch and the filename
        else if (args.length == 2) {
            filename = args[1];
            if ("-ast".equals(args[0])) {
                isPrintAST = true;
                Runner.Run(filename, isPrintAST);
            }
            else {
                System.out.println("Invalid arguments");
                return;
            }
        }
        // if there is no argument or more than 2 arguments
        else {
            System.out.println("Invalid arguments");
            return;
        }
    }
}