package org.duarte.ui.custom.screen;

import org.duarte.model.Space;
import org.duarte.service.BoardService;
import org.duarte.service.EventEnum;
import org.duarte.service.NotifierService;
import org.duarte.ui.custom.buttom.CheckGameStatusButton;
import org.duarte.ui.custom.buttom.FinishedGameButtom;
import org.duarte.ui.custom.buttom.ResetButtom;
import org.duarte.ui.custom.frame.MainFrame;
import org.duarte.ui.custom.input.NumberText;
import org.duarte.ui.custom.input.NumberTextLimit;
import org.duarte.ui.custom.panel.MainPanel;
import org.duarte.ui.custom.panel.SudokuSector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import org.duarte.model.Board;

public class MainScreen {

    private final static Dimension dimension = new Dimension(600,600);
    private final BoardService boardService;
    private final NotifierService notifierService;

    private JButton finishGameStatusButton;
    private JButton checkGamesStatusButton;
    private JButton resetButton;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
    }

    public void buildMainScreen(){
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);
        for (int r = 0; r < 9; r+=3){
            var endRow = r + 2;
            for (int c = 0; c < 9; c+=3){
                var endCol = c + 2;
                var spaces = getSpacesFromSector(boardService.getSpaces(), c, endCol, r, endRow);
                JPanel sector = generateSection(spaces);
                mainPanel.add(sector);
            }
        }

        addResetButton(mainPanel);
        addCheckGameStatusButton(mainPanel);
        addFinishedGameButton(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private List<Space> getSpacesFromSector(final List<List<Space>> spaces,
                                            final int initColl, final int endColl,
                                            final int initRow, final int endRow) {
        List<Space> spacesSector = new ArrayList<>();
        for (int r = initRow; r <= endRow; r++){
            for(int c = initColl; c <= endColl; c++){
                spacesSector.add(spaces.get(r).get(c));
            }

        }
        return spacesSector;
    }

    private JPanel generateSection(final List<Space> spaces){
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        fields.forEach(t -> notifierService.subscribe(EventEnum.CLEAR_SPACE, t));
        return new SudokuSector(fields);
    }

    private void addFinishedGameButton(final JPanel mainPanel) {
        finishGameStatusButton = new FinishedGameButtom(e ->{
            if(boardService.gameIsFinished()){
                JOptionPane.showMessageDialog(mainPanel,"Parabéns! Você Terminou o Jogo!");
                resetButton.setEnabled(false);
                resetButton.setEnabled(false);
                resetButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "Seu jogo tem alguma inconsistência");
            }
        });
        mainPanel.add(finishGameStatusButton);
    }

    private void addCheckGameStatusButton(final JPanel mainPanel) {
        checkGamesStatusButton = new CheckGameStatusButton(e ->{
            var hasErrors = boardService.hasErrors();
            var gameStatus = boardService.getStatus();
            var message = switch (gameStatus){
                case NOW_STARTED -> "O jogo não foi iniciado";
                case INCOMPLETE -> "O jogo está incompleto";
                case COMPLETE -> "O jogo está Completo";
            };
            message += hasErrors ? " e contém erros" : " e não contém erros";
            JOptionPane.showMessageDialog(null, message);
        });
        mainPanel.add(MainScreen.this.checkGamesStatusButton);
    }

    private void addResetButton(final JPanel mainPanel) {
        resetButton = new ResetButtom(e ->{
            var dialogResult = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo?",
                    "Limpar o Jogo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (dialogResult == 0) {
                boardService.reset();
                notifierService.notify(EventEnum.CLEAR_SPACE);
            }
        } );
        mainPanel.add(resetButton);
    }

}
