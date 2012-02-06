package org.cosmosgame.grailsgame

/**
 * Created by IntelliJ IDEA.
 * User: odysseus
 * Date: 2/2/12
 * Time: 7:26 AM
 * To change this template use File | Settings | File Templates.
 */
interface IGameEngine
{
    public void start();
    public void stop();

    //public void doAction(Action action);

    public void load(Game g);
    public List getState();
}
