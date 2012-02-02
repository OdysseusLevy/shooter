package org.cosmosgame.grailsgame

import org.cosmosgame.grailsgame.Sprite

class GameService {

    GameTimerTask gameTimerTask;
    Timer timer;

    Sprite sprite1;
    Sprite sprite2;
    def sprites;
    static int maxwidth = 800;
    static int maxheight= 1200;

    class GameTimerTask extends TimerTask
    {
        def sprites;

        public GameTimerTask(sprites)
        {
            this.sprites = sprites;
        }

        public void run() {
            sprites.each
                    { it.x += it.deltaX
                        if (it.x >= maxwidth )
                            it.x = 0;
                        else if (it.x < 0)
                        {
                            it.x = width - it.width;
                        }

                        it.y += it.deltaY;
                        if (it.y >= maxwidth)
                            it.y = 0;
                        else if (it.y < 0)
                        {
                            it.y = maxheight = it.height;
                        }
                    }
        }
    }

    GameService()
    {
        Sprite sprite1 = new Sprite()
        sprite1.x = 20;
        sprite1.y = 20;
        sprite1.width = 10;
        sprite1.height = 10;

        sprite1.deltaX = 5;
        sprite1.name = "Ship 1 (grailsgame)"

        Sprite sprite2 = new Sprite();
        sprite2.x = 60;
        sprite2.y = 80;
        sprite2.width = 20;
        sprite2.height = 20;

        sprite2.deltaY = 5;
        sprite2.name = "Ship 2 (grailsgame)"

        sprites = [sprite1, sprite2];
        gameTimerTask = new GameTimerTask(sprites);
        timer = new Timer();
        timer.scheduleAtFixedRate(gameTimerTask, 5, 50)

    }

    def getGameState() {
        return sprites;
    }
}
