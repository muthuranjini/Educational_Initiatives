import java.util.*;

// ======================= State Interface =======================
interface RocketState {
    void handle(Rocket rocket, int seconds);
    String getName();
}

// ======================= Concrete States =======================
class PreLaunchState implements RocketState {
    public void handle(Rocket rocket, int seconds) {
        System.out.println("All systems are 'Go' for launch.");
    }
    public String getName() { return "Pre-Launch"; }
}

class LaunchState implements RocketState {
    public void handle(Rocket rocket, int seconds) {
        for (int i = 1; i <= seconds; i++) {
            if (rocket.getFuel() <= 0) {
                System.out.println("Mission Failed due to insufficient fuel.");
                rocket.setState(new MissionFailedState());
                return;
            }
            rocket.consumeFuel(5);
            rocket.increaseAltitude(10);
            rocket.increaseSpeed(1000);

            System.out.printf("Stage: %d, Fuel: %d%%, Altitude: %d km, Speed: %d km/h%n",
                    rocket.getStage(), rocket.getFuel(), rocket.getAltitude(), rocket.getSpeed());

            if (rocket.getAltitude() >= 50 && rocket.getStage() == 1) {
                System.out.println("Stage 1 complete. Separating stage. Entering Stage 2.");
                rocket.nextStage();
            }
            if (rocket.getAltitude() >= 100) {
                System.out.println("Orbit achieved! Mission Successful.");
                rocket.setState(new MissionSuccessState());
                return;
            }
        }
    }
    public String getName() { return "Launch"; }
}

class MissionSuccessState implements RocketState {
    public void handle(Rocket rocket, int seconds) {
        System.out.println("Mission already successful. No further actions required.");
    }
    public String getName() { return "Mission Success"; }
}

class MissionFailedState implements RocketState {
    public void handle(Rocket rocket, int seconds) {
        System.out.println("Mission already failed. Restart to try again.");
    }
    public String getName() { return "Mission Failed"; }
}

// ======================= Rocket (Context) =======================
class Rocket {
    private RocketState state;
    private int stage = 1, fuel = 100, altitude = 0, speed = 0;

    public Rocket() { this.state = new PreLaunchState(); }

    public void setState(RocketState state) { this.state = state; }
    public void startChecks() { this.state.handle(this, 0); }
    public void launch() { this.setState(new LaunchState()); }
    public void fastForward(int seconds) { this.state.handle(this, seconds); }

    // Rocket operations
    public void consumeFuel(int amount) { fuel -= amount; }
    public void increaseAltitude(int km) { altitude += km; }
    public void increaseSpeed(int kmh) { speed += kmh; }
    public void nextStage() { stage = 2; }

    // Getters
    public int getStage() { return stage; }
    public int getFuel() { return fuel; }
    public int getAltitude() { return altitude; }
    public int getSpeed() { return speed; }
    public String getStateName() { return state.getName(); }
}

// ======================= Main Simulator =======================
public class RocketLaunchSimulator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Rocket rocket = new Rocket();

        System.out.println("Rocket Launch Simulator Started. Type 'start_checks', 'launch', or 'fast_forward X'.");

        while (true) {
            System.out.print("> ");
            String input = sc.nextLine().trim().toLowerCase();

            try {
                if (input.equals("start_checks")) {
                    rocket.startChecks();
                } else if (input.equals("launch")) {
                    rocket.launch();
                    System.out.println("Launch initiated. Simulation started...");
                } else if (input.startsWith("fast_forward")) {
                    int seconds = Integer.parseInt(input.split(" ")[1]);
                    rocket.fastForward(seconds);
                } else if (input.equals("exit")) {
                    System.out.println("Exiting simulator.");
                    break;
                } else {
                    System.out.println("Invalid command. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error processing command: " + e.getMessage());
            }
        }
        sc.close();
    }
}
