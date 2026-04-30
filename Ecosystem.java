import java.util.*;

public class Ecosystem {
    public List<String> aliveSpecies;
    
    // Maps a Predator -> List of its Prey
    public Map<String, List<String>> preyOf;
    
    // Maps a Prey -> List of its Predators
    public Map<String, List<String>> predatorsOf;
    
    // Custom class to store both the night of death and the reason
    public static class ExtinctionData {
        public int night;
        public String reason;
        public ExtinctionData(int night, String reason) {
            this.night = night;
            this.reason = reason;
        }
    }
    
    // Maps species name -> ExtinctionData
    public Map<String, ExtinctionData> scheduledExtinctions;
    private int currentNight;

    public Ecosystem() {
        currentNight = 1;
        aliveSpecies = new ArrayList<>(Arrays.asList(
            "American Crocodiles", "Bobcats", "Wild Boar", "Raccoons",
            "Pelicans", "Sea Stars", "Sea Turtles", "Clams",
            "Armadillos", "Blue Crabs", "Fiddler Crabs"
        ));
        
        scheduledExtinctions = new HashMap<>();
        preyOf = new HashMap<>();
        predatorsOf = new HashMap<>();
        
        // 1. Define what each predator eats (to check for STARVATION)
        preyOf.put("American Crocodiles", Arrays.asList("Bobcats", "Pelicans", "Wild Boar", "Raccoons", "Sea Turtles"));
        preyOf.put("Bobcats", Arrays.asList("Pelicans", "Blue Crabs", "Raccoons", "Armadillos"));
        preyOf.put("Wild Boar", Arrays.asList("Armadillos"));
        preyOf.put("Pelicans", Arrays.asList("Blue Crabs", "Fiddler Crabs"));
        preyOf.put("Raccoons", Arrays.asList("Fiddler Crabs"));
        preyOf.put("Sea Stars", Arrays.asList("Clams"));
        
        // 2. Define what eats each prey (to check for OVERPOPULATION)
        predatorsOf.put("Bobcats", Arrays.asList("American Crocodiles"));
        predatorsOf.put("Pelicans", Arrays.asList("American Crocodiles", "Bobcats"));
        predatorsOf.put("Wild Boar", Arrays.asList("American Crocodiles"));
        predatorsOf.put("Raccoons", Arrays.asList("American Crocodiles", "Bobcats"));
        predatorsOf.put("Sea Turtles", Arrays.asList("American Crocodiles"));
        predatorsOf.put("Blue Crabs", Arrays.asList("Bobcats", "Pelicans"));
        predatorsOf.put("Armadillos", Arrays.asList("Bobcats", "Wild Boar"));
        predatorsOf.put("Fiddler Crabs", Arrays.asList("Pelicans", "Raccoons"));
        predatorsOf.put("Clams", Arrays.asList("Sea Stars"));
    }

    public String triggerDirectExtinction(String species, String reason) {
        if (aliveSpecies.contains(species)) {
            aliveSpecies.remove(species);
            checkCascades(species); 
            return species + " have died out! They were " + reason;
        }
        return "You searched for " + species + ", but they are already extinct here.";
    }

    public boolean isExtinct(String species) {
        return !aliveSpecies.contains(species);
    }

    // Schedule a death. If they are already scheduled, overwrite ONLY if the new date is sooner.
    private void scheduleDeath(String species, int daysFromNow, String reason) {
        int targetNight = currentNight + daysFromNow;
        if (scheduledExtinctions.containsKey(species)) {
            if (targetNight < scheduledExtinctions.get(species).night) {
                scheduledExtinctions.put(species, new ExtinctionData(targetNight, reason));
            }
        } else {
            scheduledExtinctions.put(species, new ExtinctionData(targetNight, reason));
        }
    }

    // Checks relationships to see if the loss of 'deadSpecies' triggers secondary deaths
    private void checkCascades(String deadSpecies) {
        // 1. Check predators of the dead species (they might starve)
        if (predatorsOf.containsKey(deadSpecies)) {
            for (String predator : predatorsOf.get(deadSpecies)) {
                if (aliveSpecies.contains(predator)) {
                    
                    boolean allPreyDead = true;
                    if (preyOf.containsKey(predator)) {
                        for (String prey : preyOf.get(predator)) {
                            if (aliveSpecies.contains(prey)) {
                                allPreyDead = false;
                                break;
                            }
                        }
                    } else {
                        allPreyDead = false; 
                    }
                    
                    if (allPreyDead) {
                        scheduleDeath(predator, 1, "Starved to death because ALL of its prey species were wiped out.");
                    }
                }
            }
        }

        // 2. Check prey of the dead species (they might overpopulate)
        if (preyOf.containsKey(deadSpecies)) {
            for (String prey : preyOf.get(deadSpecies)) {
                if (aliveSpecies.contains(prey)) {
                    
                    boolean allPredatorsDead = true;
                    if (predatorsOf.containsKey(prey)) {
                        for (String predator : predatorsOf.get(prey)) {
                            if (aliveSpecies.contains(predator)) {
                                allPredatorsDead = false;
                                break;
                            }
                        }
                    } else {
                        allPredatorsDead = false;
                    }
                    
                    if (allPredatorsDead) {
                        scheduleDeath(prey, 3, "Died from rampant disease and overpopulation after losing ALL natural predators.");
                    }
                }
            }
        }
    }

    public List<String> processNightExtinctions() {
        currentNight++;
        List<String> diedTonight = new ArrayList<>();
        List<String> newlyDead = new ArrayList<>();

        Iterator<Map.Entry<String, ExtinctionData>> it = scheduledExtinctions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ExtinctionData> entry = it.next();
            String species = entry.getKey();
            ExtinctionData data = entry.getValue();

            if (currentNight >= data.night && aliveSpecies.contains(species)) {
                aliveSpecies.remove(species);
                diedTonight.add("• " + species + ": " + data.reason);
                newlyDead.add(species);
                it.remove();
            }
        }

        // Chain reaction: species that cascaded tonight might trigger MORE cascades
        for (String dead : newlyDead) {
            checkCascades(dead);
        }

        return diedTonight;
    }

    public boolean isEcosystemDead() {
        return aliveSpecies.isEmpty();
    }
}
