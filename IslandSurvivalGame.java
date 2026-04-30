import java.util.List;
import javax.swing.*;

public class IslandSurvivalGame extends JFrame {

    private Ecosystem ecosystem;
    private int score;
    private int nightCount;

    // Placeholder image icons
    private ImageIcon popupIcon = new ImageIcon("popup_placeholder.png");

    public IslandSurvivalGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomeScreen welcome = new WelcomeScreen(() -> {
                IslandSurvivalGame game = new IslandSurvivalGame();
                game.startGame();
            });
            welcome.setVisible(true);
        });
    }

    public void startGame() {
        ecosystem = new Ecosystem();
        score = 0;
        nightCount = 1;
        gameLoop();
    }

    public void gameLoop() {
        while (!ecosystem.isEcosystemDead()) {

            // 1. Process cascading extinctions from previous nights
            if (nightCount > 1) {
                List<String> diedTonight = ecosystem.processNightExtinctions();
                if (!diedTonight.isEmpty()) {
                    String message = "NIGHT " + nightCount + " Update\n\n" +
                            "Due to the collapsing food web, the following species died out overnight:\n\n" +
                            String.join("\n\n", diedTonight);

                    JOptionPane.showMessageDialog(null, message, "Ecological Cascade",
                            JOptionPane.WARNING_MESSAGE, popupIcon);
                }
            }

            // Check if ecosystem collapsed before making a choice
            if (ecosystem.isEcosystemDead()) {
                break;
            }

            // 2. Player Action Choice
            String[] actions = {"Build Traps", "Go Fishing", "Scavenge & Build Fire"};
            int actionChoice = JOptionPane.showOptionDialog(null,
                    "Night " + nightCount + " | Score: " + score + "\n\nWhat will you do to survive today?",
                    "Survival Action", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    popupIcon, actions, actions[0]);

            if (actionChoice == JOptionPane.CLOSED_OPTION) System.exit(0);

            // 3. Player Location Choice & Extinction Mapping
            String targetSpecies = "";
            String actionReason = "";
            String[] locations;

            if (actionChoice == 0) { // Build Traps
                locations = new String[]{"Coral Reef", "Mangrove Roots", "Jungle Path"};
                int locChoice = showLocationDialog("Where will you place your traps?", locations);
                if (locChoice == 0) { targetSpecies = "Sea Stars"; actionReason = "accidentally crushed by heavy trap anchors on the fragile reef."; }
                else if (locChoice == 1) { targetSpecies = "Blue Crabs"; actionReason = "over-trapped within the mangrove roots until none remained."; }
                else if (locChoice == 2) { targetSpecies = "Wild Boar"; actionReason = "snared aggressively along the jungle paths to extinction."; }

            } else if (actionChoice == 1) { // Go Fishing
                locations = new String[]{"Lagoon", "Deep Water", "Estuary"};
                int locChoice = showLocationDialog("Where will you fish?", locations);
                if (locChoice == 0) { targetSpecies = "Fiddler Crabs"; actionReason = "wiped out as dragging lagoon nets utterly destroyed their habitat."; }
                else if (locChoice == 1) { targetSpecies = "Sea Turtles"; actionReason = "tangled and tragically drowned in heavy deep water nets."; }
                else if (locChoice == 2) { targetSpecies = "Raccoons"; actionReason = "caught and drowned in estuary nets while they were foraging."; }

            } else { // Scavenge & Build Fire
                locations = new String[]{"Forest Edge", "Beach Sand", "Cliffs"};
                int locChoice = showLocationDialog("Where will you scavenge?", locations);
                if (locChoice == 0) { targetSpecies = "Armadillos"; actionReason = "hunted to total extinction at the forest edge by your group."; }
                else if (locChoice == 1) { targetSpecies = "Clams"; actionReason = "completely overharvested and dug out from the beach sand."; }
                else if (locChoice == 2) { targetSpecies = "Pelicans"; actionReason = "driven away forever as you destroyed their nests on the cliffs."; }
            }

            // 4. Resolve Direct Extinction
            String resultMsg = ecosystem.triggerDirectExtinction(targetSpecies, actionReason);
            boolean alreadyExtinct = resultMsg.contains("already extinct");

            if (alreadyExtinct) {
                JOptionPane.showMessageDialog(null, resultMsg, "Action Results",
                        JOptionPane.INFORMATION_MESSAGE, popupIcon);

                JOptionPane.showMessageDialog(null,
                        "You wasted the day searching for resources that no longer exist.\n" +
                        "With no food or shelter secured, you barely survive through the night.\n\n" +
                        "Score: " + score + " → " + (score - 50),
                        "A Hungry Night...",
                        JOptionPane.WARNING_MESSAGE, popupIcon);

                score -= 50;
            } else {
                JOptionPane.showMessageDialog(null, resultMsg, "Action Results",
                        JOptionPane.INFORMATION_MESSAGE, popupIcon);

                score += 100;
            }

            nightCount++;
        }

        gameOver();
    }

    private int showLocationDialog(String prompt, String[] locations) {
        int choice = JOptionPane.showOptionDialog(null, prompt, "Choose Location",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                popupIcon, locations, locations[0]);
        if (choice == JOptionPane.CLOSED_OPTION) System.exit(0);
        return choice;
    }

    private void gameOver() {
        String finalMessage = "GAME OVER\n\nAll non-producer species on the island have died out.\n" +
                "Without a functional food web, the island can no longer support you.\n" +
                "You have died of starvation.\n\n" +
                "Total Nights Survived: " + (nightCount - 1) + "\n" +
                "Final Score: " + score;

        JOptionPane.showMessageDialog(null, finalMessage, "Game Over",
                JOptionPane.ERROR_MESSAGE, popupIcon);
        System.exit(0);
    }
}
