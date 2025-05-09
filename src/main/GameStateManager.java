package main;
public class GameStateManager {


    public enum GameState {
        MAIN_MENU,
        GAMEPLAY,
        GAME_OVER


    }
    private GameState currentState = GameState.MAIN_MENU;


    public  GameState getState() {

        return currentState;
    }

    public void setState(GameState state) {

        this.currentState = state;
    }

    public boolean is(GameState state) {
        return this.currentState == state;
    }
}






