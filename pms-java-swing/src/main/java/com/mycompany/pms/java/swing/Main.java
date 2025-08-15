package com.mycompany.pms.java.swing;

import com.mycompany.pms.java.swing.ui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configurar Look and Feel - version compatible
        try {
            String systemLAF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(systemLAF);
        } catch (Exception e) {
            // Usar Look and Feel por defecto si falla
            System.out.println("Usando Look and Feel por defecto");
        }
        
        // Ejecutar en Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Error al iniciar la aplicacion: " + e.getMessage());
                    e.printStackTrace();
                    
                    JOptionPane.showMessageDialog(
                        null, 
                        "Error al iniciar la aplicacion:\n" + e.getMessage(),
                        "Error de Inicio", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }
}