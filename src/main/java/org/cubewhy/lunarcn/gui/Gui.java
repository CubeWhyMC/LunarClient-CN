package org.cubewhy.lunarcn.gui;

import javax.swing.*;

public class Gui {
    public static void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
