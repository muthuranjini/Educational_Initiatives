import java.util.*;

// ======================= Command Pattern =======================
interface Command {
    void execute();
}

class MoveCommand implements Command {
    private final Rover rover;
    public MoveCommand(Rover rover) { this.rover = rover; }
    public void execute() { rover.move(); }
}

class TurnLeftCommand implements Command {
    private final Rover rover;
    public TurnLeftCommand(Rover rover) { this.rover = rover; }
    public void execute() { rover.turnLeft(); }
}

class TurnRightCommand implements Command {
    private final Rover rover;
    public TurnRightCommand(Rover rover) { this.rover = rover; }
    public void execute() { rover.turnRight(); }
}

class CommandFactory {
    private final Map<Character, Command> commandMap;
    public CommandFactory(Rover rover) {
        commandMap = Map.of(
            'M', new MoveCommand(rover),
            'L', new TurnLeftCommand(rover),
            'R', new TurnRightCommand(rover)
        );
    }
    public Command getCommand(char c) {
        return commandMap.get(c);
    }
}

// ======================= Direction Strategy =======================
enum Direction {
    NORTH {
        public void move(Rover r) { r.setY(r.getY() + 1); }
        public Direction left() { return WEST; }
        public Direction right() { return EAST; }
    },
    SOUTH {
        public void move(Rover r) { r.setY(r.getY() - 1); }
        public Direction left() { return EAST; }
        public Direction right() { return WEST; }
    },
    EAST {
        public void move(Rover r) { r.setX(r.getX() + 1); }
        public Direction left() { return NORTH; }
        public Direction right() { return SOUTH; }
    },
    WEST {
        public void move(Rover r) { r.setX(r.getX() - 1); }
        public Direction left() { return SOUTH; }
        public Direction right() { return NORTH; }
    };

    public abstract void move(Rover r);
    public abstract Direction left();
    public abstract Direction right();
}

// ======================= Grid & Obstacles =======================
class Grid {
    private final int width, height;
    private final Set<String> obstacles = new HashSet<>();

    public Grid(int width, int height) {
        this.width = width; this.height = height;
    }

    public void addObstacle(int x, int y) { obstacles.add(x + "," + y); }
    public boolean isObstacle(int x, int y) { return obstacles.contains(x + "," + y); }
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }
}

// ======================= Rover =======================
class Rover {
    private int x, y;
    private Direction direction;
    private final Grid grid;

    public Rover(int x, int y, Direction dir, Grid grid) {
        this.x = x; this.y = y; this.direction = dir; this.grid = grid;
    }

    public void move() {
        int oldX = x, oldY = y;
        direction.move(this);
        if (!grid.isWithinBounds(x, y) || grid.isObstacle(x, y)) {
            x = oldX; y = oldY;
            System.out.println("Movement blocked! Obstacle or boundary encountered.");
        }
    }

    public void turnLeft() { direction = direction.left(); }
    public void turnRight() { direction = direction.right(); }
    public String statusReport() { return "Rover is at (" + x + ", " + y + ") facing " + direction; }

    // Getters & Setters
    public int getX() { return x; } public int getY() { return y; }
    public void setX(int x) { this.x = x; } public void setY(int y) { this.y = y; }
}

// ======================= Main App =======================
public class MarsRoverApp {
    public static void main(String[] args) {
        Grid grid = new Grid(10, 10);
        grid.addObstacle(2, 2);
        grid.addObstacle(3, 5);

        Rover rover = new Rover(0, 0, Direction.NORTH, grid);
        CommandFactory factory = new CommandFactory(rover);

        char[] commands = {'M', 'M', 'R', 'M', 'L', 'M'};
        for (char c : commands) {
            Command cmd = factory.getCommand(c);
            if (cmd != null) cmd.execute();
        }

        System.out.println(rover.statusReport());
    }
}
