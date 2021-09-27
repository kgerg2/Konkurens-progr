// package concurent.labs.task;

// /**
//  * Resources used by ThreadCraft
//  *
//  * Includes goldmine capacity, gold owned by the player, houses built by the
//  * player All the actions manipulating these stats should be implemented here
//  */
// public class Resources {

//     // TODO: Make the methods thread safe - ✔
//     // Multiple threads will try to access these resources at the same time

//     private Goldmine goldmineCapacity;
//     private GoldCounter gold;
//     private HouseCounter houses;

//     /**
//      * If the goldmine hasn't run out yet, mines some gold and adds it to the gold
//      * resource
//      * 
//      * @return Whether mining has been successful or not
//      */
//     public boolean tryToMineGold() {
//         synchronized (this) {
//             if (goldmineCapacity.getCapacity() > 0) {
//                 goldmineCapacity.mine(Configuration.MINING_AMOUNT);
//                 gold.add(Configuration.MINING_AMOUNT);
//                 return true;
//             }
//         }
//         return false;
//     }

//     /**
//      * Returns number of houses built
//      * 
//      * @return
//      */
//     public int getHouses() {
//         return houses.getCount();
//     }

//     /**
//      * If there is enough gold to build a house, it does Increments number of
//      * houses, removes the cost from gold
//      * 
//      * @return Whether building was successful or not
//      */
//     public boolean tryToBuildHouse() {
//         synchronized (this) {
//             if (gold.getCount() >= Configuration.HOUSE_COST) {
//                 houses.build();
//                 gold.remove(Configuration.HOUSE_COST);
//                 return true;
//             }
//         }
//         return false;
//     }

//     /**
//      * Returns gold owned by the player
//      * 
//      * @return
//      */
//     public int getGold() {
//         return gold.getCount();
//     }

//     /**
//      * Return how much gold can be mined from the mine
//      * 
//      * @return
//      */
//     public int getGoldmineCapacity() {
//         return goldmineCapacity.getCapacity();
//     }

// }

// class Goldmine {
//     private int capacity = Configuration.GOLDMINE_CAPACITY;

//     public synchronized void mine(int amount) {
//         capacity -= amount;
//     }

//     public synchronized int getCapacity() {
//         return capacity;
//     }
// }

// class GoldCounter {
//     private int count = 0;

//     public synchronized void add(int amount) {
//         count += amount;
//     }

//     public synchronized void remove(int amount) {
//         count -= amount;
//     }

//     public synchronized int getCount() {
//         return count;
//     }
// }

// class HouseCounter {
//     private int count = 0;

//     public synchronized void build() {
//         count++;
//     }

//     public synchronized int getCount() {
//         return count;
//     }
// }