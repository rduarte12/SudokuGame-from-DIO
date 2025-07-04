package org.duarte.ui.custom.buttom;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ResetButtom extends JButton {
    public ResetButtom(final ActionListener actionListener) {
        this.setText("Reiniciar Jogo");
        this.addActionListener(actionListener);
    }
}
