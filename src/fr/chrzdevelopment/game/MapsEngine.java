package fr.chrzdevelopment.game;

import static fr.chrzdevelopment.game.Const.*;

import fr.chrzdevelopment.game.entities.*;

import java.util.List;


/**
 * <h1 style="font-size: 115%;">Le générateur de cartes</h1>
 * <h2 style="font-size: 105%; text-decoration: underline;">Qu'est-ce qu'il fait ?</h2>
 * <ul>
 *     <li><p>Genere une carte avec la taille qu'on lui a mit lors de l'initialisation grâce à la fonction "generationMap()".</p></li>
 *     <li><p>Genere des obstacles, le nombre d'obstacles est générer de facon aleatoire y compris la taille de celui-ci, mais pas que, le placement des obstacles le sont aussi sur la carte.</p></li>
 *     <li><p>Place le joueur sur la carte qui sera obtenable grâce au getter "getPlayer()" ou bien dans cette classe grâce a la variable player.</p></li>
 *     <li><p>Genere les monstres avec des positions aléatoire grâce a "findALocation()" et range les monstres dans "allSprites", c'est Sprite sont updates dans la fonction "updates()" de cette classe</p></li>
 *     <li><p>Genere les coffres et les pieces qui permet une victoire sur la carte (Sur les nombres de pieces obtenue)</p></li>
 *     <li><p>Dessine toutes les choses present sur la carte grâce a la fonction "draw()"</p></li>
 * </ul>
 *
 * @see fr.chrzdevelopment.game.Game
 * @since 1.0
 * @author CHRZASZCZ Naulan
 */
public class MapsEngine
{
    // Calques
    private boolean[][] calqueCollide;
    private int[][] map;
    // Map size
    private int width;
    private int height;

    private int determinateCoins;
    private int mapLvl = 1;

    private boolean isGenerate = false;


    /**
     * @param width La taille initiale de la carte en largeur
     * @param height La taille initiale de la carte en hauteur
     */
    public MapsEngine(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    /** Determine une position et fait en sorte que ne soit pas dans un mur. */
    private int[] findALocation()
    {
        int[] loc = new int[2];
        do {
            loc[0] = RANDOM.nextInt(1, map[0].length-1);
            loc[1] = RANDOM.nextInt(1, map.length-1);
        } while (map[loc[1]][loc[0]] == WALL || map[loc[1]][loc[0]] == MONSTER || map[loc[1]][loc[0]] == CHEST || map[loc[1]][loc[0]] == COIN);

        return loc;
    }

    /** Crée et place les monstres et le joueur sur la carte */
    public Player spawnPlayer(List<Entity> allSprites)
    {
        int x; int y; int[] loc;
        loc = findALocation();
        x = loc[0]; y = loc[1];

        if (allSprites.size() != 0 && allSprites.get(0).getDataImg() == PLAYER)
            System.out.println("dfgkl,dfgnjklmergklpùer^flmopêrfrkopergkopegkortpejkoergjierjietjioe'tjioertjiortejioer");
        // Crée le joueur
        return new Player(allSprites, x, y, 1);
    }

    public Monster[] spawnMonster(List<Entity> allSprites)
    {
        int nbMonster = RANDOM.nextInt(0, 6);
        Monster[] monsters = new Monster[nbMonster];

        int x; int y; int[] loc;
        for (int m = 0; m < nbMonster; m++) {
            loc = findALocation();
            x = loc[0];
            y = loc[1];

            // Crée le monstre et le range dans le tableau.
            monsters[m] = new Monster(allSprites, x, y, 1);
        }

        return monsters;
    }

    public Coin[] spawnCoin(List<Entity> allSprites)
    {
        determinateCoins = RANDOM.nextInt(1, 11);
        Coin[] coins = new Coin[determinateCoins];

        int x; int y; int[] loc;
        // TODO: Faire un truc plus complet avec des formes et des chemins de piece
        for (int c = 0; c < determinateCoins; c++) {
            loc = findALocation();
            x = loc[0]; y = loc[1];

            coins[c] = new Coin(allSprites, x, y);
        }

        return coins;
    }

    public Chest[] spawnChest(List<Entity> allSprites)
    {
        int nbChest = 2;
        Chest[] chests = new Chest[nbChest];

        int x; int y; int[] loc;
        for (int c = 0; c < chests.length; c++) {
            loc = findALocation();
            x = loc[0]; y = loc[1];

            // Crée le coffre, on lui dit ce qu'il va loot et on le place dans le tableau
            String loot = LOOTS[RANDOM.nextInt(0, LOOTS.length)];
            if (loot.equalsIgnoreCase("coin"))
                determinateCoins++;
            chests[c] = new Chest(allSprites, loot, x, y);
        }

        return chests;
    }

    public Key[] spawnKey(List<Entity> allSprites)
    {
        int nbKey = 2;
        Key[] keys = new Key[nbKey];

        int x; int y; int[] loc;
        for (int k = 0; k < keys.length; k++) {
            loc = findALocation();
            x = loc[0]; y = loc[1];

            keys[k] = new Key(allSprites, x, y);
        }

        return keys;
    }

    /**
     * <p>Ecrit sur la carte, une donnée propre a l'entité selon la position en x et en y</p>
     * <ul>
     *     <li>Si, il ne sait pas déplacé... Il ne clear pas la dernier frame.</li>
     *     <li>Sinon, il clear la frame si, il ce deplace.</li>
     * </ul>
     * @param entity Un Sprite (ou une entité) qui doit avoir un clear de la frame precedent.
     */
    public void updateEntity(Entity entity, boolean collide)
    {
        // Modifier la carte selon la position du joueur
        setElementMap(entity.getXPosition(), entity.getYPosition(), entity.getDataImg(), collide);
        if (entity.getXPreviousPosition() != -1)    // Dans le cas où, le joueur ne se serait pas déplacé
            // Clear la dernière "frame"
            setElementMap(entity.getXPreviousPosition(), entity.getYPreviousPosition(), EMPTY, false);
    }

    public int[][] getMap() { return map; }
    public boolean getIsGenerate() { return isGenerate; }
    /** Donne une matrice de 0 et de 1 qui determine sur la map, qu'est-ce qui ont la fonction "collide" */
    public boolean[][] getCalqueCollide() { return calqueCollide; }
    public int getDeterminateCoins() { return determinateCoins; }
    public int getMapLvl() { return mapLvl; }

    public void setWidth(int newWidthSize) { width = newWidthSize; }
    public void setHeight(int newHeightSize) { height = newHeightSize; }
    /** Place un element sur la matrice de la carte puis determine sur le calque de collision, si c'est un object de type "collide" */
    public void setElementMap(int x, int y, int val, boolean isCollideObject)
    {
        calqueCollide[y][x] = isCollideObject;
        map[y][x] = val;
    }

    public void addMapLvl()
    {
        isGenerate = false;
        mapLvl++;
    }

    /** Genere une map selon la taille specifié lors de l'initialisation de la classe */
    public void generateMap()
    {
        map = new int[height][width];
        calqueCollide = new boolean[height][width];

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                if ((y == 0 || y == height-1) || (x == 0 || x == width-1))
                    setElementMap(x, y, WALL, true);
                else setElementMap(x, y, EMPTY, false);
        isGenerate = true;
    }

    public void generateObstacles()
    {
        // Determine le nombre d'obstacle a prevoir
        int nbObstacle = RANDOM.nextInt(1, 4);

        // Generation des obstacles
        for (int o = 0; o <= nbObstacle; o++) {
            // Détermine la taille
            int h = RANDOM.nextInt(1, 5);
            int w = RANDOM.nextInt(1, 5);
            // Creation de l'obstacle
            char[][] obstacle = new char[h][w];
            for (int c = 0; c < h; c++)
                for (int r = 0; r < w; r++)
                    obstacle[c][r] = WALL;

            // Placement sur la Map
            int x = Math.abs(RANDOM.nextInt(0, width)-(obstacle[0].length-1));
            int y = Math.abs(RANDOM.nextInt(0, height)-(obstacle.length-1));
            // Ecriture sur la Map
            for (int yMap = 0; yMap < y; yMap++)
                for (int xMap = 0; xMap < x; xMap++)
                    if (y+yMap < map.length && x+xMap < map[0].length)
                        setElementMap(x+xMap, y+yMap, WALL, true);
        }
    }

    public void draw()
    {
        for (int[] row : map) {
            StringBuilder line = new StringBuilder();
            for (int column : row)
                allDataObjImg.computeIfPresent(column, (a, b) -> { line.append(b); return b; });
            System.out.println(line);
        }
    }
}
