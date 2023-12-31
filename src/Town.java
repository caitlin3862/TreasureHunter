/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean notSearchedTown;
    private int goldDiff;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        notSearchedTown = true;
    }

    public void getLatestNews() {
        System.out.println(printMessage);
        printMessage = "";
    }

    public int getGoldDiff() { return goldDiff; }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + Colors.PURPLE + item + Colors.RESET + " to cross the " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + Colors.PURPLE + item + Colors.RESET + ".";
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + Colors.BLUE + terrain.getNeededItem() + Colors.RESET + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }


        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
            goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                if (hunter.hasItemInKit("sword")) {
                    printMessage = "";
                    printMessage += "The brawler, seeing your sword, realizes he picked a losing fight and gives you his gold.";
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                    printMessage += "\nYou " + Colors.GREEN + "won" + Colors.RESET + " the brawl and receive " + Colors.YELLOW + goldDiff + " gold" + Colors.RESET + ".";
                    hunter.changeGold(goldDiff);
                }
            } else {
                if (hunter.hasItemInKit("sword")) {
                    printMessage = "";
                    printMessage += "The brawler, seeing your sword, realizes he picked a losing fight and gives you his gold." + "\nYou receive " + Colors.YELLOW + goldDiff + " gold" + Colors.RESET + ".";
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "\nYou " + Colors.GREEN + "won" + Colors.RESET + " the brawl and receive " + Colors.YELLOW + goldDiff + " gold" + Colors.RESET + ".";
                    hunter.changeGold(-goldDiff);
                }

            }
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }

    public void findTreasure(){
        if (notSearchedTown) {
            double chance = (Math.random());
            String treasure;
            if (chance < 0.25) {
                treasure = "a crown";
            } else if (chance < 0.5) {
                treasure = "a trophy";
            } else if (chance < 0.75) {
                treasure = "a gem";
            } else {
                treasure = "dust";
            }
                System.out.println();
                System.out.println("You found " + treasure + "!");
                if (!treasure.equals("dust")){
                    hunter.addTreasure(treasure);
                }
            notSearchedTown = false;
        } else {
            System.out.println();
            System.out.println("You have already searched this town");
        }
    }

    public void digForGold() {
        if (notSearchedTown) {
            if (hunter.hasItemInKit("shovel")) {
                int goldChance = (int) (Math.random() * 4) + 3; //generates nums 3 thru 6
                if (goldChance <= 3) {
                    int gold = (int) (Math.random() * 20) + 1;
                    hunter.changeGold(gold);
                    System.out.println("You dug up " + gold + " gold!");
                } else {
                    System.out.println("You dug but only found dirt.");
                }
                notSearchedTown = false;

            } else {
                System.out.println("You can't dig for gold without a shovel!");
            }
        } else {
            System.out.println("You have already dug for gold in this town.");
        }
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random()*1.5;
        if (rnd < .25) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .50) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .75) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < 1) {
            return new Terrain("Desert", "Water");
        } else if (rnd < 1.25){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (TreasureHunter.getEasyMode()){
            return false;
        }
        double rand = Math.random();
        return (rand < 0.5);
    }
}