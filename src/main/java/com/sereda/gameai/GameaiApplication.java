package com.sereda.gameai;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.SecureRandom;
import java.util.*;


@SpringBootApplication
public class GameaiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(GameaiApplication.class, args);
    }

    Map<Move, Integer> movesMap = new HashMap<>();
    Map<Integer, Map<Move, Integer>> iterationsMap = new HashMap<>();
    int iterations = 0;
    int step = 0;
    int[][] desc = new int[][]{
            {1, 1, 2, 0, 0, 1, 1},
            {1, 1, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {1, 1, 0, 0, 0, 1, 1},
            {1, 1, 0, 0, 0, 1, 1},
    };
    Set<Point> deskSet = new HashSet<>();
    Set<Point> figureSet = new HashSet<>();

    static Point magnetPoint = new Point(2, 0);

    public static Map.Entry<Move, Integer> sortByValue(final Map<Move, Integer> map) {

        Map.Entry<Move, Integer> minEntry = null;

        for (Map.Entry<Move, Integer> entry : map.entrySet()) {
            if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                minEntry = entry;
            }
        }
        return minEntry;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeGame();

        for (int i = 0; true; ) {
            if (figureSet.size() == 1 && figureSet.contains(new Point(2, 0))) {
                System.out.printf("SUCCESS on iteration %d !!!%n", iterations);
                System.out.println(figureSet);
                print();
                break;
            } else {
                iterations++;
                if (iterations % 10000 == 0) {
                    System.out.printf("Fail on iteration %d !!!", iterations);
                    System.out.println();
                }
                initializeGame();
                proceedGame();
            }
        }

    }

    private void print() {
        System.out.println("_____________");

        for (int i = 0; i < desc.length; i++) {
            for (int j = 0; j < desc[i].length; j++) {
                Point point = new Point(i, j);
                if (figureSet.contains(point)) {
                    System.out.print(" 0 ");
                } else if (deskSet.contains(point)) {
                    System.out.print(" 2 ");
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
        System.out.println("_____________");
        System.out.println("figures " + figureSet.size() + " desc " + deskSet.size());


    }

    private void initializeGame() {
        deskSet.clear();
        figureSet.clear();
        for (int i = 0; i < desc.length; i++) {
            for (int j = 0; j < desc[i].length; j++) {
                if (desc[i][j] == 2) {
                    deskSet.add(new Point(i, j));
                }
                if (desc[i][j] == 0) {
                    figureSet.add(new Point(i, j));
                }
            }
        }
    }


    private List<Move> proceedGame() {
        int step = 0;
        List<Move> stepsRecorder = new LinkedList<>();
        while (figureSet.size() > 1) {
            step++;
            List<Move> possibleMoves = new LinkedList<>();
            for (Point point :
                    figureSet) {
                possibleMoves.addAll(getAllPossibleMoves(point));
            }
            if (possibleMoves.size() == 0) {
                return null;
            }

            SecureRandom random = new SecureRandom ();
            int min = 0;
            int max = possibleMoves.size() - 1;
            int rand = 0;
            if (possibleMoves.size() > 1) {
                rand = min + random.nextInt(max - min);
            }
            Map<Move, Integer> moveIntegerMap = iterationsMap.get(step);
            Move move = possibleMoves.get(rand);

            move(move);
            stepsRecorder.add(move);
        }
        return stepsRecorder;

    }

    private void move(Move move) {
        figureSet.add(move.getEndPoint());
        figureSet.remove(move.getStartPoint());
        figureSet.remove(move.getMidPoint());
        deskSet.add(move.getStartPoint());
        deskSet.add(move.getMidPoint());
        deskSet.remove(move.getEndPoint());
    }

    private List<Move> getAllPossibleMoves(Point point) {
        List<Move> result = new LinkedList<>();
        checkUp(point).ifPresent(result::add);
        checkDown(point).ifPresent(result::add);
        checkLeft(point).ifPresent(result::add);
        checkRight(point).ifPresent(result::add);
        return result;
    }

    private Optional<Move> checkUp(Point startPoint) {
        Point endPoint = new Point(startPoint.getX(), startPoint.getY() - 2);
        Point midPoint = new Point(startPoint.getX(), startPoint.getY() - 1);
        if (deskSet.contains(endPoint) && figureSet.contains(midPoint)) {
            return Optional.of(new Move(startPoint, midPoint, endPoint));
        } else return Optional.empty();
    }

    private Optional<Move> checkDown(Point startPoint) {
        Point endPoint = new Point(startPoint.getX(), startPoint.getY() + 2);
        Point midPoint = new Point(startPoint.getX(), startPoint.getY() + 1);
        if (deskSet.contains(endPoint) && figureSet.contains(midPoint)) {
            return Optional.of(new Move(startPoint, midPoint, endPoint));
        } else return Optional.empty();
    }

    private Optional<Move> checkLeft(Point startPoint) {
        Point endPoint = new Point(startPoint.getX() - 2, startPoint.getY());
        Point midPoint = new Point(startPoint.getX() - 1, startPoint.getY());
        if (deskSet.contains(endPoint) && figureSet.contains(midPoint)) {
            return Optional.of(new Move(startPoint, midPoint, endPoint));
        } else return Optional.empty();
    }

    private Optional<Move> checkRight(Point startPoint) {
        Point endPoint = new Point(startPoint.getX() + 2, startPoint.getY());
        Point midPoint = new Point(startPoint.getX() + 1, startPoint.getY());
        if (deskSet.contains(endPoint) && figureSet.contains(midPoint)) {
            return Optional.of(new Move(startPoint, midPoint, endPoint));
        } else return Optional.empty();
    }


}

final class Move implements Comparable<Move> {
    int distance;
    private Point startPoint;
    private Point midPoint;
    private Point endPoint;

    Move(Point startPoint, Point midPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.midPoint = midPoint;
        this.endPoint = endPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public Point getMidPoint() {
        return midPoint;
    }

    @Override
    public int compareTo(Move move) {
        return this.startPoint.compareTo(move.startPoint);
    }
}

final class Point implements Comparable<Point> {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public int compareTo(Point point) {
        return (x + y) > (point.x + point.y) ? 1 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

}
