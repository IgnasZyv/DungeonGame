package uk.ac.bradford.dungeongame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import uk.ac.bradford.dungeongame.Entity.EntityType;

/**
 * The GameEngine class is responsible for managing information about the game,
 * creating levels, the player and monsters, as well as updating information
 * when a key is pressed while the game is running.
 * @author prtrundl
 */
public class GameEngine {

    /**
     * An enumeration type to represent different types of tiles that make up
     * a dungeon level. Each type has a corresponding image file that is used
     * to draw the right tile to the screen for each tile in a level. Floors are
     * open for monsters and the player to move into, walls should be impassable,
     * stairs allow the player to progress to the next level of the dungeon, and
     * chests can yield a reward when moved over.
     */
    public enum TileType {
        WALL, FLOOR, STAIRS, CHEST, CHESTOPEN, LAVA
    }

    /**
     * The width of the dungeon level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int DUNGEON_WIDTH = 25;

    /**
     * The height of the dungeon level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int DUNGEON_HEIGHT = 18;

    /**
     * The maximum number of monsters that can be generated on a single level
     * of the dungeon. This attribute can be used to fix the size of an array
     * (or similar) that will store monsters.
     */
    public static int MAX_MONSTERS = 5;


    public int damage = - 10;
    /**
     * The chance of a wall being generated instead of a floor when generating
     * the level. 1.0 is 100% chance, 0.0 is 0% chance.
     */
    public final static double WALL_CHANCE = 0.2;
    public final static double lavaChance = 0.01;

    public final double damageChance = 0.5;

    /**
     * A random number generator that can be used to include randomised choices
     * in the creation of levels, in choosing places to spawn the player and
     * monsters, and to randomise movement and damage. This currently uses a seed
     * value of 123 to generate random numbers - this helps you find bugs by
     * giving you the same numbers each time you run the program. Remove
     * the seed value if you want different results each game.
     */
    private Random rng = new Random();

    /**
     * The current level number for the dungeon. As the player moves down stairs
     * the level number should be increased and can be used to increase the
     * difficulty e.g. by creating additional monsters with more health.
     */
    private int depth = 1;  //current dungeon level

    /**
     * The GUI associated with a GameEngine object. THis link allows the engine
     * to pass level (tiles) and entity information to the GUI to be drawn.
     */
    private GameGUI gui;

    /**
     * The 2 dimensional array of tiles the represent the current dungeon level.
     * The size of this array should use the DUNGEON_HEIGHT and DUNGEON_WIDTH
     * attributes when it is created.
     */
    private TileType[][] tiles = new TileType[DUNGEON_WIDTH][DUNGEON_HEIGHT];

    /**
     * An ArrayList of Point objects used to create and track possible locations
     * to spawn the player and monsters.
     */
    private ArrayList<Point> spawns;

    /**
     * An Entity object that is the current player. This object stores the state
     * information for the player, including health and the current position (which
     * is a pair of co-ordinates that corresponds to a tile in the current level)
     */
    private Entity player;

    /**
     * An array of Entity objects that represents the monsters in the current
     * level of the dungeon. Elements in this array should be of the type Entity,
     * meaning that a monster is alive and needs to be drawn or moved, or should
     * be null which means nothing is drawn or processed for movement.
     * Null values in this array are skipped during drawing and movement processing.
     * Monsters (Entity objects) that die due to player attacks can be replaced
     * with the value null in this array which removes them from the game.
     */
    private Entity[] monsters;


    /**
     * Constructor that creates a GameEngine object and connects it with a GameGUI
     * object.
     *
     * @param gui The GameGUI object that this engine will pass information to in
     *            order to draw levels and entities to the screen.
     */
    public GameEngine(GameGUI gui) {
        this.gui = gui;
        //startGame();
    }

    /**
     * Generates a new dungeon level. The method builds a 2D array of TileType values
     * that will be used to draw tiles to the screen and to add a variety of
     * elements into each level. Tiles can be floors, walls, stairs (to progress
     * to the next level of the dungeon) or chests. The method should contain
     * the implementation of an algorithm to create an interesting and varied
     * level each time it is called.
     *
     * @return A 2D array of TileTypes representing the tiles in the current
     * level of the dungeon. The size of this array should use the width and
     * height of the dungeon.
     */
    private TileType[][] generateLevel() {
        //Add your code here to build a 2D array containing TileType values to create a level
        int lavaPlaced = 0;
        // Place stairs in the level
        int select1 = rng.nextInt(DUNGEON_WIDTH - 2);
        select1 += 1;
        int select2 = rng.nextInt(DUNGEON_HEIGHT - 2);
        select2 += 1;
        tiles[select1][select2] = TileType.STAIRS;
        // Place Chest in the level
        int select3 = rng.nextInt(DUNGEON_WIDTH - 2);
        select3 += 1;
        int select4 = rng.nextInt(DUNGEON_HEIGHT - 2);
        select4 += 1;
        if (tiles[select3][select4] != TileType.STAIRS) {
            tiles[select3][select4] = TileType.CHEST;
        }

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[j].length; j++) {
                // Place Walls on the edges of the game.
                if (i == 0 || j == 0) {
                    tiles[i][0] = TileType.WALL;
                    tiles[i][DUNGEON_HEIGHT - 1] = TileType.WALL; // Vertical Walls
                    tiles[0][j] = TileType.WALL;
                    tiles[DUNGEON_WIDTH - 1][j] = TileType.WALL; // Horizontal Walls
                }
                // Spawn random walls in the level
                if (rng.nextDouble() <= WALL_CHANCE && tiles[i][j] != TileType.STAIRS && tiles[i][j] != TileType.CHEST) {
                tiles[i][j] = TileType.WALL;
                }
                if (rng.nextDouble() <= lavaChance && tiles[i][j] != TileType.STAIRS && tiles[i][j] != TileType.CHEST && tiles[i][j] != TileType.WALL) {
                    tiles[i][j] = TileType.LAVA;
                }

                // Place floor tile if the tile is not occupied.
                if (tiles[i][j] == null) {
                    tiles[i][j] = TileType.FLOOR;
                }

            }
        }return tiles;   //return the 2D array
    }


    /**
     * Generates spawn points for the player and monsters. The method processes
     * the tiles array and finds tiles that are suitable for spawning, i.e.
     * tiles that are not walls or stairs. Suitable tiles should be added
     * to the ArrayList that will contain Point objects - Points are a
     * simple kind of object that contain an X and a Y co-ordinate stored using
     * the int primitive type and are part of the Java language (search for the
     * Point API documentation and examples of their use)
     * @return An ArrayList containing Point objects representing suitable X and
     * Y co-ordinates in the current level that the player or monsters can be
     * spawned in
     */
    private ArrayList<Point> getSpawns() {
        ArrayList<Point> s = new ArrayList<>();
        //Add code here to find tiles in the level array that are suitable spawn points
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == TileType.FLOOR){
                    Point p = new Point(i, j);
                    s.add(p);
                    Collections.shuffle(s);

                }
            }
        }
        //Add these points to the ArrayList s
        return s;
    }

    /**
     * Spawns monsters in suitable locations in the current level. The method
     * uses the spawns ArrayList to pick suitable positions to add monsters,
     * removing these positions from the spawns ArrayList as they are used
     * (using the remove() method) to avoid multiple monsters spawning in the
     * same location. The method creates monsters by instantiating the Entity
     * class, setting health, and setting the X and Y position for the monster
     * using the X and Y values in the Point object removed from the spawns ArrayList.
     * @return A array of Entity objects representing the monsters for the current
     * level of the dungeon
     */

    private Entity[] spawnMonsters() {

        int monsterNum = depth;
        if (depth > MAX_MONSTERS) {
            monsterNum = MAX_MONSTERS;
        }
        Entity[] m = new Entity[monsterNum];
        for (int i = 0; i < m.length; i++) {
            Point p = spawns.get(i);
            Entity monster = new Entity(50 + depth, p.x, p.y, EntityType.MONSTER);
            m[i] = monster;
            Collections.shuffle(spawns);
        }
        return m;    //Should be changed to return an array of monsters instead of null
    }

    /**
     * Spawns a player entity in the game. The method uses the spawns ArrayList
     * to select a suitable location to spawn the player and removes the Point
     * from the spawns ArrayList. The method instantiates the Entity class and
     * assigns values for the health, position and type of Entity.
     * @return An Entity object representing the player in the game
     */
    private Entity spawnPlayer() {
        Point Point = spawns.get(0);
        Entity player = new Entity(100, Point.x, Point.y, Entity.EntityType.PLAYER);
        spawns.remove(spawns.get(0));
        Collections.shuffle(spawns);
        return player; //Should be changed to return an Entity (the player) instead of null
    }

    /**
     * Handles the movement of the player when attempting to move left in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the left arrow key on the keyboard. The method checks
     * whether the tile to the left of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile to the left of the player is not empty the method will not
     * update the player position, but may make other changes to the game, such
     * as damaging a monster in the tile to the left, or breaking a wall etc.
     */
    public void movePlayerLeft() {

        for (Entity monster : monsters) {
            if (monster != null) {
                if (player.getX() - 1 == monster.getX() && player.getY() == monster.getY()) { // If monster is to the left of the player coordinates
                    hitMonster(monster);
                    if (tiles[monster.getX() - 1][monster.getY()] != TileType.WALL) {
                        monster.setPosition(monster.getX() - 1, monster.getY());
                        }
                    }
            }
        }

        if (tiles[player.getX() - 1][player.getY()] == TileType.STAIRS) {
            player.setPosition(player.getX() - 1, player.getY());
        }else if (tiles[player.getX() - 1][player.getY()] == TileType.CHEST) {
            tiles[player.getX() - 1][player.getY()] = TileType.CHESTOPEN;
            player.setPosition(player.getX() - 1, player.getY());
            if (rng.nextDouble() <= damageChance) {
                damage -= 3;
                System.out.println("Damage has been increased, damage is: " + damage);
            }else {
                player.changeHealth(+ 10);
                System.out.println("Player health increased, health is: " + player.getHealth());
            }

        }else if (tiles[player.getX() - 1][player.getY()] == TileType.LAVA) {
            player.setPosition(player.getX()- 1, player.getY());
            player.changeHealth(-10);
            System.out.println("Player stepped in lava! Health remaining: " + player.getHealth());

        }
        else if (tiles[player.getX() - 1][player.getY()] == TileType.FLOOR || tiles[player.getX() - 1][player.getY()] == TileType.CHESTOPEN) {
            player.setPosition(player.getX() - 1, player.getY());
        }



    }
        
        




    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the right arrow key on the keyboard. The method checks
     * whether the tile to the right of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile to the right of the player is not empty the method will not
     * update the player position, but may make other changes to the game, such
     * as damaging a monster in the tile to the right, or breaking a wall etc.
     */
    public void movePlayerRight() {

        for (Entity monster : monsters) {
            if (monster != null) {
                if (player.getX() + 1 == monster.getX() && player.getY() == monster.getY()) {
                    hitMonster(monster);
                    if (tiles[monster.getX() + 1][monster.getY()] != TileType.WALL) {
                        monster.setPosition(monster.getX() + 1, monster.getY());
                    }
                }
            }
        }

        if (tiles[player.getX() + 1][player.getY()] == TileType.STAIRS) {
            player.setPosition(player.getX() + 1, player.getY());
        }else if (tiles[player.getX() + 1][player.getY()] == TileType.CHEST) {
            tiles[player.getX() + 1][player.getY()] = TileType.CHESTOPEN;
            player.setPosition(player.getX() + 1, player.getY());
            if (rng.nextDouble() <= damageChance) {
                damage -= 3;
                System.out.println("Damage has been increased, damage is: " + damage);
            }else {
                player.changeHealth(+ 10);
                System.out.println("Player health increased, health is: " + player.getHealth());
            }

        }else if (tiles[player.getX() + 1][player.getY()] == TileType.LAVA) {
            player.setPosition(player.getX() + 1, player.getY());
            player.changeHealth(-10);
            System.out.println("Player stepped in lava! Health remaining: " + player.getHealth());

        }else if (tiles[player.getX() + 1][player.getY()] == TileType.FLOOR || tiles[player.getX() + 1][player.getY()] == TileType.CHESTOPEN) {
            player.setPosition(player.getX() + 1, player.getY());
        }

    }

    /**
     * Handles the movement of the player when attempting to move up in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the up arrow key on the keyboard. The method checks
     * whether the tile above the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile above the player is not empty the method will not
     * update the player position, but may make other changes to the game, such
     * as damaging a monster in the tile above the player, or breaking a wall etc.
     */
    public void movePlayerUp() {
        for (Entity monster : monsters) {
            if (monster != null) {
                if (player.getX() == monster.getX() && player.getY() - 1 == monster.getY()) {
                    hitMonster(monster);
                    if (tiles[monster.getX()][monster.getY() - 1] != TileType.WALL) {
                        monster.setPosition(monster.getX(), monster.getY() - 1);
                    }


                }
            }
        }

        if (tiles[player.getX()][player.getY() - 1] == TileType.STAIRS) {
            player.setPosition(player.getX(), player.getY() - 1);
        }else if (tiles[player.getX()][player.getY() - 1] == TileType.CHEST) {
            tiles[player.getX()][player.getY() - 1] = TileType.CHESTOPEN;
            player.setPosition(player.getX(), player.getY() - 1);
            if (rng.nextDouble() <= damageChance) {
                damage -= 3;
                System.out.println("Damage has been increased, damage is: " + damage);
            }else {
                player.changeHealth(+ 10);
                System.out.println("Player health increased, health is: " + player.getHealth());
            }

        }else if (tiles[player.getX()][player.getY() - 1] == TileType.LAVA) {
            player.setPosition(player.getX(), player.getY() - 1);
            player.changeHealth(-10);
            System.out.println("Player stepped in lava! Health remaining: " + player.getHealth());

        }else if (tiles[player.getX()][player.getY() - 1] == TileType.FLOOR || tiles[player.getX()][player.getY() - 1] == TileType.CHESTOPEN) {
            player.setPosition(player.getX(), player.getY() - 1);
        }

    }
    



    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the down arrow key on the keyboard. The method checks
     * whether the tile below the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile below the player is not empty the method will not
     * update the player position, but may make other changes to the game, such
     * as damaging a monster in the tile below the player, or breaking a wall etc.
     */
    public void movePlayerDown() {
        for (Entity monster : monsters) {
            if (monster != null) {
                if (player.getX() == monster.getX() && player.getY() + 1 == monster.getY()) {
                    hitMonster(monster);
                    if (tiles[monster.getX()][monster.getY() + 1] != TileType.WALL) {
                        monster.setPosition(monster.getX(), monster.getY() + 1);
                    }


                }
            }
        }

        if (tiles[player.getX()][player.getY() + 1] == TileType.STAIRS) {
            player.setPosition(player.getX(), player.getY() + 1);
        }else if (tiles[player.getX()][player.getY() + 1] == TileType.CHEST) {
            tiles[player.getX()][player.getY() + 1] = TileType.CHESTOPEN;
            player.setPosition(player.getX(), player.getY() + 1);
            if (rng.nextDouble() <= damageChance) {
                damage -= 2;
                System.out.println("Damage has been increased, damage is: " + damage);
            }else {
                player.changeHealth(+ 10);
                System.out.println("Player health increased, health is: " + player.getHealth());
            }

        }else if (tiles[player.getX()][player.getY() + 1] == TileType.LAVA) {
            player.setPosition(player.getX(), player.getY() + 1);
            player.changeHealth(-10);
            System.out.println("Player stepped in lava! Health remaining: " + player.getHealth());

        }else if (tiles[player.getX()][player.getY() + 1] == TileType.FLOOR || tiles[player.getX()][player.getY() + 1] == TileType.CHESTOPEN) {
            player.setPosition(player.getX(), player.getY() + 1);
        }


    }
    

    /**
     * Reduces a monster's health in response to the player attempting to move
     * into the same square as the monster (attacking the monster).
     * @param m The Entity which is the monster that the player is attacking
     */
    private void hitMonster(Entity m) {
        m.changeHealth(damage);
        System.out.println("This monster has " + m.getHealth() + " health");
    }

    /**
     * Moves all monsters on the current level. The method processes all non-null
     * elements in the monsters array and calls the moveMonster method for each one.
     */
    private void moveMonsters() {
        for (Entity mon : monsters) {
            if (mon != null) {
                moveMonster(mon);

            }
        }
    }

    /**
     * Moves a specific monster in the game. The method updates the X and Y
     * attributes of the monster Entity to reflect its new position.
     * @param m The Entity (monster) that needs to be moved
     */
    private void moveMonster(Entity m) {
        Collections.shuffle(spawns);
        Point p = spawns.get(0);
        int monX = m.getX();
        int monY = m.getY();

        for (Entity monster : monsters) {
            if (monster != null) {
                if (m.getX() - 1 == monster.getX() && m.getY() == monster.getY()) { // Check if there is monster in the tile to the left
                    if (tiles[m.getX() + 1][m.getY()] != TileType.WALL){
                        m.setPosition(m.getX() + 1, m.getY());
                    }else {
                        m.setPosition(p.x, p.y);
                    }
                } else if (m.getX() + 1 == monster.getX() && m.getY() == monster.getY()) { // Check if there is monster in the tile to the right
                    if (tiles[m.getX() - 1][m.getY()] != TileType.WALL){
                        m.setPosition(m.getX() - 1, m.getY());
                    }else {
                        m.setPosition(p.x, p.y);
                    }
                } else if (m.getX() == monster.getX() && m.getY() - 1 == monster.getY()) { // Check if there is monster in the tile above
                    if (tiles[m.getX() ][m.getY() + 1] != TileType.WALL){
                        m.setPosition(m.getX() + 1, m.getY());
                    }else {
                        m.setPosition(p.x, p.y);
                    }
                } else if (m.getX() == monster.getX() && m.getY() + 1 == monster.getY()) { // Check if there is monster in the tile below
                    if (tiles[m.getX()][m.getY() - 1] != TileType.WALL){
                        m.setPosition(m.getX() - 1, m.getY());
                    }else {
                        m.setPosition(p.x, p.y);
                    }
                }
            }
        }


            // Check if the player is adjacent if so don't change position.
            if (m.getX() - 1 == player.getX() && m.getY() == player.getY()) {
                m.setPosition(m.getX(), m.getY());
                return;
            } else if (m.getX() + 1 == player.getX() && m.getY() == player.getY()) {
                m.setPosition(m.getX(), m.getY());
                return;
            } else if (m.getX() == player.getX() && m.getY() - 1 == player.getY()) {
                m.setPosition(m.getX(), m.getY());
                return;
            } else if (m.getX() == player.getX() && m.getY() + 1 == player.getY()) {
                m.setPosition(m.getX(), m.getY());
                return;
            }


            if (tiles[m.getX() - 1][m.getY()] == TileType.FLOOR || tiles[m.getX() - 1][m.getY()] == TileType.LAVA) { // LEFT
                if (m.getX() > player.getX()) {
                    m.setPosition(m.getX() - 1, m.getY());
                    if (tiles[m.getX()][m.getY()] == TileType.LAVA) {
                        hitMonster(m);
                    }
                }
            }
            if (tiles[m.getX() + 1][m.getY()] == TileType.FLOOR || tiles[m.getX() + 1][m.getY()] == TileType.LAVA) { // RIGHT
                if (m.getX() < player.getX()) {
                    m.setPosition(m.getX() + 1, m.getY());
                    if (tiles[m.getX()][m.getY()] == TileType.LAVA) {
                        hitMonster(m);
                    }
                }
            }
            if (tiles[m.getX()][m.getY() - 1] == TileType.FLOOR || tiles[m.getX()][m.getY() - 1] == TileType.LAVA) { // Up
                if (m.getY() > player.getY()) {
                    m.setPosition(m.getX(), m.getY() - 1);
                    if (tiles[m.getX()][m.getY()] == TileType.LAVA) {
                        hitMonster(m);
                    }
                }
            }
            if (tiles[m.getX()][m.getY() + 1] == TileType.FLOOR || tiles[m.getX()][m.getY() + 1] == TileType.LAVA) { // down
                if (m.getY() < player.getY()) {
                    m.setPosition(m.getX(), m.getY() + 1);
                    if (tiles[m.getX()][m.getY()] == TileType.LAVA) {
                        hitMonster(m);
                    }
                }
            }
            if (m.getX() == player.getX() && m.getY() == player.getY()) { // Attack player
                hitPlayer();
                m.setPosition(p.x, p.y); // Teleport to random floor tile
            }


            // Checks for a suitable position to walk to if the position didn't change, if there is none teleport to a random location.
            if (m.getX() == monX && m.getY() == monY) {
                // If there is a wall to the left
                if (tiles[m.getX() - 1][m.getY()] == TileType.WALL || tiles[m.getX() - 1][m.getY()] == TileType.STAIRS) {
                    if (m.getX() > player.getX()) {
                        if (tiles[m.getX()][m.getY() - 1] != TileType.WALL && tiles[m.getX() - 1][m.getY() - 1] == TileType.FLOOR && tiles[m.getX() - 2][m.getY() - 1] != TileType.WALL) {
                            m.setPosition(m.getX() - 1, m.getY() - 1);
                            return;
                        } else if (tiles[m.getX()][m.getY() + 1] != TileType.WALL && tiles[m.getX() - 1][m.getY() + 1] == TileType.FLOOR && tiles[m.getX() - 2][m.getY() + 1] != TileType.WALL) {
                            m.setPosition(m.getX() - 1, m.getY() + 1);
                            return;
                        } else {
                            m.setPosition(p.x, p.y);
                        }
                    }

                }

                // If there is a wall to the right
                if (tiles[m.getX() + 1][m.getY()] == TileType.WALL || tiles[m.getX() + 1][m.getY()] == TileType.STAIRS) {
                    if (m.getX() < player.getX()) {
                        if (tiles[m.getX()][m.getY() - 1] != TileType.WALL && tiles[m.getX() + 1][m.getY() - 1] == TileType.FLOOR && tiles[m.getX() + 2][m.getY() - 1] != TileType.WALL) {
                            m.setPosition(m.getX() + 1, m.getY() - 1);
                            return;
                        } else if (tiles[m.getX()][m.getY() + 1] != TileType.WALL && tiles[m.getX() + 1][m.getY() + 1] == TileType.FLOOR && tiles[m.getX() + 2][m.getY() + 1] != TileType.WALL) {
                            m.setPosition(m.getX() + 1, m.getY() + 1);
                            return;
                        } else {
                            m.setPosition(p.x, p.y);
                        }
                    }

                }
                // if there is a wall above
                if (tiles[m.getX()][m.getY() - 1] == TileType.WALL || tiles[m.getX()][m.getY() - 1] == TileType.STAIRS) {
                    if (m.getY() > player.getY()) {
                        if (tiles[m.getX() - 1][m.getY()] != TileType.WALL && tiles[m.getX() - 1][m.getY() - 1] == TileType.FLOOR && tiles[m.getX() - 1][m.getY() - 2] != TileType.WALL) {
                            m.setPosition(m.getX() - 1, m.getY() - 1);
                            return;
                        } else if (tiles[m.getX() + 1][m.getY()] != TileType.WALL && tiles[m.getX() + 1][m.getY() - 1] == TileType.FLOOR && tiles[m.getX() + 1][m.getY() - 2] != TileType.WALL) {
                            m.setPosition(m.getX() + 1, m.getY() - 1);
                            return;
                        } else {
                            m.setPosition(p.x, p.y);
                        }
                    }
                }
                // if there is a wall below.
                if (tiles[m.getX()][m.getY() + 1] == TileType.WALL || tiles[m.getX()][m.getY() + 1] == TileType.STAIRS) {
                    if (m.getY() < player.getY()) {
                        if (tiles[m.getX() - 1][m.getY()] != TileType.WALL && tiles[m.getX() - 1][m.getY() + 1] == TileType.FLOOR && tiles[m.getX() - 1][m.getY() + 2] != TileType.WALL) {
                            m.setPosition(m.getX() - 1, m.getY() + 1);
                        } else if (tiles[m.getX() + 1][m.getY()] != TileType.WALL && tiles[m.getX() + 1][m.getY() + 1] == TileType.FLOOR && tiles[m.getX() + 1][m.getY() + 2] != TileType.WALL) {
                            m.setPosition(m.getX() + 1, m.getY() + 1);
                        } else {
                            m.setPosition(p.x, p.y);
                        }
                    }
                }
            }

        }

    

    /**
     * Reduces the health of the player when hit by a monster - a monster next
     * to the player can attack it instead of moving and should call this method
     * to reduce the player's health
     */
    private void hitPlayer() {
        player.changeHealth(-5);
        System.out.println("Player took damage, health is " + player.getHealth());
    }

    /**
     * Processes the monsters array to find any Entity in the array with 0 or
     * less health. Any Entity in the array with 0 or less health should be
     * set to null; when drawing or moving monsters the null elements in the
     * monsters array are skipped.
     */
    private void cleanDeadMonsters() {
        
        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i] != null) {
                if (monsters[i].getHealth() <= 0) {
                monsters[i] = null;
                }
            }
            
        }
    }
    

    /**
     * Called in response to the player moving into a Stair tile in the game.
     * The method increases the dungeon depth, generates a new level by calling
     * the generateLevel method, fills the spawns ArrayList with suitable spawn
     * locations and spawns monsters. Finally it places the player in the new
     * level by calling the placePlayer() method. Note that a new player object
     * should not be created here unless the health of the player should be reset.
     */
    private void descendLevel() {
        depth += 1;
        System.out.println("The depth is " + depth);
        tiles = new TileType[DUNGEON_WIDTH][DUNGEON_HEIGHT];
        tiles = generateLevel();
        spawns = getSpawns();
        monsters = spawnMonsters();
        placePlayer();

        
    }


    /**
     * Places the player in a dungeon level by choosing a spawn location from the
     * spawns ArrayList, removing the spawn position as it is used. The method sets
     * the players position in the level by calling its setPosition method with the
     * x and y values of the Point taken from the spawns ArrayList.
     */
    private void placePlayer() {
        getSpawns();
        int select1 = rng.nextInt(spawns.size());
        Point p = spawns.get(select1);
        player.setPosition(p.x, p.y);
    }

    /**
     * Performs a single turn of the game when the user presses a key on the
     * keyboard. The method cleans dead monsters, moves any monsters still alive
     * and then checks if the player is dead, exiting the game or resetting it
     * after an appropriate output to the user is given. It checks if the player
     * moved into a stair tile and calls the descendLevel method if it does.
     * Finally it requests the GUI to redraw the game level by passing it the
     * tiles, player and monsters for the current level.
     */
    public void doTurn() {
        cleanDeadMonsters();
        moveMonsters();
        if (player != null) {       //checks a player object exists
            if (player.getHealth() < 1) {
                System.exit(0);     //exits the game when player is dead
            }
            if (tiles[player.getX()][player.getY()] == TileType.STAIRS) {
                descendLevel();     //moves to next level if the player is on Stairs
            }
        }
        gui.updateDisplay(tiles, player, monsters);     //updates GUI
    }

    /**
     * Starts a game. This method generates a level, finds spawn positions in
     * the level, spawns monsters and the player and then requests the GUI to
     * update the level on screen using the information on tiles, player and
     * monsters.
     */
    public void startGame() {
        tiles = generateLevel();
        spawns = getSpawns();
        monsters = spawnMonsters();
        player = spawnPlayer();
        gui.updateDisplay(tiles, player, monsters);
    }
}
