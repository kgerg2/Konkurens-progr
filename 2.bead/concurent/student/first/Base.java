package concurent.student.first;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

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

        for (int i = 0; i < STARTER_PEASANT_NUMBER; i++) {
            peasants.add(Peasant.createPeasant(this));
        }

        for (int i = 0; i < 3; i++) {
            peasants.get(i).startMining();
        }

        peasants.get(3).startCuttingWood();
    }

    public void startPreparation() {
        ExecutorService executor = Executors.newCachedThreadPool();

        build(executor, UnitType.FARM, 3);
        train(executor, 3, 5, Peasant::startMining);
        train(executor, 1, 2, Peasant::startCuttingWood);
        train(executor, 1, 3, p -> {});

        build(executor, UnitType.LUMBERMILL, 1);
        build(executor, UnitType.BLACKSMITH, 1);

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (peasants) {
            for (Peasant peasant : peasants) {
                peasant.stopHarvesting();
            }
        }

        System.out.println(this.name + " finished creating a base");
        System.out.println(this.name + " peasants: " + this.peasants.size());
        for (Building b : buildings) {
            System.out.println(this.name + " has a  " + b.getUnitType().toString());
        }

    }

    private void train(ExecutorService executor, int count, int required, Consumer<Peasant> action) {
        executor.submit(() -> {
            int peasantCount = count;
            while (peasantCount < required) {
                Peasant newPeasant = createPeasant();
                if (newPeasant != null) {
                    action.accept(newPeasant);
                    peasants.add(newPeasant);
                    peasantCount++;
                } else {
                    sleepForMsec(10);
                }
            }
        });
    }

    private void build(ExecutorService executor, UnitType type, int required) {
        executor.submit(() -> {
            int count = 0;
            while (count < required) {
                Peasant free = getFreePeasant();
                if (free != null && free.tryBuilding(type)) {
                    count++;
                } else {
                    sleepForMsec(10);
                }
            }
            while (!hasEnoughBuilding(type, required)) {
                sleepForMsec(10);
            }
        });
    }

    /**
     * Returns a peasants that is currently free.
     * Being free means that the peasant currently isn't harvesting or building.
     *
     * @return Peasant object, if found one, null if there isn't one
     */
    private Peasant getFreePeasant() {
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
            try {
                trainingLock.lockInterruptibly();

                sleepForMsec(UnitType.PEASANT.buildTime);
                resources.removeCost(UnitType.PEASANT.goldCost, UnitType.PEASANT.woodCost);
                resources.updateCapacity(UnitType.PEASANT.foodCost);
                result = Peasant.createPeasant(this);

                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                trainingLock.unlock();
            }
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
        synchronized (buildings) {
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
