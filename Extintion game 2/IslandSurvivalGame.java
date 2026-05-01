import java.util.List;
import javax.swing.*;

public class IslandSurvivalGame extends JFrame {

    private Ecosystem ecosystem;
    private int score;
    private int nightCount;
    private boolean gameStarted = false;

    // Image icons
    private ImageIcon gameStartIcon = new ImageIcon("Pictures for extinction game 1/gamestart page.png");
    private ImageIcon nightlyUpdateIcon = new ImageIcon("Pictures for extinction game 1/nightly update.png");
    private ImageIcon homeScreenIcon = new ImageIcon("Pictures for extinction game 1/homescreen.png");
    
    // Animal icons
    private ImageIcon seaStarsIcon = new ImageIcon("Pictures for extinction game 1/starfish.png");
    private ImageIcon blueCrabsIcon = new ImageIcon("Pictures for extinction game 1/crab.png");
    private ImageIcon wildBoarIcon = new ImageIcon("Pictures for extinction game 1/wild boar.png");
    private ImageIcon fiddlerCrabsIcon = new ImageIcon("Pictures for extinction game 1/crab.png");
    private ImageIcon seaTurtlesIcon = new ImageIcon("Pictures for extinction game 1/sea turtle.png");
    private ImageIcon racoonsIcon = new ImageIcon("Pictures for extinction game 1/racoon.png");
    private ImageIcon armadillosIcon = new ImageIcon("Pictures for extinction game 1/armadillo.png");
    private ImageIcon clamsIcon = new ImageIcon("Pictures for extinction game 1/clam.png");
    private ImageIcon pelicansIcon = new ImageIcon("Pictures for extinction game 1/pelican.png");
    
    // Action icons
    private ImageIcon buildTrapIcon = new ImageIcon("Pictures for extinction game 1/build trap.png");
    private ImageIcon goFishingIcon = new ImageIcon("Pictures for extinction game 1/go fishing.png");
    private ImageIcon fireIcon = new ImageIcon("Pictures for extinction game 1/fire.png");
    private ImageIcon coralMangroveJunglePathIcon = new ImageIcon("coralmangrovejunglepath.png");
    private ImageIcon forestEdgeBeachCliffIcon = new ImageIcon("forestedgebeachcliff.png");
    private ImageIcon lagoonDeepWaterEstuaryIcon = new ImageIcon("lagoondeepwaterestuary.png");
    
    private ImageIcon popupIcon;

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
        popupIcon = gameStartIcon;  // Show gamestart page on first launch
        gameStarted = true;
        gameLoop();
    }

    public void gameLoop() {
        // Switch to homescreen after game starts
        popupIcon = homeScreenIcon;
        
        while (!ecosystem.isEcosystemDead()) {

            // 1. Process cascading extinctions from previous nights
            if (nightCount > 1) {
                List<String> diedTonight = ecosystem.processNightExtinctions();
                if (!diedTonight.isEmpty()) {
                    String message = "NIGHT " + nightCount + " Update\n\n" +
                            "Due to the collapsing food web, the following species died out overnight:\n\n" +
                            String.join("\n\n", diedTonight);

                    ImageIcon scaledIcon = scaleImageIcon(nightlyUpdateIcon, 400, 300);
                    javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(0, 15));
                    panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    
                    javax.swing.JLabel imageLabel = new javax.swing.JLabel(scaledIcon);
                    imageLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
                    
                    javax.swing.JLabel textLabel = new javax.swing.JLabel("<html><div style='text-align: center;'>" + message.replace("\n", "<br>") + "</div></html>");
                    textLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
                    
                    panel.add(imageLabel, java.awt.BorderLayout.NORTH);
                    panel.add(textLabel, java.awt.BorderLayout.SOUTH);
                    
                    JOptionPane.showMessageDialog(null, panel, "Ecological Cascade",
                            JOptionPane.WARNING_MESSAGE, null);
                }
            }

            // Check if ecosystem collapsed before making a choice
            if (ecosystem.isEcosystemDead()) {
                break;
            }

            // 2. Player Action Choice
            String[] actions = {"Build Traps", "Go Fishing", "Scavenge & Build Fire"};
            String actionPrompt = "Night " + nightCount + " | Score: " + score + "\n\nWhat will you do to survive today?";
            
            ImageIcon scaledHomeIcon = scaleImageIcon(homeScreenIcon, 400, 300);
            javax.swing.JPanel actionPanel = new javax.swing.JPanel(new java.awt.BorderLayout(0, 15));
            actionPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            javax.swing.JLabel actionImageLabel = new javax.swing.JLabel(scaledHomeIcon);
            actionImageLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
            
            javax.swing.JLabel actionTextLabel = new javax.swing.JLabel("<html><div style='text-align: center;'>" + actionPrompt.replace("\n", "<br>") + "</div></html>");
            actionTextLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
            
            actionPanel.add(actionImageLabel, java.awt.BorderLayout.NORTH);
            actionPanel.add(actionTextLabel, java.awt.BorderLayout.SOUTH);
            
            int actionChoice = JOptionPane.showOptionDialog(null,
                    actionPanel,
                    "Survival Action", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, actions, actions[0]);

            if (actionChoice == JOptionPane.CLOSED_OPTION) System.exit(0);

            // 3. Player Location Choice & Extinction Mapping
            String targetSpecies = "";
            String actionReason = "";
            String[] locations;
            ImageIcon actionIcon = homeScreenIcon;

            if (actionChoice == 0) { // Build Traps
                actionIcon = buildTrapIcon;
                showActionDialog("You've decided to build traps!", buildTrapIcon);
                locations = new String[]{"Coral Reef", "Mangrove Roots", "Jungle Path"};
                int locChoice = showLocationDialog("Where will you place your traps?", locations, coralMangroveJunglePathIcon);
                if (locChoice == 0) { targetSpecies = "Sea Stars"; actionReason = "accidentally crushed by heavy trap anchors on the fragile reef."; }
                else if (locChoice == 1) { targetSpecies = "Blue Crabs"; actionReason = "over-trapped within the mangrove roots until none remained."; }
                else if (locChoice == 2) { targetSpecies = "Wild Boar"; actionReason = "snared aggressively along the jungle paths to extinction."; }

            } else if (actionChoice == 1) { // Go Fishing
                actionIcon = goFishingIcon;
                showActionDialog("You've decided to go fishing!", goFishingIcon);
                locations = new String[]{"Lagoon", "Deep Water", "Estuary"};
                int locChoice = showLocationDialog("Where will you fish?", locations, lagoonDeepWaterEstuaryIcon);
                if (locChoice == 0) { targetSpecies = "Fiddler Crabs"; actionReason = "wiped out as dragging lagoon nets utterly destroyed their habitat."; }
                else if (locChoice == 1) { targetSpecies = "Sea Turtles"; actionReason = "tangled and tragically drowned in heavy deep water nets."; }
                else if (locChoice == 2) { targetSpecies = "Raccoons"; actionReason = "caught and drowned in estuary nets while they were foraging."; }

            } else { // Scavenge & Build Fire
                actionIcon = fireIcon;
                showActionDialog("You've decided to scavenge and build a fire!", fireIcon);
                locations = new String[]{"Forest Edge", "Beach Sand", "Cliffs"};
                int locChoice = showLocationDialog("Where will you scavenge?", locations, forestEdgeBeachCliffIcon);
                if (locChoice == 0) { targetSpecies = "Armadillos"; actionReason = "hunted to total extinction at the forest edge by your group."; }
                else if (locChoice == 1) { targetSpecies = "Clams"; actionReason = "completely overharvested and dug out from the beach sand."; }
                else if (locChoice == 2) { targetSpecies = "Pelicans"; actionReason = "driven away forever as you destroyed their nests on the cliffs."; }
            }

            // 4. Resolve Direct Extinction
            String resultMsg = ecosystem.triggerDirectExtinction(targetSpecies, actionReason);
            boolean alreadyExtinct = resultMsg.contains("already extinct");
            ImageIcon speciesIcon = getSpeciesIcon(targetSpecies);

            if (alreadyExtinct) {
                showImageWithText(resultMsg, speciesIcon, "Action Results");

                JOptionPane.showMessageDialog(null,
                        "You wasted the day searching for resources that no longer exist.\n" +
                        "With no food or shelter secured, you barely survive through the night.\n\n" +
                        "Score: " + score + " → " + (score - 50),
                        "A Hungry Night...",
                        JOptionPane.WARNING_MESSAGE, nightlyUpdateIcon);

                score -= 50;
            } else {
                showImageWithText(resultMsg, speciesIcon, "Action Results");

                score += 100;
            }

            nightCount++;
        }

        gameOver();
    }

    private int showLocationDialog(String prompt, String[] locations, ImageIcon icon) {
        // Scale the icon for better display (larger, about half the page)
        ImageIcon scaledIcon = scaleImageIcon(icon, 400, 300);
        
        // Create a panel with image on top and text below
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(0, 15));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        javax.swing.JLabel imageLabel = new javax.swing.JLabel(scaledIcon);
        imageLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        javax.swing.JLabel textLabel = new javax.swing.JLabel("<html><div style='text-align: center;'>" + prompt + "</div></html>");
        textLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        panel.add(imageLabel, java.awt.BorderLayout.NORTH);
        panel.add(textLabel, java.awt.BorderLayout.SOUTH);
        
        int choice = JOptionPane.showOptionDialog(null, panel, "Choose Location",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, locations, locations[0]);
        if (choice == JOptionPane.CLOSED_OPTION) System.exit(0);
        return choice;
    }
    
    private void showActionDialog(String message, ImageIcon icon) {
        // Scale the icon for better display (larger, about half the page)
        ImageIcon scaledIcon = scaleImageIcon(icon, 400, 300);
        
        // Create a panel with image on top and text below
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(0, 15));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        javax.swing.JLabel imageLabel = new javax.swing.JLabel(scaledIcon);
        imageLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        javax.swing.JLabel textLabel = new javax.swing.JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        textLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        panel.add(imageLabel, java.awt.BorderLayout.NORTH);
        panel.add(textLabel, java.awt.BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(null, panel, "Action",
                JOptionPane.INFORMATION_MESSAGE, null);
    }
    
    private void showImageWithText(String message, ImageIcon icon, String title) {
        // Scale the icon for better display (larger, about half the page)
        ImageIcon scaledIcon = scaleImageIcon(icon, 400, 300);
        
        // Create a panel with image on top and text below
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(0, 15));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        javax.swing.JLabel imageLabel = new javax.swing.JLabel(scaledIcon);
        imageLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        javax.swing.JLabel textLabel = new javax.swing.JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        textLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        panel.add(imageLabel, java.awt.BorderLayout.NORTH);
        panel.add(textLabel, java.awt.BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(null, panel, title,
                JOptionPane.INFORMATION_MESSAGE, null);
    }
    
    private ImageIcon scaleImageIcon(ImageIcon icon, int width, int height) {
        if (icon == null || icon.getImage() == null) {
            return icon;
        }
        int originalWidth = icon.getIconWidth();
        int originalHeight = icon.getIconHeight();
        if (originalWidth <= 0 || originalHeight <= 0) {
            return icon;
        }
        double scale = Math.min((double) width / originalWidth, (double) height / originalHeight);
        int scaledWidth = (int) Math.max(1, originalWidth * scale);
        int scaledHeight = (int) Math.max(1, originalHeight * scale);
        java.awt.Image scaledImage = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
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

    private ImageIcon getSpeciesIcon(String species) {
        switch (species) {
            case "Sea Stars":
                return seaStarsIcon;
            case "Blue Crabs":
                return blueCrabsIcon;
            case "Wild Boar":
                return wildBoarIcon;
            case "Fiddler Crabs":
                return fiddlerCrabsIcon;
            case "Sea Turtles":
                return seaTurtlesIcon;
            case "Raccoons":
                return racoonsIcon;
            case "Armadillos":
                return armadillosIcon;
            case "Clams":
                return clamsIcon;
            case "Pelicans":
                return pelicansIcon;
            default:
                return homeScreenIcon;
        }
    }
}
