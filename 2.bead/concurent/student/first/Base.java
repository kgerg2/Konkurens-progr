package concurent.student.first;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Base {

    private static final int STARTER_PEASANT_NUMBER = 5;
    private static final int PEASANT_NUMBER_GOAL = 10;

    // lock to ensure only one unit can be trained at one time
    private final ReentrantLock trainingLock = new ReentrantLock();

    private final String name;
    private final Resources resources = new Resources();
    private final List<Peasant> peasants = Collections.synchronizedList(new LinkedList<>());
    private final List<Building> buildings = Collections.synchronizedList(new LinkedList<>());

    public Base(String name) {
        this.name = name;
        // TODO Create the initial 5 peasants - Use the STARTER_PEASANT_NUMBER constant
        // TODO 3 of them should mine gold
        // TODO 1 of them should cut tree
        // TODO 1 should do nothing
        // TODO Use the createPeasant() method
        for (int i = 0; i < STARTER_PEASANT_NUMBER; i++) {
            peasants.add(Peasant.createPeasant(this));
        }

        for (int i = 0; i < 3; i++) {
            peasants.get(i).startMining();
        }

        peasants.get(3).startCuttingWood();
    }

    public void startPreparation() {
        // TODO Start the building and training preparations on separate threads
        // TODO Tip: use the hasEnoughBuilding method
        ExecutorService executor = Executors.newCachedThreadPool();

        // TODO Build 3 farms - use getFreePeasant() method to see if there is a peasant
        // without any work
        executor.submit(() -> {
            int farmCount = 0;
            while (farmCount < 3) {
                Peasant free = getFreePeasant();
                if (free != null && free.tryBuilding(UnitType.FARM)) {
                    farmCount++;
                }
                sleepForMsec(10);
            }
            while (!hasEnoughBuilding(UnitType.FARM, 3)) {
                sleepForMsec(10);
            }
        });

        // TODO Create remaining 5 peasants - Use the PEASANT_NUMBER_GOAL constant
        // TODO 5 of them should mine gold
        executor.submit(() -> {
            int goldMinerCount = 3;
            while (goldMinerCount < 5) {
                Peasant newPeasant = createPeasant();
                if (newPeasant != null) {
                    newPeasant.startMining();
                    peasants.add(newPeasant);
                    goldMinerCount++;
                }
                sleepForMsec(10);
            }
        });
        
        // TODO 2 of them should cut tree
        executor.submit(() -> {
            int treeCutterCount = 1;
            while (treeCutterCount < 2) {
                Peasant newPeasant = createPeasant();
                if (newPeasant != null) {
                    newPeasant.startMining();
                    peasants.add(newPeasant);
                    treeCutterCount++;
                }
                sleepForMsec(10);
            }
        });
        
        // TODO 3 of them should do nothing
        executor.submit(() -> {
            int freeCount = 1;
            while (freeCount < 3) {
                Peasant newPeasant = createPeasant();
                if (newPeasant != null) {
                    peasants.add(newPeasant);
                    freeCount++;
                }
                sleepForMsec(10);
            }
        });

        // TODO Use the createPeasant() method

        // TODO Build a lumbermill - use getFreePeasant() method to see if there is a
        // peasant without any work
        executor.submit(() -> {
            int lumbermillCount = 0;
            while (lumbermillCount < 1) {
                Peasant free = getFreePeasant();
                if (free != null && free.tryBuilding(UnitType.LUMBERMILL)) {
                    lumbermillCount++;
                }
                sleepForMsec(10);
            }
            while (!hasEnoughBuilding(UnitType.LUMBERMILL, 1)) {
                sleepForMsec(10);
            }
        });

        // TODO Build a blacksmith - use getFreePeasant() method to see if there is a
        // peasant without any work
        executor.submit(() -> {
            int blacksmithCount = 0;
            while (blacksmithCount < 1) {
                Peasant free = getFreePeasant();
                if (free != null && free.tryBuilding(UnitType.BLACKSMITH)) {
                    blacksmithCount++;
                }
                sleepForMsec(10);
            }
            while (!hasEnoughBuilding(UnitType.BLACKSMITH, 1)) {
                sleepForMsec(10);
            }
        });

        // TODO Wait for all the necessary preparations to finish
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        synchronized (peasants) {
            for (Peasant peasant : peasants) {
                peasant.stopHarvesting();
            }
        }

        // TODO Stop harvesting with the peasants once everything is ready
        System.out.println(this.name + " finished creating a base");
        System.out.println(this.name + " peasants: " + this.peasants.size());
        for (Building b : buildings) {
            System.out.println(this.name + " has a  " + b.getUnitType().toString());
        }

    }

    /**
     * Returns a peasants that is currently free.
     * Being free means that the peasant currently isn't harvesting or building.
     *
     * @return Peasant object, if found one, null if there isn't one
     */
    private Peasant getFreePeasant() {
        // TODO implement - use the peasant's isFree() method
        synchronized (peasants) {
            for (Peasant peasant : peasants) {
                if (peasant.isFree()) {
                    return peasant;
                }
            }
        }
        return null;
    }

    /**
     * Creates a peasant.
     * A peasant could only be trained if there are sufficient
     * gold, wood and food for him to train.
     *
     * At one time only one Peasant can be trained.
     *
     * @return The newly created peasant if it could be trained, null otherwise
     */
    private Peasant createPeasant() {
        Peasant result;
        if (resources.canTrain(UnitType.PEASANT.goldCost, UnitType.PEASANT.woodCost, UnitType.PEASANT.foodCost)) {

            // TODO 1: Sleep as long as it takes to create a peasant - use sleepForMsec()
            // method
            // TODO 2: Remove costs
            // TODO 3: Update capacity
            // TODO 4: Use the Peasant class' createPeasant method to create the new Peasant

            // TODO Remember that at one time only one peasant can be trained
            // return result;

            trainingLock.lock();

            sleepForMsec(UnitType.PEASANT.buildTime);
            resources.removeCost(UnitType.PEASANT.goldCost, UnitType.PEASANT.woodCost);
            resources.updateCapacity(UnitType.PEASANT.foodCost);
            result = Peasant.createPeasant(this);

            trainingLock.unlock();

            return result;
        }
        return null;
    }

    public Resources getResources() {
        return this.resources;
    }

    public List<Building> getBuildings() {
        return this.buildings;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Helper method to determine if a base has the required number of a certain
     * building.
     *
     * @param unitType Type of the building
     * @param required Number of required amount
     * @return true, if required amount is reached (or surpassed), false otherwise
     */
    private boolean hasEnoughBuilding(UnitType unitType, int required) {
        // TODO check in the buildings list if the type has reached the required amount
        synchronized(buildings) {
            int count = 0;
            for (Building building : buildings) {
                if (building.getUnitType() == unitType) {
                    count++;
                    if (count == required) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void sleepForMsec(int sleepTime) {
        try {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }

}
