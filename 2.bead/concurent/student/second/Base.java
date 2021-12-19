package concurent.student.second;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Base {

    private static final int STARTER_PEASANT_NUMBER = 5;
    private static final int PEASANT_NUMBER_GOAL = 10;
    private static final int FOOTMAN_NUMBER_GOAL = 10;

    // lock to ensure only one unit can be trained at one time
    private final ReentrantLock trainingLock = new ReentrantLock();
    private final ReentrantLock footmanTrainingLock = new ReentrantLock();

    private final String name;
    private final Resources resources = new Resources();
    private final List<Peasant> peasants = Collections.synchronizedList(new LinkedList<>());
    private final List<Footman> footmen = Collections.synchronizedList(new LinkedList<>());
    private final List<Building> buildings = Collections.synchronizedList(new LinkedList<>());
    private final List<Personnel> army = Collections.synchronizedList(new LinkedList<>());

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

        train(executor, 3, 5, Peasant::startMining, this::createPeasant, peasants);
        train(executor, 1, 2, Peasant::startCuttingWood, this::createPeasant, peasants);
        train(executor, 1, 3, p -> {}, this::createPeasant, peasants);

        build(executor, UnitType.LUMBERMILL, 1);
        build(executor, UnitType.BLACKSMITH, 1);
        build(executor, UnitType.BARRACKS, 1);

        train(executor, 0, FOOTMAN_NUMBER_GOAL, f -> {}, this::createFootman, footmen);

        // executor.submit(() -> {
        //     int count = 0;
        //     while (count < FOOTMAN_NUMBER_GOAL) {
        //         Footman footman = createFootman();
        //         if (footman != null) {
        //             footmen.add(footman);
        //             count++;
        //         } else {
        //             sleepForMsec(10);
        //         }
        //     }
        // });

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
        System.out.println(this.name + " footmen: " + this.footmen.size());
        for (Building b : buildings) {
            System.out.println(this.name + " has a  " + b.getUnitType().toString());
        }
    }

    private <T extends Personnel> void train(ExecutorService executor, int count, int required, Consumer<T> action, Supplier<T> supplier, List<T> list) {
        executor.submit(() -> {
            int personellCount = count;
            while (personellCount < required) {
                T personell = supplier.get();
                if (personell != null) {
                    action.accept(personell);
                    list.add(personell);
                    personellCount++;
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
     * Assemble the army - call the peasants and footmen to arms
     * 
     * @param latch
     */
    public void assembleArmy(CountDownLatch latch) {
        synchronized (footmen) {
            for (Footman footman : footmen) {
                army.add(footman);
            }
        }
        ExecutorService executor = Executors.newCachedThreadPool();
        synchronized (peasants) {
            for (Peasant peasant : peasants) {
                executor.submit(() -> {
                    while (!peasant.isFree()) {
                        sleepForMsec(10);
                    }
                    army.add(peasant);
                });
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(this.name + " is ready for war");
        // the latch is used to keep track of both factions
        latch.countDown();
    }

    /**
     * Starts a war between the two bases.
     *
     * @param enemy    Enemy base's personnel
     * @param warLatch Latch to make sure they attack at the same time
     */
    public void goToWar(List<Personnel> enemy, CountDownLatch warLatch) {
        // This is necessary to ensure that both armies attack at the same time
        warLatch.countDown();
        try {
            // Waiting for the other army to be ready for war
            warLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread[] threads;

        synchronized (army) {
            threads = new Thread[army.size()];
            for (int i = 0; i < threads.length; i++) {
                int I = i;
                threads[i] = new Thread(() -> {
                    army.get(I).startWar(enemy);
                });
            }
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // If our army has no personnel, we failed
        if (army.isEmpty()) {
            System.out.println(this.name + " has lost the fight");
        } else {
            System.out.println(this.name + " has won the fight");
        }
    }

    /**
     * Resolves the event when a personnel dies;
     * Remove it from the army and update the capacity.
     * 
     * @param p The fallen personnel
     */
    public void signalPersonnelDeath(Personnel p) {
        resources.updateCapacity(-p.getUnitType().foodCost);

        army.remove(p);
        if (p.getUnitType() == UnitType.FOOTMAN) {
            footmen.remove(p);
        } else {
            peasants.remove(p);
        }

        System.out.println(this.name + " has lost a " + p.getUnitType().toString());
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
                System.out.println(this.name + " created a peasant");

                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                trainingLock.unlock();
            }
        }
        return null;
    }

    private Footman createFootman() {
        Footman result;
        if (resources.canTrain(UnitType.FOOTMAN.goldCost, UnitType.FOOTMAN.woodCost, UnitType.FOOTMAN.foodCost) &&
                hasEnoughBuilding(UnitType.BARRACKS, 1)) {

            try {
                footmanTrainingLock.lockInterruptibly();

                sleepForMsec(UnitType.FOOTMAN.buildTime);
                resources.removeCost(UnitType.FOOTMAN.goldCost, UnitType.FOOTMAN.woodCost);
                resources.updateCapacity(UnitType.FOOTMAN.foodCost);
                result = Footman.createFootman(this);
                System.out.println(this.name + " created a footman");

                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                footmanTrainingLock.unlock();
            }
        }
        return null;
    }

    public Resources getResources() {
        return this.resources;
    }

    public List<Personnel> getArmy() {
        return this.army;
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
