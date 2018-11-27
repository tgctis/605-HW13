public interface SteamHammer extends java.rmi.Remote{
    String version() throws java.rmi.RemoteException;
    void consume(int[] types, int[] amounts) throws java.rmi.RemoteException;
    void produce(int type, int amount) throws java.rmi.RemoteException;
    int consumed() throws java.rmi.RemoteException;
    int produced() throws java.rmi.RemoteException;
}
