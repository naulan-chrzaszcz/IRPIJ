package fr.chrzdevelopment.game;
// https://r12a.github.io/app-conversion/   Java char compatibility

import static fr.chrzdevelopment.game.Const.*;
import fr.chrzdevelopment.game.entities.*;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Ce Thread représente un processus parallel qui se charge des inputs au clavier
 */
class MyThread1 extends Thread
{
    private KeyboardInput keyboardInput;
    private Game game;


    public MyThread1(Game game, KeyboardInput keyboardInput)
    {
        this.keyboardInput = keyboardInput;
        this.game = game;
    }

    @Override
    public void run()
    {
        synchronized (this) {
            while (game.running) {
                keyboardInput.getInput();

                try {
                    this.wait(100);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }
}


/**
 * Ce Thread représente un processus parallel qui se charge de l'actualisation et l'affichage du jeu
 */
class MyThread2 extends Thread
{
    private Game game;


    MyThread2(Game game) { this.game = game; }

    @Override
    public void run()
    {
        synchronized (this) {
            while (game.running) {
                game.updates();
                game.draws();

                try {
                    this.wait(100);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }
}


/**
 * <p>Permet le bon fonctionnement du jeu</p>
 * <p>Rassemble tous !</p>
 */
public class Game
{
    private JSONObject saveFile;

    // Entities
    private final List<Entity> allSprites = new CopyOnWriteArrayList<>();
    private Monster[] monsters;
    private Chest[] chests;
    private Coin[] coins;
    private Key[] keys;
    private Player player;

    private int totalCoins;
    private int maxLvl;
    private String playerName;

    private final KeyboardInput keyboardInput = new KeyboardInput();
    private MapsEngine mapsEngine;
    private final String OS = System.getProperty("os.name");
    // clear terminal commands
    private final ProcessBuilder processBuilder = (OS.equalsIgnoreCase("windows") || OS.equalsIgnoreCase("windows 10")) ? new ProcessBuilder("cmd", "/c", "cls") : new ProcessBuilder("clear");

    protected boolean running = true;


    public Game()
    {
        // Load JSON file
        try {
            // In an IDE
            saveFile = new JSONObject(new JSONTokener( new FileReader("res/save.json")));
        } catch (FileNotFoundException e) {
            // In a JAR
            InputStream in = getClass().getResourceAsStream("/save.json");
            saveFile = new JSONObject(new JSONTokener( new BufferedReader(new InputStreamReader(in))));
        }
        playerName = saveFile.getString("playerName");
        totalCoins = saveFile.getInt("totalCoins");
        maxLvl = saveFile.getInt("maxLvl");

        // Load graphics
        if (OS.equalsIgnoreCase("windows") || OS.equalsIgnoreCase("windows 10"))
            windowsGraphics();
        else linuxGraphics();

        // Crée la taille de la carte
        mapsEngine = new MapsEngine(30, 20);

        // La generation.
        mapsEngine.generateMap();
        spawnEntity();
    }

    /** Démarre la boucle principale du jeu */
    public void loop()
    {
        Sound.play("music.wav", -1);
        clearConsole();

        System.out.println();
        // Title screen
        System.out.println("\t" + ANSI_RED + "                 ▄       ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄       ▄                 ");
        System.out.println("\t" + "                ▐░▌     ▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌     ▐░▌                ");
        System.out.println("\t" + "               ▐░▌       ▀▀▀▀█░█▀▀▀▀ ▐░█▀▀▀▀▀▀▀█░▌▐░█▀▀▀▀▀▀▀█░▌ ▀▀▀▀█░█▀▀▀▀  ▀▀▀▀▀█░█▀▀▀       ▐░▌               ");
        System.out.println("\t" + "              ▐░▌            ▐░▌     ▐░▌       ▐░▌▐░▌       ▐░▌     ▐░▌           ▐░▌           ▐░▌              ");
        System.out.println("\t" + " ▄▄▄▄▄▄▄▄▄▄▄ ▐░▌             ▐░▌     ▐░█▄▄▄▄▄▄▄█░▌▐░█▄▄▄▄▄▄▄█░▌     ▐░▌           ▐░▌            ▐░▌ ▄▄▄▄▄▄▄▄▄▄▄ ");
        System.out.println("\t" + "▐░░░░░░░░░░░▌▐░▌             ▐░▌     ▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌     ▐░▌           ▐░▌            ▐░▌▐░░░░░░░░░░░▌");
        System.out.println("\t" + " ▀▀▀▀▀▀▀▀▀▀▀ ▐░▌             ▐░▌     ▐░█▀▀▀▀█░█▀▀ ▐░█▀▀▀▀▀▀▀▀▀      ▐░▌           ▐░▌            ▐░▌ ▀▀▀▀▀▀▀▀▀▀▀ ");
        System.out.println("\t" + "              ▐░▌            ▐░▌     ▐░▌     ▐░▌  ▐░▌               ▐░▌           ▐░▌           ▐░▌              ");
        System.out.println("\t" + "               ▐░▌       ▄▄▄▄█░█▄▄▄▄ ▐░▌      ▐░▌ ▐░▌           ▄▄▄▄█░█▄▄▄▄  ▄▄▄▄▄█░▌          ▐░▌               ");
        System.out.println("\t" + "                ▐░▌     ▐░░░░░░░░░░░▌▐░▌       ▐░▌▐░▌          ▐░░░░░░░░░░░▌▐░░░░░░░▌         ▐░▌                ");
        System.out.println("\t" + "                  ▀      ▀▀▀▀▀▀▀▀▀▀▀  ▀         ▀  ▀            ▀▀▀▀▀▀▀▀▀▀▀  ▀▀▀▀▀▀▀           ▀                 " + ANSI_RESET);
        System.out.println();

        System.out.println();
        System.out.println("Bienvenue chèr(e) aventurièr(e) ! Ici, multiple aventure vous attend, entre les monstres, les coffres, les obstacles... Il a de quoi faire !");
        System.out.println(ANSI_RED + "============================================================================================================================================" + ANSI_RESET);
        System.out.print("\t" + ANSI_GREEN + "Entrez votre nom > ");
        playerName = keyboardInput.getStringInput();

        // Game loops
        Thread myThread2 = new MyThread2(this);
        Thread myThread1 = new MyThread1(this, keyboardInput);

        myThread2.start();
        myThread1.setPriority(Thread.MAX_PRIORITY);
        myThread1.start();
    }

    /**
     * Nettoie tout ce qui est present et afficher sur le terminal.
     * @exception IOException Essaye avec une deuxième méthode pour nettoyer la console
     * @exception InterruptedException Essaye encore une fois tout le processus
     */
    private void clearConsole()
    {
        // TODO: Demander a l'enseignant si cela est correct. J'ai essayé de bien faire pour qu'il est deux essaie au cas où
        // Essaye encore une fois si le premier essaie ne marche pas sinon il casse la boucle.
        Process process = null;
        try {
            process = processBuilder.inheritIO().start();
        } catch (IOException IOe) {
            if (!OS.equalsIgnoreCase("windows") || !OS.equalsIgnoreCase("windows 10")) {
                // la deuxième méthode qui permet de nettoyer la console...
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        }

        if (process != null)
            try {
                process.waitFor();
            } catch (InterruptedException ignored) { }
    }

    private void spawnEntity()
    {
        allSprites.clear();
        player = mapsEngine.spawnPlayer(allSprites);
        monsters = mapsEngine.spawnMonster(allSprites);
        coins = mapsEngine.spawnCoin(allSprites);
        chests = mapsEngine.spawnChest(allSprites);
        keys = mapsEngine.spawnKey(allSprites);
    }

    /** Dessine les elements qui nécessite à voir sur la console */
    public synchronized void draws()
    {
        clearConsole();

        // Information relative au nombre de piece necessaire pour gagner le niveau
        System.out.println("\t" + ANSI_RED + "NIVEAUX: " + mapsEngine.getMapLvl() + ANSI_RESET);
        System.out.println(ANSI_GREEN + playerName + " ! Vous devez avoir " + mapsEngine.getDeterminateCoins() + " " + COIN_IMG + ANSI_GREEN + " pour pouvoir gagner le niveau" + ANSI_RESET);
        mapsEngine.draw();

        // Affiche les pieces du joueur obtenu et son nombre de point de vie total (Nombre de <3)
        StringBuilder msgHud = new StringBuilder().append(COIN_IMG).append(": ").append(player.getCoins()).append("   ");
        for (int h = 1; h <= player.getHealth(); h++)
            msgHud.append(HEART_IMG).append(" ");
        msgHud.append("   ").append(KEY_IMG).append(": ").append((player.getHaveAKey()) ? "oui" : "non");
        System.out.print("\t" + msgHud.toString());
        System.out.println();
    }

    /** Actualise les valeurs qui ont besoin d'etre actualisé à chaque passage de la boucle */
    public synchronized void updates()
    {
        if (keyboardInput.getQuitAction())
            running = false;
        else
            // les déplacements du joueur
            if (!player.getCollideUp() && keyboardInput.getMoveUp())
                player.moveUp();
            if (!player.getCollideDown() && keyboardInput.getMoveDown())
                player.moveDown();
            if (!player.getCollideLeft() && keyboardInput.getMoveLeft())
                player.moveLeft();
            if (!player.getCollideRight() && keyboardInput.getMoveRight())
                player.moveRight();

        // TODO: faire un truc plus propre pour les changements de niveau
        if (player.getCoins() == mapsEngine.getDeterminateCoins())
            mapsEngine.addMapLvl();

        if (mapsEngine.getMapLvl() > 1 && !mapsEngine.getIsGenerate()) {
            mapsEngine.setHeight(RANDOM.nextInt(20, 60));
            mapsEngine.setWidth(RANDOM.nextInt(20, 60));
            mapsEngine.generateMap();
            mapsEngine.generateObstacles();
            spawnEntity();
        }

        for (Entity sprite : allSprites)
        {
            for (Coin coin : coins)
                if (sprite.equals(coin))
                    if (sprite.getXPosition() == player.getXPosition() && sprite.getYPosition() == player.getYPosition()) {
                        player.addCoin();
                        coin.isPickup();
                    }

            for (Chest chest : chests)
               if (sprite.equals(chest))
                   if (keyboardInput.getSelect())
                       if ((sprite.getXPosition() == player.getXPosition()-1 || sprite.getXPosition() == player.getXPosition()+1)
                               || (sprite.getYPosition() == player.getYPosition()-1 || sprite.getYPosition() == player.getYPosition()+1))
                           if (player.getHaveAKey()) {
                               if (chest.getWhatInside().equalsIgnoreCase("coin"))
                                   player.addCoin();
                               else if (chest.getWhatInside().equalsIgnoreCase("health"))
                                   player.setHealth(player.getHealth()+1);

                               chest.hit();
                               player.haventKey();
                           }

            for (Key key : keys)
                if (sprite.equals(key))
                    if (keyboardInput.getSelect())
                        if ((sprite.getXPosition() == player.getXPosition()-1 || sprite.getXPosition() == player.getXPosition()+1)
                                || (sprite.getYPosition() == player.getYPosition()-1 || sprite.getYPosition() == player.getYPosition()+1))
                            if (!player.getHaveAKey()) {
                                player.haveKey();
                                key.hit();
                            }


            sprite.checkCollision(mapsEngine.getCalqueCollide());
            sprite.updates();
            mapsEngine.updateEntity(sprite, sprite.getDataImg() != COIN && sprite.getDataImg() != KEY);
        }
    }
}
