import javax.swing.JFrame;


public class ServerTest {
    public static void main(String[] args) {
        Server sabin = new Server();
        // Make sure close when hit the x.
        sabin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sabin.startRunning();
    }
}
