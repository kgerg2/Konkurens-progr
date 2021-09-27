package concurent.labs.task;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resources used by ThreadCraft
 *
 * Includes goldmine capacity, gold owned by the player, houses built by the
 * player All the actions manipulating these stats should be implemented here
 */
public class Resources {

    // TODO: Make the methods thread safe - ✔
    // Multiple threads will try to access these resources at the same time

    private AtomicInteger goldmineCapacity = new AtomicInteger(Configuration.GOLDMINE_CAPACITY);
    private AtomicInteger gold = new AtomicInteger();
    private AtomicInteger houses = new AtomicInteger();

    /**
     * If the goldmine hasn't run out yet, mines some gold and adds it to the gold
     * resource
     * 
     * @return Whether mining has been successful or not
     */
    public boolean tryToMineGold() {
        synchronized (this) {
            if (goldmineCapacity.get() > 0) {
                goldmineCapacity.addAndGet(-Configuration.MINING_AMOUNT);
                gold.addAndGet(Configuration.MINING_AMOUNT);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns number of houses built
     * 
     * @return
     */
    public int getHouses() {
        return houses.get();
    }

    /**
     * If there is enough gold to build a house, it does Increments number of
     * houses, removes the cost from gold
     * 
     * @return Whether building was successful or not
     */
    public boolean tryToBuildHouse() {
        synchronized (this) {
            if (gold.get() >= Configuration.HOUSE_COST) {
                houses.incrementAndGet();
                gold.addAndGet(-Configuration.HOUSE_COST);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns gold owned by the player
     * 
     * @return
     */
    public int getGold() {
        return gold.get();
    }

    /**
     * Return how much gold can be mined from the mine
     * 
     * @return
     */
    public int getGoldmineCapacity() {
        return goldmineCapacity.get();
    }

}
