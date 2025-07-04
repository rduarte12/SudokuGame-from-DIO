package org.duarte.ui.custom.buttom;

import javax.swing.*;
import java.awt.event.ActionListener;

public class FinishedGameButtom extends JButton {
    public FinishedGameButtom(final ActionListener actionListener) {
        this.setText("Concluir jogo");
        this.addActionListener(actionListener);
    }

}
