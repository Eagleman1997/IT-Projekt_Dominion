package Server_GameLogic;

import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import Cards.Card;
import Cards.Copper_Card;

/**
 * @author Bodo
 * @version 1.0
 * @created 31-Okt-2017 17:08:57
 */
public class Player {

	protected int actions;
	protected int buys;
	protected int coins;
	protected Stack<Card> deckPile;
	protected Stack<Card> discardPile;
	protected Game gameThread;
	protected LinkedList<Card> handCards;
	protected LinkedList<Card> playedCards;
	protected String playerName;
	protected int victoryPoints;

	private final int NUM_OF_HANDCARDS = 5;
	
	protected Socket clientSocket;
	
	private boolean isFinished;
	
	private String actualPhase;
	

	/**
	 * 
	 * @param name
	 */
	public Player(String name) {
		this.deckPile = new Stack<Card>();
		this.discardPile = new Stack<Card>();
		this.handCards = new LinkedList<Card>();
		this.playedCards = new LinkedList<Card>();
		
		this.coins = 0;
		this.actions = 1;
		this.buys = 0;
		
		this.isFinished = false;
		
		this.actualPhase = "";
	}

	/**
	 * 
	 * @param gameThread
	 */
	public void addGameThread(Game gameThread) {
		this.gameThread = gameThread;
	}

	/**
	 * 
	 * @param cardName
	 */
	public Card buy(String cardName) {
		Card buyedCard = null;
		this.actualPhase = "buy";
		
		switch(cardName){
		case "Copper_Card":
			buyedCard = this.gameThread.getCopperPile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Cellar_Card":
			buyedCard = this.gameThread.getCellarPile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Duchy_Card":
			buyedCard = this.gameThread.getDuchyPile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Estate_Card":
			buyedCard = this.gameThread.getEstatePile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Gold_Card":
			buyedCard = this.gameThread.getGoldPile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Market_Card":
			buyedCard = this.gameThread.getMarketPile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Mine_Card":
			buyedCard = this.gameThread.getMinePile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Province_Card":
			buyedCard = this.gameThread.getProvincePile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Remodel_Card":
			buyedCard = this.gameThread.getRemodelPile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Silver_Card":
			buyedCard = this.gameThread.getSilverPile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Smithy_Card":
			buyedCard = this.gameThread.getSmithyPile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Village_Card":
			buyedCard = this.gameThread.getVillagePile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Woodcutter_Card":
			buyedCard = this.gameThread.getWoodcutterPile().pop();
			this.discardPile.push(buyedCard);
			break;
		case "Workshop_Card":
			buyedCard = this.gameThread.getWorkshopPile().pop();
			this.discardPile.push(buyedCard);
			break;
		}
		
		return buyedCard;
	}

	public void cleanUp() {
		this.actualPhase = "cleanUp";
		
		while (!playedCards.isEmpty()){
			this.discardPile.push(playedCards.remove());
		}
		
		while(!handCards.isEmpty()){
			this.discardPile.push(handCards.remove());
		}
		
		this.draw();
		
		this.setFinished(true);
	
		gameThread.checkGameEnding();

	}

	/** 
	* If Deckpile is empty, the discard pile fills the deckPile. Eventually
	* the deckPiles get shuffled and the player draws 5 Cards from deckPile
	* to HandPile.
	*
	* Else If the deckpile size is lower than 5, the rest of deckPiles
	* will be drawed and the discard pile fills the deckPile.
	* eventually the deckPile get shuffled and the player draws the
	* rest of the Cards until he has 5 Cards in the HandPile.
	*
	* Else if they are enough cards in the deckPile, the player draws 5
	* cards into the handPile
	*
	* NOT FINISHED!
	*/

	public void draw() {
		
		if (deckPile.isEmpty()) {
			while (!discardPile.isEmpty())
				deckPile.push(discardPile.pop());
			Collections.shuffle(deckPile);
			for (int i = 0; i < NUM_OF_HANDCARDS; i++)
				handCards.add(deckPile.pop());
		} else if (deckPile.size() < NUM_OF_HANDCARDS) {
			while (!deckPile.isEmpty())
				handCards.add(deckPile.pop());
			while (!discardPile.isEmpty())
				deckPile.push(discardPile.pop());
			Collections.shuffle(deckPile);
			for (int i = 0; i < NUM_OF_HANDCARDS - handCards.size(); i++)
				handCards.add(deckPile.pop());
		} else {
			for (int i = 0; i < NUM_OF_HANDCARDS; i++)
				handCards.add(deckPile.pop());
		}
	}
	
	public void draw(int number){
		
	}
	
	

	/**
	 * 
	 *
	 */
	public void play(String cardName) {
		Card playedCard = null;
		int index = 0;
		this.actualPhase = "play";
		
		switch(cardName){
		case "Copper_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Cellar_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Duchy_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Estate_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Gold_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Market_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Mine_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Province_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Remodel_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Silver_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Smithy_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Village_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Woodcutter_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		case "Workshop_Card":
			index = this.handCards.indexOf(cardName);
			playedCard = this.handCards.remove(index);
			playedCard.executeCard(this);
			playedCards.add(playedCard);
			break;
		}
	}

	public void skipPhase() {
		switch(this.actualPhase){
		case "play":
			//?
		case "buy":
			//?
		case "cleanUp":
			//?
		}
	}

	public int getActions() {
		return actions;
	}

	public void setActions(int actions) {
		this.actions = actions;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public Stack<Card> getDeckPile() {
		return deckPile;
	}

	public void setDeckPile(Stack<Card> deckPile) {
		this.deckPile = deckPile;
	}

	public int getBuys() {
		return buys;
	}

	public void setBuys(int buys) {
		this.buys = buys;
	}

	public LinkedList<Card> getHandCards() {
		return handCards;
	}

	public void setHandCards(LinkedList<Card> handCards) {
		this.handCards = handCards;
	}

	public Game getGameThread() {
		return gameThread;
	}

	public void setGameThread(Game gameThread) {
		this.gameThread = gameThread;
	}

	public Stack<Card> getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(Stack<Card> discardPile) {
		this.discardPile = discardPile;
	}

	public LinkedList<Card> getPlayedCards() {
		return playedCards;
	}

	public void setPlayedCards(LinkedList<Card> playedCards) {
		this.playedCards = playedCards;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getVictoryPoints() {
		return victoryPoints;
	}

	public void setVictoryPoints(int victoryPoints) {
		this.victoryPoints = victoryPoints;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public String getActualPhase() {
		return actualPhase;
	}

	public void setActualPhase(String actualPhase) {
		this.actualPhase = actualPhase;
	}
}// end Player