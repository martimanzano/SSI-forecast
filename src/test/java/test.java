import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class test {
    public static void main(String a[]) throws RserveException, REXPMismatchException {
        RConnection rconnection = new RConnection("127.0.0.1", 6311);

        //TEST CONNECTION
        String vector = "c(1,2,3,4)";
        String test = rconnection.eval("meanVal=mean(" + vector + ")").asString();
        System.out.println("RSERVE CONNECTION TEST. AVERAGING [1,2,3,4]: EXPECTED RESPONSE: 2.5 / RECEIVED: " + test);
    }
}

