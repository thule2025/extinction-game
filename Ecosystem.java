import java.util.*;

public class Ecosystem {
    public List<String> aliveSpecies;
    public Map<String, List<String>> foodWebDependencies;
    
    // Maps species name -> the night number on which it will die
    public Map<String, Integer> scheduledExtinctions;
    private int currentNight;

    public Ecosystem() {
        currentNight = 1;
        aliveSpecies = new ArrayList<>(Arrays.asList(
            "American Crocodiles", "Bobcats", "Wild Boar", "Raccoons",
            "Pelicans", "Sea Stars", "Sea Turtles", "Clams",
            "Armadillos", "Blue Crabs", "Fiddler Crabs"
        ));
        
        scheduledExtinctions = new HashMap<>();
        foodWebDependencies = new HashMap<>();
        
        foodWebDependencies.put("Clams", Arrays.asList("Sea Stars"));
        foodWebDependencies.put("Fiddler Crabs", Arrays.asList("Raccoons", "Pelicans"));
        foodWebDependencies.put("Blue Crabs", Arrays.asList("Bobcats", "Pelicans"));
        foodWebDependencies.put("Armadillos", Arrays.asList("Wild Boar", "Bobcats"));
        foodWebDependencies.put("Raccoons", Arrays.asList("Bobcats", "American Crocodiles"));
        foodWebDependencies.put("Pelicans", Arrays.asList("Bobcats", "American Crocodiles"));
        foodWebDependencies.put("Wild Boar", Arrays.asList("American Crocodiles"));
        foodWebDependencies.put("Bobcats", Arrays.asList("American Crocodiles"));
        foodWebDependencies.put("Sea Turtles", Arrays.asList("American Crocodiles"));
    }

    public String triggerDirectExtinction(String species) {
        if (aliveSpecies.contains(species)) {
            aliveSpecies.remove(species);
            scheduleCascadingExtinctions(species, 3); // predators starve 3 nights later
            return species + " have died out due to your actions!";
        }
        return "You searched for " + species + ", but they are already extinct here.";
    }

    public boolean isExtinct(String species) {
        return !aliveSpecies.contains(species);
    }

    // Schedule predators to die `daysFromNow` nights in the future
    private void scheduleCascadingExtinctions(String extinctSpecies, int daysFromNow) {
        if (foodWebDependencies.containsKey(extinctSpecies)) {
            int deathNight = currentNight + daysFromNow;
            for (String predator : foodWebDependencies.get(extinctSpecies)) {
                // Only schedule if alive and not already scheduled to die sooner
                if (aliveSpecies.contains(predator)) {
                    int existingDeathNight = scheduledExtinctions.getOrDefault(predator, Integer.MAX_VALUE);
                    if (deathNight < existingDeathNight) {
                        scheduledExtinctions.put(predator, deathNight);
                    }
                }
            }
        }
    }

    public List<String> processNightExtinctions() {
        currentNight++;
        List<String> diedTonight = new ArrayList<>();

        // Find all species whose scheduled death night has arrived
        Iterator<Map.Entry<String, Integer>> it = scheduledExtinctions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = it.next();
            String species = entry.getKey();
            int deathNight = entry.getValue();

            if (currentNight >= deathNight && aliveSpecies.contains(species)) {
                aliveSpecies.remove(species);
                diedTonight.add(species);
                it.remove();

                // Chain reaction: that species' predators now also get scheduled 3 nights out
                scheduleCascadingExtinctions(species, 3);
            }
        }

        return diedTonight;
    }

    public boolean isEcosystemDead() {
        return aliveSpecies.isEmpty();
    }
}