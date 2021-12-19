package concurent.student.first;

import java.util.concurrent.atomic.AtomicBoolean;

public class Peasant extends Unit {

    private static final int HARVEST_WAIT_TIME = 100;
    private static final int HARVEST_AMOUNT = 10;

    private AtomicBoolean isHarvesting = new AtomicBoolean(false);
    private AtomicBoolean isBuilding = new AtomicBoolean(false);

    private Thread worker;

    private Peasant(Base owner) {
        super(owner, UnitType.PEASANT);
    }

    public static Peasant createPeasant(Base owner) {
        return new Peasant(owner);
    }

    /**
     * Starts harvesting in a new thread (stored in worker).
     * 
     * @param action the harvesting action.
     */
    private synchronized void startHarvesting(Runnable action) {
        while (isBuilding.get()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return;
            }
        }

        if (isHarvesting.get()) {
            worker.interrupt();
        }

        isHarvesting.set(true);

        worker = new Thread(() -> {
            while (this.isHarvesting.get()) {
                try {
                    Thread.sleep(HARVEST_WAIT_TIME);
                } catch (InterruptedException e) {
                    break;
                }
                action.run();
            }
        });

        worker.start();
    }

    /**
     * Starts gathering gold.
     */
    public void startMining() {
        System.out.println("Peasant starting mining");
        startHarvesting(() -> getOwner().getResources().addGold(HARVEST_AMOUNT));
    }

    /**
     * Starts gathering wood.
     */
    public void startCuttingWood() {
        System.out.println("Peasant starting cutting wood");
        startHarvesting(() -> getOwner().getResources().addWood(HARVEST_AMOUNT));
    }

    /**
     * Peasant should stop all harvesting once this is invoked
     */
    public void stopHarvesting() {
        this.isHarvesting.set(false);
    }

    /**
     * Tries to build a certain type of building.
     * Can only build if there are enough gold and wood for the building
     * to be built.
     *
     * @param buildingType Type of the building
     * @return true, if the building process has started
     *         false, if there are insufficient resources
     */
    public boolean tryBuilding(UnitType buildingType) {
        if (this.getOwner().getResources().canBuild(buildingType.goldCost, buildingType.woodCost)) {
            synchronized (this) {
                if (!this.isHarvesting.get() && !this.isBuilding.get()) {
                    worker = new Thread(() -> startBuilding(buildingType));
                    worker.start();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Start building a certain type of building.
     * Keep in mind that a peasant can only build one building at one time.
     *
     * @param buildingType Type of the building
     */
    private void startBuilding(UnitType buildingType) {
        isBuilding.set(true);
        this.getOwner().getResources().removeCost(buildingType.goldCost, buildingType.woodCost);
        getOwner().getBuildings().add(Building.createBuilding(buildingType, getOwner()));
        try {
            Thread.sleep(buildingType.buildTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (this) {
            isBuilding.set(false);
            notifyAll();
        }
    }

    /**
     * Determines if a peasant is free or not.
     * This means that the peasant is neither harvesting, nor building.
     *
     * @return Whether he is free
     */
    public boolean isFree() {
        return !isHarvesting.get() && !isBuilding.get();
    }

}
