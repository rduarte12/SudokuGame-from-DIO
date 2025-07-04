package org.duarte;

import org.duarte.model.Board;
import org.duarte.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.duarte.util.BoardTemplate.BOARD_TEMPLATE;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    public final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        final var positions = Stream.of(args)
                .collect(Collectors.toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                ));
        var option = -1;
        while (true){
            System.out.println("Selecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            option = scanner.nextInt();
            switch (option){
                case 1 ->   startGame(positions);
                case 2 ->   inputNumber();
                case 3 ->   removeNumber();
                case 4 ->   showCurrentGame();
                case 5 ->   showGameStatus();
                case 6 ->   clearGame();
                case 7 ->   finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção invalida! Selecione uma opção do menu!");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (extracted(nonNull(board), "Jogo já foi iniciado!")) return;
        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionsConfig = positions.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionsConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionsConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
            board = new Board(spaces);
            System.out.println("O jogo está pronto para começar!");
        }
    }

    private static void inputNumber() {
        if (extracted(isNull(board), "O Jogo ainda não foi iniciado!")) return;

        System.out.println("Informe a coluna que o número será incluído: ");
        var col = getValidNumber(0,8);

        System.out.println("Informe a linha que o número será incluído: ");
        var row = getValidNumber(0,8);
        System.out.printf("Informe o número que vai entrar na posição [%s,%s]\n", col, row);
        var value = getValidNumber(1, 9);
        if(!board.changeValue(col, row, value)){
            System.out.printf("A posição [%s,%s] tem um valor fixo\n", col, row);
        }
    }

    private static void removeNumber() {
        if (extracted(isNull(board), "O Jogo ainda não foi iniciado!")) return;

        System.out.println("Informe a coluna que o número será excluído: ");
        var col = getValidNumber(0,8);
        System.out.println("Informe a linha que o número será excluído ");
        var row = getValidNumber(0,8);

        if(!board.clearValue(col, row)){
            System.out.printf("A posição [%s,%s] tem um valor fixo\n", col, row);
        }
    }

    private static void showCurrentGame() {
        if (extracted(isNull(board), "O Jogo ainda não foi iniciado!")) return;

        var args = new Object[81];
        var argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col: board.getSpaces()){
                args[argPos++] = " " + (isNull(col.get(i).getActual()) ? " " : col.get(i).getActual());
            }
        }

        System.out.println("O jogo está da seguinte maneira:");
        System.out.printf((BOARD_TEMPLATE) + "/n", args);

    }

    private static void showGameStatus() {
        extracted(isNull(board), "O Jogo ainda não foi iniciado!");

        System.out.printf("O jogo se encontra atualmente no seguinte status: %s\n", board.getStatus().getLabel());
        if (board.hasErrors()){
            System.out.println("O jogo contém erros");
        } else {
            System.out.println("O jogo não contém erros");
        }
    }

    private static void clearGame() {
        extracted(isNull(board), "O Jogo ainda não foi iniciado!");

        System.out.println("Tem certeza que você quer reiniciar o jogo e perder o seu progresso? [sim/não]");
        var confirm = scanner.nextLine();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")) {
            System.out.println("Informe sim ou não");
            confirm = scanner.nextLine();
        }

        if (confirm.equalsIgnoreCase("sim")) {
            board.reset();
        }

    }

    private static void finishGame() {
        extracted(isNull(board), "O Jogo ainda não foi iniciado!");

        if (board.gameIsFinished()) {
            System.out.println("O jogo foi finalizado! Parabéns!");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()){
            System.out.println("Seu jogo contém erros, verifique o seu board e ajuste-o");
        } else {
            System.out.println("Você ainda não terminou o jogo! Preencha os espaços em branco!");
        }
    }

    private static boolean extracted(boolean board, String x) {
        if (board) {
            System.out.println(x);
            return true;
        }
        return false;
    }

    private static int getValidNumber(final int min, final int max) {
        var current = scanner.nextInt();
        while (current < min || current > max) {
            while (!String.valueOf(current).matches("[0-9]+")) {
                System.out.printf("Informe um numero entre %s e %s", min, max);
                current = scanner.nextInt();
            }
        }
        return current;

    }
    //0,0;4,false 1,0;7,false 2,0;9,true 3,0;5,false 4,0;8,true 5,0;6,true 6,0;2,true 7,0;3,false 8,0;1,false 0,1;1,false 1,1;3,true 2,1;5,false 3,1;4,false 4,1;7,true 5,1;2,false 6,1;8,false 7,1;9,true 8,1;6,true 0,2;2,false 1,2;6,true 2,2;8,false 3,2;9,false 4,2;1,true 5,2;3,false 6,2;7,false 7,2;4,false 8,2;5,true 0,3;5,true 1,3;1,false 2,3;3,true 3,3;7,false 4,3;6,false 5,3;4,false 6,3;9,false 7,3;8,true 8,3;2,false 0,4;8,false 1,4;9,true 2,4;7,false 3,4;1,true 4,4;2,true 5,4;5,true 6,4;3,false 7,4;6,true 8,4;4,false 0,5;6,false 1,5;4,true 2,5;2,false 3,5;3,false 4,5;9,false 5,5;8,false 6,5;1,true 7,5;5,false 8,5;7,true 0,6;7,true 1,6;5,false 2,6;4,false 3,6;2,false 4,6;3,true 5,6;9,false 6,6;6,false 7,6;1,true 8,6;8,false 0,7;9,true 1,7;8,true 2,7;1,false 3,7;6,false 4,7;4,true 5,7;7,false 6,7;5,false 7,7;2,true 8,7;3,false 0,8;3,false 1,8;2,false 2,8;6,true 3,8;8,true 4,8;5,true 5,8;1,false 6,8;4,true 7,8;7,false 8,8;9,false
}