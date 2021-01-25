/*
  CHECKERS - implementation for terminal
  Logic is based on two long digits for each player (White and Black)
  where all information about the 12 pieces are kept
  example: 0b100010001_100010011_100010101_100010111_100001000_100001010
             pawn6,      pawn5,    pawn4,    pawn3,    pawn2,     pawn1
  pawn: sfkyyyxxx
  state (0 - eliminated, 1 - still in game),
  figure (0 - pawn, 1 - queen),
  color (0 - black, 1 - white),
  posY (b5, b4, b3),
  posX (b2, b1, b0)

  Rules: en.wikipedia.org/wiki/Draughts
 */

import java.util.Scanner;

public class Checkers {

    static long white1;
    static long white2;
    static long black1;
    static long black2;

    static char blackField = '\u2B1C';
    static char whiteField = '\u2B1B';
    static char blackPawn = '\u265F';
    static char whitePawn = '\u2659';
    static char whiteQueen = '\u2655';
    static char blackQueen = '\u265B';


    public static void main(String[] args) {
        game();
    }

    // Initial values.
    public static void start() {
        white1 = 0b101110011101110001101111110101111100101111010101111000L;
        white2 = 0b101101110101101100101101010101101000101110111101110101L;
        black1 = 0b100001010100001000100000111100000101100000011100000001L;
        black2 = 0b100010111100010101100010011100010001100001110100001100L;
    }

    public static void game() {
        System.out.println("~~~~~~~~~~~~~~~~CHECKERS~~~~~~~~~~~~~~~~" +
                "\n\nHello!" +
                "\nThe game is started by the player with white pawns." +
                "\nThe player who beats all of the other player's pawns wins." +
                "\n\thorizontally(x): 0-7" +
                "\n\tvertically(y): 0-7" +
                "\n\n~~~~~~~~~~~~~~~~START~~~~~~~~~~~~~~~~");

        start();
        printBoard();
        whitePlayer();
    }

    public static void whitePlayer() {
        //if none of the players is the winner
        if (!checkWinnerB(0) && !checkWinnerW(0)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nWHITE PLAYER");

            //checking if the opponent's beating occurs in the current move
            jumpWhite((int) getPosX(pawnBits(white1, 0)), (int) getPosY(pawnBits(white1, 0)), 0);

            System.out.println("Enter your move from a position: ");
            System.out.println("x: ");
            int x1 = scanner.nextInt();
            System.out.println("y: ");
            int y1 = scanner.nextInt();
            System.out.printf("x: %d y: %d\n", x1, y1);

            //Checking whether there is a pawn in the given position - color match, whether it is in play
            if (!containsPawn(x1, y1) || blackColor(getPawn(0, x1, y1))) {
                System.out.println("There is no pawn in the position you specified, try again");
            } else {
                System.out.println("Enter your move into position x: ");
                int x2 = scanner.nextInt();
                System.out.println("y: ");
                int y2 = scanner.nextInt();
                System.out.printf("x: %d y: %d\n", x2, y2);

                //if the given pawn is a queen
                //if a queen beats: checking if the given pawn is a queen, checking if the given queen should beat the opponent's pawns
                if (pawnOrQueen(getPawn(0, x1, y1)) &&
                        (jumpQueen(x1 - 1, y1 - 1, x1 - 2, y1 - 2, false) ||
                                jumpQueen(x1 - 1, y1 + 1, x1 - 2, y1 + 2, false) ||
                                jumpQueen(x1 + 1, y1 - 1, x1 + 2, y1 - 2, false) ||
                                jumpQueen(x1 + 1, y1 + 1, x1 + 2, y1 + 2, false))) {
                    if (queenJump(x1, y1, x2, y2)) {
                        //changing the bits of a pawn
                        setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
                        printBoard();
                        //checking if there is another queen beating
                        if ((jumpQueen(x2 - 1, y2 - 1, x2 - 2, y2 - 2, false) ||
                                jumpQueen(x2 - 1, y2 + 1, x2 - 2, y2 + 2, false) ||
                                jumpQueen(x2 + 1, y2 - 1, x2 + 2, y2 - 2, false) ||
                                jumpQueen(x2 + 1, y2 + 1, x2 + 2, y2 + 2, false))) {
                            whitePlayer();
                        } else {
                            blackPlayer();
                        }
                    }
                }
                //if there is only a queen move, without beating the opponent's pawn
                if (pawnOrQueen(getPawn(0, x1, y1)) && queenJump(x1, y1, x2, y2)) {
                    setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
                    printBoard();
                    blackPlayer();
                }

                //Checking if there is a pawn beat
                if (jumpPawn(x1, y1, x2, y2)) {
                    validJumpPawn(x1, y1, x2, y2);
                }
                //A simple pawn move
                if (validMove(x1, y1, x2, y2)) {
                    setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
                    if (y2 == 0 && !pawnOrQueen(getPawn(0, x2, y2))) {
                        //changing a pawn to a queen
                        newQueen(x2, y2);
                    }
                    printBoard();
                    blackPlayer();
                }
            }
            //if a player makes a wrong move
            System.out.println("TRY AGAIN");
            whitePlayer();
        } else {
            //new game
            game();
        }
    }

    public static void blackPlayer() {
        if (!checkWinnerB(0) && !checkWinnerW(0)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nBLACK PLAYER");
            jumpBlack((int) getPosX(pawnBits(black1, 0)), (int) getPosY(pawnBits(black1, 0)), 0);
            System.out.println("Enter your move from: ");
            System.out.println("x: ");
            int x1 = scanner.nextInt();
            System.out.println("y: ");
            int y1 = scanner.nextInt();
            System.out.printf("x: %d y: %d\n", x1, y1);

            if (!containsPawn(x1, y1) || !blackColor(getPawn(0, x1, y1))) {
                System.out.println("There is no pawn in the position you specified, try again");
            } else {
                System.out.println("Enter your move into position x: ");
                int x2 = scanner.nextInt();
                System.out.println("y: ");
                int y2 = scanner.nextInt();
                System.out.printf("x: %d y: %d\n", x2, y2);
                if (pawnOrQueen(getPawn(0, x1, y1)) &&
                        (jumpQueen(x1 - 1, y1 - 1, x1 - 2, y1 - 2, true) ||
                                jumpQueen(x1 - 1, y1 + 1, x1 - 2, y1 + 2, true) ||
                                jumpQueen(x1 + 1, y1 - 1, x1 + 2, y1 - 2, true) ||
                                jumpQueen(x1 + 1, y1 + 1, x1 + 2, y1 + 2, true))) {
                    if (queenJump(x1, y1, x2, y2)) {
                        setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
                        printBoard();
                        if ((jumpQueen(x2 - 1, y2 - 1, x2 - 2, y2 - 2, true) ||
                                jumpQueen(x2 - 1, y2 + 1, x2 - 2, y2 + 2, true) ||
                                jumpQueen(x2 + 1, y2 - 1, x2 + 2, y2 - 2, true) ||
                                jumpQueen(x2 + 1, y2 + 1, x2 + 2, y2 + 2, true))) {
                            blackPlayer();
                        } else {
                            whitePlayer();
                        }
                    }
                }
                if (pawnOrQueen(getPawn(0, x1, y1)) && queenJump(x1, y1, x2, y2)) {
                    setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
                    printBoard();
                    whitePlayer();
                }
                if (jumpPawn(x1, y1, x2, y2)) {
                    validJumpPawn(x1, y1, x2, y2);
                }
                if (validMove(x1, y1, x2, y2)) {
                    setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
                    if (y2 == 7 && !pawnOrQueen(getPawn(0, x2, y2))) {
                        newQueen(x2, y2);
                    }
                    printBoard();
                    System.out.println();
                    whitePlayer();
                }
            }
            System.out.println("TRY AGAIN");
            blackPlayer();
        } else {
            game();
        }
    }


    /**
     * Print the board.
     * i - rows(y)
     * j - columns(x)
     * Separating into white and black fields.
     * Checking if a pawn should stand on a given field, according to its color.
     * Checking if a pawn is a queen.
     */

    public static void printBoard() {
        System.out.println("\n");
        System.out.println(" \t0\t1\t2\t3\t4\t5\t6\t7");
        for (int i = 0; i < 8; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 != 0) {
                    if (containsPawn(j, i) && blackColor(getPawn(0, j, i))) {
                        if (pawnOrQueen(getPawn(0, j, i))) {
                            System.out.print(blackQueen + "\t");
                        } else {
                            System.out.print(blackPawn + "\t");
                        }
                    } else if (containsPawn(j, i) && !blackColor(getPawn(0, j, i))) {
                        if (pawnOrQueen(getPawn(0, j, i))) {
                            System.out.print(whiteQueen + "\t");
                        } else {
                            System.out.print(whitePawn + "\t");
                        }
                    } else {
                        System.out.print(blackField + "\t");
                    }
                } else {
                    System.out.print(whiteField + "\t");
                }

            }
            System.out.println();
        }
    }


    //obtaining bits of a given pawn (numbering from 0 - 11)
    public static long pawnBits(long allBits, int k) {
        long mask = 0b111111111;
        return (allBits >> (9 * k)) & mask;
    }

    public static long getPawn(int k, int x1, int y1) {
        if (getPosX(pawnBits(black1, k)) == x1 && getPosY(pawnBits(black1, k)) == y1 && pawnInGame(pawnBits(black1, k))) {
            return pawnBits(black1, k);
        } else if (getPosX(pawnBits(black2, k)) == x1 && getPosY(pawnBits(black2, k)) == y1 && pawnInGame(pawnBits(black2, k))) {
            return pawnBits(black2, k);
        } else if (getPosX(pawnBits(white1, k)) == x1 && getPosY(pawnBits(white1, k)) == y1 && pawnInGame(pawnBits(white1, k))) {
            return pawnBits(white1, k);
        } else if (getPosX(pawnBits(white2, k)) == x1 && getPosY(pawnBits(white2, k)) == y1 && pawnInGame(pawnBits(white2, k))) {
            return pawnBits(white2, k);
        } else {
            if (k < 5) {
                return getPawn(k + 1, x1, y1);
            } else {
                return 0;
            }
        }
    }

    //placing new bits of a given pawn
    public static long setBits(long oldPawn, long newPawn, int k) {
        long mask = 0b111111111;
        if (String.format("%54s", Long.toBinaryString(black1)).replace(" ", "0").substring(9 * (6 - (k + 1)), (9 * (6 - k))).equals(Long.toBinaryString(oldPawn))) {
            black1 &= ~(mask << (9 * k));
            black1 |= newPawn << (9 * k);
            return black1;
        } else if (String.format("%54s", Long.toBinaryString(black2)).replace(" ", "0").substring(9 * (6 - (k + 1)), (9 * (6 - k))).equals(Long.toBinaryString(oldPawn))) {
            black2 &= ~(mask << (9 * k));
            black2 |= newPawn << (9 * k);
            return black2;
        } else if (String.format("%54s", Long.toBinaryString(white1)).replace(" ", "0").substring(9 * (6 - (k + 1)), (9 * (6 - k))).equals(Long.toBinaryString(oldPawn))) {
            white1 &= ~(mask << (9 * k));
            white1 |= newPawn << (9 * k);
            return white1;
        } else if (String.format("%54s", Long.toBinaryString(white2)).replace(" ", "0").substring(9 * (6 - (k + 1)), (9 * (6 - k))).equals(Long.toBinaryString(oldPawn))) {
            white2 &= ~(mask << (9 * k));
            white2 |= newPawn << (9 * k);
            return white2;
        } else {
            if (k < 5) {
                return setBits(oldPawn, newPawn, k + 1);
            } else {
                System.out.println("Błędny ruch");
                if (blackColor(oldPawn)) {
                    blackPlayer();
                } else {
                    whitePlayer();
                }
                return 0;
            }
        }
    }

    // getting a position for a pawn
    // s - state, f - figure, k - color, posY, posX
    // 0bsfkyyyxxx
    public static long getPosX(long pawn) {
        short posXmask = 0b000000111;
        return (pawn & posXmask);
    }

    public static long getPosY(long pawn) {
        short posYmask = 0b000111000;
        return (pawn & posYmask) >> 3;
    }

    //new position for a given pawn
    public static long setPosition(long pawn, long x, long y) {
        int posMask = 0b000111111;
        pawn &= ~posMask;
        pawn |= x;
        pawn |= y << 3;
        return pawn;
    }

    //color
    //0bsfkyyyxxx, if  0 --> czarny; 1 --> bialy
    //Checking if a given pawn is black
    public static boolean blackColor(long pawn) {
        return ((pawn >> 6) & 1) == 0;
    }

    //state 0bsfkyyyxxx, (0 - eliminated, 1 - still in game)
    //Checking if a pawn is still in the game
    public static boolean pawnInGame(long pionek) {
        return ((pionek >> 8) & 1) != 0;
    }

    //Checking if a player won. If all the opponent's pawns are out of the game.
    public static boolean checkWinnerW(int k) {
        if (!pawnInGame(pawnBits(black1, k)) && !pawnInGame(pawnBits(black2, k))) {
            if (k == 5) {
                System.out.println("~~~~~~~~~~~~~~~~THE END~~~~~~~~~~~~~~~~\n\n\t\tWHITE PLAYER IS THE WINNER\n\n");
                return true;
            } else {
                return checkWinnerW(k + 1);
            }
        } else {
            return false;
        }
    }

    public static boolean checkWinnerB(int k) {
        if (!pawnInGame(pawnBits(white1, k)) && !pawnInGame(pawnBits(white2, k))) {
            if (k == 5) {
                System.out.println("~~~~~~~~~~~~~~~~THE END~~~~~~~~~~~~~~~~\n\n\t\tBLACK PLAYER IS THE WINNER\n\n");
                return true;
            } else {
                return checkWinnerB(k + 1);
            }
        } else {
            return false;
        }
    }

    //remove from the board, from the game
    //0bsfkyyyxxx, s (0 - eliminated, 1 - still in game)
    public static void outOfGame(int x, int y) {
        long pawn = getPawn(0, x, y);
        pawn &= ~(1 << 8);
        setBits(getPawn(0, x, y), pawn, 0);
    }

    //figure (0 - pawn, 1 - queen), 0bsfkyyyxxx
    //if queen --> true
    public static boolean pawnOrQueen(long pawn) {
        return ((pawn >> 7) & 1) != 0;
    }

    //changing a pawn to a queen
    public static void newQueen(int x, int y) {
        long pawn = getPawn(0, x, y);
        //wyczyszczenie 7 bitu
        pawn |= 1 << 7;
        setBits(getPawn(0, x, y), pawn, 0);
    }


    /**
     * Validation of a pawn's movement.
     * Restriction of movement due to board size.
     * Restricting movement to black fields only.
     * Restricting the pawn to move backwards.
     * Restricting a pawn to move only one field.
     * Restriction due to already occupied field by another pawn.
     */

    public static boolean validMove(int x1, int y1, int x2, int y2) {
        if (x2 < 0 || x2 > 7 || y2 < 0 || y2 > 7) { //board size
            System.out.println("Movement out of the board");
            return false;
        } else if ((x2 + y2) % 2 == 0) {
            System.out.println("Movement to the white field is not possible");
            return false;
        } else if (blackColor(getPawn(0, x1, y1)) && y1 > y2) {
            System.out.println("The black pawn cannot go back");
            return false;
        } else if (!blackColor(getPawn(0, x1, y1)) && y1 < y2) {
            System.out.println("The white pawn cannot go back");
            return false;
        } else if (Math.abs(x2 - x1) != 1) {
            System.out.println("Pawn cannot move more than one field");
            return false;
        } else if (containsPawn(x2, y2)) {
            System.out.println("There is another pawn on the field of your choice");
            return false;
        } else
            return true;
    }

    //Checking if the position contains a pawn that is in the game
    public static boolean containsPawn(int x2, int y2) {
        return (getPosX(getPawn(0, x2, y2)) == x2 && getPosY(getPawn(0, x2, y2)) == y2 && pawnInGame(getPawn(0, x2, y2)));
    }


    /**
     * Validation of pawn jumping/beating.
     * Restriction of movement due to board size.
     * If the distance of the pawn is given by 2 fields.
     * If there is another pawn of the opposite color on a field with a distance of 1 and the next field is empty.
     * Removing a beaten pawn from the game.
     * Checking the next beat.
     */

    public static boolean jumpPawn(int x1, int y1, int x2, int y2) {
        if (Math.abs(x1 - x2) == 2 && Math.abs(y1 - y2) == 2 && !containsPawn(x2, y2) && x2 >= 0 && x2 <= 7 && y2 >= 0 && y2 <= 7) {
            if (blackColor(getPawn(0, x1, y1))) {
                if (x1 > x2 && y1 > y2 && containsPawn(x1 - 1, y1 - 1) && !blackColor(getPawn(0, x1 - 1, y1 - 1))) {
                    return true;
                } else if (x1 > x2 && y1 < y2 && containsPawn(x1 - 1, y1 + 1) && !blackColor(getPawn(0, x1 - 1, y1 + 1))) {
                    return true;
                } else if (x1 < x2 && y1 > y2 && containsPawn(x1 + 1, y1 - 1) && !blackColor(getPawn(0, x1 + 1, y1 - 1))) {
                    return true;
                } else
                    return x1 < x2 && y1 < y2 && containsPawn(x1 + 1, y1 + 1) && !blackColor(getPawn(0, x1 + 1, y1 + 1));
            } else {
                if (x1 > x2 && y1 > y2 && containsPawn(x1 - 1, y1 - 1) && blackColor(getPawn(0, x1 - 1, y1 - 1))) {
                    return true;
                } else if (x1 > x2 && y1 < y2 && containsPawn(x1 - 1, y1 + 1) && blackColor(getPawn(0, x1 - 1, y1 + 1))) {
                    return true;
                } else
                    if (x1 < x2 && y1 > y2 && containsPawn(x1 + 1, y1 - 1) && blackColor(getPawn(0, x1 + 1, y1 - 1))) {
                        return true;
                    } else
                        return x1 < x2 && y1 < y2 && containsPawn(x1 + 1, y1 + 1) && blackColor(getPawn(0, x1 + 1, y1 + 1));
            }
        } else {
            return false;
        }
    }

    public static void validJumpPawn(int x1, int y1, int x2, int y2) {
        if ((x1 > x2 && y1 > y2) && ((blackColor(getPawn(0, x1, y1)) && !blackColor(getPawn(0, x1 - 1, y1 - 1))
                || (!blackColor(getPawn(0, x1, y1)) && blackColor(getPawn(0, x1 - 1, y1 - 1)))))) {
            setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
            outOfGame(x1 - 1, y1 - 1);
            //Checking if there is another beating
            if (jumpPawn(x2, y2, x2 - 2, y2 - 2) || jumpPawn(x2, y2, x2 + 2, y2 - 2) || jumpPawn(x2, y2, x2 - 2, y2 + 2) || jumpPawn(x2, y2, x2 + 2, y2 + 2)) {
                printBoard();
                if (blackColor(getPawn(0, x2, y2))) {
                    blackPlayer();
                } else {
                    whitePlayer();
                }
            } else {
                if (((y2 == 0 && !blackColor(getPawn(0, x2, y2))) || (y2 == 7 && blackColor(getPawn(0, x2, y2)))) && !pawnOrQueen(getPawn(0, x2, y2))) {
                    newQueen(x2, y2);
                }
                printBoard();
                if (blackColor(getPawn(0, x2, y2))) {
                    whitePlayer();
                } else {
                    blackPlayer();
                }
            }
        } else if ((x1 > x2 && y1 < y2) && ((blackColor(getPawn(0, x1, y1)) && !blackColor(getPawn(0, x1 - 1, y1 + 1)) ||
                (!blackColor(getPawn(0, x1, y1)) && blackColor(getPawn(0, x1 - 1, y1 + 1)))))) { // lewy dolny
            setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
            outOfGame(x1 - 1, y1 + 1);
            if (jumpPawn(x2, y2, x2 - 2, y2 - 2) || jumpPawn(x2, y2, x2 + 2, y2 - 2) || jumpPawn(x2, y2, x2 - 2, y2 + 2) || jumpPawn(x2, y2, x2 + 2, y2 + 2)) {
                printBoard();
                if (blackColor(getPawn(0, x2, y2))) {
                    blackPlayer();
                } else {
                    whitePlayer();
                }
            } else {
                if (((y2 == 0 && !blackColor(getPawn(0, x2, y2))) || (y2 == 7 && blackColor(getPawn(0, x2, y2)))) && !pawnOrQueen(getPawn(0, x2, y2))) {
                    newQueen(x2, y2);
                }
                printBoard();
                if (blackColor(getPawn(0, x2, y2))) {
                    whitePlayer();
                } else {
                    blackPlayer();
                }
            }
        } else if ((x1 < x2 && y1 > y2) && ((blackColor(getPawn(0, x1, y1)) && !blackColor(getPawn(0, x1 + 1, y1 - 1)) ||
                (!blackColor(getPawn(0, x1, y1)) && blackColor(getPawn(0, x1 + 1, y1 - 1)))))) { // prawy gorny
            setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
            outOfGame(x1 + 1, y1 - 1);
            if (jumpPawn(x2, y2, x2 - 2, y2 - 2) || jumpPawn(x2, y2, x2 + 2, y2 - 2) || jumpPawn(x2, y2, x2 - 2, y2 + 2) || jumpPawn(x2, y2, x2 + 2, y2 + 2)) {
                printBoard();
                if (blackColor(getPawn(0, x2, y2))) {
                    blackPlayer();
                } else {
                    whitePlayer();
                }
            } else {
                if (((y2 == 0 && !blackColor(getPawn(0, x2, y2))) || (y2 == 7 && blackColor(getPawn(0, x2, y2)))) && !pawnOrQueen(getPawn(0, x2, y2))) {
                    newQueen(x2, y2);
                }
                printBoard();
                if (blackColor(getPawn(0, x2, y2))) {
                    whitePlayer();
                } else {
                    blackPlayer();
                }
            }
        } else if ((x1 < x2 && y1 < y2) && ((blackColor(getPawn(0, x1, y1)) && !blackColor(getPawn(0, x1 + 1, y1 + 1)) ||
                (!blackColor(getPawn(0, x1, y1)) && blackColor(getPawn(0, x1 + 1, y1 + 1)))))) {  // prawy dolny
            setBits(getPawn(0, x1, y1), setPosition(getPawn(0, x1, y1), x2, y2), 0);
            outOfGame(x1 + 1, y1 + 1);
            if (jumpPawn(x2, y2, x2 - 2, y2 - 2) || jumpPawn(x2, y2, x2 + 2, y2 - 2) || jumpPawn(x2, y2, x2 - 2, y2 + 2) || jumpPawn(x2, y2, x2 + 2, y2 + 2)) {
                printBoard();
                if (blackColor(getPawn(0, x2, y2))) {
                    blackPlayer();
                } else {
                    whitePlayer();
                }
            } else {
                if (((y2 == 0 && !blackColor(getPawn(0, x2, y2))) || (y2 == 7 && blackColor(getPawn(0, x2, y2)))) && !pawnOrQueen(getPawn(0, x2, y2))) {
                    newQueen(x2, y2);
                }
                printBoard();
                if (blackColor(getPawn(0, x2, y2))) {
                    whitePlayer();
                } else {
                    blackPlayer();
                }
            }
        } else {
            if (blackColor(getPawn(0, x1, y1))) {
                whitePlayer();
            } else {
                blackPlayer();
            }
        }
    }

    /**
     * Validation of queen movement/beating.
     * Restriction on movement due to board size.
     * Restriction due to white fields.
     * Checking whether a field is empty in turn.
     * If there is another pawn of the opposite color on the field at a distance of 1, and the next field is empty, then the dam beat is executed.
     * Removing the beaten pawn from the game.
     * Checking further possibility of the queen movement.
     */

    public static boolean isQueenJumpValid(int x1, int y1, int x2, int y2, boolean blackColor) {
        if (x1 > x2 && y1 > y2 && !containsPawn(x1 - 1, y1 - 1)) {
            outOfGame(x1, y1);
            return isQueenMoveValid(x1 - 1, y1 - 1, x2, y2, blackColor);
        } else if (x1 > x2 && y1 < y2 && !containsPawn(x1 - 1, y1 + 1)) {
            outOfGame(x1, y1);
            return isQueenMoveValid(x1 - 1, y1 + 1, x2, y2, blackColor);
        } else if (x1 < x2 && y1 > y2 && !containsPawn(x1 + 1, y1 - 1)) {
            outOfGame(x1, y1);
            return isQueenMoveValid(x1 + 1, y1 - 1, x2, y2, blackColor);
        } else if (x1 < x2 && y1 < y2 && !containsPawn(x1 + 1, y1 + 1)) {
            outOfGame(x1, y1);
            return isQueenMoveValid(x1 + 1, y1 + 1, x2, y2, blackColor);
        } else {
            return false;
        }
    }

    //if the possibility of beating
    public static boolean isQueenMoveValid(int x1, int y1, int x2, int y2, boolean blackColor) {
        if (containsPawn(x1, y1)) {
            if (blackColor && !blackColor(getPawn(0, x1, y1))) {
                return isQueenJumpValid(x1, y1, x2, y2, blackColor);
            } else if (!blackColor && blackColor(getPawn(0, x1, y1))) {
                return isQueenJumpValid(x1, y1, x2, y2, blackColor);
            } else {
                return false;
            }
        } else {
            if (x1 == x2 && y1 == y2) {
                return true;
            } else if (x1 > x2 && y1 > y2) {
                return isQueenMoveValid(x1 - 1, y1 - 1, x2, y2, blackColor);
            } else if (x1 > x2 && y1 < y2) {
                return isQueenMoveValid(x1 - 1, y1 + 1, x2, y2, blackColor);
            } else if (x1 < x2 && y1 > y2) {
                return isQueenMoveValid(x1 + 1, y1 - 1, x2, y2, blackColor);
            } else {
                return isQueenMoveValid(x1 + 1, y1 + 1, x2, y2, blackColor);
            }
        }
    }

    public static boolean queenJump(int x1, int y1, int x2, int y2) {
        if (x2 < 0 || x2 > 7 || y2 < 0 || y2 > 7) {
            System.out.println("Movement out of the board");
            return false;
        } else if ((x2 + y2) % 2 == 0) {
            System.out.println("Movement to the white field is not possible");
            return false;
        } else if (x1 > x2 && y1 > y2) {
            return isQueenMoveValid(x1 - 1, y1 - 1, x2, y2, blackColor(getPawn(0, x1, y1)));
        } else if (x1 > x2 && y1 < y2) {
            return isQueenMoveValid(x1 - 1, y1 + 1, x2, y2, blackColor(getPawn(0, x1, y1)));
        } else if (x1 < x2 && y1 > y2) {
            return isQueenMoveValid(x1 + 1, y1 - 1, x2, y2, blackColor(getPawn(0, x1, y1)));
        } else if (x1 < x2 && y1 < y2) {
            return isQueenMoveValid(x1 + 1, y1 + 1, x2, y2, blackColor(getPawn(0, x1, y1)));
        } else {
            return false;
        }
    }


    /**
     * Checking the information whether a pawn is beaten in a given move.
     * Split according to the pawn and queen. If there is a beat in the move, a message is printed.
     */

    public static boolean jumpWhite(int x1, int y1, int k) {
        if (!pawnOrQueen(getPawn(0, x1, y1)) && pawnInGame(getPawn(0, x1, y1)) && (jumpPawn(x1, y1, x1 + 2, y1 + 2)
                || jumpPawn(x1, y1, x1 + 2, y1 - 2) || jumpPawn(x1, y1, x1 - 2, y1 + 2) || jumpPawn(x1, y1, x1 - 2, y1 - 2))) {
            System.out.println("\nBEATING");
            return true;
        } else if (pawnOrQueen(getPawn(0, x1, y1)) && (jumpQueen(x1 - 1, y1 - 1, x1 - 2, y1 - 2, false) ||
                jumpQueen(x1 - 1, y1 + 1, x1 - 2, y1 + 2, false) ||
                jumpQueen(x1 + 1, y1 - 1, x1 + 2, y1 - 2, false) ||
                jumpQueen(x1 + 1, y1 + 1, x1 + 2, y1 + 2, false))) {
            System.out.println("\nBEATING");
            return true;
        } else if (k < 5) {
            return jumpWhite((int) getPosX(pawnBits(white1, k + 1)), (int) getPosY(pawnBits(white1, k + 1)), k + 1);
        } else if (k < 11) {
            return jumpWhite((int) getPosX(pawnBits(white2, k - 5)), (int) getPosY(pawnBits(white2, k - 5)), k + 1);
        } else {
            return false;
        }
    }


    public static boolean jumpBlack(int x1, int y1, int k) {
        if (!pawnOrQueen(getPawn(0, x1, y1)) && pawnInGame(getPawn(0, x1, y1)) && (jumpPawn(x1, y1, x1 + 2, y1 + 2)
                || jumpPawn(x1, y1, x1 + 2, y1 - 2) || jumpPawn(x1, y1, x1 - 2, y1 + 2) || jumpPawn(x1, y1, x1 - 2, y1 - 2))) {
            System.out.println("\nBEATING");
            return true;
        } else if (pawnOrQueen(getPawn(0, x1, y1)) && (jumpQueen(x1 - 1, y1 - 1, x1 - 2, y1 - 2, true) ||
                jumpQueen(x1 - 1, y1 + 1, x1 - 2, y1 + 2, true) ||
                jumpQueen(x1 + 1, y1 - 1, x1 + 2, y1 - 2, true) ||
                jumpQueen(x1 + 1, y1 + 1, x1 + 2, y1 + 2, true))) {
            System.out.println("\nBEATING");
            return true;
        } else if (k < 5) {
            return jumpBlack((int) getPosX(pawnBits(black1, k + 1)), (int) getPosY(pawnBits(black1, k + 1)), k + 1);
        } else if (k < 11) {
            return jumpBlack((int) getPosX(pawnBits(black2, k - 5)), (int) getPosY(pawnBits(black2, k - 5)), k + 1);
        } else {
            return false;
        }
    }

    //Checking if there is a queen beating in a given move
    public static boolean jumpQueen(int x1, int y1, int x2, int y2, boolean blackColor) {
        if (x2 < 0 || x2 > 7 || y2 < 0 || y2 > 7) {
            return false;
        } else if (containsPawn(x1, y1) && !containsPawn(x2, y2)) {
            if (blackColor && !blackColor(getPawn(0, x1, y1))) {
                return true;
            } else return !blackColor && blackColor(getPawn(0, x1, y1));
        } else if (!containsPawn(x1, y1)) {
            if (x1 > x2 && y1 > y2) {
                return jumpQueen(x1 - 1, y1 - 1, x1 - 2, y1 - 2, blackColor);
            } else if (x1 > x2 && y1 < y2) {
                return jumpQueen(x1 - 1, y1 + 1, x1 - 2, y1 + 2, blackColor);
            } else if (x1 < x2 && y1 > y2) {
                return jumpQueen(x1 + 1, y1 - 1, x1 + 2, y1 - 2, blackColor);
            } else {
                return jumpQueen(x1 + 1, y1 + 1, x1 + 2, y1 + 2, blackColor);
            }
        } else {
            return false;
        }
    }

}