package ex3.part3;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIPrimeServerInterface extends Remote{
    boolean primeService(long number) throws RemoteException;
    int primeServiceAsync(long number) throws RemoteException;
    boolean calculationDone(int index) throws RemoteException;
    boolean isPrimeResult(int index) throws RemoteException;
}
