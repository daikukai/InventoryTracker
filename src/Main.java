import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Ensures that GUI updates are done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               // new InventoryGUI().setVisible(true);
            }
        });
    }
}