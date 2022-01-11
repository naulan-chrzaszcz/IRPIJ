package fr.chrzdevelopment.game.entities;

import fr.chrzdevelopment.game.Sound;

import java.util.List;

import static fr.chrzdevelopment.game.Const.*;


/**
 * <h1 style="font-size: 115%">Objet Monster qui hérite de la classe Entity</h1>
 * <h2 style="font-size: 105%; text-decoration: underline;">Qu'est-ce qu'il fait ?</h2>
 * <ul>
 *     <li><p>Bouge sur la carte aléatoirement si aucune collision est présente grâce à la fonction "randomMove()" sur les 4 directions possible qui est droite, gauche, haut et bas</p></li>
 *     <li><p>Tire une sorte de rayon laser aléatoirement grâce à la fonction "randomShoot()" et que lorsque le joueurs rentre en collision avec ce rayon laser, démarre la scene de combat. La variable offsetWhereShooting determine où vas t-il tirée, une animation va ce produire lorsqu'il tire (Un laser rouge)</p></li>
 *     <li><p>Aussi, lors d'un contact physique avec le Joueur, il va aussi ce produire une scene de combat</p></li>
 *     <li><p>Lorsque le monstre meurs, il s'auto supprime dans la liste "group" grâce a "getGroup()" dans Entity. Tous cela se passe dans "updates()" de cette classe.</p></li>
 * </ul>
 *
 * @see fr.chrzdevelopment.game.entities.Entity
 * @since v1.0
 * @author CHRZASZCZ Naulan
 */
public class Monster extends Entity
{
    /**
     * @param group Un endroit où on place tous les Sprites (Les entités).
     * @param x La localisation en x du Sprite.
     * @param y La localisation en y du Sprite.
     * @param velocity La vitesse du Monstre.
     */
    public Monster(List<Entity> group, int x, int y, int velocity) { super(group, "Monster", x, y, velocity); }

    /** Bouge le monstre aléatoirement sur les 4 directions possible. */
    private void randomMove()
    {
        int moveRandomly = (int) (Math.random()*4);

        if (moveRandomly == UP && !getWhereCollide()[0])
            moveUp();
        if (moveRandomly == DOWN && !getWhereCollide()[1])
            moveDown();
        if (moveRandomly == LEFT && !getWhereCollide()[2])
            moveLeft();
        if (moveRandomly == RIGHT && !getWhereCollide()[3])
            moveRight();
    }

    /** Fait tirer un rayon laser aléatoirement ou en direction du joueur selon en X ou en Y au monstre sur 4 directions possible. */
    public void shoot(Player player, int XMax, int YMax)
    {
        if (player.getYPosition() == getYPosition() || player.getXPosition() == getXPosition()) {
            if (player.getXPosition() > getXPosition() && player.getYPosition() == getYPosition()) {
                for (int x1 = getXPosition(); x1 < XMax; x1++)
                    if (x1 == player.getXPosition()) {
                        getGroup().add(new Laser(getGroup(), getXPosition(), getYPosition(), 1));
                        Sound.play("laserShoot.wav", 0);
                        return;
                    }
            } else if (player.getXPosition() < getXPosition() && player.getYPosition() == getYPosition()) {
                for (int x2 = getXPosition(); x2 > 0; x2--)
                    if (x2 == player.getXPosition()) {
                        getGroup().add(new Laser(getGroup(), getXPosition(), getYPosition(), 2));
                        Sound.play("laserShoot.wav", 0);
                        return;
                    }
            }

            if (player.getYPosition() > getYPosition() && player.getXPosition() == getXPosition()) {
                for (int y1 = getYPosition(); y1 < YMax; y1++)
                    if (y1 == player.getYPosition()) {
                        getGroup().add(new Laser(getGroup(), getXPosition(), getYPosition(), 3));
                        Sound.play("laserShoot.wav", 0);
                        return;
                    }
            } else if (player.getYPosition() < getYPosition() && player.getXPosition() == getXPosition()) {
                for (int y2 = getYPosition(); y2 > 0; y2--)
                    if (y2 == player.getYPosition()) {
                        getGroup().add(new Laser(getGroup(), getXPosition(), getYPosition(), 0));
                        Sound.play("laserShoot.wav", 0);
                        return;
                    }
            }
        } else {
            int shoot = (int) (Math.random()*50);

            if (shoot == 0 || shoot == 1 || shoot == 2 || shoot == 3) {
                getGroup().add(new Laser(getGroup(), getXPosition(), getYPosition(), shoot));

                Sound.play("laserShoot.wav", 0);
            }
        }
    }

    @Override
    public void updates()
    {
        randomMove();

        if (getHealth() <= 0)
            getGroup().remove(this);
    }
}
