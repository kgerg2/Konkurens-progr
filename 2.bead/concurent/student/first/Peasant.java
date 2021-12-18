package concurent.student.first;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Peasant extends Unit {

    private static final int HARVEST_WAIT_TIME = 100;
    private static final int HARVEST_AMOUNT = 10;

    private AtomicBoolean isHarvesting = new AtomicBoolean(false);
    private AtomicBoolean isBuilding = new AtomicBoolean(false);

    private ExecutorService worker = Executors.newSingleThreadExecutor();

    private Peasant(Base owner) {
        super(owner, UnitType.PEASANT);
    }

    public static Peasant createPeasant(Base owner) {
        return new Peasant(owner);
    }

    /**
     * Starts gathering gold.
     */
    public void startMining() {
        // TODO Set isHarvesting to true
        // TODO Start harvesting on a new thread
        // TODO Harvesting: Sleep for HARVEST_WAIT_TIME, then add the resource -
        // HARVEST_AMOUNT
        System.out.println("Peasant starting mining");
        isHarvesting.set(true);
        worker.submit(() -> {
            while (this.isHarvesting.get()) {
                try {
                    Thread.sleep(HARVEST_WAIT_TIME);
                } catch (InterruptedException e) {
                    break;
                }
                this.getOwner().getResources().addGold(HARVEST_AMOUNT);
            }
            System.out.println("Mining finished.");
        });
    }

    /**
     * Starts gathering wood.
     */
    public void startCuttingWood() {
        // TODO Set isHarvesting to true
        // TODO Start harvesting on a new thread
        // TODO Harvesting: Sleep for HARVEST_WAIT_TIME, then add the resource -
        // HARVEST_AMOUNT
        System.out.println("Peasant starting cutting wood");
        isHarvesting.set(true);
        worker.submit(() -> {
            while (this.isHarvesting.get()) {
                try {
                    Thread.sleep(HARVEST_WAIT_TIME);
                } catch (InterruptedException e) {
                    break;
                }
                this.getOwner().getResources().addWood(HARVEST_AMOUNT);
            }
            System.out.println("Cutting wood finished.");
        });
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
        // TODO Start building on a separate thread if there are enough resources
        // TODO Use the Resources class' canBuild method to determine
        // TODO Use the startBuilding method if the process can be started
        if (this.getOwner().getResources().canBuild(buildingType.goldCost, buildingType.woodCost)
                && !this.isHarvesting.get()) {
            worker.submit(() -> startBuilding(buildingType));
            return true;
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
        // TODO Ensure that only one building can be built at a time - use isBuilding
        // atomic boolean
        // TODO Building steps: Remove cost, build the building, wait the wait time
        // TODO Use Building's createBuilding method to create the building
        isBuilding.set(true);
        this.getOwner().getResources().removeCost(buildingType.goldCost, buildingType.woodCost);
        getOwner().getBuildings().add(Building.createBuilding(buildingType, getOwner()));
        try {
            Thread.sleep(buildingType.buildTime);
        } catch (InterruptedException e) { }
        isBuilding.set(false);
        System.out.println("Building finished.");
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
