package atoms;

import atoms.view.MainFrame;

import javax.swing.*;

/**
 * Contains the entrypoint of the application.
 */
public class Main {
    /**
     * The entrypoint of the application.
     * @param args Application arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
